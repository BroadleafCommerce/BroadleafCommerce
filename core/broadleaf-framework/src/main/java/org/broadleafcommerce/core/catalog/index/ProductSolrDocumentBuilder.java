package org.broadleafcommerce.core.catalog.index;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.index.DocumentBuilder;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

@Component("blProductSolrDocumentBuilder")
public class ProductSolrDocumentBuilder implements DocumentBuilder<Product, SolrInputDocument> {
    
    private static final Log LOG = LogFactory.getLog(ProductSolrDocumentBuilder.class);
    
    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager extensionManager;

    @Override
    public SolrInputDocument buildDocument(Product indexable, List<Locale> locales) {
        if (shouldIndex(indexable)) {
            final SolrInputDocument document = new SolrInputDocument();

            attachBasicDocumentFields(indexable, document);

            attachIndexableDocumentFields(document, indexable, fields, locales);

            attachAdditionalDocumentFields(indexable, document);

            extensionManager.getProxy().attachChildDocuments(indexable, document, fields, locales);

            return document;
        }
        return null;
    }

    protected boolean shouldIndex(Product indexable) {
        if (indexable != null && indexable.isActive()) {
            return true;
        }
        return false;
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
    
    protected void attachBasicDocumentFields(Product indexable, SolrInputDocument document) {
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
    
    protected void attachAdditionalDocumentFields(Indexable indexable, SolrInputDocument document) {
        //Empty implementation. Placeholder for others to extend and add additional fields
        extensionManager.getProxy().attachAdditionalDocumentFields(indexable, document);
    }
}
