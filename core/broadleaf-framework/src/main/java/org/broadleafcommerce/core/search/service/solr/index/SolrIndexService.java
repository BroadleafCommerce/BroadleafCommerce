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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.IndexField;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Service exposing several methods for creating a Solr index based on catalog product data.
 *
 * @see org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface SolrIndexService {

    /**
     * <p>
     * Executes a full rebuild of the Solr index. This will rebuild the index on a separate core/collection and then swap
     * out the active core/collection with the new version of the index (essentially replacing all documents that are
     * currently in the index).
     * 
     * <p>
     * The order of methods that are apart of rebuilding the entire index:
     * <ol>
     *  <li><pre>{@link #preBuildIndex()}</pre></li>
     *  <li><pre>{@link #buildIndex()}</pre></li>
     *  <li><pre>{@link #postBuildIndex()}</pre></li>
     * </ol>
     * 
     * 
     * @throws IOException 
     * @throws ServiceException
     */
    public void rebuildIndex() throws ServiceException, IOException;

    /**
     * Executed before we do any indexing when rebuilding the index. Usually this handles deleting the current index. This
     * is called at the beginning of {@link #rebuildIndex()}
     *
     * @throws ServiceException
     */
    public void preBuildIndex() throws ServiceException;

    /**
     * <p>
     * Handles all the document building for the current index rebuild. This is where all of the SolrIndexOperation's need to be
     * created, executed and the documents built and added to the Solr index
     * 
     * <p>
     * This is the method that should be overridden to specify which operations should be run to build the correct index.
     *
     * @throws IOException
     * @throws ServiceException
     * @see {@link #rebuildIndex()}
     * @see {@link #preBuildIndex()}
     */
    public void buildIndex() throws IOException, ServiceException;

    /**
     * Executed after we do any indexing when rebuilding the current index. Usually this handles optimizing the index and swapping the cores.
     *
     * @throws IOException
     * @throws ServiceException
     */
    public void postBuildIndex() throws IOException, ServiceException;
    
    /**
     * Creates the  SolrIndexOperation for rebuilding the current index, used by {@link #buildIndex()}. This is the primary
     * index operation used to rebuild the index.
     *
     * @return a SolrIndexOperation capable of rebuilding the current index
     */
    public SolrIndexOperation getReindexOperation();

    /**
     * Executes the given <b>operation</b> in the correct method order
     *
     * @param operation the SolrIndexOperation that is to be executed
     * @throws ServiceException
     * @throws IOException
     */
    public void executeSolrIndexOperation(SolrIndexOperation operation) throws ServiceException, IOException;
    
    /**
     * Builds a set of {@link Indexable}s against the given {@link SolrServer}
     * @param collection The collection to be used for incremental indexing
     * @param indexables the list of items to index
     * @param solrServer if non-null, adds and commits the indexed documents to the server. If this is null, this will
     * simply return the documents that were built from <b>indexables</b>
     *
     * @throws ServiceException
     * @return the {@link SolrInputDocument}s that were built from the given <b>indexables</b>
     */
    public Collection<SolrInputDocument> buildIncrementalIndex(String collection, List<? extends Indexable> indexables, SolrClient solrServer) throws ServiceException;

    /**
     * Builds a set of {@link Indexable}s against the given {@link SolrServer}
     * @param indexables the list of items to index
     * @param solrServer if non-null, adds and commits the indexed documents to the server. If this is null, this will
     * simply return the documents that were built from <b>indexables</b>
     *
     * @throws ServiceException
     * @return the {@link SolrInputDocument}s that were built from the given <b>indexables</b>
     * @deprecated Use {@link #buildIncrementalIndex(String, List, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public Collection<SolrInputDocument> buildIncrementalIndex(List<? extends Indexable> indexables, SolrClient solrServer) throws ServiceException;

    /**
     * Saves some global context that might be altered during indexing.
     * 
     * @return
     */
    public Object[] saveState();

    /**
     * Restores state that was saved prior to indexing that might have been altered.
     * 
     * @see #saveState()
     * @param pack
     */
    public void restoreState(Object[] pack);

    /**
     * Triggers the Solr optimize index function on the given server. This is typically called at the end of a {@link #rebuildIndex()}
     * @param collection The collection to optimize
     * @param server The server to use to optimze the given collection
     *
     * @throws ServiceException
     * @throws IOException
     */
    public void optimizeIndex(String collection, SolrClient server) throws ServiceException, IOException;

    /**
     * Triggers the Solr optimize index function on the given server. This is typically called at the end of a {@link #rebuildIndex()}
     * @param server The server to use to optimze the default collection
     *
     * @throws ServiceException
     * @throws IOException
     * @deprecated Use {@link #optimizeIndex(String, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public void optimizeIndex(SolrClient server) throws ServiceException, IOException;

    /**
     * Allows a commit to be called.  By default, the details of the commit will depend on system properties, including:
     * 
     * solr.index.commit - if false, then no commit will be performed. autoCommit (and autoSoftCommit) should be configured in Solr.
     * solr.index.softCommit - indicates if a soft commit should be performed
     * solr.index.waitSearcher - indicates if the process should wait for a searcher to be configured
     * solr.index.waitFlush - indicates if the process should wait for a flush to disk
     * @param collection The collection to commit
     * @param server The server used to commit the specified collection
     *
     * @throws ServiceException
     * @throws IOException
     */

    public void commit(String collection, SolrClient server) throws ServiceException, IOException;

    /**
     * Allows a commit to be called.  By default, the details of the commit will depend on system properties, including:
     *
     * solr.index.commit - if false, then no commit will be performed. autoCommit (and autoSoftCommit) should be configured in Solr.
     * solr.index.softCommit - indicates if a soft commit should be performed
     * solr.index.waitSearcher - indicates if the process should wait for a searcher to be configured
     * solr.index.waitFlush - indicates if the process should wait for a flush to disk
     * @param server The server used to commit the default collection
     *
     * @throws ServiceException
     * @throws IOException
     * @deprecated Use {@link #commit(String, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public void commit(SolrClient server) throws ServiceException, IOException;

    /**
     * This allows an external caller to force a commit to the SolrClient.  See Solr Documentation for
     * additional details.  If using softCommit, you should ensure that a hardCommit is performed, either 
     * using autoCommit, or at the end of the commit process to flush the changes to the disk.
     * 
     * Note that this method will force a commit even if solr.index.commit=false
     * @param collection The collection to commit
     * @param server - the SolrClient to use to commit the provided collection
     * @param softCommit - soft commit is an efficient commit that does not write the data to the file system
     * @param waitSearcher - whether or not to wait for a new searcher to be created
     * @param waitFlush - whether or not to wait for a flush to disk.
     *
     * @throws ServiceException
     * @throws IOException
     */
    public void commit(String collection, SolrClient server, boolean softCommit, boolean waitSearcher, boolean waitFlush) throws ServiceException, IOException;

    /**
     * This allows an external caller to force a commit to the SolrClient.  See Solr Documentation for
     * additional details.  If using softCommit, you should ensure that a hardCommit is performed, either
     * using autoCommit, or at the end of the commit process to flush the changes to the disk.
     *
     * Note that this method will force a commit even if solr.index.commit=false
     * @param server - the SolrClient to commit with
     * @param softCommit - soft commit is an efficient commit that does not write the data to the file system
     * @param waitSearcher - whether or not to wait for a new searcher to be created
     * @param waitFlush - whether or not to wait for a flush to disk.
     *
     * @throws ServiceException
     * @throws IOException
     * @deprecated Use {@link #commit(String, SolrClient, boolean, boolean, boolean)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public void commit(SolrClient server, boolean softCommit, boolean waitSearcher, boolean waitFlush) throws ServiceException, IOException;

    public void deleteAllNamespaceDocuments(String collection, SolrClient server) throws ServiceException;

    /**
     * @deprecated Use {@link #deleteAllNamespaceDocuments(String, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public void deleteAllNamespaceDocuments(SolrClient server) throws ServiceException;

    public void deleteAllDocuments(String collection, SolrClient server) throws ServiceException;

    /**
     * @deprecated Use {@link #deleteAllDocuments(String, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    public void deleteAllDocuments(SolrClient server) throws ServiceException;

    /**
     * Prints out the docs to the trace logger
     * 
     * @param documents
     */
    public void logDocuments(Collection<SolrInputDocument> documents);

    /**
     * @return a list of all possible locale prefixes to consider
     */
    public List<Locale> getAllLocales();

    /**
     * Given a product, fields that relate to that product, and a list of locales and pricelists, builds a 
     * SolrInputDocument to be added to the Solr index.
     * 
     * @param product
     * @param fields
     * @param locales
     * @return the document
     */
    public SolrInputDocument buildDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales);

    /**
     * SolrIndexService exposes {@link #buildIncrementalIndex(int, int, boolean)}.
     * By wrapping the call to this method inside of a {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation.CacheOperation},
     * a single cache will be used for all the contained calls to buildIncrementalIndex. Here's an example:
     * {@code
     *  performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {
     *        @Override
     *        public void execute() throws ServiceException {
     *            int page = 0;
     *            while ((page * pageSize) < numProducts) {
     *                buildIncrementalIndex(page, pageSize);
     *                page++;
     *            }
     *        }
     *    });
     * }
     * Note {@link #rebuildIndex()} already internally wraps its call using CacheOperation, so it is not necessary to call performCacheOperation
     * for calls to rebuildIndex().
     *
     * @param cacheOperation the block of code to perform using a single cache for best performance
     * @throws ServiceException
     */
    public void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException;

    /**
     * Iterates through the fields for this indexable and indexes any SearchField's or SearchFacet's.
     *  @param document
     * @param indexable
     * @param fields
     * @param locales
     */
    public void attachIndexableDocumentFields(SolrInputDocument document, Indexable indexable, List<IndexField> fields, List<Locale> locales);

    void deleteByQuery(String deleteQuery) throws SolrServerException, IOException;

    void addDocuments(Collection<SolrInputDocument> documents) throws IOException, SolrServerException;

    void logDeleteQuery(String deleteQuery);

    /**
     * Indicates if this should be used.  The alternative is to use {@link CatalogSolrIndexUpdateService}. By default, this is driven by the property,
     * 'solr.catalog.useLegacySolrIndexer', which defaults to true.
     *
     * @return
     */
    public boolean useLegacyIndexer();
}

