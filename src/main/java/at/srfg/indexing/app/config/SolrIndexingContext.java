package at.srfg.indexing.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import at.srfg.indexing.core.solr.SolrContext;


@Configuration
@EnableSolrRepositories(basePackages= {
			"at.srfg.indexing.core.service.repository"
		}, 
		schemaCreationSupport=true)
public class SolrIndexingContext extends SolrContext {
}
