/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.StopWatch;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuActiveDatesService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuActiveDateConsiderationContext;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.dao.SolrIndexDao;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.SolrContext;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Resource;


/**
 * Responsible for building and rebuilding the Solr index
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 */
@Service("blSolrIndexService")
public class SolrIndexServiceImpl implements SolrIndexService {

    private static final Log LOG = LogFactory.getLog(SolrIndexServiceImpl.class);

    @Value("${solr.index.errorOnConcurrentReIndex}")
    protected boolean errorOnConcurrentReIndex = false;

    @Value("${solr.index.product.pageSize}")
    protected int pageSize;

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Value("${solr.index.commit}")
    protected boolean commit;

    @Value("${solr.index.softCommit}")
    protected boolean softCommit;

    @Value("${solr.index.waitSearcher}")
    protected boolean waitSearcher;

    @Value("${solr.index.waitFlush}")
    protected boolean waitFlush;

    @Resource(name = "blProductDao")
    protected ProductDao productDao;

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blFieldDao")
    protected FieldDao fieldDao;

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

    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;

    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;

    @Override
    public void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException {
        try {
            CatalogStructure cache = new CatalogStructure();
            SolrIndexCachedOperation.setCache(cache);
            cacheOperation.execute();
        } finally {
            SolrIndexCachedOperation.clearCache();
        }
    }

    @Override
    public void rebuildIndex() throws ServiceException, IOException {
        LOG.info("Rebuilding the entire Solr index...");
        StopWatch s = new StopWatch();

        try{
            preBuildIndex();
            buildIndex();
        } finally {
            postBuildIndex();
        }

        LOG.info(String.format("Finished building entire Solr index in %s", s.toLapString()));
    }

    @Override
    public void preBuildIndex() throws ServiceException {
        deleteAllNamespaceDocuments(SolrContext.getReindexServer());
    }

    @Override
    public void buildIndex() throws IOException, ServiceException {
        executeSolrIndexOperation(getReindexOperation());
    }

    @Override
    public void postBuildIndex() throws IOException, ServiceException {
        // this is required to be at the very very very end after rebuilding the whole index
        optimizeIndex(SolrContext.getReindexServer());
        // Swap the active and the reindex cores
        shs.swapActiveCores();
    }

    @Override
    public SolrIndexOperation getReindexOperation() {
        return new GlobalSolrFullReIndexOperation(this, shs, errorOnConcurrentReIndex) {

            @Override
            public List<? extends Indexable> readIndexables(int pageSize, Long lastId) {
                return readAllActiveIndexables(pageSize, lastId);
            }

            @Override
            public Long countIndexables() {
                return countIndexableItems();
            }

            @Override
            public void buildPage(List<? extends Indexable> indexables) throws ServiceException {
                buildIncrementalIndex(indexables, getSolrServerForIndexing());
            }
        };
    }

    @Override
    public void executeSolrIndexOperation(final SolrIndexOperation operation) throws ServiceException, IOException {
        operation.obtainLock();

        try {
            LOG.info("Executing Indexing operation");
            StopWatch s = new StopWatch();

            Object[] pack = saveState();
            try {
                final Long numItemsToIndex;
                try {
                    operation.beforeCountIndexables();

                    numItemsToIndex = operation.countIndexables();
                } finally {
                    operation.afterCountIndexables();
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("There are at most " + numItemsToIndex + " items to index");
                }
                performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {

                    @Override
                    public void execute() throws ServiceException {
                        int page = 0;
                        Long lastId = null;
                        while ((page * pageSize) < numItemsToIndex) {
                            LOG.info(String.format("Building page number %s", page));
                            lastId = buildIncrementalIndex(pageSize, lastId, operation);
                            page++;
                        }
                    }
                });

            } finally {
                restoreState(pack);
            }

            LOG.info(String.format("Indexing operation completed in %s", s.toLapString()));
        } finally {
            operation.releaseLock();
        }
    }

    /**
     * @return
     */
    protected Long countIndexableItems() {
        if (useSku) {
            return skuDao.readCountAllActiveSkus();
        } else {
            return productDao.readCountAllActiveProducts();
        }
    }

    /**
     * <p>
     * This method deletes all of the documents from {@link SolrContext#getReindexServer()}
     * 
     * @throws ServiceException if there was a problem removing the documents
     * @deprecated use {@link #deleteAllReindexCoreDocuments()} instead
     */
    @Deprecated
    protected void deleteAllDocuments() throws ServiceException {
        deleteAllReindexCoreDocuments();
    }
    
    /**
     * <p>
     * This method deletes all of the documents from {@link SolrContext#getReindexServer()}
     * 
     * @throws ServiceException if there was a problem removing the documents
     */
    protected void deleteAllReindexCoreDocuments() throws ServiceException {
        deleteAllNamespaceDocuments(SolrContext.getReindexServer());
    }

    @Override
    public void deleteAllNamespaceDocuments(SolrClient server) throws ServiceException {
        try {
            String deleteQuery = shs.getNamespaceFieldName() + ":(\"" + shs.getCurrentNamespace() + "\")";
            LOG.debug("Deleting by query: " + deleteQuery);
            server.deleteByQuery(deleteQuery);

            //Explicitly do a hard commit here since we just deleted the entire index
            server.commit();
        } catch (Exception e) {
            if (ServiceException.class.isAssignableFrom(e.getClass())) {
                throw (ServiceException) e;
            }
            throw new ServiceException("Could not delete documents", e);
        }
    }
    
    @Override
    public void deleteAllDocuments(SolrClient server) throws ServiceException {
        try {
            String deleteQuery = "*:*";
            LOG.debug("Deleting by query: " + deleteQuery);
            server.deleteByQuery(deleteQuery);
            server.commit();
        } catch (Exception e) {
            throw new ServiceException("Could not delete documents", e);
        }
    }

    protected Long buildIncrementalIndex(int pageSize, Long lastId, SolrIndexOperation operation) throws ServiceException {
        TransactionStatus status = TransactionUtils.createTransaction("readItemsToIndex",
            TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        if (SolrIndexCachedOperation.getCache() == null) {
            LOG.warn("Consider using SolrIndexService.performCachedOperation() in combination with " +
                    "SolrIndexService.buildIncrementalIndex() for better caching performance during solr indexing");
        }
        Long response = null;
        try {
            List<? extends Indexable> indexables;
            try {
                operation.beforeReadIndexables();
                indexables = operation.readIndexables(pageSize, lastId);
                if (CollectionUtils.isNotEmpty(indexables)) {
                    response = indexables.get(indexables.size()-1).getId();
                }
            } finally {
                operation.afterReadIndexables();
            }

            try {
                operation.beforeBuildPage();

                operation.buildPage(indexables);
            } finally {
                operation.afterBuildPage();
            }


            TransactionUtils.finalizeTransaction(status, transactionManager, false);
        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw e;
        }
        return response;
    }

    @Override
    public Collection<SolrInputDocument> buildIncrementalIndex(List<? extends Indexable> indexables, SolrClient solrServer) throws ServiceException {
        TransactionStatus status = TransactionUtils.createTransaction("executeIncrementalIndex",
                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        if (SolrIndexCachedOperation.getCache() == null) {
            LOG.warn("Consider using SolrIndexService.performCachedOperation() in combination with " +
                    "SolrIndexService.buildIncrementalIndex() for better caching performance during solr indexing");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Building incremental product index - pageSize: [%s]...", indexables.size()));
        }

        StopWatch s = new StopWatch();
        try {
            sandBoxHelper.ignoreCloneCache(true);
            extensionManager.getProxy().startBatchEvent(indexables);
            Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            List<Locale> locales = getAllLocales();

            List<Long> productIds = BLCCollectionUtils.collectList(indexables, new TypedTransformer<Long>() {
                @Override
                public Long transform(Object input) {
                    return shs.getCurrentProductId((Indexable) input);
                }
            });

            solrIndexDao.populateProductCatalogStructure(productIds, SolrIndexCachedOperation.getCache());

            List<IndexField> fields = null;
            FieldEntity currentFieldType = null;
            for (Indexable indexable : indexables) {
                if (fields == null || ObjectUtils.notEqual(currentFieldType, indexable.getFieldEntityType())) {
                    fields = indexFieldDao.readFieldsByEntityType(indexable.getFieldEntityType());
                }

                SolrInputDocument doc = buildDocument(indexable, fields, locales);
                //If someone overrides the buildDocument method and determines that they don't want a product 
                //indexed, then they can return null. If the document is null it does not get added to 
                //to the index.
                if (doc != null) {
                    documents.add(doc);
                }
            }

            extensionManager.getProxy().modifyBuiltDocuments(documents, indexables, fields, locales);

            logDocuments(documents);

            if (!CollectionUtils.isEmpty(documents) && solrServer != null) {
                solrServer.add(documents);
                commit(solrServer);
            }
            TransactionUtils.finalizeTransaction(status, transactionManager, false);

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Built incremental product index - pageSize: [%s] in [%s]", indexables.size(), s.toLapString()));
            }

            return documents;
        } catch (SolrServerException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new ServiceException("Could not rebuild index", e);
        } catch (IOException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new ServiceException("Could not rebuild index", e);
        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw e;
        } finally {
            extensionManager.getProxy().endBatchEvent(indexables);
            sandBoxHelper.ignoreCloneCache(false);
        }
    }

    protected List<? extends Indexable> readAllActiveIndexables(int pageSize, Long lastId) {
        if (useSku) {
            List<Sku> skus = skuDao.readAllActiveSkus(pageSize, lastId);
            return filterIndexableSkus(skus);
        } else {
            return productDao.readAllActiveProducts(pageSize, lastId);
        }
    }

    @Override
    public List<Sku> filterIndexableSkus(List<Sku> skus) {
        ArrayList<Sku> skusToIndex = new ArrayList<Sku>();

        if (CollectionUtils.isNotEmpty(skus)) {
            for (Sku sku : skus) {
                //If the sku is not active, don't index it...
                if (!sku.isActive()) {
                    continue;
                }

                //If this is the default sku and the product has product options
                //and is not allowed to be sold without product options
                if (sku.getDefaultProduct() != null
                        && !sku.getProduct().getCanSellWithoutOptions()
                        && !sku.getProduct().getAdditionalSkus().isEmpty()) {
                    continue;
                }
                
                if (sku.getDefaultProduct() instanceof ProductBundle) {
                    continue;
                }

                skusToIndex.add(sku);
            }
        }
        return skusToIndex;
    }

    @Override
    public List<Locale> getAllLocales() {
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
    
    @Override
    public SolrInputDocument buildDocument(final Indexable indexable, List<IndexField> fields, List<Locale> locales) {
        final SolrInputDocument document = new SolrInputDocument();

        attachBasicDocumentFields(indexable, document);

        attachIndexableDocumentFields(document, indexable, fields, locales);

        attachAdditionalDocumentFields(indexable, document);

        return document;
    }

    @Override
    public void attachIndexableDocumentFields(SolrInputDocument document, Indexable indexable, List<IndexField> fields, List<Locale> locales) {
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

    /**
     * Implementors can extend this and override this method to add additional fields to the Solr document.
     * 
     * @param sku
     * @param document
     */
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
        document.addField(shs.getNamespaceFieldName(), shs.getCurrentNamespace());
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

                String categorySortFieldName = shs.getCategorySortFieldName(shs.getCategoryId(categoryId));
                String displayOrderKey = categoryId + "-" + cacheKey;
                if (cache.getDisplayOrdersByCategoryProduct().containsKey(displayOrderKey)) {
                    Long displayOrder = convertDisplayOrderToLong(cache, displayOrderKey);

                    if (document.getField(categorySortFieldName) == null && displayOrder != null) {
                        document.addField(categorySortFieldName, displayOrder);
                    }
                }

                // This is the entire tree of every category defined on the product
                buildFullCategoryHierarchy(document, cache, categoryId, new HashSet<Long>());
            }
        }
    }

    /**
     * Walk the category hierarchy upwards, adding a field for each level to the solr document
     *
     * @param document the solr document for the product
     * @param cache the catalog structure cache
     * @param categoryId the current category id
     */
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

    /**
     * Returns a map of prefix to value for the requested attributes. For example, if the requested field corresponds to
     * a Sku's description and the locales list has the en_US locale and the es_ES locale, the resulting map could be
     * 
     * { "en_US" : "A description",
     *   "es_ES" : "Una descripcion" }
     * 
     * @param product
     * @param sku
     * @param field
     * @param fieldType
     * @param locales
     * @return the value of the property
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected Map<String, Object> getPropertyValues(Indexable indexedItem, Field field, FieldType fieldType, List<Locale> locales)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String propertyName = field.getPropertyName();
        Map<String, Object> values = new HashMap<String, Object>();

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
     * Converts a propertyName to one that is able to reference inside a map. For example, consider the property
     * in Product that references a List<ProductAttribute>, "productAttributes". Also consider the utility method
     * in Product called "mappedProductAttributes", which returns a map of the ProductAttributes keyed by the name
     * property in the ProductAttribute. Given the parameters "productAttributes.heatRange", "productAttributes", 
     * "mappedProductAttributes" (which would represent a property called "productAttributes.heatRange" that 
     * references a specific ProductAttribute inside of a product whose "name" property is equal to "heatRange", 
     * this method will convert this property to mappedProductAttributes(heatRange).value, which is then usable 
     * by the standard beanutils PropertyUtils class to get the value.
     * 
     * @param propertyName
     * @param listPropertyName
     * @param mapPropertyName
     * @return the converted property name
     * 
     * @deprecated see SolrHelperService.getPropertyValue()
     */
    @Deprecated
    protected String convertToMappedProperty(String propertyName, String listPropertyName, String mapPropertyName) {
        String[] splitName = StringUtils.split(propertyName, "\\.");
        StringBuilder convertedProperty = new StringBuilder();
        for (int i = 0; i < splitName.length; i++) {
            if (convertedProperty.length() > 0) {
                convertedProperty.append(".");
            }

            if (splitName[i].equals(listPropertyName)) {
                convertedProperty.append(mapPropertyName).append("(");
                convertedProperty.append(splitName[i + 1]).append(").value");
                i++;
            } else {
                convertedProperty.append(splitName[i]);
            }
        }
        return convertedProperty.toString();
    }

    @Override
    public Object[] saveState() {
         return new Object[] {
             BroadleafRequestContext.getBroadleafRequestContext(),
             SkuPricingConsiderationContext.getSkuPricingConsiderationContext(),
             SkuPricingConsiderationContext.getSkuPricingService(),
             SkuActiveDateConsiderationContext.getSkuActiveDatesService()
         };
     }
         
    @Override
    @SuppressWarnings("rawtypes")
    public void restoreState(Object[] pack) {
         BroadleafRequestContext.setBroadleafRequestContext((BroadleafRequestContext) pack[0]);
         SkuPricingConsiderationContext.setSkuPricingConsiderationContext((HashMap) pack[1]);
         SkuPricingConsiderationContext.setSkuPricingService((DynamicSkuPricingService) pack[2]);
         SkuActiveDateConsiderationContext.setSkuActiveDatesService((DynamicSkuActiveDatesService) pack[3]);
     }
     
    @Override
    public void optimizeIndex(SolrClient server) throws ServiceException, IOException {
        shs.optimizeIndex(server);
    }

    @Override
    public void commit(SolrClient server) throws ServiceException, IOException {
        if (this.commit) {
            commit(server, this.softCommit, this.waitSearcher, this.waitFlush);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("The flag / property \"solr.index.commit\" is false. Not committing! Ensure autoCommit is configured.");
        }
    }

    @Override
    public void commit(SolrClient server, boolean softCommit, boolean waitSearcher, boolean waitFlush) throws ServiceException, IOException {
        try {
            if (!this.commit) {
                LOG.warn("The flag / property \"solr.index.commit\" is set to false but a commit is being forced via the API.");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Committing changes to Solr index: softCommit: " + softCommit
                        + ", waitSearcher: " + waitSearcher + ", waitFlush: " + waitFlush);
            }

            server.commit(waitFlush, waitSearcher, softCommit);
        } catch (SolrServerException e) {
            throw new ServiceException("Could not commit changes to Solr index", e);
        }
    }

    @Override
    public void logDocuments(Collection<SolrInputDocument> documents) {
        if (LOG.isTraceEnabled()) {
            for (SolrInputDocument document : documents) {
                LOG.trace(document);
            }
        }
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

    @Override
    public void deleteByQuery(String deleteQuery) throws SolrServerException, IOException {
        String docType = (useSku) ? FieldEntity.SKU.getType() : FieldEntity.PRODUCT.getType();
        String childDeleteQuery = "{!child of=" + shs.getTypeFieldName() + ":" + docType + "} " + deleteQuery;
        SolrContext.getServer().deleteByQuery(childDeleteQuery);
        SolrContext.getServer().deleteByQuery(deleteQuery);

        logDeleteQuery(childDeleteQuery);
        logDeleteQuery(deleteQuery);
    }

    @Override
    public void addDocuments(Collection<SolrInputDocument> documents) throws IOException, SolrServerException {
        SolrContext.getServer().add(documents);
        logDocuments(documents);
    }

    @Override
    public void logDeleteQuery(String deleteQuery) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delete query: " + deleteQuery);
        }
    }

}
