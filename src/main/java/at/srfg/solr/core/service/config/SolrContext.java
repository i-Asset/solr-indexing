package at.srfg.solr.core.service.config;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.data.solr.server.support.HttpSolrClientFactory;
import org.springframework.util.StringUtils;

import at.srfg.solr.core.solr.HttpSolrClient;
import at.srfg.solr.core.solr.schema.SolrTemplate;


@Configuration
@EnableSolrRepositories(basePackages= {
			"at.srfg.solr.core.service.repository"
		}, 
		schemaCreationSupport=true)
public class SolrContext {
	
	@Resource
	private Environment environment;
	
	@Bean
	public SolrClientFactory solrClientFactory() {
		String host = environment.getProperty("solr.host");
		String user = environment.getProperty("solr.user");
		String pass = environment.getProperty("solr.password");
		if (! (StringUtils.isEmpty(user) || StringUtils.isEmpty(pass))) {
			return new HttpSolrClientFactory(solrClient(host), new UsernamePasswordCredentials(user, pass), "BASIC");
		}
		return new HttpSolrClientFactory(solrClient(host));
	}
	@Bean
	public SolrClient solrClient(@Value("${solr.host}") String solrHost) {
		if ( solrHost.startsWith("http")) {
//			return new MySolrClient.Builder(solrHost).build();
			return new HttpSolrClient.Builder(solrHost).build();
		}
		else {
//			return new CloudSolrClient(solrHost);
			return new CloudSolrClient.Builder(Collections.singletonList(solrHost)).build();
		}
	}
	@Bean
	public SolrTemplate solrTemplate() {
		return new SolrTemplate(solrClientFactory());
	}
}
