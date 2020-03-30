//package at.srfg.iot.indexing.service.event;
//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import at.srfg.indexing.model.party.PartyType;
//import at.srfg.iot.indexing.service.repository.PartyRepository;
//
//@Component
//public class PartyEventListener implements ApplicationListener<PartyEvent> {
//
//	@Autowired
//	private PartyRepository partyRepo;
//	
//	@Override
//	public void onApplicationEvent(PartyEvent event) {
//	
//		Optional<PartyType> pt = partyRepo.findById(event.getPartyType().getId());
//		if (! pt.isPresent() ) {
//			// be sure to have the manufacturer in the index
//			partyRepo.save(event.getPartyType());
//		}
//
//		
//	}
//
//}
