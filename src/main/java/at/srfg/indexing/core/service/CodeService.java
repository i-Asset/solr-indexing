package at.srfg.indexing.core.service;

import java.util.Set;

import at.srfg.indexing.model.common.CodedType;
import at.srfg.indexing.model.solr.SearchResult;

public interface CodeService extends SolrService<CodedType> {
	
	public SearchResult<CodedType> findByListId(String listId);
	
	public SearchResult<CodedType> findByListIdAndCode(String listId, String code);
	
	public SearchResult<CodedType> findByUris(Set<String> uriSet);
	
	public SearchResult<CodedType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);
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
