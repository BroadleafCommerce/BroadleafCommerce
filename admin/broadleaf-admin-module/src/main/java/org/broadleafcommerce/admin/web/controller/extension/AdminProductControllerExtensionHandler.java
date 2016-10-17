/*
 * #%L
 * BroadleafCommerce Pricelist
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

import org.broadleafcommerce.admin.web.controller.entity.AdminOfferController;
import org.broadleafcommerce.admin.web.controller.entity.AdminProductController;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminAbstractControllerExtensionHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blAdminProductControllerExtensionHandler")
public class AdminProductControllerExtensionHandler extends AbstractAdminAbstractControllerExtensionHandler {

    @Resource(name = "blAdminAbstractControllerExtensionManager")
    protected AdminAbstractControllerExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey) {
        if (AdminProductController.SECTION_KEY.equals(sectionKey)) {
            List<ClassTree> entityTypes = (List<ClassTree>) model.asMap().get("entityTypes");

            if (entityTypes != null) {
                entityTypes = removeProductBundleEntityType(entityTypes);
                model.addAttribute("entityTypes", entityTypes);
                return ExtensionResultStatusType.HANDLED;
            }
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    protected List<ClassTree> removeProductBundleEntityType(List<ClassTree> entityTypes) {
        for (ClassTree entityType : entityTypes) {
            if (isProductBundleEntityType(entityType)) {
                entityTypes.remove(entityType);
                break;
            }
        }
        return entityTypes;
    }

    protected boolean isProductBundleEntityType(ClassTree entityType) {
        try {
            String fullyQualifiedClassname = entityType.getFullyQualifiedClassname();
            return ProductBundle.class.isAssignableFrom(Class.forName(fullyQualifiedClassname));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
