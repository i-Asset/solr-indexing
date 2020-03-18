package at.srfg.indexing.solr.query;


import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.SimpleField;


/**
 * Class representing a JOIN in SOLR
 * @author dglachs
 *
 */
public class SolrJoin {
	private String fromField;
	private String fromIndex;
	private Class<?> fromClass;
	private String toField;
	
	private SolrJoin(java.lang.reflect.Field field, at.srfg.indexing.model.solr.annotation.SolrJoin joinAnnotation) {
		this.fromField = joinAnnotation.joinedField();
		if ( this.fromField.isEmpty()) {
			// find the @Id element in the target class
			this.fromField = findTargetId(joinAnnotation.joinedType());
		}
		this.toField = fieldName(field); 
		this.fromClass = joinAnnotation.joinedType();
		SolrDocument solrDocument = joinAnnotation.joinedType().getAnnotation(SolrDocument.class);
		if ( solrDocument != null && solrDocument.collection() != null) {
			this.fromIndex = solrDocument.collection();
		}
		else {
			throw new IllegalArgumentException("Join is not complete!");
		}
	}
	public Class<?> getJoinedType() {
		return fromClass;
	}
	public Field getToField() {
		return new SimpleField(toField);
	}
	public String getTo() {
		return toField;
	}
	public String getFromIndex() {
		return fromIndex;
	}
	public String toString() {
		return String.format("{!join from=%s to=%s fromIndex=%s}", fromField, toField, fromIndex);
	}
	public Join getJoin() {
		return Join.from(fromField).fromIndex(fromIndex).to(toField);
	}
	/**
	 * Static method to resolve a "named" join in the query or field query.
	 * The <code>joinName</code> is used to identify field annotated with {@link at.srfg.indexing.model.solr.annotation.SolrJoin}
	 * in the <code>sourceClass</code>.
	 * 
	 * @param sourceClass
	 * @param joinName
	 * @return 
	 */
    public static Optional<SolrJoin> findJoin(Class<?> sourceClass, String joinName) {
    	if ( sourceClass == null) {
    		return Optional.empty();
    	}
    	java.lang.reflect.Field[] fields = sourceClass.getDeclaredFields();
    	for ( java.lang.reflect.Field f : fields) {
    		at.srfg.indexing.model.solr.annotation.SolrJoin joinAnnotation = f.getAnnotation(at.srfg.indexing.model.solr.annotation.SolrJoin.class);
    		// 
    		if ( joinAnnotation != null ) {
    			SolrDocument solrDocument = joinAnnotation.joinedType().getAnnotation(SolrDocument.class);
    			
    			if ( solrDocument != null && solrDocument.collection() != null) {
    				
    				if ( joinName.equalsIgnoreCase(solrDocument.collection())) {
    					return Optional.of(new SolrJoin(f, joinAnnotation));
    				} else {
    					String[] joinNames = joinAnnotation.joinName();
    					for ( String name : joinNames ) {
    						// additional join names to be evaluated
    						if ( name.equalsIgnoreCase(joinName)) {
    	    					return Optional.of(new SolrJoin(f, joinAnnotation));
    						}
    					}
    				}
    			}
    		}
    	}
    	return findJoin(sourceClass.getSuperclass(), joinName);
    }
    /**
     * Detect the field annotated with {@link Id}
     * @param clazz The class having the {@link Id} annotated field element
     * @return The index field name of the {@link Id} element
     */
    private static String findTargetId(Class<?> clazz) {
    	if ( clazz == null ) {
    		return null;
    	}
    	java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
    	for ( java.lang.reflect.Field f : fields) {
    		Id idAnnotation = f.getAnnotation(Id.class);
    		if ( idAnnotation != null) {
    			return fieldName(f);
    		}
    	}
    	return findTargetId(clazz.getSuperclass());
    }
    /**
     * Resolve the index field name in use for a given {@link java.lang.reflect.Field}.
     * <p>
     * The index field name is determined with the following order
     * <ol>
     * <li>The name property of the {@link Indexed} annotation, when not set
     * <li>the value property of the {@link Indexed} annotation, when not set
     * <li>the value property of the {@link Field} annotation, when not set
     * <li>the name of the field itself.
     * </ol>
     * </p>
     * @param f The field
     * @return The index field name
     */
    private static String fieldName(java.lang.reflect.Field f) {
		String toField = f.getName();
		// search for the proper field name to use
		// the field name may be specified wie
		// @Field(
		// @Indexed("name") or
		// @Indexed(name="name") 
		org.apache.solr.client.solrj.beans.Field fieldAnnotation = f.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
		if ( fieldAnnotation != null ) {
			toField = fieldAnnotation.value();
		}
		Indexed indexedAnnotation = f.getAnnotation(Indexed.class);
		if ( indexedAnnotation !=null) {
			if ( indexedAnnotation.name()!=null) {
				toField = indexedAnnotation.name();
			} else if ( indexedAnnotation.value()!=null) {
				toField = indexedAnnotation.value();
			}
		}
		return toField;

    }


}
