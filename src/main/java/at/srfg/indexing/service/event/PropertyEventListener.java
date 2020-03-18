package at.srfg.indexing.service.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.jena.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.srfg.indexing.model.common.IPropertyAware;
import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.common.ValueQualifier;
import at.srfg.indexing.service.repository.PropertyRepository;

@Component
public class PropertyEventListener implements ApplicationListener<PropertyEvent> {

	@Autowired
	private PropertyRepository propRepo;

	@Override
	public void onApplicationEvent(PropertyEvent event) {

		IPropertyAware item = event.getEventObject();
		if (item.getPropertyMap() != null && !item.getPropertyMap().isEmpty()) {
			// find all properties based on idxField name
			List<PropertyType> existing = propRepo.findByItemFieldNamesIn(item.getPropertyMap().keySet());
			// keep a map of properties to change
			Map<String, PropertyType> changed = new HashMap<String, PropertyType>();

			// check whether an existing property lacks a itemFieldName or a label
			existing.forEach(new Consumer<PropertyType>() {

				@Override
				public void accept(PropertyType c) {
					// try to find the property in the custom property map based
					// on the localNaem
					Optional<String> key = findInItem(item, c.getItemFieldNames());
					if ( key.isPresent()) {
						boolean changeDetected = false;
						// 
						PropertyType change = item.getPropertyMap().get(key.get());
						// harmonize item field names
						for (String idxField : change.getItemFieldNames()) {
							if (!c.getItemFieldNames().contains(idxField)) {
								c.addItemFieldName(idxField);
								// add to changed with URI - to have it saved ...
								changeDetected = true;
							}
						}
						// harmonize labels
						if ( harmonizeLabels(c.getLabel(), change.getLabel())) {
							changeDetected = true;
						}
						if ( harmonizeLabels(c.getComment(), change.getComment()) ) {
							changeDetected = true;
						}
						if ( harmonizeLabels(c.getDescription(), change.getDescription()) ) {
							changeDetected = true;
						}
						// remove any existing property so that is not added twice
						if ( changeDetected ) {
							changed.put(c.getUri(), c);
						}
						// in any case remove the checked property (based on the key) from the item
						item.getPropertyMap().remove(key.get());
					}
				}
			});
			// process the remaining properties - they are not yet indexed!
			item.getPropertyMap().forEach(new BiConsumer<String, PropertyType>() {

				@Override
				public void accept(String qualifier, PropertyType newProp) {
					PropertyType pt = new PropertyType();
					// how to specify uri, localName & nameSpace
					// TODO - use namespace from config
					pt.setUri("urn:nimble:custom:" + qualifier);
					pt.setNameSpace("urn:nimble:custom:");
					pt.setLocalName(qualifier);
					pt.setItemFieldNames(newProp.getItemFieldNames());
					pt.setLabel(newProp.getLabel());
					pt.setComment(newProp.getComment());
					pt.setDescription(newProp.getDescription());
					pt.setPropertyType("CustomProperty");

					pt.setValueQualifier(
							newProp.getValueQualifier() != null ? newProp.getValueQualifier() : ValueQualifier.STRING);
					switch (pt.getValueQualifier()) {
					case BOOLEAN:
						pt.setRange(XSD.xboolean.getURI());
						break;
					case TEXT:
					case STRING:
						pt.setValueQualifier(ValueQualifier.STRING);
						pt.setRange(XSD.xstring.getURI());
						break;
					default:
						pt.setRange(XSD.xdouble.getURI());
						break;
					}
					// add the new custom property to the index
					changed.put(pt.getUri(), pt);
				}
			});

			// save all changed & new properties
			for (PropertyType newPt : changed.values()) {
				propRepo.save(newPt);
			}
		}
	}
	/**
	 * Helper method 
	 * @param toAdd
	 * @param from
	 */
	private boolean harmonizeLabels(Map<String, String> toAdd, Map<String, String> from) {
		boolean changeDetected = false;
		if (toAdd != null && from != null) {
			for (String lang : from.keySet()) {
				String old = toAdd.get(lang);
				if ( old == null || !old.equals(from.get(lang))) {
					toAdd.put(lang, from.get(lang));
					changeDetected = true;
				}
			}
		}
		return changeDetected;
	}
	private Optional<String> findInItem(IPropertyAware item, Collection<String> idxField) {
		return idxField.stream()
			.filter(new Predicate<String>() {

				@Override
				public boolean test(String t) {
					return item.getPropertyMap().containsKey(t);
				}
				
			})
			.findFirst();
	}
}