package at.srfg.indexing.core.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;

import at.srfg.indexing.ClassTypeIndexing;
import at.srfg.indexing.CodedTypeIndexing;
import at.srfg.indexing.PropertyTypeIndexing;
import at.srfg.indexing.core.service.ClassService;
import at.srfg.indexing.core.service.CodeService;
import at.srfg.indexing.core.service.PropertyService;
import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.common.CodedType;
import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.solr.FacetResult;
import at.srfg.indexing.model.solr.IndexField;
import at.srfg.indexing.model.solr.Search;
import at.srfg.indexing.model.solr.SearchResult;
/**
 * Abstract basic indexing API
 * <p>
 * 
 * </p>
 * @author dglachs
 *
 */
//@CrossOrigin
public abstract class BasicIndexingAPI implements ClassTypeIndexing, PropertyTypeIndexing, CodedTypeIndexing {

	@Autowired
	protected PropertyService propertyService;

	@Autowired
	protected ClassService classService;
//
//	@Autowired
//	private PartyService partyService;

//	@Autowired
//	private ItemService itemService;

	@Autowired
	protected CodeService codeService;

//	@Autowired
//	private IdentityService identityService;

	private static final Logger logger = LoggerFactory.getLogger(BasicIndexingAPI.class);

	public Optional<ClassType> getClassType(String uri) throws Exception {
		return classService.get(uri);
	}

	@Override
	public Collection<IndexField> fieldsForClassType(Set<String> fieldNames) throws Exception {
		return classService.fields(fieldNames);
	}

	public SearchResult<ClassType> searchForClassType(
//			String bearerToken,
			String query, List<String> filterQuery, List<String> facetFields, int facetLimit, int facetMinCount,
			int start, int rows) throws Exception {
		return classService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
	}

	@Override
	public SearchResult<ClassType> searchForClassType(Search search) {
		return classService.search(search);
	}

	@Override
	public FacetResult suggestForClassType(String query, String fieldName, int limit, int minCount) throws Exception {
		return classService.suggest(query, fieldName, limit, minCount);
	}

	@Override
	public ClassType setClassType(ClassType prop) throws Exception {

//		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,PUBLISHER,
//				COMPANY_ADMIN,EFACTORYUSER) == false)
//			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		classService.set(prop);
		return prop;
	}

	@Override
	public boolean deleteClassType(List<String> toDelete) throws Exception {
		for (String uri : toDelete) {
			classService.remove(uri);
		}
		return true;
	}

	@Override
	public Optional<PropertyType> getPropertyType(String uri) throws Exception {
		return propertyService.get(uri);
	}

	@Override
	public Collection<IndexField> fieldsForPropertyType(Set<String> fieldNames) throws Exception {
		return propertyService.fields(fieldNames);
	}

	@Override
	public SearchResult<PropertyType> searchForPropertyType(String query, List<String> filterQuery,
			List<String> facetFields, int facetLimit, int facetMinCount, int start, int rows) throws Exception {
		return propertyService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
	}

	@Override
	public SearchResult<PropertyType> searchForPropertyType(Search search) throws Exception {
		return propertyService.search(search);
	}

	@Override
	public FacetResult suggestForPropertyType(String query, String fieldName, int limit, int minCount)
			throws Exception {
		return propertyService.suggest(query, fieldName, limit, minCount);
	}

	@Override
	public PropertyType setPropertyType(PropertyType prop) throws Exception {
		propertyService.set(prop);
		return prop;
	}

	@Override
	public boolean deletePropertyType(List<String> toDelete) throws Exception {
		for ( String uri : toDelete ) {
			propertyService.remove(uri);
		}
		return true;
	}

	@Override
	public Optional<CodedType> getCodedType(String uri) throws Exception {
		return codeService.get(uri);
	}

	@Override
	public Collection<IndexField> fieldsForCodedType(Set<String> fieldNames) throws Exception {
		return codeService.fields(fieldNames);
	}

	@Override
	public SearchResult<CodedType> searchForCodedType(String query, List<String> filterQuery,
			List<String> facetFields, int facetLimit, int facetMinCount, int start, int rows) throws Exception {
		return codeService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
	}

	@Override
	public SearchResult<CodedType> searchForCodedType(Search search) throws Exception {
		return codeService.search(search);
	}

	@Override
	public FacetResult suggestForCodedType(String query, String fieldName, int limit, int minCount)
			throws Exception {
		return codeService.suggest(query, fieldName, limit, minCount);
	}

	@Override
	public CodedType setCodedType(CodedType prop) throws Exception {
		codeService.set(prop);
		return prop;
	}

	@Override
	public boolean deleteCodedType(List<String> toDelete) throws Exception {
		for (String uri : toDelete) {
			codeService.remove(uri);
		}
		return true;
	}

}
