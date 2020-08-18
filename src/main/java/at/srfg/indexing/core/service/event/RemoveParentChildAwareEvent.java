package at.srfg.indexing.core.service.event;

import org.springframework.context.ApplicationEvent;

import at.srfg.indexing.model.common.ClassType;

public class RemoveParentChildAwareEvent extends ApplicationEvent {

	public RemoveParentChildAwareEvent(Object source, ClassType custom) {
		super(source);

		this.item = custom;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ClassType item;
	
	public ClassType getEventObject() {
		return item;
	}
	
	

}
