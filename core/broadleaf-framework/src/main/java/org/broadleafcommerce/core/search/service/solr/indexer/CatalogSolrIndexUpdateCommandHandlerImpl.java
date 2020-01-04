/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
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
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

/**
 * Default command handler to handle Catalog Solr index commands. This is multi-threaded.  SKU browsing is not supported with this implementation.
 * 
 * @author Kelly Tisdell
 *
 */
@Service("blCatalogSolrUpdateCommandHandler")
public class CatalogSolrIndexUpdateCommandHandlerImpl extends AbstractSolrIndexUpdateCommandHandlerImpl implements CatalogSolrIndexCommandHandler, DisposableBean {
    
    private static final Log LOG = LogFactory.getLog(CatalogSolrIndexUpdateCommandHandlerImpl.class);
    
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
    
    @Value("${solr.index.product.pageSize:100}")
    protected int pageSize = 100;
    
    private final ThreadPoolTaskExecutor backgroundOperationExecutor;
    
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
        
        Lock lock = queueProvider.createOrRetrieveCommandLock(getCommandGroup() + SolrIndexQueueProvider.COMMAND_LOCK_NAME);
        //Should be able to immediately obtain the command lock because the current thread should already hold it if it's delegating to this method.
        //Locking again is more of a safeguard.
        if (lock.tryLock()) {
            try {
                if (command instanceof IncrementalUpdateCommand) {
                    executeCommandInternal((IncrementalUpdateCommand)command);
                } else if (command instanceof FullReindexCommand) {
                    executeFullReindexCommand((FullReindexCommand)command);
                } else if (command instanceof CatalogReindexCommand) {
                    executeCatalogReindexCommand((CatalogReindexCommand)command);
                } else if (command instanceof SiteReindexCommand) {
                    executeSiteReindexCommand((SiteReindexCommand)command);
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
                        + getBackgroundCollectionName() 
                        + "'.");
            }
            
            //Now we create the state holder.
            holder = ReindexStateHolder.getInstance(reindexCollectionName, !solrConfiguration.isSingleCoreMode(), true);
        }
        
        try {
            beforeProcess(holder);
            deleteAllDocuments(getBackgroundCollectionName(), !solrConfiguration.isSingleCoreMode());
            populateIndex(holder, null, null, null);
        } catch (Exception e) {
            holder.failFast(e);
            throw new ServiceException("Error occured writing data to Solr.", e);
        } finally {
            try {
                try {
                    afterProcess(holder);
                } finally {
                    try {
                        finalizeChanges(reindexCollectionName, holder.isFailed(), true);
                    } finally {
                        ReindexStateHolder.deregister(reindexCollectionName);
                    }
                }
            } finally {
                final long endTime = System.currentTimeMillis();
                LOG.info("Full Catalog Reindex Process completed in " + (endTime - startTime) + " milliseconds, or " + ((endTime - startTime) / 60000L) + " minutes.");
            }
        }
    }
    
    protected void executeCatalogReindexCommand(CatalogReindexCommand command) throws ServiceException {
        throw new UnsupportedOperationException("By default, the " + command.getClass().getName() + " is not supported. Consider overriding this method or installing Broadleaf's Multi-Tenant module.");
    }
    
    protected void executeSiteReindexCommand(SiteReindexCommand command) throws ServiceException {
        throw new UnsupportedOperationException("By default, the " + command.getClass().getName() + " is not supported. Consider overriding this method or installing Broadleaf's Multi-Tenant module.");
    }

    @Override
    public SolrInputDocument buildDocument(Indexable indexable) {
        return buildDocument(indexable, indexFieldDao.readFieldsByEntityType(indexable.getFieldEntityType()), getAllLocales());
    }
    
    @Override
    public SolrInputDocument buildDocument(final Indexable indexable, final List<IndexField> fields, final List<Locale> locales) {
        try {
            final SolrInputDocument document = new SolrInputDocument();
            attachBasicDocumentFields(indexable, document);
            attachIndexableDocumentFields(document, indexable, fields, locales);
            attachAdditionalDocumentFields(indexable, document);
            extensionManager.getProxy().attachChildDocuments(indexable, document, fields, locales);
            return document;
        } catch (Exception e) {
            LOG.warn("An error occured trying to build a SolrInputDocument for Indexable of type, " + indexable.getClass().getName() + " with an ID of " + indexable.getId(), e);
            return null;
        }
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
    
    protected void attachIndexableDocumentFields(SolrInputDocument document, Indexable indexable, List<IndexField> fields, List<Locale> locales) {
        for (IndexField indexField : fields) {
            try {
                // If we find an IndexField entry for this field, then we need to store it in the index
                if (indexField != null) {
                    List<IndexFieldType> searchableFieldTypes = indexField.getFieldTypes();

                    // For each of its search field types, get the property values, and add a field to the document for each property value
                    for (IndexFieldType sft : searchableFieldTypes) {
                        FieldType fieldType = sft.getFieldType();
                        Map<String, Object> propertyValues = getPropertyValues(indexable, indexField.getField(), fieldType, locales);

                        ExtensionResultStatusType result = extensionManager.getProxy().populateDocumentForIndexField(document, indexField, fieldType, propertyValues);

                        if (ExtensionResultStatusType.NOT_HANDLED.equals(result)) {
                            // Build out the field for every prefix
                            for (Entry<String, Object> entry : propertyValues.entrySet()) {
                                String prefix = entry.getKey();
                                prefix = StringUtils.isBlank(prefix) ? prefix : prefix + "_";

                                String solrPropertyName = shs.getPropertyNameForIndexField(indexField, fieldType, prefix);
                                Object value = entry.getValue();

                                if (FieldType.isMultiValued(fieldType)) {
                                    document.addField(solrPropertyName, value);
                                } else {
                                    document.setField(solrPropertyName, value);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                LOG.error("Could not get value for property[" + indexField.getField().getQualifiedFieldName() + "] for product id["
                        + indexable.getId() + "]", e);
                throw ExceptionHelper.refineException(e);
            }
        }
    }
    
    protected void attachAdditionalDocumentFields(Indexable indexable, SolrInputDocument document) {
        //Empty implementation. Placeholder for others to extend and add additional fields
        extensionManager.getProxy().attachAdditionalDocumentFields(indexable, document);
    }
    
    protected void attachBasicDocumentFields(Indexable indexable, SolrInputDocument document) {
        CatalogStructure cache = SolrIndexCachedOperation.getCache();
        if (cache == null) {
            String msg = "SolrIndexService.performCachedOperation() must be used in conjuction with"
                + " solrIndexDao.populateProductCatalogStructure() in order to correctly build catalog documents or should"
                + " be invoked from buildIncrementalIndex()";
            throw new IllegalStateException(msg);
        }

        // Add the namespace and ID fields for this product
        document.addField(shs.getNamespaceFieldName(), solrConfiguration.getNamespace());
        document.addField(shs.getIdFieldName(), shs.getSolrDocumentId(document, indexable));
        document.addField(shs.getTypeFieldName(), shs.getDocumentType(indexable));
        document.addField(shs.getIndexableIdFieldName(), shs.getIndexableId(indexable));

        extensionManager.getProxy().attachAdditionalBasicFields(indexable, document, shs);

        Long cacheKey = this.shs.getCurrentProductId(indexable); // current
        if (!cache.getParentCategoriesByProduct().containsKey(cacheKey)) {
            cacheKey = sandBoxHelper.getOriginalId(cacheKey); // parent
            if (!cache.getParentCategoriesByProduct().containsKey(cacheKey)) {
                cacheKey = shs.getIndexableId(indexable); // master
            }
        }

        // TODO: figure this out more generally; this doesn't work for CMS content
        // The explicit categories are the ones defined by the product itself
        if (cache.getParentCategoriesByProduct().containsKey(cacheKey)) {
            for (Long categoryId : cache.getParentCategoriesByProduct().get(cacheKey)) {
                document.addField(shs.getExplicitCategoryFieldName(), shs.getCategoryId(categoryId));

                // Make sure that we're always referencing the parent for the sort field
                String categorySortFieldName = shs.getCategorySortFieldName(shs.getCategoryId(categoryId));
                // The issue here was the super category id is always what is stored in the cache, while the category
                // by product id is the overridden versions. Need to always look at parent version for cache stuff, which
                // is given from shs.getCategoryId
                // First try the current level
                String displayOrderKey = categoryId + "-" + cacheKey;
                Long displayOrder = convertDisplayOrderToLong(cache, displayOrderKey);
                if (displayOrder == null) {
                    // Didn't find the cache at the current level, this might be an override so look upwards
                    displayOrderKey = shs.getCategoryId(categoryId) + "-" + cacheKey;
                    displayOrder = convertDisplayOrderToLong(cache, displayOrderKey);
                }
                
                if (document.getField(categorySortFieldName) == null && displayOrder != null) {
                    document.addField(categorySortFieldName, displayOrder);
                }

                // This is the entire tree of every category defined on the product
                buildFullCategoryHierarchy(document, cache, categoryId, new HashSet<Long>());
            }
        }
    }
    
    protected void buildFullCategoryHierarchy(SolrInputDocument document, CatalogStructure cache, Long categoryId, Set<Long> indexedParents) {
        Long catIdToAdd = shs.getCategoryId(categoryId); 

        Collection<Object> existingValues = document.getFieldValues(shs.getCategoryFieldName());
        if (existingValues == null || !existingValues.contains(catIdToAdd)) {
            document.addField(shs.getCategoryFieldName(), catIdToAdd);
        }

        Set<Long> parents = cache.getParentCategoriesByCategory().get(categoryId);
        for (Long parent : parents) {
            if (!indexedParents.contains(parent)) {
                indexedParents.add(parent);
                buildFullCategoryHierarchy(document, cache, parent, indexedParents);
            }
        }
    }
    
    protected Map<String, Object> getPropertyValues(Indexable indexedItem, Field field, FieldType fieldType, List<Locale> locales)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String propertyName = field.getPropertyName();
        Map<String, Object> values = new HashMap<>();

        ExtensionResultStatusType extensionResult = ExtensionResultStatusType.NOT_HANDLED;
        if (extensionManager != null) {
            extensionResult = extensionManager.getProxy().addPropertyValues(indexedItem, field, fieldType, values, propertyName, locales);
        }
        
        if (ExtensionResultStatusType.NOT_HANDLED.equals(extensionResult)) {
            Object propertyValue = shs.getPropertyValue(indexedItem, field);
            if (propertyValue != null) {
                values.put("", propertyValue);
            }
        }

        return values;
    }
    
    /**
     *  We multiply the BigDecimal by 1,000,000 to maintain any possible decimals in use the
     *  displayOrder value.
     *
     * @param cache
     * @param displayOrderKey
     * @return
     */
    protected Long convertDisplayOrderToLong(CatalogStructure cache, String displayOrderKey) {
        BigDecimal displayOrder = cache.getDisplayOrdersByCategoryProduct().get(displayOrderKey);

        if (displayOrder == null) {
            return null;
        }

        return displayOrder.multiply(BigDecimal.valueOf(1000000)).longValue();
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
    
    protected void populateIndex(final ReindexStateHolder holder, final Long catalogId, final Long siteId, final SandBox sandbox) throws ServiceException {
        try {
            final Semaphore sem = new Semaphore(0);
            try {
                final Catalog catalog = findCatalog(catalogId);
                final Site site = findSite(siteId);
                final BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
                brc.setSandBox(sandbox);
                brc.setCurrentCatalog(catalog);
                brc.setNonPersistentSite(site);
                
                int leases = 0;
                
                final int batchSize = pageSize * 10;
                final AtomicReference<Long> lastId = new AtomicReference<>();
                while (true) {
                    if (holder.isFailed()) {
                        break;
                    }
                    final List<Long> batch;
                    if (catalog != null) {
                        batch = IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<List<Long>, Exception>() {
                            @Override
                            public List<Long> execute() throws Exception {
                                return readIdBatch(holder, catalogId, batchSize, lastId.get());
                            }
                        }, site, catalog);
                    } else {
                        batch = IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<List<Long>, Exception>() {
                            @Override
                            public List<Long> execute() throws Exception {
                                return readIdBatch(holder, catalogId, batchSize, lastId.get());
                            }
                        });
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
                            getBackgroundOperationExecutor().execute(createBackgroundRunnable(holder, page, sem, catalogId, siteId, sandbox));
                            leases++;
                            pageHolder = new ArrayList<>(pageSize);
                        }
                    }
                    
                    if (!pageHolder.isEmpty()) {
                        getBackgroundOperationExecutor().execute(createBackgroundRunnable(holder, pageHolder, sem, catalogId, siteId, sandbox));
                        leases++;
                    }
                    
                    if (batch.size() < batchSize) {
                        break;
                    }
                    
                    lastId.set(batch.get(batch.size() - 1));
                    
                }
                
                sem.acquire(leases);
                
            } catch (Exception e) {
                holder.failFast(e);
                throw new ServiceException("An unexpected error occured reindexing solr for command group " + getCommandGroup() + ". Please check the logs.", holder.getFailure());
            }
        } finally {
            //Don't reset the BroadleafRequestContext here because we did not create / bind it.
        }
    }
    
    protected void deleteAllDocuments(String collection, boolean commit) throws ServiceException {
        try {
            getSolrConfiguration().getReindexServer().deleteByQuery(collection, "(*:*)");
            if (commit) {
                commit(collection, true, true, false);
            }
        } catch (Exception e) {
            throw new ServiceException("An error occured deleting the contents of background Solr index, " + collection, e);
        }
    }
    
    protected void finalizeChanges(final String collection, final boolean error, boolean swap) throws ServiceException {
        try {
            if (error) {
                rollback(collection);
            } else {
                commit(collection, true, true, false);
                if (swap) {
                    swapCollections();
                }
            }
        } catch (Exception e) {
            if (error) {
                throw new ServiceException("An error rolling back the Solr index, " + collection, e);
            } else if (swap){
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

    @Override
    public void destroy() throws Exception {
        if (backgroundOperationExecutor != null) {
            backgroundOperationExecutor.shutdown();
        }
    }
    
    protected ThreadPoolTaskExecutor getBackgroundOperationExecutor() {
        return backgroundOperationExecutor;
    }
    
    protected int getThreadsForBackgroundExecution() {
        return 10;
    }
    
    protected ThreadPoolTaskExecutor createBackgroundOperationExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setThreadGroupName(getCommandGroup() + "-solr-reindex-workers");
        exec.setThreadNamePrefix(getCommandGroup() + "-solr-reindex-worker-");
        exec.setCorePoolSize(getThreadsForBackgroundExecution());
        exec.setMaxPoolSize(getThreadsForBackgroundExecution());
        exec.initialize();
        return exec;
    }
    
    /**
     * This is where most of the heavy lifting happens.
     * 
     * @param collectionName
     * @param holder
     * @param sem
     * @param incrementalCommits
     * @return
     */
    protected EntityManagerAwareRunnable createBackgroundRunnable(final ReindexStateHolder holder, final List<Long> ids, final Semaphore sem, final Long catalogId, final Long siteId, final SandBox sandBox) {
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
                brc.setCurrentCatalog(catalog);
                brc.setNonPersistentSite(site);
                
                beforeBackgroundThread(holder, catalog, site, sandBox);
                
                try {
                    //Pass shared state from the ReindexStateHolder to the thread-bound BroadleafRequestContext.
                    brc.getAdditionalProperties().putAll(holder.getAdditionalState());
                    
                    if (catalog != null) {
                        IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Void, Exception>() {
                            @Override
                            public Void execute() throws Exception {
                                performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {

                                    @Override
                                    public void execute() throws ServiceException {
                                        try {
                                            List<Product> products = readProductsByIds(holder, ids);
                                            buildIncrementalIndex(ids, products, holder, catalog, site);
                                            if (LOG.isInfoEnabled()) {
                                                LOG.info("Processed " + products.size() + " products for catalog " + catalog.getName() + " with ID of " + catalog.getId() 
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
                        }, site, catalog);
                    } else {
                        IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Void, Exception>() {
                            @Override
                            public Void execute() throws Exception {
                                performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {

                                    @Override
                                    public void execute() throws ServiceException {
                                        try {
                                            List<Product> products = readProductsByIds(holder, ids);
                                            buildIncrementalIndex(ids, products, holder, catalog, site);
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Processed " + products.size() + " products.");
                                            }
                                        } catch (Exception e) {
                                            holder.failFast(e);
                                            return;
                                        }
                                    }
                                    
                                });
                                
                                return null;
                            }
                        });
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
    
    protected List<SolrInputDocument> buildPage(List<Long> productIds, List<Product> products, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) throws Exception {
        final List<SolrInputDocument> docs = new ArrayList<>();
        
        try {
            beforePage(productIds, products, locales, fields, holder);
            if (products != null && !products.isEmpty()) {
                sandBoxHelper.ignoreCloneCache(true);
                try {
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
        } finally {
            afterPage(productIds, products, locales, fields, holder);
        }
    }
    
    protected List<Product> readProductsByIds(final ReindexStateHolder holder, final List<Long> productIds) throws Exception {
        try {
            beforeReadProducts(holder, productIds);
            if (productIds == null || productIds.isEmpty()) {
                return null;
            }
            return HibernateUtils.executeWithoutCache(new GenericOperation<List<Product>>() {
                @Override
                public List<Product> execute() throws Exception {
                    return productDao.readProductsByIds(productIds);
                }
            });
        } finally {
            afterReadProducts(holder, productIds);
        }
    }
    
    protected List<IndexField> getIndexFields() {
        return indexFieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
    }
    
    protected void buildIncrementalIndex(
            final List<Long> productIds,
            final List<Product> products,
            final ReindexStateHolder holder, 
            final Catalog catalog, 
            final Site site) throws Exception {
        
        if (products != null && ! products.isEmpty()) {
            List<Locale> locales = getAllLocales();
            List<IndexField> fields = getIndexFields();
            List<SolrInputDocument> docs = buildPage(productIds, products, locales, fields, holder);
            if (docs != null && ! docs.isEmpty()) {
                addDocuments(holder.getCollectionName(), docs);
                incrementalCommit(holder);
            }
        }
    }
    
    protected List<Long> readIdBatch(final ReindexStateHolder holder, final Long catalogId, int batchSize, final Long lastId) throws Exception {
        Assert.isTrue(pageSize > 0, "Page size must be greater than 0.  Default is 100.");
        
        boolean readError = false;
        final List<Long> batch;
        
        try {
            beforeReadIdBatch(holder, catalogId, batchSize, lastId);
            
            TransactionStatus status = TransactionUtils.createTransaction("readProductIdBatchForReindexing",
                    TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
            try {
                batch = productDao.readAllActiveProductIds(lastId, batchSize);
            } catch (Exception e) {
                readError = true;
                throw new ServiceException("An error occured reading batches of product IDs.", e);
            } finally {
                TransactionUtils.finalizeTransaction(status, transactionManager, readError);
            }
            
            LOG.info("Read batch ids for catalog " + catalogId + ". Last ID was " + lastId + ". Number of IDs found was " + batch.size());
        } finally {
            afterReadIdBatch(holder, catalogId, batchSize, lastId);
        }
        
        return batch;
    }
    
    protected void beforeProcess(ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void afterProcess(ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void beforeBackgroundThread(ReindexStateHolder holder, Catalog catalog, Site site, SandBox sandBox) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void afterBackgroundThread(ReindexStateHolder holder, Catalog catalog, Site site, SandBox sandBox) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void beforePage(List<Long> productIds, List<Product> products, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void afterPage(List<Long> productIds, List<Product> products, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void beforeReadProducts(ReindexStateHolder holder, List<Long> productIds) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void afterReadProducts(ReindexStateHolder holder, List<Long> productIds) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void beforeReadIdBatch(final ReindexStateHolder holder, final Long catalogId, int batchSize, final Long lastId) throws ServiceException {
        //Nothing to do by default.
    }
    
    protected void afterReadIdBatch(final ReindexStateHolder holder, final Long catalogId, int batchSize, final Long lastId) throws ServiceException {
        //Nothing to do by default.
    }
    
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

    protected Site findSite(final Long siteId) {
        if (siteId == null) {
            return null;
        }
        return IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Site, RuntimeException>() {
            @Override
            public Site execute() throws RuntimeException {
                return siteService.retrievePersistentSiteById(siteId);
            }
        });
    }
    
    /**
     * The amount of time between commits during a full reindex.  Commits are global, so there's no need to have every thread committing every page.
     * 
     * Default is 30 seconds.
     * 
     * @return
     */
    protected long getIncrementalCommitInterval() {
        return 30000L;
    }
    
    protected synchronized void incrementalCommit(ReindexStateHolder holder) throws Exception {
        if (holder.isIncrementalCommits()) {
            long currentTime = System.currentTimeMillis();
            if (holder.getLastCommitted() < 0L || (currentTime - holder.getLastCommitted() > getIncrementalCommitInterval())) {
                commit(holder.getCollectionName(), true, false, false);
                holder.setLastCommitted(currentTime);
            }
        }
    }
}
