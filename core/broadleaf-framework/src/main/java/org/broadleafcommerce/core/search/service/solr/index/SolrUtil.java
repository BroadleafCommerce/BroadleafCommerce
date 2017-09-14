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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;

import java.io.IOException;
import java.util.List;

/**
 * This component offers a number of utility methods to simplify communication with Solr/SolrJ.  In particular, 
 * it automatically sets the Basic Auth headers in the event that the username and password properties are set.
 * 
 * It also provides retry logic in the event there was a short lived network or server error.
 * 
 * @author Kelly Tisdell
 *
 */
public interface SolrUtil {
    
    /**
     * Executes a query against Solr, setting the appropriate Basic Auth headers, if needed.
     * 
     * The index name (or collection name) is generally important, especially if using SolrCloud, 
     * and if the SolrClient does not have an assigned defaultCollection.  See the Solr documentation for more details.
     * 
     * @param query
     * @param indexName
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public QueryResponse query(SolrQuery query, String indexName) throws ServiceException;
    
    /**
     * 
     * The index name (or collection name) is generally important, especially if using SolrCloud, 
     * and if the SolrClient does not have an assigned defaultCollection.  See the Solr documentation for more details.
     * 
     * @param docs
     * @param indexName
     * @param commitWithin
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, String indexName, int commitWithin) throws ServiceException;
    
    /**
     * 
     * @param docs
     * @param commitWithin
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public UpdateResponse updateIndex(List<SolrInputDocument> docs, int commitWithin) throws ServiceException;

    /**
     * 
     * 
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public UpdateResponse updateIndex(UpdateRequest request) throws ServiceException;
    
    /**
     * Convenience methods to send updates to Solr.  The update request could contain SolrInputDocuments, a delete query, etc.
     * 
     * The index name (or collection name) is generally important, especially if using SolrCloud, 
     * and if the SolrClient does not have an assigned defaultCollection.  See the Solr documentation for more details.
     * 
     * @param request
     * @param indexName
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public UpdateResponse updateIndex(UpdateRequest request, String indexName) throws ServiceException;
    
    /**
     * This takes a primary and secondary alias, and swaps their assignment to 
     * their respective collection names.  So, for example, assume you have 2 aliases: catalog_primary and catalog_reindex. 
     * The parameters MUST NOT be null and both parameters MUST be existing aliases for existing collections.
     * 
     * Let's assume that they are assigned to collections as follows:
     * 
     * catalog_primary => blcCatalogCollection0
     * catalog_reindex => blcCatalogCollection1
     * 
     * A call to this method will swap them so that they are assigned as follows:
     * 
     * catalog_primary => blcCatalogCollection1
     * catalog_reindex => blcCatalogCollection0
     * 
     * The assumption is that the catalog_primary collection will be referenced by the live site.  The catalog_reindex 
     * collection will be referenced by a background process to reindex, or refresh, the data in the collection.  The 
     * site will always reference the catalog_primary alias, but the underlying collection will be reassigned, transparently 
     * to the user, after a call to this method.
     * 
     * Under the covers, we use the SolrJ API and CollectionAdminRequests to do this.
     * 
     * Note, by default, this is only supported for SolrCloudClient.  You may wish to override this and use SolrJ's 
     * Core Admin Requests.
     * 
     * @param primaryAliasName
     * @param secondaryAliasName
     * @throws SolrServerException
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public void swap(String primaryAliasName, String secondaryAliasName) throws ServiceException;
    
    /**
     * Returns the instance of the underlying SolrClient (e.g. CloudSolrClient).
     * 
     * @return
     */
    public SolrClient getSolrClient();
    
    /**
     * Indicates if the SolrClient is an instance of CloudSolrClient.
     * 
     * @return
     */
    public boolean isSolrCloudMode();
    
    /**
     * Default method to use to communicate with Solr.  The default is POST, which allows for larger, more complex 
     * queries and data to be passed to the server.
     * 
     * @return
     */
    public METHOD getDefaultHttpMethod();
    
    /**
     * Generic wrapper that allows processing of Solr requests.  If the following properties are set, then they'll be passed 
     * along on the request for security:
     * 
     * com.broadleafcommerce.solr.auth.basic.username=some_user
     * com.broadleafcommerce.solr.auth.basic.password=some_password
     * 
     * @param request
     * @param indexName
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public SolrResponse process(SolrRequest<? extends SolrResponse> request, String indexName) throws ServiceException;
    
    /**
     * Generic wrapper that allows processing of Solr requests.  If the following properties are set, then they'll be passed 
     * along on the request for security:
     * 
     * com.broadleafcommerce.solr.auth.basic.username=some_user
     * com.broadleafcommerce.solr.auth.basic.password=some_password
     * 
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public SolrResponse process(SolrRequest<? extends SolrResponse> request) throws ServiceException;
    
    /**
     * This issues a commit to Solr.  Please check the docs for Solr.  This IS NOT like a database commit in that 
     * it is a Global commit.  It is not tied to a particular session or connection.  This will issue a commit against 
     * the specified collection or core name, or against a default if the collection name is null.
     * 
     * In this case, waitSearcher will be true, waitFlush will be true, and softCommit will be false.
     * 
     * As a general rule, commits SHOULD NOT be issued from clients.  Solr should be set up with autoCommits turned on. 
     * However, settings for bulk loading are often different than for near real time (NRT) updates.  
     * As a result, we usually recommend turning autoCommit and softAutoCommit on, and during bulk loading, issue a 
     * manual hard commit at the very end of the reindex process.  For incremental updates, allow the autoCommit settings 
     * to handle the changes transparently.
     * 
     * For additional information, please see:
     * 
     * https://cwiki.apache.org/confluence/display/solr/UpdateHandlers+in+SolrConfig
     * https://lucidworks.com/2013/08/23/understanding-transaction-logs-softcommit-and-commit-in-sorlcloud/
     * 
     * @param collection
     * @throws SolrServerException
     * @throws IOException
     */
    public void commit(String collection) throws ServiceException;
    
    /**
     * This issues a commit to Solr.  Please check the docs for Solr.  This IS NOT like a database commit in that 
     * it is a Global commit.  It is not tied to a particular session or connection.  This will issue a commit against 
     * the specified collection or core name, or against a default if the collection name is null.
     * 
     * In this case, waitSearcher will be true, waitFlush will be true, and softCommit will be false.
     * 
     * As a general rule, commits SHOULD NOT be issued from clients.  Solr should be set up with autoCommits turned on. 
     * However, settings for bulk loading are often different than for near real time (NRT) updates.  
     * As a result, we usually recommend turning autoCommit and softAutoCommit on, and during bulk loading, issue a 
     * manual hard commit at the very end of the reindex process.  For incremental updates, allow the autoCommit settings 
     * to handle the changes transparently.
     * 
     * For additional information, please see:
     * 
     * https://cwiki.apache.org/confluence/display/solr/UpdateHandlers+in+SolrConfig
     * https://lucidworks.com/2013/08/23/understanding-transaction-logs-softcommit-and-commit-in-sorlcloud/
     * 
     * @param collection
     * @param waitFlush
     * @param waitSearcher
     * @param softCommit
     * @throws SolrServerException
     * @throws IOException
     */
    public void commit(String collection, boolean waitFlush, boolean waitSearcher, boolean softCommit) throws ServiceException;
    
    /**
     * This method returns the collection name associated with an alias (or null).  This throws an UnsupportedOperationException 
     * if the SolrClient is not a CloudSolrClient.
     * @param alias
     * @return
     */
    public String getCollectionNameForAlias(String alias);
    
}
