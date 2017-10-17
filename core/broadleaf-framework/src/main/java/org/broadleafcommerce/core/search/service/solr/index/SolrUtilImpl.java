/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrUtilImpl implements SolrUtil {
    
    private static final Log LOG = LogFactory.getLog(SolrUtilImpl.class);
    protected static final int DEFAULT_MAX_TRIES = 3;
    protected static final long DEFAULT_WAIT_TIME = 50L;
    
    protected final boolean solrCloudMode;
    protected final int maxTries;
    protected final long waitTime;
    protected final String basicAuthUserName;
    protected final String basicAuthPassword;
    protected final SolrClient client;
    
    private boolean connected = false;
    
    public SolrUtilImpl(SolrClient client) {
        this(client, null, null, DEFAULT_MAX_TRIES, DEFAULT_WAIT_TIME);
    }
    
    public SolrUtilImpl(SolrClient client, String basicAuthUserName, String basicAuthPassword) {
        this(client, basicAuthPassword, basicAuthPassword, DEFAULT_MAX_TRIES, DEFAULT_WAIT_TIME);
    }
    
    public SolrUtilImpl(SolrClient client, String basicAuthUserName, String basicAuthPassword, int maxTries, long waitTime) {
        Assert.notNull(client, "The SolrClient cannot be null.");
        Assert.isTrue(maxTries > 0, "maxTries must be greater than 0");
        this.client = client;
        this.maxTries = maxTries;
        this.waitTime = waitTime;
        this.basicAuthUserName = basicAuthUserName;
        this.basicAuthPassword = basicAuthPassword;
        this.solrCloudMode = (this.client instanceof CloudSolrClient);
        try {
            connect();
        } catch (Exception e) {
            LOG.error("Error connecting to Zookeeper while initializing. Will try again when attempting to talk to Solr.", e);
        }
    }
    
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
            
            String primaryCollectionName = getCollectionNameForAlias(primaryAliasName);
            String secondaryCollectionName = getCollectionNameForAlias(secondaryAliasName);
            if (primaryCollectionName == null) {
                throw new ServiceException("Unable to determine collection name from primary alias '" + primaryAliasName + "'");
            }
            if (secondaryCollectionName == null) {
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
        return solrCloudMode;
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
        
        connect(); //If it's already connected this will do nothing.  Otherwise, it will connect to Zookeeper, if applicable.
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
    
    @Override
    public String getCollectionNameForAlias(String alias) {
        if (isSolrCloudMode()) {
            CloudSolrClient cloudClient = (CloudSolrClient)getSolrClient();
            Aliases aliases = cloudClient.getZkStateReader().getAliases();
            Map<String,String> aliasMap = aliases.getCollectionAliasMap();
            String collection = aliasMap.get(alias);
            if (collection != null) {
                return collection.split(",")[0];
            }
            
            return collection;
        } else {
            throw new UnsupportedOperationException("Cannot obtain alias for a collection. Not in SolrCloud mode.");
        }
    }
    
    /**
     * This allows us to quietly connect on first use.  This helps prevent BLC from starting when Zookeeper is 
     * not yet started, which can be helpful for local development.
     */
    private void connect() {
        if (!connected && isSolrCloudMode()) {
            synchronized (this) {
                if (!connected) {
                    ((CloudSolrClient)getSolrClient()).connect();
                    connected = true;
                }
            }
        }
    }
}
