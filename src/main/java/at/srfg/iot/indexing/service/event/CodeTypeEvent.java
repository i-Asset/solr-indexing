package at.srfg.iot.indexing.service.event;

import org.springframework.context.ApplicationEvent;

import at.srfg.indexing.model.common.CodedType;

public class CodeTypeEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	
	public CodeTypeEvent(Object source, CodedType coded) {
		super(source);
		this.codedType = coded;
	}
	private final CodedType codedType;
	
	public CodedType getCodedType() {
		return codedType;
	}


}
