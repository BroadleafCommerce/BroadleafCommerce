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
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.CreateAlias;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.cloud.Aliases;
import org.broadleafcommerce.common.exception.ServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Component("blSolrUtil")
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
    public QueryResponse query(SolrQuery query, String indexName) throws ServiceException {
        QueryRequest request = new QueryRequest(query);
        return (QueryResponse)process(request, indexName);
    }
    
    @Override
    public UpdateResponse updateIndex(UpdateRequest request) throws ServiceException {
        return updateIndex(request, null);
    }

    @Override
    public UpdateResponse updateIndex(UpdateRequest request, String indexName) throws ServiceException {
        return (UpdateResponse)process(request, indexName);
    }

    @Override
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, String indexName, int commitWithin) throws ServiceException {
        if (docs == null || docs.isEmpty()) {
            LOG.warn("The collection of SolrInputDocuments was empty.  Skipping...");
            return null;
        }
        
        UpdateRequest request = new UpdateRequest();
        request.add(docs);
        if (commitWithin > 0) {
            request.setCommitWithin(commitWithin);
        } 
        return (UpdateResponse)process(request, indexName);
    }

    @Override
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, int commitWithin) throws ServiceException {
        return updateIndex(docs, null, commitWithin);
    }
    
    @Override
    public void swap(String primaryAliasName, String secondaryAliasName) throws ServiceException {
        if (isSolrCloudMode()) {
            CloudSolrClient cloudClient = (CloudSolrClient)getSolrClient();
            Aliases aliases = cloudClient.getZkStateReader().getAliases();
            Map<String,String> aliasMap = aliases.getCollectionAliasMap();
            
            String primaryCollectionName = aliasMap.get(primaryAliasName);
            if (primaryCollectionName != null) {
                primaryCollectionName = primaryCollectionName.split(",")[0];
            } else {
                throw new ServiceException("Unable to determine collection name from primary alias '" + primaryAliasName + "'");
            }
            String secondaryCollectionName = aliasMap.get(secondaryAliasName);
            if (secondaryCollectionName != null) {
                secondaryCollectionName = secondaryCollectionName.split(",")[0];
            } else {
                throw new ServiceException("Unable to determine collection name from secondary alias '" + secondaryAliasName + "'");
            }
            
            try {
                //Assign the primary alias to the secondary collection
                CreateAlias createAlias = CollectionAdminRequest.createAlias(primaryAliasName, secondaryCollectionName);
                process(createAlias);
                
                //Assign the secondary alias to the primary collection
                createAlias = CollectionAdminRequest.createAlias(secondaryAliasName, primaryCollectionName);
                process(createAlias);
            } catch (Exception e) {
                throw new ServiceException("Error swapping primary alias '" + primaryAliasName + "' to backup alias '" 
                        + secondaryAliasName + "' and vice versa.", e);
            }
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
        return (getSolrClient() != null && getSolrClient() instanceof CloudSolrClient);
    }
    
    @Override
    public SolrResponse process(SolrRequest<? extends SolrResponse> request) throws ServiceException {
        return process(request, null);
    }
    
    @Override
    public SolrResponse process(SolrRequest<? extends SolrResponse> request, String indexName) throws ServiceException {
        //If these are null they will be ignored.  Otherwise, they will be used to set a Basic Auth header.
        request.setBasicAuthCredentials(basicAuthUserName, basicAuthPassword);
        request.setMethod(getDefaultHttpMethod());
        
        
        int tries = 0;
        long pause = 0L;
        while (true) {
            try {
                if (indexName != null) {
                    return request.process(getSolrClient(), indexName);
                } else {
                    return request.process(getSolrClient());
                }
            } catch (SolrServerException | IOException e) {
                tries++;
                if (tries > maxTries) {
                    throw new ServiceException("Error occured communicating with Solr.", e);
                }
                LOG.warn("Error occured communicating with Solr.  Retrying...", e);
                if (pause > 0L) {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ie) {
                        throw new ServiceException("Error occured communicating with Solr.", e);
                    }
                }
                pause += waitTime;
            }
        }
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
            ((CloudSolrClient)getSolrClient()).connect();
        }
    }
    
    @Override
    public void commit(String collection) throws ServiceException {
        commit(collection, true, true, false);
    }
    
    @Override
    public void commit(String collection, boolean waitFlush, boolean waitSearcher, boolean softCommit) throws ServiceException {
        try {
            if (collection != null) {
                getSolrClient().commit(collection, waitFlush, waitSearcher, softCommit);
            } else {
                getSolrClient().commit(waitFlush, waitSearcher, softCommit);
            }
        } catch (SolrServerException | IOException e) {
            throw new ServiceException("An error occured trying to force commit a Solr index for collection: " + collection, e);
        }
    }
}
