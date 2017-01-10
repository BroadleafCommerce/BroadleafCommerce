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
package com.broadleafcommerce.export.admin.web.form.extension;

import org.apache.commons.io.FileUtils;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.service.AbstractFormBuilderExtensionHandler;
import org.broadleafcommerce.openadmin.web.service.FormBuilderExtensionManager;
import org.springframework.stereotype.Component;

import com.broadleafcommerce.export.domain.ExportInfo;
import com.broadleafcommerce.export.domain.ExportInfoImpl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component("blExportInfoFormBuilderExtensionHandler")
public class ExportInfoFormBuilderExtensionHandler extends AbstractFormBuilderExtensionHandler {
    
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
        if (ExportInfo.class.getName().equals(className) || ExportInfoImpl.class.getName().equals(className)) {
            for (Field f : record.getFields()) {
                if (f.getName().equals("size")) {
                    formatDisplayValue(f);
                    return ExtensionResultStatusType.HANDLED_CONTINUE;
                }
            }
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    protected void formatDisplayValue(Field f) {
        Long size = Long.parseLong(f.getDisplayValue());
        f.setDisplayValue(FileUtils.byteCountToDisplaySize(size));
    }
}
