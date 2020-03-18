package at.srfg.indexing.service.event;

import org.springframework.context.ApplicationEvent;

import at.srfg.indexing.model.party.PartyType;

public class PartyEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	
	public PartyEvent(Object source, PartyType party) {
		super(source);
		this.manufacturer = party;
	}
	private final PartyType manufacturer;
	
	public PartyType getPartyType() {
		return manufacturer;
	}


}
