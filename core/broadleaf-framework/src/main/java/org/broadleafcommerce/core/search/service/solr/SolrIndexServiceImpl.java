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
package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.StopWatch;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuActiveDatesService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuActiveDateConsiderationContext;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
 */
@Service("blSolrIndexService")
public class SolrIndexServiceImpl implements SolrIndexService {
    private static final Log LOG = LogFactory.getLog(SolrIndexServiceImpl.class);

    @Value("${solr.index.product.pageSize}")
    protected int pageSize;

    @Resource(name = "blProductDao")
    protected ProductDao productDao;

    @Resource(name = "blFieldDao")
    protected FieldDao fieldDao;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;

    public static String ATTR_MAP = "productAttributes";

    @Override
    public void rebuildIndex() throws ServiceException, IOException {
        LOG.info("Rebuilding the solr index...");
        StopWatch s = new StopWatch();

        // If we are in single core mode, we have to delete the documents before reindexing
        if (SolrContext.isSingleCoreMode()) {
            deleteAllDocuments();
        }

        Object[] pack = saveState();
        try {
            Long numProducts = productDao.readCountAllActiveProducts();
            if (LOG.isDebugEnabled()) {
                LOG.debug("There are " + numProducts + " total products");
            }
            int page = 0;
            while ((page * pageSize) < numProducts) {
                buildIncrementalIndex(page, pageSize);
                page++;
            }
            optimizeIndex(SolrContext.getReindexServer());
        } catch (ServiceException e) {
            throw e;
        } finally {
            restoreState(pack);
        }

        // Swap the active and the reindex cores
        shs.swapActiveCores();

        // If we are not in single core mode, we delete the documents for the unused core after swapping
        if (!SolrContext.isSingleCoreMode()) {
            deleteAllDocuments();
        }

        LOG.info(String.format("Finished building index in %s", s.toLapString()));
    }

    protected void deleteAllDocuments() throws ServiceException {
        try {
            String deleteQuery = shs.getNamespaceFieldName() + ":(\"" + shs.getCurrentNamespace() + "\")";
            LOG.debug("Deleting by query: " + deleteQuery);
            SolrContext.getReindexServer().deleteByQuery(deleteQuery);
            SolrContext.getReindexServer().commit();
        } catch (Exception e) {
            throw new ServiceException("Could not delete documents", e);
        }
    }

    protected void buildIncrementalIndex(int page, int pageSize) throws ServiceException {
        buildIncrementalIndex(page, pageSize, true);
    }

    @Override
    public void buildIncrementalIndex(int page, int pageSize, boolean useReindexServer) throws ServiceException {
        TransactionStatus status = TransactionUtils.createTransaction("readProducts",
                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Building index - page: [%s], pageSize: [%s]", page, pageSize));
        }
        StopWatch s = new StopWatch();
        try {
            List<Product> products = readAllActiveProducts(page, pageSize);
            List<Field> fields = fieldDao.readAllProductFields();
            List<Locale> locales = getAllLocales();

            Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            for (Product product : products) {
                documents.add(buildDocument(product, fields, locales));
            }

            if (LOG.isTraceEnabled()) {
                for (SolrInputDocument document : documents) {
                    LOG.trace(document);
                }
            }

            if (!CollectionUtils.isEmpty(documents)) {
                SolrServer server = useReindexServer ? SolrContext.getReindexServer() : SolrContext.getServer();
                server.add(documents);
                server.commit();
            }
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
        } catch (SolrServerException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new ServiceException("Could not rebuild index", e);
        } catch (IOException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new ServiceException("Could not rebuild index", e);
        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw e;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Built index - page: [%s], pageSize: [%s] in [%s]", page, pageSize, s.toLapString()));
        }
    }

    /**
     * This method to read all active products will be slow if you have a large catalog. In this case, you will want to
     * read the products in a different manner. For example, if you know the fields that will be indexed, you can configure
     * a DAO object to only load those fields. You could also use a JDBC based DAO for even faster access. This default
     * implementation is only suitable for small catalogs.
     * 
     * @return the list of all active products to be used by the index building task
     */
    protected List<Product> readAllActiveProducts() {
        return productDao.readAllActiveProducts();
    }

    /**
     * This method to read active products utilizes paging to improve performance over {@link #readAllActiveProducts()}.
     * While not optimal, this will reduce the memory required to load large catalogs.
     * 
     * It could still be improved for specific implementations by only loading fields that will be indexed or by accessing
     * the database via direct JDBC (instead of Hibernate).
     * 
     * @return the list of all active products to be used by the index building task
     * @since 2.2.0
     */
    protected List<Product> readAllActiveProducts(int page, int pageSize) {
        return productDao.readAllActiveProducts(page, pageSize);
    }

    /**
     * @return a list of all possible locale prefixes to consider
     */
    protected List<Locale> getAllLocales() {
        return localeService.findAllLocales();
    }

    /**
     * Given a product, fields that relate to that product, and a list of locales and pricelists, builds a 
     * SolrInputDocument to be added to the Solr index.
     * 
     * @param product
     * @param fields
     * @param locales
     * @return the document
     */
    protected SolrInputDocument buildDocument(Product product, List<Field> fields, List<Locale> locales) {
        SolrInputDocument document = new SolrInputDocument();

        attachBasicDocumentFields(product, document);

        // Keep track of searchable fields added to the index.   We need to also add the search facets if 
        // they weren't already added as a searchable field.
        List<String> addedProperties = new ArrayList<String>();

        for (Field field : fields) {
            try {
                // Index the searchable fields
                if (field.getSearchable()) {
                    List<FieldType> searchableFieldTypes = shs.getSearchableFieldTypes(field);
                    for (FieldType sft : searchableFieldTypes) {
                        Map<String, Object> propertyValues = getPropertyValues(product, field, sft, locales);

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
                    Map<String, Object> propertyValues = getPropertyValues(product, field, facetType, locales);

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
                LOG.trace("Could not get value for property[" + field.getQualifiedFieldName() + "] for product id["
                        + product.getId() + "]", e);
            }
        }

        return document;
    }

    /**
     * Adds the ID, category, and explicitCategory fields for the product to the document
     * 
     * @param product
     * @param document
     */
    protected void attachBasicDocumentFields(Product product, SolrInputDocument document) {
        // Add the namespace and ID fields for this product
        document.addField(shs.getNamespaceFieldName(), shs.getCurrentNamespace());
        document.addField(shs.getIdFieldName(), shs.getSolrDocumentId(document, product));
        document.addField(shs.getProductIdFieldName(), product.getId());
        extensionManager.getProxy().attachAdditionalBasicFields(product, document, shs);

        // The explicit categories are the ones defined by the product itself
        for (CategoryProductXref categoryXref : product.getAllParentCategoryXrefs()) {
            document.addField(shs.getExplicitCategoryFieldName(), categoryXref.getCategory().getId());

            String categorySortFieldName = shs.getCategorySortFieldName(categoryXref.getCategory());
            int index = -1;
            int count = 0;
            for (CategoryProductXref productXref : categoryXref.getCategory().getAllProductXrefs()) {
                if (productXref.getProduct().equals(product)) {
                    index = count;
                    break;
                }
                count++;
            }
            document.addField(categorySortFieldName, index);
        }

        // This is the entire tree of every category defined on the product
        Set<Category> fullCategoryHierarchy = new HashSet<Category>();
        for (CategoryProductXref categoryXref : product.getAllParentCategoryXrefs()) {
            fullCategoryHierarchy.addAll(categoryXref.getCategory().buildFullCategoryHierarchy(null));
        }
        for (Category category : fullCategoryHierarchy) {
            document.addField(shs.getCategoryFieldName(), category.getId());
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
     * @param field
     * @param isPriceField
     * @param prefix
     * @return the value of the property
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected Map<String, Object> getPropertyValues(Product product, Field field, FieldType fieldType,
            List<Locale> locales) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String propertyName = field.getPropertyName();
        Map<String, Object> values = new HashMap<String, Object>();

        if (extensionManager != null) {
            ExtensionResultStatusType result = extensionManager.getProxy().addPropertyValues(product, field, fieldType, values, propertyName, locales);

            if (ExtensionResultStatusType.NOT_HANDLED.equals(result)) {
                final Object propertyValue;
                if (propertyName.contains(ATTR_MAP)) {
                    propertyValue = PropertyUtils.getMappedProperty(product, ATTR_MAP, propertyName.substring(ATTR_MAP.length() + 1));
                } else {
                    propertyValue = PropertyUtils.getProperty(product, propertyName);
                }
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
     */
    protected String convertToMappedProperty(String propertyName, String listPropertyName, String mapPropertyName) {
        String[] splitName = StringUtils.split(propertyName, ".");
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
         try {
             if (LOG.isDebugEnabled()) {
                 LOG.debug("Optimizing the index...");
             }
             server.optimize();
         } catch (SolrServerException e) {
             throw new ServiceException("Could not optimize index", e);
         }
     }

}
