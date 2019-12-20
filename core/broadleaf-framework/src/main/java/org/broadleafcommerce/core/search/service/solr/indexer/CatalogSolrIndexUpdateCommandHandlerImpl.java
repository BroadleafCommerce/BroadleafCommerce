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
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

@Service("blCatalogSolrUpdateCommandHandler")
public class CatalogSolrIndexUpdateCommandHandlerImpl extends AbstractSolrIndexUpdateCommandHandlerImpl implements DisposableBean {
    
    private static final Log LOG = LogFactory.getLog(CatalogSolrIndexUpdateCommandHandlerImpl.class);
    
    @Qualifier("blCatalogSolrConfiguration")
    @Autowired(required = false)
    protected SolrConfiguration solrConfiguration;
    
    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

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
    
    @Resource(name = "blCatalogSolrIndexUpdateServiceExtensionManager")
    protected CatalogSolrIndexUpdateServiceExtensionManager catalogSolrIndexUpdateServiceExtensionManager;
    
    @Value("${solr.index.product.pageSize:100}")
    protected int pageSize = 100;
    
    private final ThreadPoolTaskExecutor executor;
    
    public CatalogSolrIndexUpdateCommandHandlerImpl() {
        super("catalog");
        this.executor = createExecutor();
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
        final String reindexCollectionName = getSolrConfiguration().getReindexName();
        boolean error = false;
        ReindexStateHolder holder;
        synchronized (ReindexStateHolder.class) {
            holder = ReindexStateHolder.getInstance(reindexCollectionName, false, false);
            if (holder != null) {
                //We're in a bad state here.  This should not be created yet.
                throw new IllegalStateException("Tried to execute a full reindex, "
                        + "but it appears that someone already started one, or did not deregister one after completion for collection '" 
                        + getSolrConfiguration().getReindexName() 
                        + "'.");
            }
            
            //Now we create the state holder.
            holder = ReindexStateHolder.getInstance(reindexCollectionName, !solrConfiguration.isSingleCoreMode(), true);
        }
        
        try {
            catalogSolrIndexUpdateServiceExtensionManager.getProxy().preProcess(holder);
            
            deleteAllDocuments(getSolrConfiguration().getReindexName(), !solrConfiguration.isSingleCoreMode());
            populateIndex(holder, null, null);
            catalogSolrIndexUpdateServiceExtensionManager.getProxy().postProcess(holder);
        } catch (Exception e) {
            error = true;
            if (ServiceException.class.isAssignableFrom(e.getClass())) {
                throw (ServiceException)e;
            } else {
                throw new ServiceException("Error occured writing data to Solr.", e);
            }
        } finally {
            ReindexStateHolder.deregister(reindexCollectionName);
            finalizeChanges(reindexCollectionName, error);
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
    public SolrInputDocument buildDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales) {
        try {
            final SolrInputDocument document = new SolrInputDocument();
            catalogSolrIndexUpdateServiceExtensionManager.getProxy().preDocument(indexable, fields, locales, document);
            attachBasicDocumentFields(indexable, document);
            attachIndexableDocumentFields(document, indexable, fields, locales);
            attachAdditionalDocumentFields(indexable, document);
            extensionManager.getProxy().attachChildDocuments(indexable, document, fields, locales);
            catalogSolrIndexUpdateServiceExtensionManager.getProxy().postDocument(indexable, fields, locales, document);
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
            LOG.error(msg);
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
    
    protected void populateIndex(final ReindexStateHolder holder, final Catalog catalog, final Site site) throws ServiceException {
        try {
            final Semaphore sem = new Semaphore(0);
            try {
                final int threads = getThreadsForBackgroundExecution();
                Assert.isTrue(threads > 0, "getThreadsForBackgroundExecution() must return an integer that is greater than 0.");
                if (threads > 25) {
                    LOG.info("The number of threads used to reindex the catalog is " + threads + ". This may cause performance issues, depending on the environment.");
                }
                
                for (int i = 0; i < threads; i++) {
                    getTaskExecutor().execute(createBackgroundRunnable(holder, sem, catalog, site));
                }
                try {
                    //Begin filling the queue
                    if (catalog != null) {
                        IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Void, Exception>() {
                            @Override
                            public Void execute() throws Exception {
                                populateProductIdQueue(holder);
                                return null;
                            }
                        }, site, catalog);
                    } else {
                        IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Void, Exception>() {
                            @Override
                            public Void execute() throws Exception {
                                populateProductIdQueue(holder);
                                return null;
                            }
                        });
                    }
                    
                    holder.markQueueLoadCompleted();
                    sem.acquire(threads);
                    if (holder.isFailed()) {
                        if (holder.getFailure() != null) {
                            if (holder.getFailure() instanceof ServiceException) {
                                throw (ServiceException)holder.getFailure();
                            } else {
                                throw new ServiceException("An unexpected error occured reindexing solr for command group " + getCommandGroup() + ". Please check the logs.", holder.getFailure());
                            }
                        } else {
                            throw new ServiceException("An unknown error occured reindexing solr for command group " + getCommandGroup() + ". Please check the logs.");
                        }
                    } else {
                        //Log success...
                        
                    }
                    
                } catch (InterruptedException e) {
                    holder.failFast(e);
                    throw e;
                }
            } catch (Exception e) {
                holder.failFast(e);
                if (holder.getFailure() instanceof ServiceException) {
                    throw (ServiceException)holder.getFailure();
                } else {
                    throw new ServiceException("An unexpected error occured reindexing solr for command group " + getCommandGroup() + ". Please check the logs.", holder.getFailure());
                }
            }
        } finally {
            //Nothing...
        }
    }
    
    protected void deleteAllDocuments(String collection, boolean commit) throws ServiceException {
        try {
            getSolrConfiguration().getReindexServer().deleteByQuery(collection, "(*:*)");
            if (commit) {
                commit(collection, true);
            }
        } catch (Exception e) {
            throw new ServiceException("An error occured deleting the contents of background Solr index, " + getSolrConfiguration().getReindexName(), e);
        }
    }
    
    protected void finalizeChanges(final String collection, final boolean error) throws ServiceException {
        try {
            if (error) {
                rollback(collection);
            } else {
                commit(collection, true);
                if (!solrConfiguration.isSingleCoreMode()) {
                    shs.swapActiveCores(solrConfiguration);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("An error occured committing and swapping the Solr index, " + collection, e);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }
    
    protected ThreadPoolTaskExecutor getTaskExecutor() {
        return executor;
    }
    
    protected int getThreadsForBackgroundExecution() {
        return 25;
    }
    
    protected ThreadPoolTaskExecutor createExecutor() {
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
    protected EntityManagerAwareRunnable createBackgroundRunnable(final ReindexStateHolder holder, final Semaphore sem, final Catalog catalog, final Site site) {
        return new EntityManagerAwareRunnable(sem) {
            @Override
            protected void executeInternal() throws Exception {
                //It's expected that this is run in another thread, so this should create a brand new BroadleafRequestContext
                final BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
                try {
                    //Pass shared state from the ReindexStateHolder to the thread-bound BroadleafRequestContext.
                    brc.getAdditionalProperties().putAll(holder.getAdditionalState());
                    
                    performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {
                        @Override
                        public void execute() throws ServiceException {
                            try {
                                final List<Locale> locales = IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<List<Locale>, RuntimeException>() {
                                    @Override
                                    public List<Locale> execute() throws RuntimeException {
                                        return getAllLocales();
                                    }
                                });
                                
                                final List<IndexField> fields = IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<List<IndexField>, RuntimeException>() {
                                    @Override
                                    public List<IndexField> execute() throws RuntimeException {
                                        return getIndexFields();
                                    }
                                });
                                
                                while (!holder.isFailed()) {
                                    final List<Long> ids = holder.getIdQueue().poll(1000L, TimeUnit.MILLISECONDS);
                                    boolean finished;
                                    
                                    if (catalog != null) {
                                        finished = IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Boolean, Exception>() {
                                            @Override
                                            public Boolean execute() throws Exception {
                                                return getReindexBatchOperation(getEntityManager(), ids, locales, fields, holder, catalog, site).execute();
                                            }
                                        }, site, catalog);
                                    } else {
                                        finished = IdentityExecutionUtils.runOperationAndIgnoreIdentifier(new IdentityOperation<Boolean, Exception>() {
                                            @Override
                                            public Boolean execute() throws Exception {
                                                return getReindexBatchOperation(getEntityManager(), ids, locales, fields, holder, catalog, site).execute();
                                            }
                                        });
                                    }
                                    
                                    getEntityManager().clear();
                                    
                                    if (finished) {
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                holder.failFast(e);
                                return;
                            }
                        }
                    });
                } finally {
                    BroadleafRequestContext.setBroadleafRequestContext(null);
                }
            }

            @Override
            protected void registerError(Exception e) {
                holder.failFast(e);
            }
        };
    }
    
    protected List<SolrInputDocument> buildPage(List<Long> productIds, List<Locale> locales, List<IndexField> fields, ReindexStateHolder holder) throws Exception {
        ArrayList<SolrInputDocument> docs = new ArrayList<>();
        
        catalogSolrIndexUpdateServiceExtensionManager.getProxy().prePage(productIds, locales, fields, holder);
        
        TransactionStatus status = TransactionUtils.createTransaction("readItemsToIndex",
                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        if (SolrIndexCachedOperation.getCache() == null) {
            LOG.warn("Consider using SolrIndexService.performCachedOperation() in combination with " +
                    "SolrIndexService.buildIncrementalIndex() for better caching performance during solr indexing");
        }
        try {
            List<Product> products = readProductsByIds(productIds);
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
                    extensionManager.getProxy().endBatchEvent(products);
                    sandBoxHelper.ignoreCloneCache(false);
                }
            }
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            catalogSolrIndexUpdateServiceExtensionManager.getProxy().postPage(productIds, locales, fields, holder);
            return docs;
        } catch (Exception e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw e;
        }
    }
    
    protected List<Product> readProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return null;
        }
        return productDao.readProductsByIds(productIds);
    }
    
    protected List<IndexField> getIndexFields() {
        return indexFieldDao.readFieldsByEntityType(FieldEntity.PRODUCT);
    }
    
    protected GenericOperation<Boolean> getReindexBatchOperation(
            final EntityManager em, 
            final List<Long> ids, 
            final List<Locale> locales, 
            final List<IndexField> fields, 
            final ReindexStateHolder holder, 
            final Catalog catalog, 
            final Site site) {
        return new GenericOperation<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (ids != null) {
                    final List<SolrInputDocument> documents = HibernateUtils.executeWithoutCache(new GenericOperation<List<SolrInputDocument>>() {
                        @Override
                        public List<SolrInputDocument> execute() throws Exception {
                            return buildPage(ids, locales, fields, holder);
                        }
                        
                    }, em);
                    
                    if (! documents.isEmpty()) {
                        addDocuments(holder.getCollectionName(), documents);
                        
                        if (holder.isIncrementalCommits()) {
                            commit(holder.getCollectionName(), false);
                        }
                    }
                    //Go get some more.
                    return false;
                } else if (holder.isQueueLoadCompleted()) {
                    //We're done here.
                    return true;
                }
                
                //Keep trying
                return false;
            }
        };
    }
    
    protected void populateProductIdQueue(ReindexStateHolder holder) throws Exception {
        Assert.isTrue(pageSize > 0, "Page size must be greater than 0.  Default is 100.");
        
        Long lastId = null;
        
        //We're going to query for 10 pages at a time to stay ahead of the workers
        int batchSize = pageSize * 10;
        while (true) {
            boolean readError = false;
            final List<Long> batch;
            
            TransactionStatus status = TransactionUtils.createTransaction("readProductIdsForReindexing",
                    TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
            try {
                batch = productDao.readAllActiveProductIds(lastId, batchSize);
            } catch (Exception e) {
                readError = true;
                throw e;
            } finally {
                TransactionUtils.finalizeTransaction(status, transactionManager, readError);
            }
            
            if (batch == null || batch.isEmpty()) {
                //Nothing left to do so let's break.
                break;
            }
            
            //Now break the batch up into pages and add them to the queue.
            final ArrayList<Long> page = new ArrayList<>(pageSize);
            
            Iterator<Long> itr = batch.iterator();
            while (itr.hasNext()) {
                page.add(itr.next());
                if (page.size() == pageSize) {
                    final ArrayList<Long> queueEntry = new ArrayList<>(page);
                    while (!holder.getIdQueue().offer(queueEntry, 1000L, TimeUnit.MILLISECONDS)) {
                        if (holder.isFailed()) {
                            return;
                        }
                    }
                    
                    page.clear();
                }
            }
            
            if (!page.isEmpty()) {
                while (!holder.getIdQueue().offer(page, 1000L, TimeUnit.MILLISECONDS)) {
                    if (holder.isFailed()) {
                        return;
                    }
                }
            }
            
            if (batch.size() < batchSize) {
                break;
            }
            
            lastId = batch.get(batch.size() - 1);
        }
    }
    
}
