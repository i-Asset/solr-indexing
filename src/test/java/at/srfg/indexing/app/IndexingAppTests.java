package at.srfg.indexing.app;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import at.srfg.indexing.core.service.ClassService;
import at.srfg.indexing.core.service.PropertyService;
import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.common.PropertyType;
import at.srfg.indexing.model.common.ValueQualifier;

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
	public void testProperty() {
		PropertyType p = new PropertyType();
		p.setLabel(Locale.GERMAN, "Demo");
		p.setCode("demo");
		p.setUri("urn:test:demo");
		p.setLocalName("demo");
		p.setNameSpace("urn:test");
		p.setItemFieldNames(Collections.singleton("demo"));
		p.setValueQualifier(ValueQualifier.STRING);
		//
		propertyService.set(p);
		
		ClassType c = new ClassType();
		c.setLabel(Locale.GERMAN, "Demo");
		c.setCode("class");
		c.setUri("urn:test:class");
		c.setLocalName("class");
		c.setNameSpace("urn:test");
		c.addProperty(p);
		classService.set(c);
		
		Optional<PropertyType> read = propertyService.get(p.getUri());
		PropertyType p2 = read.get();
		assertTrue(read.isPresent());
		assertTrue(p2.getPropertyUsage().containsValue(c.getUri()));
		// 
		classService.remove(c.getUri());
		try {
			// wait a while so that asynchronous processing can take place
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Optional<PropertyType> read3 = propertyService.get(p.getUri());
		PropertyType p3 = read3.get();
		assertTrue(read.isPresent());
		assertTrue(!p3.getPropertyUsage().containsValue(c.getUri()));
		//
		propertyService.remove(p.getUri());
	}
	@Test
	public void testClassification() {
		PropertyType labelProperty = new PropertyType();
		labelProperty.setLabel(Locale.GERMAN, "Bezeichnung");
		labelProperty.setLabel(Locale.ENGLISH, "Label");
		labelProperty.setCode("label");
		labelProperty.setUri("urn:test:label");
		labelProperty.setLocalName("label");
		labelProperty.setNameSpace("urn:test:");
		labelProperty.setValueQualifier(ValueQualifier.STRING);
		labelProperty.addItemFieldName("label");
		propertyService.set(labelProperty);
		ClassType classType = new ClassType();
		classType.setUri("urn:test:class1");
		classType.setLabel(Locale.GERMAN, "Roboter");
		classType.setLabel(Locale.ENGLISH, "Robot");
		classType.addProperty(labelProperty);
		classService.set(classType);
		// 
		propertyService.remove(labelProperty.getUri());
		classService.remove(classType.getUri());
		
	}

}

