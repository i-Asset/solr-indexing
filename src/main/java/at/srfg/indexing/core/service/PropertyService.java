package at.srfg.indexing.core.service;

import java.util.Optional;
import java.util.Set;

import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.solr.SearchResult;

public interface PropertyService extends SolrService<PropertyType> {

	SearchResult<PropertyType> findByUris(Set<String> uriSet);

	SearchResult<PropertyType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);

	SearchResult<PropertyType> findForClass(String classType);
	
	SearchResult<PropertyType> findForClasses(Set<String> classTypes);

	SearchResult<PropertyType> findByIdxNames(Set<String> idxNames);

	Optional<PropertyType> findCustomProperty(Class<?> nameSpace, String idxName);
	/**
	 * Delete all documents of the provided nameSpace
	 * @param nameSpace
	 */
	public long deleteNameSpace(String nameSpace);
	/**
	 * Delete all documents of the provided nameSpaces
	 * @param nameSpace List of nameSpaces to remove
	 */
	public long deleteNameSpaces(Set<String> nameSpace);	
}
