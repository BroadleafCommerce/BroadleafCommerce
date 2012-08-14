/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.service.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.service.ProductSearchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class SolrProductSearchServiceImpl implements ProductSearchService {
	
	protected SolrServer server;
	
	public SolrProductSearchServiceImpl() throws IOException, ParserConfigurationException, SAXException {
		System.setProperty("solr.solr.home", "/solrtemp");
		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
		this.server = server;
	}
	
	public SolrProductSearchServiceImpl(SolrServer server) {
		this.server = server;
	}

	@Override
	public ProductSearchResult findProductsByCategory(Category category, ProductSearchCriteria searchCriteria) throws ServiceException {
		System.out.println("SEARCHING");
		
	    SolrQuery query = new SolrQuery();
	    query.setQuery( "*:*" );
	    query.addSortField( "price_d", SolrQuery.ORDER.asc );
	    query.setRows(40);

	    try {
	    	QueryResponse rsp = server.query( query );
	    	SolrDocumentList docs = rsp.getResults();
	    	
	    	for (SolrDocument doc : docs) {
	    		System.out.println(doc.toString());
	    	}
	    	
	    } catch (SolrServerException e) {
	    	throw new ServiceException("Could not perform search", e);
	    }
		
		
		
		// TODO Auto-generated method stub
		return new ProductSearchResult();
	}

	@Override
	public ProductSearchResult findProductsByQuery(String query, ProductSearchCriteria searchCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchFacetDTO> getSearchFacets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchFacetDTO> getCategoryFacets(Category category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Scheduled(fixedRate = 3000000)
	public void rebuildIndex() throws ServiceException, IOException {
		System.out.println("REBUILDING");
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField("id_t", "id1", 1.0f);
	    doc1.addField("name_t", "doc1", 1.0f);
	    doc1.addField("price_d", 10.0);
	    SolrInputDocument doc2 = new SolrInputDocument();
	    doc2.addField("id_t", "id2", 1.0f);
	    doc2.addField("name_t", "doc2", 1.0f);
	    doc2.addField("price_d", 20.0);
	    
	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	    docs.add(doc1);
	    docs.add(doc2);
	    
	    try {
		    server.add(docs);
		    server.commit();
		    
		    
		    
		    UpdateRequest req = new UpdateRequest();
		    req.setAction(UpdateRequest.ACTION.COMMIT, false, false);
		    req.add(docs);
		    UpdateResponse rsp = req.process( server );
	    } catch (SolrServerException e) {
	    	throw new ServiceException("Could not rebuild index", e);
	    }
	    
	}

}
