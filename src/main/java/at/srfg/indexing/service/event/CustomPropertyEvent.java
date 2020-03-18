package at.srfg.indexing.service.event;

import org.springframework.context.ApplicationEvent;

import at.srfg.indexing.model.common.ICustomPropertyAware;

public class CustomPropertyEvent extends ApplicationEvent {

	public CustomPropertyEvent(Object source, ICustomPropertyAware custom) {
		super(source);

		this.item = custom;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ICustomPropertyAware item;
	
	public ICustomPropertyAware getItem() {
		return item;
	}
	
	

}