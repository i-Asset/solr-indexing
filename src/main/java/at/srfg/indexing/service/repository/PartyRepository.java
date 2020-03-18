package at.srfg.indexing.service.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import at.srfg.indexing.model.party.PartyType;

@Repository
public interface PartyRepository extends SolrCrudRepository<PartyType, String> {
	/**
	 * Retrieve all manufacturers as provided in the list
	 * @param id
	 * @return
	 */
	List<PartyType> findByUriIn(Set<String> id);

}
