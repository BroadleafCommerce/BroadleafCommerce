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
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.i18n.service.TranslationConsiderationContext;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
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

    private static String PRODUCT_ATTR_MAP = SolrIndexServiceImpl.PRODUCT_ATTR_MAP;
    private static String SKU_ATTR_MAP = SolrIndexServiceImpl.SKU_ATTR_MAP;

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
                    String localeCode = locale.getLocaleCode();
                    if (!Boolean.TRUE.equals(locale.getUseCountryInSearchIndex())) {
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

                    final Object propertyValue;

                    if (useSku) {
                        if (propertyName.contains(SKU_ATTR_MAP)) {
                            propertyValue = PropertyUtils.getMappedProperty(sku, SKU_ATTR_MAP, propertyName.substring(SKU_ATTR_MAP.length() + 1));
                        } else {
                            propertyValue = shs.getPropertyValue(sku, propertyName); //PropertyUtils.getProperty(sku, propertyName);
                        }
                    } else {
                        if (propertyName.contains(PRODUCT_ATTR_MAP)) {
                            propertyValue = PropertyUtils.getMappedProperty(product, PRODUCT_ATTR_MAP, propertyName.substring(PRODUCT_ATTR_MAP.length() + 1));
                        } else {
                            propertyValue = shs.getPropertyValue(product, propertyName); //PropertyUtils.getProperty(product, propertyName);
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
    public int getPriority() {
        return 1000;
    }
}
