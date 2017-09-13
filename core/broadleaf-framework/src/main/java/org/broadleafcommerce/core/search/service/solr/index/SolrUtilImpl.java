/*-
 * #%L
 * BroadleafCommerce Core Solr Components Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

public class SolrUtilImpl implements SolrUtil, InitializingBean {
    
    private static final Log LOG = LogFactory.getLog(SolrUtilImpl.class);
    
    @Value("${com.broadleafcommerce.solr.index.update.maxTries:3}")
    protected int maxTries = 3;
    
    @Value("${com.broadleafcommerce.solr.index.update.error.waitTime:250}")
    protected long waitTime = 250L;
    
    @Value("${com.broadleafcommerce.solr.auth.basic.username:}")
    protected String basicAuthUserName;
    
    @Value("${com.broadleafcommerce.solr.auth.basic.password:}")
    protected String basicAuthPassword;
    
    @Resource(name="blSolrClient")
    protected SolrClient client;
    
    @Override
    public QueryResponse query(SolrQuery query, String indexName) throws SolrServerException, IOException {
        QueryRequest request = new QueryRequest(query);
        int tries = 0;
        long pause = 0L;
        while (true) {
            try {
                return (QueryResponse)process(request, indexName);
            } catch (SolrServerException | IOException e) {
                tries++;
                if (tries > maxTries) {
                    throw e;
                }
                LOG.warn("Error occured sending documents to Solr.  Retrying...", e);
                if (pause > 0L) {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ie) {
                        throw e;
                    }
                }
                pause += waitTime;
            }
        }
    }
    
    @Override
    public UpdateResponse updateIndex(UpdateRequest request) throws SolrServerException, IOException {
        return updateIndex(request, null);
    }

    @Override
    public UpdateResponse updateIndex(UpdateRequest request, String indexName) throws SolrServerException, IOException {
        int tries = 0;
        long pause = 0L;
        while (true) {
            try {
                return (UpdateResponse)process(request, indexName);
            } catch (SolrServerException | IOException e) {
                tries++;
                if (tries > maxTries) {
                    throw e;
                }
                LOG.warn("Error occured sending documents to Solr.  Retrying...", e);
                if (pause > 0L) {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ie) {
                        throw e;
                    }
                }
                pause += waitTime;
            }
        }
    }

    @Override
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, String indexName, int commitWithin) throws SolrServerException, IOException {
        if (docs == null || docs.isEmpty()) {
            LOG.warn("The collection of SolrInputDocuments was empty.  Skipping...");
            return null;
        }
        
        int tries = 0;
        long pause = 0L;
        while (true) {
            try {
                UpdateRequest request = new UpdateRequest();
                request.add(docs);
                if (commitWithin > 0) {
                    request.setCommitWithin(commitWithin);
                } 
                return (UpdateResponse)process(request, indexName);
            } catch (SolrServerException | IOException e) {
                tries++;
                if (tries > maxTries) {
                    throw e;
                }
                LOG.warn("Error occured sending documents to Solr.  Retrying...", e);
                if (pause > 0L) {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ie) {
                        throw e;
                    }
                }
                pause += waitTime;
            }
        }
    }

    @Override
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, int commitWithin) throws SolrServerException, IOException {
        return updateIndex(docs, null, commitWithin);
    }
    
    @Override
    public void swap(String primaryAliasName, String secondaryAliasName) throws SolrServerException, IOException {
        if (isSolrCloudMode()) {
            
        } else {
            throw new UnsupportedOperationException("Realias operation is only supported in SolrCloud.");
        }
    }

    @Override
    public SolrClient getSolrClient() {
        return client;
    }

    @Override
    public boolean isSolrCloudMode() {
        return (client != null && client instanceof CloudSolrClient);
    }
    
    @Override
    public SolrResponse process(SolrRequest<? extends SolrResponse> request) throws SolrServerException, IOException {
        return process(request, null);
    }
    
    @Override
    public SolrResponse process(SolrRequest<? extends SolrResponse> request, String indexName) throws SolrServerException, IOException {
        //If these are null they will be ignored.  Otherwise, they will be used to set a Basic Auth header.
        request.setBasicAuthCredentials(basicAuthUserName, basicAuthPassword);
        request.setMethod(getDefaultHttpMethod());
        
        //If the index name is null, it will be ignored.
        return request.process(client, indexName);
    }
    
    @Override
    public METHOD getDefaultHttpMethod() {
        //Allows more data and more complex queries to be submitted to the server.
        //Solr's default is GET, which creates issues when the query string is too long.
        return METHOD.POST;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (isSolrCloudMode()) {
            //This should happen as part of creating this bean.  However, it won't hurt to call it again, 
            //so we'll ensure that this is connected once here.
            ((CloudSolrClient)client).connect();
        }
    }
    
    @Override
    public void commit(String collection) throws SolrServerException, IOException {
        commit(collection, true, true, false);
    }
    
    @Override
    public void commit(String collection, boolean waitFlush, boolean waitSearcher, boolean softCommit) throws SolrServerException, IOException {
        if (collection != null) {
            client.commit(collection, waitFlush, waitSearcher, softCommit);
        } else {
            client.commit(waitFlush, waitSearcher, softCommit);
        }
    }
}
