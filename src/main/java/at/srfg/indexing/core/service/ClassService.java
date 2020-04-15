package at.srfg.indexing.core.service;

import java.util.Set;

import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.solr.SearchResult;

public interface ClassService extends SolrService<ClassType> {
	
	public SearchResult<ClassType> findByProperty(String property);
	
	public SearchResult<ClassType> findByUris(Set<String> uriSet);
	
	public SearchResult<ClassType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);

}
