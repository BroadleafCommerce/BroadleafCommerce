/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.extension;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.service.AbstractFormBuilderExtensionHandler;
import org.broadleafcommerce.openadmin.web.service.FormBuilderExtensionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service("blParentCategorySortExtensionHandler")
public class ParentCategorySortExtensionHandler extends AbstractFormBuilderExtensionHandler {

    @Value("${allow.product.parent.category.sorting:false}")
    protected boolean allowProductParentCategorySorting = false;

    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyListGrid(String className, ListGrid listGrid) {
        if (Product.class.getName().equals(className) && !allowProductParentCategorySorting) {
            for (Field f : listGrid.getHeaderFields()) {
                if (f.getName().equals("defaultCategory")) {
                    f.setFilterSortDisabled(true);
                    return ExtensionResultStatusType.HANDLED;
                }
            }
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
