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
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;
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
@Service("blI18nSolrIndexServiceExtensionHandler")
public class I18nSolrIndexServiceExtensionHandler extends AbstractSolrIndexServiceExtensionHandler
        implements SolrIndexServiceExtensionHandler {

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager extensionManager;

    @Resource(name = "blTranslationService")
    protected TranslationService translationService;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    protected boolean getTranslationEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("i18n.translation.enabled");
    }

    @PostConstruct
    public void init() {
        extensionManager.registerHandler(this);
    }

    @Override
    public ExtensionResultStatusType addPropertyValues(Indexable indexable, Field field, FieldType fieldType,
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
                    String localeCode = locale.getLocaleCode();
                    if (Boolean.FALSE.equals(locale.getUseCountryInSearchIndex())) {
                        int pos = localeCode.indexOf("_");
                        if (pos > 0) {
                            localeCode = localeCode.substring(0, pos);
                            if (processedLocaleCodes.contains(localeCode)) {
                                continue;
                            } else {
                                locale = localeService.findLocaleByCode(localeCode);
                            }
                        }
                    }

                    processedLocaleCodes.add(localeCode);
                    tempContext.setLocale(locale);

                    Object propertyValue = shs.getPropertyValue(indexable, propertyName);
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
                    if (!Boolean.TRUE.equals(locale.getUseCountryInSearchIndex())) {
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
    
    @Override
    public ExtensionResultStatusType startBatchEvent(List<? extends Indexable> indexables) {
        List<String> skuIds = new ArrayList<String>(indexables.size());
        List<String> productIds = new ArrayList<String>();
        List<String> skuAttributeIds = new ArrayList<String>();
        List<String> productAttributeIds = new ArrayList<String>();
        for (Indexable indexable : indexables) {
            Sku sku = null;
            if (Product.class.isAssignableFrom(indexable.getClass())) {
                Product product = (Product) indexable;
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
    public ExtensionResultStatusType endBatchEvent(List<? extends Indexable> indexables) {
        TranslationBatchReadCache.clearCache();
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
