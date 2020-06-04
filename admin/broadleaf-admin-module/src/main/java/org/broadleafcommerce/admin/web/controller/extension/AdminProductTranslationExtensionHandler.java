/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.extension;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.server.service.JSCompatibilityHelper;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminTranslationControllerExtensionHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminTranslationControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.TranslationForm;
import org.springframework.stereotype.Component;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAdminProductTranslationExtensionHandler")
public class AdminProductTranslationExtensionHandler extends AbstractAdminTranslationControllerExtensionHandler {
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blAdminTranslationControllerExtensionManager")
    protected AdminTranslationControllerExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    protected boolean getTranslationEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("i18n.translation.enabled");
    }

    /**
     * If we are trying to translate a field on Product that starts with "defaultSku.", we really want to associate the
     * translation with Sku, its associated id, and the property name without "defaultSku."
     */
    @Override
    public ExtensionResultStatusType applyTransformation(TranslationForm form) {
        if (getTranslationEnabled()) {
            String defaultSkuPrefix = "defaultSku.";
            String unencodedPropertyName = JSCompatibilityHelper.unencode(form.getPropertyName());
            if (form.getCeilingEntity().equals(Product.class.getName()) && unencodedPropertyName.startsWith(defaultSkuPrefix)) {
                Product p = catalogService.findProductById(Long.parseLong(form.getEntityId()));
                form.setCeilingEntity(Sku.class.getName());
                form.setEntityId(String.valueOf(p.getDefaultSku().getId()));
                form.setPropertyName(unencodedPropertyName.substring(defaultSkuPrefix.length()));
            }
        }
        
        return ExtensionResultStatusType.HANDLED;
    }
    
}
