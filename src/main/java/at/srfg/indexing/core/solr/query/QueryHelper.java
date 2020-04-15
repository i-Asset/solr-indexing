package at.srfg.indexing.core.solr.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleFilterQuery;


public class QueryHelper {
	private static final String QUOTE = "\"";
	private Class<?> targetClass;
	private final String collection;

	private Set<Field> facetFields = new HashSet<>();
	private Set<FilterQuery> filterQueries = new HashSet<>();
	private Map<String, SolrJoin> joinedList = new HashMap<>();
	
	private Map<String, Set<Field>> joinedFields = new HashMap<>();
	
	
	private Map<SolrJoin, Set<FilterQuery>> filter = new HashMap<>();
	public QueryHelper(Class<?> targetClass) {
		SolrDocument solrDocument = targetClass.getAnnotation(SolrDocument.class);
		this.targetClass = targetClass;
		this.collection = solrDocument.collection();
	}

	public void addFilter(String fromString) {
		
		int fieldDelimPos = fromString.indexOf(":");
		if ( fieldDelimPos>0 ) {
			String fieldName = fromString.substring(0,fieldDelimPos);
			int joinDelimPos = fieldName.indexOf(".");
			Criteria crit = null;
			if ( joinDelimPos > 0 ) {
				String joinName = fieldName.substring(0,joinDelimPos);
				Optional<SolrJoin> join = SolrJoin.findJoin(targetClass, joinName);
				if (join.isPresent()) {
					// get the remainder of the join
					String joinedFieldName = fieldName.substring(joinDelimPos+1);
					crit = Criteria.where(joinedFieldName).expression(fromString.substring(fieldDelimPos+1));
					// add the join filter 
					addFilter(join.get(), crit);
				}
			}
			else {
				crit = Criteria.where(fieldName).expression(fromString.substring(fieldDelimPos+1));
				addFilter(crit);
			}
		}
	}

	/**
	 * Add a facet field, perform join verification
	 * @param fieldName
	 */
	public void addFacetField(String fieldName) {
		int joinDelimPos = fieldName.indexOf(".");
		Field field = null;
		if ( joinDelimPos > 0 ) {
			String joinName = fieldName.substring(0,joinDelimPos);
			Optional<SolrJoin> join = SolrJoin.findJoin(targetClass, joinName);
			if ( join.isPresent()) {
				addJoin(joinName, join.get());
				
				// keep the mapping of the used join name
				String joinedFieldName = fieldName.substring(joinDelimPos+1);
				
				field = new SimpleField(joinedFieldName);
				// add the facet field along with the joinName
				addField(joinName, field);
				
			}
		}
		else {
			facetFields.add(new SimpleField(fieldName));
		}
	}
	/**
	 * Searches and resolves joins in the provided query. 
	 * <p>
	 * A query string may contain terms with a join prefix. The join
	 * prefixes are expanded to the proper join. E.g. a query like 
	 * <pre>
	 * q=classification.en_label:value*
	 * </pre>
	 * is expanded to a query
	 * <pre>
	 * q={!join to=classification_id from=id fromIndex=classification}en_label:value*
	 * </pre>
	 * The prerequisite is that the clazz has a field <code>collection_id</code> 
	 * annotated with {@link SolrJoin}. The annotation points to the joined type 
	 * and to the joined collection.
	 * </p><p>
	 * An exemplary annotation shown below<pre>
	 * <code>@SolrJoin(joinedType=<i>Classification.class, joinedFieldjoinName="classification")</i>)</code></pre>
	 * is activated with classification. and will expand 
	 *  
	 *  
	 * 
	 * @param clazz The class with the {@link SolrDocument} annotation
	 * @param queryString The provided query string
	 * @see at.srfg.indexing.model.solr.annotation.SolrJoin
	 * @return
	 */
	public String parseQuery(Class<?> clazz, String queryString) {
		String[] queryParts = queryString.split(Pattern.quote(" "));
		String query = "";

		for (String queryPart : queryParts) {
			int fieldDelimPos = queryPart.indexOf(":");
			int joinDelimPosOuter = queryPart.indexOf(".");
			if (fieldDelimPos > 0) {
				String fieldName = queryPart.substring(0, fieldDelimPos);
				//handle join statement separately
				if (joinDelimPosOuter > 0) {
					int joinPosInner = fieldName.indexOf(".");
					if (joinPosInner > 0) {
						
						String joinName = fieldName.substring(0, joinPosInner);
						// try to find the join
						Optional<SolrJoin> join = SolrJoin.findJoin(clazz, joinName);
						if ( join.isPresent()) {
							// Append the join (string representation)
							query += join.get();
						}
					}
				} else {
					query += queryPart + " ";
				}
			} else {
				query += queryPart + " ";
			}
		}
		return query;
	}




	public Set<String> getJoins() {
		return joinedList.keySet();
	}
	public Set<FilterQuery> getFilterQueries() {
		return filterQueries;
	}
	public Set<FilterQuery> getFilterQueries(String joinName) {
		SolrJoin join = joinedList.get(joinName);
		if ( join!= null) {
			return filter.get(join);
		}
		return new HashSet<>();
	}
	
	public Set<Field> getFacetFields() {
		return facetFields;
	}
	public Set<Field> getFacetFields(String joinName) {
		return joinedFields.get(joinName);
	}
	public SolrJoin getJoinInfo(String joinName) {
		return joinedList.get(joinName);
	}
	private void addJoin(String name, SolrJoin info) {
		if ( ! joinedList.containsValue(info)) {
			// need to have the join column as facet (if not set)
			facetFields.add(info.getToField());
		}
		joinedList.put(name, info);
		
	}

	private void addField(String info, Field field) {
		if (! joinedFields.containsKey(info) ) {
			joinedFields.put(info, new HashSet<>());
		}
		joinedFields.get(info).add(field);

	}
	private void addFilter(SolrJoin info, Criteria criteria) {
		if (! filter.containsKey(info) ) {
			filter.put(info, new HashSet<>());
		}
		filter.get(info).add(new SimpleFilterQuery(criteria));
		// 
		SimpleFilterQuery joinQuery = new SimpleFilterQuery(criteria);
		joinQuery.setJoin(info.getJoin());
		filterQueries.add(joinQuery);
	}
	private void addFilter(Criteria criteria) {
		SimpleFilterQuery query = new SimpleFilterQuery(criteria);
		filterQueries.add(query);

	}

	@SuppressWarnings("unused")
	private String encode(String in) {
		// check for a colon - ensure URI's are quoted
		if (in.contains(":")) {
			if (  ( in.startsWith(QUOTE)) && in.endsWith(QUOTE) ) {
				// quotes present
				return in;
			}
			// no quotes present 
			return String.format("%s%s%s", QUOTE, in, QUOTE);
		}
		return in;
	}
	public String getCollection() {
		return collection;
	}

}
