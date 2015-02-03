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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.cloud.Aliases;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.IllegalStateException;

/**
 * Provides utility methods that are used by other Solr service classes
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blSolrHelperService")
public class SolrHelperServiceImpl implements SolrHelperService {

    private static final Log LOG = LogFactory.getLog(SolrHelperServiceImpl.class);

    // The value of these two fields has no special significance, but they must be non-blank
    protected static final String GLOBAL_FACET_TAG_FIELD = "a";
    protected static final String DEFAULT_NAMESPACE = "d";

    protected static final String PREFIX_SEPARATOR = "_";

    protected static Locale defaultLocale;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    /**
     * This should only ever be called when using the Solr reindex service to do a full reindex. 
     */
    @Override
    public synchronized void swapActiveCores() throws ServiceException {
        if (SolrContext.isSolrCloudMode()) {
            CloudSolrServer primary = (CloudSolrServer) SolrContext.getServer();
            CloudSolrServer reindex = (CloudSolrServer) SolrContext.getReindexServer();
            try {
                primary.connect();
                Aliases aliases = primary.getZkStateReader().getAliases();
                Map<String, String> aliasCollectionMap = aliases.getCollectionAliasMap();
                if (aliasCollectionMap == null || !aliasCollectionMap.containsKey(primary.getDefaultCollection())
                        || !aliasCollectionMap.containsKey(reindex.getDefaultCollection())) {
                    throw new IllegalStateException("Could not determine the PRIMARY or REINDEX "
                            + "collection or collections from the Solr aliases.");
                }

                String primaryCollectionName = aliasCollectionMap.get(primary.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                primaryCollectionName = primaryCollectionName.split(",")[0];

                String reindexCollectionName = aliasCollectionMap.get(reindex.getDefaultCollection());
                //Do this just in case primary is aliased to more than one collection
                reindexCollectionName = reindexCollectionName.split(",")[0];

                //Essentially "swap cores" here by reassigning the aliases
                CollectionAdminRequest.createAlias(primary.getDefaultCollection(), reindexCollectionName, primary);
                CollectionAdminRequest.createAlias(reindex.getDefaultCollection(), primaryCollectionName, primary);
            } catch (Exception e) {
                LOG.error("An exception occured swapping cores.", e);
                throw new ServiceException("Unable to swap SolrCloud collections after a full reindex.", e);
            }
        } else {
            if (SolrContext.isSingleCoreMode()) {
                LOG.debug("In single core mode. There are no cores to swap.");
            } else {
                LOG.debug("Swapping active cores");

                CoreAdminRequest car = new CoreAdminRequest();
                car.setCoreName(SolrContext.PRIMARY);
                car.setOtherCoreName(SolrContext.REINDEX);
                car.setAction(CoreAdminAction.SWAP);

                try {
                    SolrContext.getAdminServer().request(car);
                } catch (Exception e) {
                    LOG.error(e);
                    throw new ServiceException("Unable to swap cores", e);
                }
            }
        }
    }

    @Override
    public String getCurrentNamespace() {
        return DEFAULT_NAMESPACE;
    }

    @Override
    public String getGlobalFacetTagField() {
        return GLOBAL_FACET_TAG_FIELD;
    }

    @Override
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType, String prefix) {
        return new StringBuilder()
                .append(prefix)
                .append(field.getAbbreviation()).append("_").append(searchableFieldType.getType())
                .toString();
    }

    @Override
    public String getPropertyNameForFieldFacet(Field field, String prefix) {
        if (field.getFacetFieldType() == null) {
            return null;
        }

        return new StringBuilder()
                .append(prefix)
                .append(field.getAbbreviation()).append("_").append(field.getFacetFieldType().getType())
                .toString();
    }
    
    @Override
    public List<FieldType> getSearchableFieldTypes(Field field) {
        // We will index all configured searchable field types
        List<FieldType> typesToConsider = new ArrayList<FieldType>();
        if (CollectionUtils.isNotEmpty(field.getSearchableFieldTypes())) {
            typesToConsider.addAll(field.getSearchableFieldTypes());
        }
        
        // If there were no searchable field types configured, we will use TEXT as a default one
        if (CollectionUtils.isEmpty(typesToConsider)) {
            typesToConsider.add(FieldType.TEXT);
        }
        
        return typesToConsider;
    }

    @Override
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType) {
        List<String> prefixList = new ArrayList<String>();
        extensionManager.getProxy().buildPrefixListForSearchableField(field, searchableFieldType, prefixList);
        String prefix = convertPrefixListToString(prefixList);
        return getPropertyNameForFieldSearchable(field, searchableFieldType, prefix);
    }

    @Override
    public String getPropertyNameForFieldFacet(Field field) {
        FieldType fieldType = field.getFacetFieldType();
        if (fieldType == null) {
            return null;
        }

        List<String> prefixList = new ArrayList<String>();

        extensionManager.getProxy().buildPrefixListForSearchableFacet(field, prefixList);
        String prefix = convertPrefixListToString(prefixList);

        return getPropertyNameForFieldFacet(field, prefix);
    }

    protected String convertPrefixListToString(List<String> prefixList) {
        StringBuilder prefixString = new StringBuilder();
        for (String prefix : prefixList) {
            if (prefix != null && !prefix.isEmpty()) {
                prefixString = prefixString.append(prefix).append(PREFIX_SEPARATOR);
            }
        }
        return prefixString.toString();
    }
    
    @Override
    public Long getCategoryId(Long tentativeCategoryId) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = extensionManager.getProxy().getCategoryId(tentativeCategoryId, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return tentativeCategoryId;
    }

    @Override
    public Long getProductId(Long tentativeProductId) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = extensionManager.getProxy().getProductId(tentativeProductId, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return tentativeProductId;
    }

    @Override
    public Long getSkuId(Long tentativeSkuId) {
        Long[] returnId = new Long[1];
        ExtensionResultStatusType result = extensionManager.getProxy().getSkuId(tentativeSkuId, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return tentativeSkuId;
    }

    @Override
    public String getSolrDocumentId(SolrInputDocument document, Product product) {
        String[] returnId = new String[1];
        ExtensionResultStatusType result = extensionManager.getProxy().getSolrDocumentId(document, product, returnId);
        if (result.equals(ExtensionResultStatusType.HANDLED)) {
            return returnId[0];
        }
        return String.valueOf(product.getId());
    }
    
    @Override
    public String getSolrDocumentId(SolrInputDocument document, Sku sku) {
        return String.valueOf(sku.getId());
    }

    @Override
    public String getNamespaceFieldName() {
        return "namespace";
    }

    @Override
    public String getIdFieldName() {
        return "id";
    }
    
    @Override
    public String getProductIdFieldName() {
        return "productId";
    }

    @Override
    public String getSkuIdFieldName() {
        return "skuId";
    }

    @Override
    public String getCategoryFieldName() {
        return "category";
    }

    @Override
    public String getExplicitCategoryFieldName() {
        return "explicitCategory";
    }

    @Override
    public String getCategorySortFieldName(Category category) {
        Long categoryId = getCategoryId(category.getId());
        return new StringBuilder()
                .append(getCategoryFieldName())
                .append("_").append(categoryId).append("_").append("sort_d")
                .toString();
    }

    @Override
    public String getCategorySortFieldName(Long categoryId) {
        categoryId = getCategoryId(categoryId);
        return new StringBuilder()
                .append(getCategoryFieldName())
                .append("_").append(categoryId).append("_").append("sort_d")
                .toString();
    }

    @Override
    public String getLocalePrefix() {
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
            if (locale != null) {
                return locale.getLocaleCode() + "_";
            }
        }
        return getDefaultLocalePrefix();
    }

    @Override
    public String getDefaultLocalePrefix() {
        return getDefaultLocale().getLocaleCode() + "_";
    }

    @Override
    public Locale getDefaultLocale() {
        if (defaultLocale == null) {
            defaultLocale = localeService.findDefaultLocale();
        }

        return defaultLocale;
    }

    @Override
    public Object getPropertyValue(Object object, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getPropertyValue(object, field.getPropertyName());
    }

    @Override
    public Object getPropertyValue(Object object, String propertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] components = propertyName.split("\\.");
        return getPropertyValueInternal(object, components, 0);
    }

    /*
     * This method iteratively and recursively attempts to return the value or values of the property specified by the currentPosition in the 
     * array of components.  The components argument is an array of strings representing the object graph.
     */
    protected Object getPropertyValueInternal(Object object, String[] components, int currentPosition) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        if (object == null) {
            return null;
        }

        Object propertyObject = PropertyUtils.getProperty(object, components[currentPosition]);

        if (propertyObject != null) {
            if (currentPosition < components.length - 1) {
                if (Collection.class.isAssignableFrom(propertyObject.getClass())) {
                    Collection<?> collection = (Collection<?>) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : collection) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else if (Map.class.isAssignableFrom(propertyObject.getClass())) {
                    Map<?, ?> map = (Map<?, ?>) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : map.values()) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else if (propertyObject.getClass().isArray()) {
                    Object[] array = (Object[]) propertyObject;
                    HashSet<Object> newCollection = new HashSet<Object>();
                    for (Object item : array) {
                        Object result = getPropertyValueInternal(item, components, currentPosition + 1);
                        if (result != null) {
                            copyPropertyToCollection(newCollection, result);
                        }
                    }
                    propertyObject = newCollection;
                } else {
                    propertyObject = getPropertyValueInternal(propertyObject, components, currentPosition + 1);
                }
            }
        }

        return propertyObject;
    }

    /*
     * This adds the value of the object to the collection.  If the object is a Map, this adds the values of the 
     * map to the collection.  If the object is a Collection or an Array, it adds each of the values to the collection. 
     */
    protected void copyPropertyToCollection(Collection<Object> collection, Object o) {
        if (o == null) {
            return;
        }

        if (Collection.class.isAssignableFrom(o.getClass())) {
            collection.addAll((Collection<?>) o);
        } else if (Map.class.isAssignableFrom(o.getClass())) {
            collection.addAll(((Map<?, ?>) o).values());
        } else if (o.getClass().isArray()) {
            Object[] array = (Object[]) o;
            if (array.length > 0) {
                for (Object obj : array) {
                    collection.add(obj);
                }
            }
        } else {
            collection.add(o);
        }
    }

}
