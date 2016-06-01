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

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.service.TranslationBatchReadCache;
import org.broadleafcommerce.common.i18n.service.TranslationConsiderationContext;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * If the field is translatable, then this method prefixes the field with supported locales.
 * 
 * @author bpolster
 */
@Service("blI18nSolrSearchServiceExtensionHandler")
public class I18nSolrSearchServiceExtensionHandler extends AbstractSolrSearchServiceExtensionHandler
        implements SolrSearchServiceExtensionHandler {

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    @Resource(name = "blTranslationService")
    protected TranslationService translationService;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    protected boolean getTranslationEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("i18n.translation.enabled");
    }

    @PostConstruct
    public void init() {
        boolean shouldAdd = true;
        for (SolrSearchServiceExtensionHandler h : extensionManager.getHandlers()) {
            if (h instanceof I18nSolrSearchServiceExtensionHandler) {
                shouldAdd = false;
                break;
            }
        }
        if (shouldAdd) {
            extensionManager.getHandlers().add(this);
        }
    }

    @Override
    public ExtensionResultStatusType buildPrefixListForSearchableFacet(Field field, List<String> prefixList) {
        return getLocalePrefix(field, prefixList);
    }

    @Override
    public ExtensionResultStatusType buildPrefixListForSearchableField(Field field, FieldType searchableFieldType, List<String> prefixList) {
        return getLocalePrefix(field, prefixList);
    }

    @Override
    public ExtensionResultStatusType addPropertyValues(Product product, Field field, FieldType fieldType,
            Map<String, Object> values, String propertyName, List<Locale> locales)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        
        return addPropertyValues(product, null, false, field, fieldType, values, propertyName, locales);
    }

    @Override
    public ExtensionResultStatusType addPropertyValues(Sku sku, Field field, FieldType fieldType,
            Map<String, Object> values, String propertyName, List<Locale> locales)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        return addPropertyValues(null, sku, true, field, fieldType, values, propertyName, locales);
    }

    protected ExtensionResultStatusType addPropertyValues(Product product, Sku sku, boolean useSku, Field field, FieldType fieldType,
            Map<String, Object> values, String propertyName, List<Locale> locales)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Set<String> processedLocaleCodes = new HashSet<String>();

        ExtensionResultStatusType result = ExtensionResultStatusType.NOT_HANDLED;
        if (field.getTranslatable()) {
            result = ExtensionResultStatusType.HANDLED;

            TranslationConsiderationContext.setTranslationConsiderationContext(getTranslationEnabled());
            TranslationConsiderationContext.setTranslationService(translationService);
            BroadleafRequestContext tempContext = BroadleafRequestContext.getBroadleafRequestContext();
            if (tempContext == null) {
                tempContext = new BroadleafRequestContext();
                BroadleafRequestContext.setBroadleafRequestContext(tempContext);
            }

            Locale originalLocale = tempContext.getLocale();

            try {
                for (Locale locale : locales) {
                    tempContext.setLocale(locale);

                    final Object propertyValue;
                    if (useSku) {
                        propertyValue = shs.getPropertyValue(sku, propertyName);
                    } else {
                        propertyValue = shs.getPropertyValue(product, propertyName);
                    }

                    String localeCode = locale.getLocaleCode();
                    if (Boolean.FALSE.equals(locale.getUseCountryInSearchIndex())) {
                        int pos = localeCode.indexOf("_");
                        if (pos > 0) {
                            localeCode = localeCode.substring(0, pos);
                        }
                    }
                    
                    values.put(localeCode, propertyValue);
                }
            } finally {
                //Reset the original locale.
                tempContext.setLocale(originalLocale);
            }
        }
        return result;

    }

    /**
     * If the field is translatable, take the current locale and add that as a prefix.
     * @param context
     * @param field
     * @return
     */
    protected ExtensionResultStatusType getLocalePrefix(Field field, List<String> prefixList) {
        if (field.getTranslatable()) {
            if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
                Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
                if (locale != null) {
                    String localeCode = locale.getLocaleCode();
                    if (Boolean.FALSE.equals(locale.getUseCountryInSearchIndex())) {
                        int pos = localeCode.indexOf("_");
                        if (pos > 0) {
                            localeCode = localeCode.substring(0, pos);
                        }
                    }
                    prefixList.add(localeCode);
                    return ExtensionResultStatusType.HANDLED_CONTINUE;
                }
            }
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    /**
     * Read all of the translations for this product batch and their default Skus. By reading this up front we save some
     * time by not having to go to the database for each product in each locale
     */
    @Override
    public ExtensionResultStatusType startBatchEvent(List<Product> products) {
        List<String> skuIds = new ArrayList<String>(products.size());
        List<String> productIds = new ArrayList<String>();
        List<String> skuAttributeIds = new ArrayList<String>();
        List<String> productAttributeIds = new ArrayList<String>();
        for (Product indexable : products) {
            Sku sku = null;
            if (Product.class.isAssignableFrom(indexable.getClass())) {
                Product product = indexable;
                productIds.add(product.getId().toString());
                for (Map.Entry<String, ProductAttribute> attributeEntry :  product.getProductAttributes().entrySet()) {
                    ProductAttribute attribute = attributeEntry.getValue();
                    productAttributeIds.add(attribute.getId().toString());
                }
                sku = product.getDefaultSku();
            }
            
            if (sku != null) {
                skuIds.add(sku.getId().toString());
                for (Map.Entry<String, SkuAttribute> attributeEntry :  sku.getSkuAttributes().entrySet()) {
                    SkuAttribute attribute = attributeEntry.getValue();
                    skuAttributeIds.add(attribute.getId().toString());
                }
            }
        }

        addEntitiesToTranslationCache(skuIds, TranslatedEntity.SKU);
        addEntitiesToTranslationCache(productIds, TranslatedEntity.PRODUCT);
        addEntitiesToTranslationCache(skuAttributeIds, TranslatedEntity.SKU_ATTRIBUTE);
        addEntitiesToTranslationCache(productAttributeIds, TranslatedEntity.PRODUCT_ATTRIBUTE);

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    protected void addEntitiesToTranslationCache(List<String> entityIds, TranslatedEntity translatedEntity) {
        List<Translation> translations = translationService.findAllTranslationEntries(translatedEntity, ResultType.STANDARD, entityIds);
        TranslationBatchReadCache.addToCache(translations);
    }

    @Override
    public ExtensionResultStatusType endBatchEvent() {
        TranslationBatchReadCache.clearCache();
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }
    
    @Override
    public int getPriority() {
        return 1000;
    }
}
