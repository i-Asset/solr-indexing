package at.srfg.indexing.core.service;

import java.util.Set;

import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.solr.SearchResult;

public interface ClassService extends SolrService<ClassType> {
	
	public SearchResult<ClassType> findByProperty(String property);
	
	public SearchResult<ClassType> findByUris(Set<String> uriSet);
	/**
	 * Find all {@link ClassType} elements by localName within the provided nameSpace
	 * @param nameSpace
	 * @param localNames
	 * @return
	 */
	public SearchResult<ClassType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);
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
