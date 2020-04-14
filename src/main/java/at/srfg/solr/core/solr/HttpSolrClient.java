package at.srfg.solr.core.solr;

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.DirectFieldAccessor;

public class HttpSolrClient extends org.apache.solr.client.solrj.impl.HttpSolrClient {

	private static final long serialVersionUID = 1L;

	public HttpSolrClient(Builder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}
	@Override
	public NamedList<Object> request(final SolrRequest request, String collection) 
		      throws SolrServerException, IOException {
		DirectFieldAccessor df = new DirectFieldAccessor(getHttpClient());
		CredentialsProvider provider = (CredentialsProvider) df.getPropertyValue("credentialsProvider");
		if ( provider.getCredentials(AuthScope.ANY) != null ) {
			Credentials credentials = provider.getCredentials(AuthScope.ANY);
			
			String name = credentials.getUserPrincipal().getName();
			String pass = credentials.getPassword();
			request.setBasicAuthCredentials(name, pass);
		}
		return super.request(request, collection);
	}

	public static class Builder extends org.apache.solr.client.solrj.impl.HttpSolrClient.Builder {
		public Builder(String url) {
			super(url);
		}
		@Override
		public org.apache.solr.client.solrj.impl.HttpSolrClient build() {
			if (baseSolrUrl == null) {
				throw new IllegalArgumentException("Cannot create HttpSolrClient without a valid baseSolrUrl!");
			}
			return new HttpSolrClient(this);
		}
	}
}
