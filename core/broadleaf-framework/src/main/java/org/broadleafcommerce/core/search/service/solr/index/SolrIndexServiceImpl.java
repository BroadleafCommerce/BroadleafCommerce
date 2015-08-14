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
import org.apache.solr.client.solrj.SolrServer;
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
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuActiveDatesService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuActiveDateConsiderationContext;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SolrIndexDao;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
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
import java.util.Arrays;
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

    @Override
    public void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException {
        try {
            CatalogStructure cache = new CatalogStructure();
            SolrIndexCachedOperation.setCache(cache);
            cacheOperation.execute();
        } finally {
            if (LOG.isInfoEnabled()) {
                LOG.info("Cleaning up Solr index cache from memory - size approx: " + getCacheSizeInMemoryApproximation(SolrIndexCachedOperation.getCache()) + " bytes");
            }
            SolrIndexCachedOperation.clearCache();
        }
    }

    protected int getCacheSizeInMemoryApproximation(CatalogStructure structure) {
        try {
            if (structure == null) {
                return 0;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(structure);
            IOUtils.closeQuietly(oos);
            int size = baos.size();
            IOUtils.closeQuietly(baos);
            return size;
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        }
    }

    @Override
    public boolean isReindexInProcess() {
        return false;
    }
    
    
    @Override
    public void rebuildIndex() throws ServiceException, IOException {
        rebuildIndex(new GlobalSolrFullReIndexOperation(this, shs, errorOnConcurrentReIndex) {

            @Override
            public List<? extends Indexable> readIndexables(int page, int pageSize) {
                return readAllActiveIndexables(page, pageSize);
            }

            @Override
            public Long countIndexables() {
                return countIndexableItems();
            }

            @Override
            public void buildPage(List<? extends Indexable> indexables) throws ServiceException {
                buildIncrementalIndex(indexables, getSolrServerForIndexing());
            }
        });
    }
    
    @Override
    public void rebuildIndex(final SolrIndexOperation operation) throws ServiceException, IOException {
        operation.obtainLock();
        
        try {
            LOG.info("Rebuilding the solr index...");
            StopWatch s = new StopWatch();
            
            operation.beforeIndexOperation();
            Object[] pack = saveState();
            try {
                // TODO: allow specification of types, loop through types I'm indexing
                final Long numItemsToIndex = operation.countIndexables();
            
                if (LOG.isDebugEnabled()) {
                    LOG.debug("There are at most " + numItemsToIndex + " items to index");
                }
                performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {
                    @Override
                    public void execute() throws ServiceException {
                        int page = 0;
                        while ((page * pageSize) < numItemsToIndex) {
                            buildIncrementalIndex(page, pageSize, operation);
                            page++;
                        }
                    }
                });

                operation.afterIndexOperation();
            } finally {
                restoreState(pack);
            }

            LOG.info(String.format("Finished building index in %s", s.toLapString()));
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
    public void deleteAllNamespaceDocuments(SolrServer server) throws ServiceException {
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
    public void deleteAllDocuments(SolrServer server) throws ServiceException {
        try {
            String deleteQuery = "*:*";
            LOG.debug("Deleting by query: " + deleteQuery);
            server.deleteByQuery(deleteQuery);
            server.commit();
        } catch (Exception e) {
            throw new ServiceException("Could not delete documents", e);
        }
    }

    protected void buildIncrementalIndex(int page, int pageSize, SolrIndexOperation operation) throws ServiceException {
        TransactionStatus status = TransactionUtils.createTransaction("readItemsToIndex",
            TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        if (SolrIndexCachedOperation.getCache() == null) {
            LOG.warn("Consider using SolrIndexService.performCachedOperation() in combination with " +
                    "SolrIndexService.buildIncrementalIndex() for better caching performance during solr indexing");
        }
        
        try {
            
            List<? extends Indexable> indexables = operation.readIndexables(page, pageSize);
            operation.buildPage(indexables);
            
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw e;
        }

    }
    
    @Override
    public Collection<SolrInputDocument> buildIncrementalIndex(List<? extends Indexable> indexables, SolrServer solrServer) throws ServiceException {
        TransactionStatus status = TransactionUtils.createTransaction("executeIncrementalProductIndex",
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

            List<Field> fields = null;
            FieldEntity currentFieldType = null;
            for (Indexable indexable : indexables) {
                if (fields == null || ObjectUtils.notEqual(currentFieldType, indexable.getFieldEntityType())) {
                    fields = fieldDao.readFieldsByEntityType(indexable.getFieldEntityType());
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
            extensionManager.getProxy().endBatchEvent();
        }
    }

    protected List<? extends Indexable> readAllActiveIndexables(int page, int pageSize) {
        if (useSku) {
            List<Sku> skus = skuDao.readAllActiveSkus(page, pageSize);
            ArrayList<Sku> skusToIndex = new ArrayList<Sku>();

            if (skus != null && !skus.isEmpty()) {
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
        } else {
            return productDao.readAllActiveProducts(page, pageSize);
        }
    }

    @Override
    public List<Locale> getAllLocales() {
        return localeService.findAllLocales();
    }
    
    @Override
    public SolrInputDocument buildDocument(final Indexable indexable, List<Field> fields, List<Locale> locales) {
        final SolrInputDocument document = new SolrInputDocument();

        attachBasicDocumentFields(indexable, document);

        // Keep track of searchable fields added to the index.   We need to also add the search facets if 
        // they weren't already added as a searchable field.
        List<String> addedProperties = new ArrayList<String>();

        for (Field field : fields) {
            try {
                // Index the searchable fields
                if (field.getSearchable()) {
                    List<FieldType> searchableFieldTypes = shs.getSearchableFieldTypes(field);
                    for (FieldType sft : searchableFieldTypes) {
                        Map<String, Object> propertyValues = getPropertyValues(indexable, field, sft, locales);

                        // Build out the field for every prefix
                        for (Entry<String, Object> entry : propertyValues.entrySet()) {
                            String prefix = entry.getKey();
                            prefix = StringUtils.isBlank(prefix) ? prefix : prefix + "_";

                            String solrPropertyName = shs.getPropertyNameForFieldSearchable(field, sft, prefix);
                            Object value = entry.getValue();

                            document.addField(solrPropertyName, value);
                            addedProperties.add(solrPropertyName);
                        }
                    }
                }

                // Index the faceted field type as well
                FieldType facetType = field.getFacetFieldType();
                if (facetType != null) {
                    Map<String, Object> propertyValues = getPropertyValues(indexable, field, facetType, locales);

                    // Build out the field for every prefix
                    for (Entry<String, Object> entry : propertyValues.entrySet()) {
                        String prefix = entry.getKey();
                        prefix = StringUtils.isBlank(prefix) ? prefix : prefix + "_";

                        String solrFacetPropertyName = shs.getPropertyNameForFieldFacet(field, prefix);
                        Object value = entry.getValue();

                        if (!addedProperties.contains(solrFacetPropertyName)) {
                            document.addField(solrFacetPropertyName, value);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Could not get value for property[" + field.getQualifiedFieldName() + "] for product id["
                        + indexable.getId() + "]", e);
                throw ExceptionHelper.refineException(e);
            }
        }

        attachAdditionalDocumentFields(indexable, document);

        return document;
    }
    
    /**
     * Implementors can extend this and override this method to add additional fields to the Solr document.
     * 
     * @param sku
     * @param document
     */
    protected void attachAdditionalDocumentFields(Indexable indexable, SolrInputDocument document) {
        //Empty implementation. Placeholder for others to extend and add additional fields
    }

    protected void attachBasicDocumentFields(Indexable indexable, SolrInputDocument document) {
        boolean cacheOperationManaged = false;
        try {
            CatalogStructure cache = SolrIndexCachedOperation.getCache();
            if (cache != null) {
                cacheOperationManaged = true;
            } else {
                cache = new CatalogStructure();
                SolrIndexCachedOperation.setCache(cache);

                solrIndexDao.populateProductCatalogStructure(Arrays.asList(shs.getCurrentProductId(indexable)), SolrIndexCachedOperation.getCache());

            }
            // Add the namespace and ID fields for this product
            document.addField(shs.getNamespaceFieldName(), shs.getCurrentNamespace());
            document.addField(shs.getIdFieldName(), shs.getSolrDocumentId(document, indexable));
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
                    Long displayOrder = convertDisplayOrderToLong(cache, displayOrderKey);

                    if (document.getField(categorySortFieldName) == null) {
                        document.addField(categorySortFieldName, displayOrder);
                    }

                    // This is the entire tree of every category defined on the product
                    buildFullCategoryHierarchy(document, cache, categoryId, new HashSet<Long>());
                }
            }
        } finally {
            if (!cacheOperationManaged) {
                SolrIndexCachedOperation.clearCache();
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
    public void optimizeIndex(SolrServer server) throws ServiceException, IOException {
        shs.optimizeIndex(server);
    }

    @Override
    public void commit(SolrServer server) throws ServiceException, IOException {
        if (this.commit) {
            commit(server, this.softCommit, this.waitSearcher, this.waitFlush);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("The flag / property \"solr.index.commit\" is false. Not committing! Ensure autoCommit is configured.");
        }
    }

    @Override
    public void commit(SolrServer server, boolean softCommit, boolean waitSearcher, boolean waitFlush) throws ServiceException, IOException {
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
    private Long convertDisplayOrderToLong(CatalogStructure cache, String displayOrderKey) {
        BigDecimal displayOrder = cache.getDisplayOrdersByCategoryProduct().get(displayOrderKey);
        return displayOrder.multiply(BigDecimal.valueOf(1000000)).longValue();
    }
}
