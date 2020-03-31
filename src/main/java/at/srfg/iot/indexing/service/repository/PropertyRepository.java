package at.srfg.iot.indexing.service.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import at.srfg.indexing.model.common.IPropertyType;
import at.srfg.indexing.model.common.PropertyType;

@Repository
public interface PropertyRepository  extends SolrCrudRepository<PropertyType, String>{
	static final String[] defaultFields = new String[] {
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
	};
	/**
	 * Obtain a single property by it's uri
	 * @param uri
	 * @return
	 */
	List<PropertyType> findByUri(String uri);
	/**
	 * Retrieve all properties for a distinct product class
	 * @param product The product's uri
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByConceptClass(String product);
	/**
	 * Find all properties assigned to one of the provided products
	 * @param products
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByConceptClassIn(Set<String> products);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	@Query(fields={ 
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
	/**
	 * Retrieve multiple properties by their uri
	 * @param uri list of URI's to resolve
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByUriIn(Set<String> uri);
	/**
	 * Retrieve multiple properties by the index field name they serve as a label
	 * @param itemFieldNames
	 * @return
	 */
	List<PropertyType> findByItemFieldNamesIn(Set<String> itemFieldNames);
	/**
	 * Retrieve multiple fields by their local name OR the index field name 
	 * @param names
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.CLASSIFICATION_CLASS_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByLocalNameOrItemFieldNamesIn(Set<String> names);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);
	/**
	 * Find all properties by their namespace and the index field name
	 * @param nameSpace
	 * @param idxName
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.CLASSIFICATION_CLASS_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	Optional<PropertyType> findByNameSpaceAndItemFieldNames(String nameSpace, String idxName);
	/**
	 * Find all properties by their namespace and the index field name, multiple item field names allowed
	 * @param nameSpace
	 * @param idxName
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.CLASSIFICATION_CLASS_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD,
			IPropertyType.IS_VISIBLE_FIELD,
			IPropertyType.CODE_LIST_FIELD,
			IPropertyType.CODE_LIST_ID_FIELD
		})
	List<PropertyType> findByNameSpaceAndItemFieldNamesIn(String nameSpace, Set<String> idxNames);

}
