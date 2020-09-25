package at.srfg.indexing.core.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.srfg.indexing.core.service.CodeService;
import at.srfg.indexing.core.service.repository.CodedRepository;
import at.srfg.indexing.model.common.CodedType;
import at.srfg.indexing.model.solr.SearchResult;

@Service
public class CodeServiceImpl extends SolrServiceImpl<CodedType> implements CodeService {
	@Autowired
	private CodedRepository codedRepo;
	@Override
	public String getCollection() {
		return CodedType.COLLECTION;
	}


	@Override
	public SearchResult<CodedType> findByUris(Set<String> uriSet) {
		List<CodedType> result = codedRepo.findByUriIn(uriSet);
		return new SearchResult<CodedType>(result);
	}

	@Override
	public SearchResult<CodedType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames) {
		List<CodedType> result = codedRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
		return new SearchResult<CodedType>(result);
	}


	@Override
	public SearchResult<CodedType> findByListIdAndCode(String listId, String code) {
		List<CodedType> result = codedRepo.findByListIdAndCode(listId, code);
		return new SearchResult<CodedType>(result);
	}


	@Override
	public Class<CodedType> getSolrClass() {
		return CodedType.class;
	}


	@Override
	public SearchResult<CodedType> findByListId(String listId) {
		List<CodedType> result = codedRepo.findByListId(listId);
		return new SearchResult<CodedType>(result);
	}
	@Override
	public long deleteNameSpace(String nameSpace) {
		return codedRepo.deleteByNameSpace(nameSpace);
		
	}
	@Override
	public long deleteNameSpaces(Set<String> nameSpace) {
		return codedRepo.deleteByNameSpaceIn(nameSpace);
		
	}
	
}
