/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.service.extension;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.service.AbstractFormBuilderExtensionHandler;
import org.broadleafcommerce.openadmin.web.service.FormBuilderExtensionManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service("blTranslationsFormBuilderExtensionHandler")
public class TranslationsFormBuilderExtensionHandler extends AbstractFormBuilderExtensionHandler {

    private final Log LOG = LogFactory.getLog(TranslationsFormBuilderExtensionHandler.class);

    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyListGridRecord(String className, ListGridRecord record, Entity entity) {
        try {
            if (Translation.class.isAssignableFrom(Class.forName(className))) {
                Field translatedValue = record.getField("translatedValue");
                if (translatedValue != null && translatedValue.getDisplayValue() != null && translatedValue.getValue() != null) {
                    String displayValue = translatedValue.getDisplayValue();
                    String value = translatedValue.getValue();
                    if (displayValue.length() > 30) {
                        displayValue = displayValue.substring(0, 30) + "...";
                    }
                    translatedValue.setDisplayValue(StringEscapeUtils.escapeHtml(displayValue));
                    if (value.length() > 30) {
                        value = value.substring(0, 30) + "...";
                    }
                    translatedValue.setValue(StringEscapeUtils.escapeHtml(value));
                }
                return ExtensionResultStatusType.HANDLED;
            }
        } catch (ClassNotFoundException e) {
            LOG.error("An error has occurred ",e);
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
