package at.srfg.indexing.service.impl;

import org.springframework.stereotype.Service;

import at.srfg.indexing.model.party.PartyType;
import at.srfg.indexing.service.PartyService;

@Service
public class PartyServiceImpl extends SolrServiceImpl<PartyType> implements PartyService {

	@Override
	public String getCollection() {
		return PartyType.COLLECTION;
	}

	@Override
	public Class<PartyType> getSolrClass() {
		return PartyType.class;
	}


}
