package at.srfg.solr.core.service.event;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.srfg.indexing.model.common.CodedType;
import at.srfg.solr.core.service.repository.CodedRepository;

@Component
public class CodeTypeEventListener implements ApplicationListener<CodeTypeEvent> {

	@Autowired
	private CodedRepository codedRepo;
	
	@Override
	public void onApplicationEvent(CodeTypeEvent event) {
	
		Optional<CodedType> pt = codedRepo.findById(event.getCodedType().getUri());
		if (! pt.isPresent() ) {
			// be sure to have the manufacturer in the index
			codedRepo.save(event.getCodedType());
		}
		else {
			// we are interested in the labels
			if ( event.getCodedType().getLabel() != null) {
				CodedType existing = pt.get();
				
				existing.setLabel(event.getCodedType().getLabel());
				// update the existing
				codedRepo.save(existing);
			}
		}
		
	}

}
