package at.srfg.indexing.service;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import at.srfg.indexing.model.asset.AssetType;
import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.common.ValueQualifier;
import at.srfg.iot.indexing.service.ClassService;
import at.srfg.iot.indexing.service.PropertyService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexingAppTests {
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private ClassService classService;
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testClassification() {
		PropertyType labelProperty = new PropertyType();
		labelProperty.setLabel("Bezeichnung", Locale.GERMAN);
		labelProperty.setLabel("Label", Locale.ENGLISH);
		labelProperty.setCode("label");
		labelProperty.setUri("urn:test:label");
		labelProperty.setLocalName("label");
		labelProperty.setNameSpace("urn:test:");
		labelProperty.setValueQualifier(ValueQualifier.STRING);
		labelProperty.addItemFieldName("label");
		propertyService.set(labelProperty);
		ClassType classType = new ClassType();
		classType.setUri("urn:test:class1");
		classType.setLabel("Roboter", Locale.GERMAN);
		classType.setLabel("Robot", Locale.ENGLISH);
		classType.addProperty(labelProperty);
		classService.set(classType);
		AssetType asset = new AssetType();
		asset.setUri("urn:test:asset1");
		asset.setLabel("Asset",Locale.GERMAN);
		asset.setLabel("Asset", Locale.ENGLISH);
		// use the idShort of the model (hiearchy)
		asset.addProperty(10.0, "operation", "mix", "demo");
		asset.setCode("asset1");
	}

}

