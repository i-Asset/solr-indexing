package at.srfg.indexing.service.impl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Service;

import at.srfg.indexing.model.common.IPropertyAware;
import at.srfg.indexing.model.solr.FacetResult;
import at.srfg.indexing.model.solr.IndexField;
import at.srfg.indexing.model.solr.Search;
import at.srfg.indexing.model.solr.SearchResult;
import at.srfg.indexing.service.SolrService;
import at.srfg.indexing.service.event.PropertyEvent;
import at.srfg.indexing.solr.query.QueryHelper;
import at.srfg.indexing.solr.query.SolrJoin;

@Service
public abstract class SolrServiceImpl<T> implements SolrService<T> {
	private static final String QUOTE = "\"";
	@Autowired
	private SolrCrudRepository<T, String> solr;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	// holds the generic class type for the SolrService
	private Class<T> clazz = getGenericType();
	
    @SuppressWarnings("unchecked")
	private Class<T> getGenericType() {
    	return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

	@Resource
	private SolrTemplate solrTemplate;

	@Override
	public Optional<T> get(String uri) {
		Optional<T> t =  solr.findById(uri);
		if ( t.isPresent()) {
			postSelect(t.get());
		}
		return t;
		
	}

	@Override
	public void set(T item) {
		// perform pre processing
		handlePersist(item);
		solr.save(item);
	}

	@Override
	public boolean set(List<T> items) {
		for (T t : items) {
			set(t);
		}
		return true;
	}
	/**
	 * Obtain the name of the SOLR collection to work with
	 * @return The collection's name
	 */
	public abstract String getCollection();
	/**
	 * Obtain the class name of the mapped SOLR document
	 * @return The class of the mapped SOLR document
	 */
	public abstract Class<T> getSolrClass();

	@Override
	public void remove(String uri) {
		Optional<T> c = get(uri);
		if (c.isPresent()) {
			solr.delete(c.get());
		}
	}

	@Override
	public SearchResult<T> search(Search search) {
		if(search.getSort() != null) {
			return select(search.getQuery(),
					search.getFilterQuery(),
					search.getFacetFields(),
					search.getSort(),
					search.getFacetLimit(),
					search.getFacetMinCount(),
					search.getPage());
		}else{
			return select(search.getQuery(),
					search.getFilterQuery(),
					search.getFacetFields(),
					search.getFacetLimit(),
					search.getFacetMinCount(),
					search.getPage());
		}
	}
	
	@Override
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, int facetLimit, int facetMinCount, Pageable page) {

		QueryHelper queryHelper = new QueryHelper(getGenericType());
		// expand main query to a wild card search when it is only a single word
		if (query.indexOf(":") == -1 && query.indexOf("*") == -1 && query.indexOf(" ") == -1) {
			query = String.format("*%s*", query);
		} else if (query.indexOf("classification.") != -1) {
			//parse the query with a join on class index for synonyms
			query = queryHelper.parseQuery(clazz, query);
		}

		Criteria qCriteria = new SimpleStringCriteria(query);

		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (String filter : filterQueries) {
				queryHelper.addFilter(filter);
			}
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			for (String fieldName : facetFields ) {
				// facet field could be a joined field
				queryHelper.addFacetField(fieldName);
			}
		}
		SearchResult<T> result = select(qCriteria, queryHelper.getFilterQueries(), queryHelper.getFacetFields(), facetLimit, facetMinCount, page);
		
		for (String join : queryHelper.getJoins()) {
			// process join facets
			if (!queryHelper.getFacetFields(join).isEmpty()) {
				// process a query for the joined facet fields
				joinFacets(result, join, queryHelper.getJoinInfo(join), queryHelper.getFilterQueries(join), queryHelper.getFacetFields(join), facetLimit, facetMinCount, page);
			}
			
		}
		return result;
	}

	@Override
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, List<String> sortFields, int facetLimit, int facetMinCount, Pageable page) {
		// expand main query to a wild card search when it is only a single word
		if (query.indexOf(":") == -1 && query.indexOf("*") == -1 && query.indexOf(" ") == -1)   {
			query = String.format("*%s*", query);
		}
		
		Criteria qCriteria = new SimpleStringCriteria(query);
		//
		QueryHelper joinHelper = new QueryHelper(getGenericType());

		if ( filterQueries != null && !filterQueries.isEmpty()) {
			//
			for (String filter : filterQueries) {
				joinHelper.addFilter(filter);
			}
		}

		if ( facetFields != null && !facetFields.isEmpty()) {
			for (String fieldName : facetFields ) {
				// facet field could be a joined field
				joinHelper.addFacetField(fieldName);
			}
		}

		SearchResult<T> result = select(qCriteria, joinHelper.getFilterQueries(), joinHelper.getFacetFields(),sortFields, facetLimit, facetMinCount, page);

		for (String join : joinHelper.getJoins()) {
			// process join facets
			if (!joinHelper.getFacetFields(join).isEmpty()) {
				// process a query for the joined facet fields
				joinFacets(result, join, joinHelper.getJoinInfo(join), joinHelper.getFilterQueries(join), joinHelper.getFacetFields(join), facetLimit, facetMinCount, page);
			}

		}
		return result;
	}


	private SearchResult<T> select(
			Criteria query,
			Set<FilterQuery> filterQueries,
			Set<Field> facetFields,
			int facetLimit,
			int facetMinCount,
			Pageable page) {

		FacetQuery fq = new SimpleFacetQuery(query, page);
		// add filter queries
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			//
			for (FilterQuery filter : filterQueries) {
				fq.addFilterQuery(filter);
			}
		}
		for (Field f : getSelectFieldList()) {
			fq.addProjectionOnField(f);
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (Field facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			//
			facetOptions.setFacetMinCount(facetMinCount);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}

		FacetPage<T> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass(), RequestMethod.POST);


		// enrich content - to be overloaded by subclasses
		postSelect(result.getContent());


		return new SearchResult<>(result);
	}

	private SearchResult<T> select(
			Criteria query,
			Set<FilterQuery> filterQueries,
			Set<Field> facetFields,
			List<String> sortFields,
			int facetLimit,
			int facetMinCount,
			Pageable page) {

		FacetQuery fq = new SimpleFacetQuery(query, page);
		// add filter queries


		if(sortFields != null){

			for(String sortVal : sortFields){
				int fieldDelimPos = sortVal.indexOf(" ");
				String fieldName = sortVal.substring(0,fieldDelimPos);
				String operation = sortVal.substring(fieldDelimPos+1,sortVal.length());
				if(operation.equalsIgnoreCase("desc")){
					fq.addSort(new Sort(Sort.Direction.DESC,fieldName));
				}else{
					fq.addSort(new Sort(Sort.Direction.ASC,fieldName));
				}
			}
		}

		if ( filterQueries != null && !filterQueries.isEmpty()) {
			//
			for (FilterQuery filter : filterQueries) {
				fq.addFilterQuery(filter);
			}
		}
		for (Field f : getSelectFieldList()) {
			fq.addProjectionOnField(f);
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (Field facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			//
			facetOptions.setFacetMinCount(facetMinCount);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}

		FacetPage<T> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass(), RequestMethod.POST);


		// enrich content - to be overloaded by subclasses
		postSelect(result.getContent());


		return new SearchResult<>(result);
	}
	/**
	 * Method to query for joined facets. The faceting result is added to the main query
	 * @param toExtend The main search result
	 * @param joinName The join name, is used to prefix the facet field name
	 * @param join The {@link JoinInfo} specifying the join between collections
	 * @param filterQueries relevant filterqueries to reduce the result
	 * @param facetFields The facets from the joined collection
	 * @param facetLimit The number of facets
	 * @param facetMinCount the minimum number of occurrences
	 * @param page The query page
	 * @return
	 */
	private SearchResult<T> joinFacets(
			SearchResult<T> toExtend,
			String joinName,
			SolrJoin join,
			Set<FilterQuery> filterQueries, 
			Set<Field> facetFields,
			int facetLimit,
			int facetMinCount,
			Pageable page) {
		


		FacetQuery fq = new SimpleFacetQuery(new SimpleStringCriteria("*:*"), page);
		// we are interested in facets only
		fq.setRows(0);
		// add filter queries
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (FilterQuery filter : filterQueries) {
				fq.addFilterQuery(filter);
			}
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (Field facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			// 
			
			facetOptions.setFacetMinCount(facetMinCount);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}
		FacetPage<?> result = solrTemplate.queryForFacetPage(join.getFromIndex(),fq, join.getJoinedType(), RequestMethod.POST);
		for (Field field :  result.getFacetFields()) {
			Page<FacetFieldEntry> facetResultPage = result.getFacetResultPage(field);
			//
			for (FacetFieldEntry entry : facetResultPage.getContent() ) {
				// add the entry value with the mapped name
				String mappedField = String.format("%s.%s",  joinName, entry.getField().getName());
				toExtend.addFacet(mappedField, entry.getValue(), entry.getValueCount());
			}
		}

		return toExtend;
	}

	@Override
	public FacetResult suggest(String query, String facetField, int facetLimit, int facetMinCount) {
		// 
		if (! query.contains("*")) {
			query = "*"+query.trim()+"*";
		}
		// restrict the querying to the field, so one has a glue
		FacetQuery fq = new SimpleFacetQuery(Criteria.where(facetField).expression(query));
		// no need to return content
		fq.setRows(0);
	
		FacetOptions facetOptions = new FacetOptions();
		facetOptions.addFacetOnField(facetField);
		// 
		
		facetOptions.setFacetMinCount(facetMinCount);
		facetOptions.setFacetLimit(facetLimit);
		fq.setFacetOptions(facetOptions);
	
		FacetPage<?> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass(), RequestMethod.POST);
		SearchResult<?> search = new SearchResult<>(result);
		if ( search.getFacets() != null && search.getFacets().containsKey(facetField)) {
			return search.getFacets().get(facetField);
		}
		return new FacetResult(facetField);
	}
	/**
	 * Retrieve the list of index-Fields in use,
	 * @return The list of {@link IndexField} objects
	 */
	@Override
	public Collection<IndexField> fields() {
		return fields(null);
	}
	/**
	 * Retrieve the list of index-Fields in use,
	 * @param fieldNames A set of requested fields, wildcards are allowed at the beginning or at the end of each requested field
	 * @return The list of {@link IndexField} objects
	 */
	@Override
	public Collection<IndexField> fields(Set<String> fieldNames) {
		
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(false);
		try {
			LukeResponse resp = luke.process(solrTemplate.getSolrClient(), getCollection());
			
			@SuppressWarnings("unchecked")
			NamedList<Object> fields = (NamedList<Object>) resp.getResponse().get("fields");
			Map<String,IndexField> inUse = getFields(fields, fieldNames);
			// enrich required fields
			enrichFields(inUse);
			return inUse.values();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
		
	}
	/**
	 * Perform post processing on a list of selected objects
	 * <p>
	 * Subclasses may override
	 * </p>
	 */
	protected void enrichFields(Map<String, IndexField> inUse) {
		// subclasses may override
	}
	/**
	 * Perform post processing on a list of selected objects
	 * <p>
	 * Subclasses may override
	 * </p>
	 */
	protected void postSelect(List<T> content) {
		// subclasses may override
	}
	/**
	 * Perform pre-processing on an object which is to be saved.
	 * <p>
	 * Subclasses may override, on overr
	 * </p>
	 */
	protected void prePersist(T t) {
		// subclasses may override
	}
	private void handlePersist(T t) {
		// call the implementors prePersist
		prePersist(t);
		if ( t instanceof IPropertyAware) {
			getEventPublisher().publishEvent(new PropertyEvent(this, (IPropertyAware)t));
		}
		
	}
	/**
	 * Perform post processing on a selected object
	 * <p>
	 * Subclasses may override
	 * </p>
	 */
	protected void postSelect(T t) {
		// subclasses may override
	}
	/**
	 * Possibility to provide a list of index fields to include
	 * in the search result. Defaults to empty list.
	 * <p>
	 * Subclasses may override
	 * </p>
	 * @return
	 */
	protected Collection<Field> getSelectFieldList() {
		return new ArrayList<>();
	}
	/**
	 * Construct a index field map from the LUKE request, additional 
	 * filters are evaluated
	 * @param fields The result of the LUKE request
	 * @param requested A set of requested fields, null or empty to include all
	 * @return A map of {@link IndexField} objects with the used <code>indexFieldName</code> as key
	 */
	private Map<String, IndexField> getFields(NamedList<Object> fields, Set<String> requested)  {
		Map<String, IndexField> ffield = new HashMap<>();
		for (Map.Entry<String, Object> field : fields) {
			String name = field.getKey();
			// check whether to include a particular index field
			if (includeField(name)) {
				if ( (requested == null || requested.isEmpty()) 
						// when requested list present and not empty
						|| isRequestedField(requested, name)) {
					
					IndexField f = new IndexField(name);
					@SuppressWarnings("unchecked")
					NamedList<Object> namedList = (NamedList<Object>) field.getValue();
					for (Entry<String, Object> prop : namedList) {
						switch(prop.getKey()) {
						case "type":
							f.setDataType(getDatatype(prop.getValue()));
							break;
						case "docs":
							f.setDocCount(Integer.valueOf(prop.getValue().toString()));
							break;
						case "dynamicBase":
							f.setDynamicBase(prop.getValue().toString());
							break;
						}
					}
					ffield.put(name, f);
				}
			}
		}
		
		return ffield;
	}
	/**
	 * exclude the distinct fields - calls 
	 * @param fieldName
	 * @return <code>false</code> when fieldName is <code>_text_</code> or <code>_version_</code>
	 * @see SolrServiceImpl#isRelevantField(String)
	 */
	private boolean includeField(String fieldName) {
		switch(fieldName) {
		case "_text_":
		case "_version_":
			return false;
		default:
			return isRelevantField(fieldName);
		}
	}
	private boolean isRequestedField(Set<String> requested, final String current) {
		Optional<String> found = requested.stream().filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				if ( t.startsWith("*")) {
					return current.endsWith(t.substring(1));
				}
				else if ( t.endsWith("*")) {
					return current.startsWith(t.substring(0,  t.length()-1));
				}
				else {
					return t.equals(current);
				}
			}}).findFirst();
		return found.isPresent(); 
	}
	/**
	 * Translate the solr datatype
	 * @param type
	 * @return
	 */
	private String getDatatype(Object type) {
		switch(type.toString()){
		case "plong":
			return "long";
		case "pint":
			return "int";
		case "pdouble":
			return "double";
		case "text_general":
			return "string";
		default:
			// string & boolean
			return type.toString();
		}
	}
	/**
	 * Express the list as Set - removing (possible) duplicates
	 * @param list
	 * @return
	 */
	protected <I> Set<I> asSet(List<I> list) {
		if ( list!= null && !list.isEmpty()) {
			return list.stream().collect(Collectors.toSet());
		}
		return new HashSet<>();
	}

	/**
	 * Determine whether a field relevant, defaults to true - subclasses must override
	 * @param name The {@link IndexField#getFieldName()} 
	 * @return <code>true</code> if relevant, <code>false</code> otherwise
	 */
	protected boolean isRelevantField(String name) {
		return true;
	}

	/**
	 * @return the eventPublisher
	 */
	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

}
