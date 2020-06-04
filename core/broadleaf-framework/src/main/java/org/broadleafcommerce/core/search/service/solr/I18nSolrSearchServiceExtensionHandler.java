/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ExtensionResultStatusType buildPrefixListForIndexField(IndexField field, FieldType fieldType, List<String> prefixList) {
        return getLocalePrefix(field.getField(), prefixList);
    }

    /**
     * If the field is translatable, take the current locale and add that as a prefix.
     * @param context
     * @param field
     * @return
     */
    protected ExtensionResultStatusType getLocalePrefix(Field field, List<String> prefixList) {
        if (field.getTranslatable() && getTranslationEnabled()) {
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

    @Override
    public int getPriority() {
        return 1000;
    }
}
