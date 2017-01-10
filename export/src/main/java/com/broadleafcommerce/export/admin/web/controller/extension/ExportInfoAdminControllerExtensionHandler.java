/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.admin.web.controller.extension;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminAbstractControllerExtensionHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.stereotype.Component;

import com.broadleafcommerce.export.domain.ExportInfo;
import com.broadleafcommerce.export.domain.ExportInfoImpl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Extension handler created so that there's no add button on the export info list grid page
 * since there's no reason to ever add one manually. Export info records are made when an export
 * is kicked off in a different part of the admin
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Component("blExportInfoAdminControllerExtensionHandler")
public class ExportInfoAdminControllerExtensionHandler extends AbstractAdminAbstractControllerExtensionHandler {

    @Resource(name = "blAdminAbstractControllerExtensionManager")
    protected AdminAbstractControllerExtensionManager extensionManager;
    
    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }
    
    @Override
    public ExtensionResultStatusType modifyMainActions(ClassMetadata cmd, List<EntityFormAction> mainActions) {
        if (cmd.getCeilingType().equals(ExportInfo.class.getName()) || cmd.getCeilingType().equals(ExportInfoImpl.class.getName())) {
            for (EntityFormAction action : mainActions) {
                if (EntityFormAction.ADD.equals(action.getId())) {
                    mainActions.remove(action);
                    break;
                }
            }
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
