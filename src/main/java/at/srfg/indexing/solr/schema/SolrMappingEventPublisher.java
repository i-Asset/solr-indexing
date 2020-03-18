package at.srfg.indexing.solr.schema;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mapping.context.MappingContextEvent;
import org.springframework.data.solr.core.mapping.SolrPersistentEntity;
import org.springframework.data.solr.core.mapping.SolrPersistentProperty;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author Christoph Strobl
 * @since 1.3
 */
public class SolrMappingEventPublisher implements ApplicationEventPublisher {

	static final String SPRING_42_PAYLOAD_APPLICATION_EVENT_CLASS_NAME = "org.springframework.context.PayloadApplicationEvent";
	static final boolean SPRING_42_PAYLOAD_APPLICATION_EVENT_PRESENT = ClassUtils.isPresent(
			SPRING_42_PAYLOAD_APPLICATION_EVENT_CLASS_NAME, null);

	SolrPersistentEntitySchemaCreator schmeaCreator;

	public SolrMappingEventPublisher(SolrPersistentEntitySchemaCreator schemaCreator) {
		this.schmeaCreator = schemaCreator;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationEventPublisher#publishEvent(org.springframework.context.ApplicationEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void publishEvent(ApplicationEvent event) {

		if (event instanceof MappingContextEvent) {
			this.schmeaCreator
					.onApplicationEvent((MappingContextEvent<SolrPersistentEntity<?>, SolrPersistentProperty>) event);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationEventPublisher#publishEvent(java.lang.Object)
	 */
	public void publishEvent(Object event) {

		Assert.notNull(event, "Event to publish must not be null!");

		publishEvent(initGenericApplicationEvent(event));
	}

	@SuppressWarnings("serial")
	private ApplicationEvent initGenericApplicationEvent(Object event) {

		ApplicationEvent applicationEvent = null;

		if (event instanceof ApplicationEvent) {
			applicationEvent = ((ApplicationEvent) event);
		}

		if (SPRING_42_PAYLOAD_APPLICATION_EVENT_PRESENT) {

			try {

				Constructor<?> ctor = ClassUtils.getConstructorIfAvailable(
						ClassUtils.forName(SPRING_42_PAYLOAD_APPLICATION_EVENT_CLASS_NAME, this.getClass().getClassLoader()),
						Object.class, Object.class);
				applicationEvent = (ApplicationEvent) BeanUtils.instantiateClass(ctor, (Object) this, event);
			} catch (Exception e) {
				// ignore and fall back to ApplicationEvent
			}
		}

		if (applicationEvent == null) {
			applicationEvent = new ApplicationEvent(event) {};
		}
		return applicationEvent;
	}
}
