package at.srfg.indexing.core.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Field;
import org.springframework.stereotype.Service;

import at.srfg.indexing.core.service.PropertyService;
import at.srfg.indexing.core.service.repository.PropertyRepository;
import at.srfg.indexing.model.common.IPropertyType;
import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.solr.SearchResult;

@Service
public class PropertyServiceImpl extends SolrServiceImpl<PropertyType> implements PropertyService
{
	@Autowired
	private PropertyRepository propRepo;

	@Override
	public String getCollection() {
		return PropertyType.COLLECTION;
	}

	@Override
	public Class<PropertyType> getSolrClass() {
		return PropertyType.class;
	}

	@Override
	public SearchResult<PropertyType> findByUris(Set<String> uriSet) {
		List<PropertyType> result = propRepo.findByUriIn(uriSet);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames) {
		List<PropertyType> result = propRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findForClass(String classType) {
		List<PropertyType> result = propRepo.findByConceptClass(classType);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findByIdxNames(Set<String> idxNames) {
		List<PropertyType> result = propRepo.findByItemFieldNamesIn(idxNames);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	protected List<Field> getSelectFieldList() {
		return IPropertyType.defaultFields();
	}
	
	@Override
	public SearchResult<PropertyType> findForClasses(Set<String> classTypes) {
		List<PropertyType> result = propRepo.findByConceptClassIn(classTypes);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public Optional<PropertyType> findCustomProperty(Class<?> nameSpace, String idxName) {
		String ns = nameSpace.getName();
		return propRepo.findByNameSpaceAndItemFieldNames(ns,idxName);
		
	}
	@Override
	public long deleteNameSpace(String nameSpace) {
		return propRepo.deleteByNameSpace(nameSpace);
		
	}
	@Override
	public long deleteNameSpaces(Set<String> nameSpace) {
		return propRepo.deleteByNameSpaceIn(nameSpace);
		
	}

}
