/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.EntityManagerAwareRunnable;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.dao.SolrIndexDao;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Default command handler to handle Catalog Solr index commands. This is multi-threaded.  SKU browsing is not supported with this implementation.
 *
 * @author Kelly Tisdell
 */
@Service("blCatalogSolrUpdateCommandHandler")
public class CatalogSolrIndexUpdateCommandHandlerImpl extends AbstractSolrIndexUpdateCommandHandlerImpl
        implements CatalogSolrIndexCommandHandler, DisposableBean {

    private static final Log LOG = LogFactory.getLog(CatalogSolrIndexUpdateCommandHandlerImpl.class);
    private final ThreadPoolTaskExecutor backgroundOperationExecutor;
    @Qualifier("blCatalogSolrConfiguration")
    @Autowired(required = false)
    protected SolrConfiguration solrConfiguration;
    @Resource(name = "blLocaleService")
    protected LocaleService localeService;
    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;
    @Resource(name = "blSiteService")
    protected SiteService siteService;
    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager extensionManager;
    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;
    @Resource(name = "blSolrIndexDao")
    protected SolrIndexDao solrIndexDao;
    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;
    @Resource(name = "blProductDao")
    protected ProductDao productDao;
    @Resource(name = "blSolrIndexQueueProvider")
    protected SolrIndexQueueProvider queueProvider;
    @Resource(name = "blCatalogDocumentBuilder")
    protected CatalogDocumentBuilder documentBuilder;
    @Value("${solr.index.product.pageSize:100}")
    protected int pageSize = 100;

    @Value("${solr.index.product.workerThreads:10}")
    protected int workerThreads = 10;

    @Value("${solr.index.product.reindexCommitInterval:30000}")
    protected long reindexCommitInterval = 30000L;
    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    public CatalogSolrIndexUpdateCommandHandlerImpl() {
        super("catalog");
        this.backgroundOperationExecutor = createBackgroundOperationExecutor();
    }

    @Override
    public <C extends SolrUpdateCommand> void executeCommand(C command) throws ServiceException {
        if (solrConfiguration == null) {
            throw new IllegalStateException("SolrConfiguration was null.  "
                    + "Please ensure that a Spring Bean with the name 'blCatalogSolrConfiguration' is in scope and that it is an instance of " + SolrConfiguration.class.getName());
        }

        Lock lock = queueProvider.createOrRetrieveCommandLock(
                getCommandGroup() + SolrIndexQueueProvider.COMMAND_LOCK_NAME
        );
        //Should be able to immediately obtain the command lock because the current thread should already hold it if it's delegating to this method.
        //Locking again is more of a safeguard.
        if (lock.tryLock()) {
            try {
                if (command instanceof IncrementalUpdateCommand) {
                    executeCommandInternal((IncrementalUpdateCommand) command);
                } else if (command instanceof FullReindexCommand) {
                    executeFullReindexCommand((FullReindexCommand) command);
                } else if (command instanceof CatalogReindexCommand) {
                    executeCatalogReindexCommand((CatalogReindexCommand) command);
                } else if (command instanceof SiteReindexCommand) {
                    executeSiteReindexCommand((SiteReindexCommand) command);
                } else {
                    executeCommandInternalNoDefaultCommandType(command);
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new ServiceException("Unable to immediately acquire a command lock. Unable to execute or handle the SolrUpdateCommand.");
        }
    }

    @Override
    public SolrInputDocument buildDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales) {
        return documentBuilder.buildDocument(indexable, fields, locales);
    }

    @Override
    public SolrInputDocument buildDocument(Indexable indexable) {
        return buildDocument(
                indexable,
                indexFieldDao.readFieldsByEntityType(indexable.getFieldEntityType()),
                getAllLocales()
        );
    }

    protected void executeFullReindexCommand(FullReindexCommand command) throws ServiceException {
        final long startTime = System.currentTimeMillis();
        final String reindexCollectionName = getBackgroundCollectionName();
        ReindexStateHolder holder;
        synchronized (ReindexStateHolder.class) {
            holder = ReindexStateHolder.getInstance(reindexCollectionName, false, false);
            if (holder != null) {
                //We're in a bad state here.  This should not be created yet.
                throw new IllegalStateException("Tried to execute a full reindex, "
                        + "but it appears that someone already started one, or did not deregister one after completion for collection '"
                        + getBackgroundCollectionName() + "'.");
            }

            //Now we create the state holder.
            holder = ReindexStateHolder.getInstance(
                    reindexCollectionName, !solrConfiguration.isSingleCoreMode(), true
            );
        }

        try {
            beforeProcess(holder);
            deleteAllDocuments(getBackgroundCollectionName(), !solrConfiguration.isSingleCoreMode());
        } catch (Exception e) {
            holder.failFast(e);
            throw new ServiceException("Error occured writing data to Solr.", e);
        } finally {
            try {
                try {
                    afterProcess(holder);
                } finally {
                    try {
                        finalizeChanges(reindexCollectionName, !isReindexSuccessful(holder), true);
                    } finally {
                        ReindexStateHolder.unregister(reindexCollectionName);
                    }
                }
            } finally {
                final long endTime = System.currentTimeMillis();
                LOG.info("Full Catalog Reindex Process completed in " + (endTime - startTime) + " milliseconds, or "
                        + ((endTime - startTime) / 60000L) + " minutes.");
            }
        }
    }

    protected void executeCatalogReindexCommand(CatalogReindexCommand command) throws ServiceException {
        throw new UnsupportedOperationException("By default, the " + command.getClass().getName()
                + " is not supported. Consider overriding this method or installing Broadleaf's Multi-Tenant module.");
    }

    protected void executeSiteReindexCommand(SiteReindexCommand command) throws ServiceException {
        throw new UnsupportedOperationException("By default, the " + command.getClass().getName()
                + " is not supported. Consider overriding this method or installing Broadleaf's Multi-Tenant module.");
    }

    @Override
    protected SolrConfiguration getSolrConfiguration() {
        return solrConfiguration;
    }

    protected List<Locale> getAllLocales() {
        List<Locale> allLocales = localeService.findAllLocales();
        Map<String, Locale> processedLocales = new HashMap<>();
        // Optimize the list of locales we are looking at. If I have an 'en' and 'en_US' in the locale set and I'm
        // not using the country code to index the values, then I only need to index the locale 'en'
        for (Locale locale : allLocales) {
            String localeCode = locale.getLocaleCode();
            int underscoreLocation = localeCode.indexOf("_");
            if (underscoreLocation > 0 && Boolean.FALSE.equals(locale.getUseCountryInSearchIndex())) {
                String localeCodeWithoutCountry = localeCode.substring(0, underscoreLocation);
                if (!processedLocales.containsKey(localeCodeWithoutCountry)) {
                    processedLocales.put(localeCodeWithoutCountry, locale);
                }
            } else {
                processedLocales.put(locale.getLocaleCode(), locale);
            }
        }
        return new ArrayList<>(processedLocales.values());
    }

    protected void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException {
        try {
            CatalogStructure cache = new CatalogStructure();
            SolrIndexCachedOperation.setCache(cache);
            cacheOperation.execute();
        } finally {
            SolrIndexCachedOperation.clearCache();
        }
    }

    /**
     * This method populates the index.
     * <p>
     * It is not recommended that you override this method.  Rather, consider overriding one of the other methods as this one generally coordinates and delegates to others.
     */
    protected void populateIndex(
            final ReindexStateHolder holder,
            final Long catalogId,
            final Long siteId,
            final SandBox sandbox
    ) throws ServiceException {
        try {
            final Semaphore sem = new Semaphore(0);
            try {
                final Catalog catalog = findCatalog(catalogId);
                final Site site = findSite(siteId);
                final BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
                brc.setSandBox(sandbox);

                int leases = 0;

                //We'll make the batch size 10 times the page size so that we can break up the results into pages for 
                //fewer round trips to the DB, especially since we're only reading IDs here.
                final int batchSize = pageSize * 10;
                final AtomicReference<Long> lastId = new AtomicReference<>();

                try {
                    while (true) {
                        if (holder.isFailed()) {
                            break;
                        }

                        final IdentityOperation<List<Long>, Exception> readIdsOperation = getReadIdsOperation(
                                holder, catalogId, siteId, batchSize, lastId.get()
                        );

                        final List<Long> batch;
                        if (catalog != null || site != null) {
                            batch = IdentityExecutionUtils.runOperationByIdentifier(readIdsOperation, site, catalog);
                        } else {
                            batch = IdentityExecutionUtils.runOperationAndIgnoreIdentifier(readIdsOperation);
                        }

                        if (batch == null || batch.isEmpty()) {
                            break;
                        }

                        ArrayList<Long> pageHolder = new ArrayList<>(pageSize);
                        Iterator<Long> itr = batch.iterator();
                        while (itr.hasNext()) {
                            pageHolder.add(itr.next());
                            if (pageHolder.size() == pageSize) {
                                if (holder.isFailed()) {
                                    return;
                                }
                                final ArrayList<Long> page = new ArrayList<>(pageHolder);
                                getBackgroundOperationExecutor().execute(
                                        createBackgroundRunnable(holder, page, sem, catalogId, siteId, sandbox)
                                );
                                leases++;
                                pageHolder = new ArrayList<>(pageSize);
                            }
                        }

                        if (!pageHolder.isEmpty()) {
                            getBackgroundOperationExecutor().execute(
                                    createBackgroundRunnable(holder, pageHolder, sem, catalogId, siteId, sandbox)
                            );
                            leases++;
                        }

                        if (batch.size() < batchSize) {
                            break;
                        }

                        lastId.set(batch.get(batch.size() - 1));
                        // Safely clean up L1 cache...
                        HibernateUtils.clearDefaultEntityManager();

                    }
                } finally {
                    sem.acquire(leases);
                }

            } catch (Exception e) {
                holder.failFast(e);
                throw new ServiceException("An unexpected error occured reindexing solr for command group "
                        + getCommandGroup() + ". Please check the logs.", holder.getFailure());
            }
        } finally {
            //Don't reset the BroadleafRequestContext here because we did not create / bind it.
        }
    }

    /**
     * Provides an {@link IdentityOperation} (function that runs in the context of a Site and/or Catalog) to read batches of IDs.
     *
     * @param holder
     * @param catalogId
     * @param siteId
     * @param batchSize
     * @param lastId
     * @return
     */
    protected IdentityOperation<List<Long>, Exception> getReadIdsOperation(
            final ReindexStateHolder holder,
            final Long catalogId,
            final Long siteId,
            final Integer batchSize,
            final Long lastId
    ) {
        return new IdentityOperation<List<Long>, Exception>() {
            @Override
            public List<Long> execute() throws Exception {
                beforeReadIdBatch(holder, catalogId, batchSize, lastId);
                try {
                    return readIdBatch(holder, catalogId, batchSize, lastId);
                } finally {
                    afterReadIdBatch(holder, catalogId, batchSize, lastId);
                }
            }
        };
    }

    /**
     * By default this deletes all items in the collection, since there is a good chance that the collection will be re-aliased or swapped into the foreground
     * after reindexing is complete.
     *
     * @param collection
     * @param commit
     * @throws ServiceException
     */
    protected void deleteAllDocuments(String collection, boolean commit) throws ServiceException {
        try {
            getSolrConfiguration().getReindexServer().deleteByQuery(null, "(*:*)");
            if (commit) {
                commit(null, true, true, false);
            }
        } catch (Exception e) {
            throw new ServiceException("An error occured deleting the contents of background Solr index, " + collection, e);
        }
    }

    /**
     * Composite method that issues a commit or rollback (if there is an error), and optionally swaps (if there is no error).
     *
     * @param collection
     * @param error
     * @param swap
     * @throws ServiceException
     */
    protected void finalizeChanges(final String collection, final boolean error, boolean swap) throws ServiceException {
        try {
            if (error) {
                rollback(collection);
            } else {
                commit(null, true, true, false);
                if (swap) {
                    swapCollections();
                }
            }
        } catch (Exception e) {
            if (error) {
                throw new ServiceException("An error rolling back the Solr index, " + collection, e);
            } else if (swap) {
                throw new ServiceException("An error occured committing and swapping the Solr index, " + collection, e);
            } else {
                throw new ServiceException("An error occured committing the Solr index, " + collection, e);
            }
        }
    }

    protected void swapCollections() throws ServiceException {
        if (!solrConfiguration.isSingleCoreMode()) {
            shs.swapActiveCores(solrConfiguration);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (backgroundOperationExecutor != null) {
            backgroundOperationExecutor.shutdown();
        }
    }

    protected ThreadPoolTaskExecutor getBackgroundOperationExecutor() {
        return backgroundOperationExecutor;
    }

    /**
     * Number of worker threads (page processing threads) in the thread pool.
     * <p>
     * Default is 10.
     *
     * @return
     */
    protected int getThreadsForBackgroundExecution() {
        return workerThreads;
    }

    protected ThreadPoolTaskExecutor createBackgroundOperationExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setThreadGroupName(getCommandGroup() + "-solr-reindex-worker");
        exec.setThreadNamePrefix(getCommandGroup() + "-solr-reindex-worker-");
        exec.setCorePoolSize(getThreadsForBackgroundExecution());
        exec.setMaxPoolSize(getThreadsForBackgroundExecution());
        exec.initialize();
        return exec;
    }

    /**
     * This is where most of the heavy lifting happens.
     *
     * @param holder
     * @param ids
     * @param sem
     * @param catalogId
     * @param siteId
     * @param sandBox
     * @return
     */
    protected EntityManagerAwareRunnable createBackgroundRunnable(
            final ReindexStateHolder holder,
            final List<Long> ids,
            final Semaphore sem,
            final Long catalogId,
            final Long siteId,
            final SandBox sandBox
    ) {
        return new EntityManagerAwareRunnable(sem) {
            @Override
            protected void executeInternal() throws Exception {
                if (ids == null || ids.isEmpty() || holder.isFailed()) {
                    return;
                }

                //The BroadleafRequestContext was created in the superclass.
                final BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
                final Catalog catalog = findCatalog(catalogId);
                final Site site = findSite(siteId);
                brc.setSandBox(sandBox);

                beforeBackgroundThread(holder, catalog, site, sandBox);

                final IdentityOperation<Void, Exception> incrementalIndexOperation = getIncrementalIndexOperation(
                        holder, catalog, site, ids
                );

                try {
                    //Pass shared state from the ReindexStateHolder to the thread-bound BroadleafRequestContext.
                    brc.getAdditionalProperties().putAll(holder.getAdditionalState());

                    if (catalog != null || site != null) {
                        IdentityExecutionUtils.runOperationByIdentifier(incrementalIndexOperation, site, catalog);
                    } else {
                        IdentityExecutionUtils.runOperationAndIgnoreIdentifier(incrementalIndexOperation);
                    }
                } finally {
                    try {
                        afterBackgroundThread(holder, catalog, site, sandBox);
                    } finally {
                        //Don't reset the BroadleafRequestContext here because we did not create / bind it.
                    }
                }
            }

            @Override
            protected void registerError(Exception e) {
                holder.failFast(e);
            }
        };
    }

    /**
     * Returns an {@link IdentityOperation} who may be executed in the context of a {@link Catalog} and/or {@link Site} to read products by IDs, and then build the index incrementally.
     *
     * @param holder
     * @param catalog
     * @param site
     * @param ids
     * @return
     */
    protected IdentityOperation<Void, Exception> getIncrementalIndexOperation(
            final ReindexStateHolder holder,
            final Catalog catalog,
            final Site site,
            final List<Long> ids
    ) {
        return new IdentityOperation<Void, Exception>() {

            @Override
            public Void execute() throws Exception {
                performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {

                    @Override
                    public void execute() throws ServiceException {
                        try {
                            beforeReadProducts(holder, ids);
                            List<Product> products = null;
                            try {
                                products = readProductsByIds(holder, ids);
                            } finally {
                                afterReadProducts(holder, ids);
                            }
                            buildIncrementalIndex(ids, products, holder, catalog, site);
                            if (LOG.isInfoEnabled()) {
                                String catalogName = "N/A";
                                String catIdString = catalogName;
                                if (catalog != null) {
                                    catalogName = catalog.getName();
                                    catIdString = String.valueOf(catalog.getId());
                                }

                                LOG.info("Processed " + products.size() + " products for catalog " + catalogName
                                        + " with ID of " + catIdString
                                        + ". Number of Product IDs provided were " + ids.size() + ".");
                            }
                        } catch (Exception e) {
                            holder.failFast(e);
                            return;
                        }
                    }
                });

                return null;
            }
        };
    }

    /**
     * Given the arguments, this builds a list of {@link SolrInputDocument}s.  This does not write them to Solr.
     *
     * @param productIds
     * @param products
     * @param locales
     * @param fields
     * @param holder
     * @return
     * @throws Exception
     */
    protected List<SolrInputDocument> buildPage(
            List<Long> productIds,
            List<Product> products,
            List<Locale> locales,
            List<IndexField> fields,
            ReindexStateHolder holder
    ) throws Exception {
        final List<SolrInputDocument> docs = new ArrayList<>();

        if (products != null && !products.isEmpty()) {
            sandBoxHelper.ignoreCloneCache(true);
            try {
                BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
                brc.getAdditionalProperties().put("defaultLocale", localeService.findDefaultLocale());
                extensionManager.getProxy().startBatchEvent(products);
                solrIndexDao.populateProductCatalogStructure(productIds, SolrIndexCachedOperation.getCache());

                for (Product product : products) {
                    SolrInputDocument document = buildDocument(product, fields, locales);
                    if (document != null) {
                        docs.add(document);
                        holder.incrementIndexableCount(1L);
                    } else {
                        holder.incrementUnindexedItemCount(1L);
                    }
                }

                if (!docs.isEmpty()) {
                    extensionManager.getProxy().modifyBuiltDocuments(docs, products, fields, locales);
                }

            } finally {
                try {
                    extensionManager.getProxy().endBatchEvent(products);
                } finally {
                    sandBoxHelper.ignoreCloneCache(false);
                }
            }
        }
        return docs;
    }

    /**
     * Reads a batch of products by IDs.
     *
     * @param holder
     * @param productIds
     * @return
     * @throws Exception
     */
    protected List<Product> readProductsByIds(final ReindexStateHolder holder, final List<Long> productIds) throws Exception {
        if (productIds == null || productIds.isEmpty()) {
            return null;
        }
        return HibernateUtils.executeWithoutCache(new GenericOperation<List<Product>>() {
            @Override
            public List<Product> execute() throws Exception {
                return productDao.readProductsByIds(productIds);
            }
        });
    }

    protected List<IndexField> getIndexFields() {
        return indexFieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
    }

    /**
     * Builds an incremental or batch portion of the index.  Activities include reading {@link Locale}s and {@link IndexField}s, building a page,
     * adding the documents to the index, and incrementally committing.  It is not recommended that you override this method.  Rather, consider overriding
     * one of the other methods as this is largely responsible for orchestrating and delegating.
     *
     * @param productIds
     * @param products
     * @param holder
     * @param catalog
     * @param site
     * @throws Exception
     */
    protected void buildIncrementalIndex(
            final List<Long> productIds,
            final List<Product> products,
            final ReindexStateHolder holder,
            final Catalog catalog,
            final Site site) throws Exception {

        if (products != null && !products.isEmpty()) {
            List<Locale> locales = getAllLocales();
            List<IndexField> fields = getIndexFields();
            List<SolrInputDocument> docs = null;
            beforePage(productIds, products, locales, fields, holder);
            try {
                docs = buildPage(productIds, products, locales, fields, holder);
            } finally {
                afterPage(productIds, products, locales, fields, holder);
            }
            if (docs != null && !docs.isEmpty()) {
                addDocuments(null, docs);
                incrementalCommit(holder);
            }
        }
    }

    /**
     * Reads a batch of {@link Product} IDs from the DB.
     *
     * @param holder
     * @param catalogId
     * @param batchSize
     * @param lastId
     * @return
     * @throws Exception
     */
    protected List<Long> readIdBatch(
            final ReindexStateHolder holder,
            final Long catalogId,
            final int batchSize,
            final Long lastId
    ) throws Exception {
        Assert.isTrue(pageSize > 0, "Page size must be greater than 0.  Default is 100.");

        boolean readError = false;
        final List<Long> batch;

        TransactionStatus status = TransactionUtils.createTransaction("readProductIdBatchForReindexing",
                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        try {
            batch = HibernateUtils.executeWithoutCache(new GenericOperation<List<Long>>() {
                @Override
                public List<Long> execute() throws Exception {
                    return productDao.readAllActiveProductIds(lastId, batchSize);
                }
            });

        } catch (Exception e) {
            readError = true;
            throw new ServiceException("An error occured reading batches of product IDs.", e);
        } finally {
            TransactionUtils.finalizeTransaction(status, transactionManager, readError);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Read batch ids for catalog " + catalogId + ". Last ID was " + lastId + ". Number of IDs found was "
                    + batch.size() + '.');
        }

        return batch;
    }

    /**
     * Hook point that is executed at the beginning of the reindex process.  If you extend this class and implement this method, consider calling the super method.
     *
     * @param holder
     * @throws ServiceException
     */
    protected void beforeProcess(ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * Hook point at that is executed at the end of the reindex process. If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occured during the process.
     *
     * @param holder
     * @throws ServiceException
     */
    protected void afterProcess(ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * The reindex process, specifically page processing, is executed in background threads.  This executes prior to the main execution of the thread.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param catalog
     * @param site
     * @param sandBox
     * @throws ServiceException
     */
    protected void beforeBackgroundThread(ReindexStateHolder holder, Catalog catalog, Site site, SandBox sandBox) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This executes after to the main execution of the background (page) thread(s).
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param catalog
     * @param site
     * @param sandBox
     * @throws ServiceException
     */
    protected void afterBackgroundThread(ReindexStateHolder holder, Catalog catalog, Site site, SandBox sandBox) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This is executed immediately before a page is processed.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param productIds
     * @param products
     * @param locales
     * @param fields
     * @param holder
     * @throws ServiceException
     */
    protected void beforePage(
            List<Long> productIds,
            List<Product> products,
            List<Locale> locales,
            List<IndexField> fields,
            ReindexStateHolder holder
    ) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This executes immediately after a page is processed.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param productIds
     * @param products
     * @param locales
     * @param fields
     * @param holder
     * @throws ServiceException
     */
    protected void afterPage(
            List<Long> productIds,
            List<Product> products,
            List<Locale> locales,
            List<IndexField> fields,
            ReindexStateHolder holder
    ) throws ServiceException {
        HibernateUtils.clearDefaultEntityManager();
    }

    /**
     * This executes immediately prior to reading a batch of products.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param productIds
     * @throws ServiceException
     */
    protected void beforeReadProducts(ReindexStateHolder holder, List<Long> productIds) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This executes immediately after reading a batch of products.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param productIds
     * @throws ServiceException
     */
    protected void afterReadProducts(ReindexStateHolder holder, List<Long> productIds) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This executes immediately prior to reading a batch of product IDs for processing in a background thread.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param catalogId
     * @param batchSize
     * @param lastId
     * @throws ServiceException
     */
    protected void beforeReadIdBatch(
            final ReindexStateHolder holder,
            final Long catalogId,
            int batchSize,
            final Long lastId
    ) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This executes immediately after reading a batch of product IDs for processing in a background thread.
     * If you extend this class and implement this method, consider calling the super method.
     * Note that you can query the {@link ReindexStateHolder} to determine if an error has occurred during the process.
     *
     * @param holder
     * @param catalogId
     * @param batchSize
     * @param lastId
     * @throws ServiceException
     */
    protected void afterReadIdBatch(
            final ReindexStateHolder holder,
            final Long catalogId,
            int batchSize,
            final Long lastId
    ) throws ServiceException {
        //Nothing to do by default.
    }

    /**
     * This reads a {@link Catalog} by ID.  If the ID is null (default, unless in Multi-Tenant mode), this returns null.
     *
     * @param catalogId
     * @return
     */
    protected Catalog findCatalog(final Long catalogId) {
        if (catalogId == null) {
            return null;
        }
        return IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Catalog, RuntimeException>() {
            @Override
            public Catalog execute() throws RuntimeException {
                return siteService.findCatalogById(catalogId);
            }
        });
    }

    /**
     * This reads a {@link Site} by ID.  If the ID is null (default, unless in Multi-Tenant mode), this returns null.
     *
     * @param siteId
     * @return
     */
    protected Site findSite(final Long siteId) {
        if (siteId == null) {
            return null;
        }
        return IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Site, RuntimeException>() {
            @Override
            public Site execute() throws RuntimeException {
                return siteService.retrieveNonPersistentSiteById(siteId);
            }
        });
    }

    /**
     * The amount of time between commits during a full reindex.  Commits are global, so there's no need to have every thread committing every page.
     * <p>
     * Default is 30 seconds.
     *
     * @return
     */
    protected long getIncrementalCommitInterval() {
        return reindexCommitInterval;
    }

    /**
     * Issues incremental commits.  Commits in Sorl are global, so we don't need multiple threads issuing commits at the same time, or even back-to-back.  This uses a
     * commit interval to determine if a commit should be issued.
     *
     * @param holder
     * @throws Exception
     */
    protected synchronized void incrementalCommit(ReindexStateHolder holder) throws Exception {
        if (holder.isIncrementalCommits()) {
            long currentTime = System.currentTimeMillis();
            if (holder.getLastCommitted() < 0L || (currentTime - holder.getLastCommitted() > getIncrementalCommitInterval())) {
                commit(null, true, false, false);
                holder.setLastCommitted(currentTime);
            }
        }
    }

    /**
     * Method to determine whether a full reindex was successful.  By default, this simply returns whether the {@link ReindexStateHolder#isFailed()} returns false
     * and that at least 1 document was indexed.  Implementors may wish to
     * override this to determine whether the {@link ReindexStateHolder#getIndexableCount()} is a certain number,
     * or if {@link ReindexStateHolder#getUnindexedItemCount()} is at least a percentage of total items or of the items that were indexed, given
     * that the failure to build a single document / product does not stop the reindex process.
     *
     * @param holder
     * @return
     */
    protected boolean isReindexSuccessful(final ReindexStateHolder holder) {
        return (!holder.isFailed() && holder.getIndexableCount() > 0L);
    }

}
