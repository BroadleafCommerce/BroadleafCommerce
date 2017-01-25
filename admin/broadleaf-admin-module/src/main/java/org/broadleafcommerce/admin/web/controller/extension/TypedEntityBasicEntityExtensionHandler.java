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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.TypedEntity;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminAbstractControllerExtensionHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Adds special behavior specific for Typed Entities during any entity persistence flows.
 * @author Jon Fleschler (jfleschler)
 */
@Component("blTypedEntityBasicEntityExtensionHandler")
public class TypedEntityBasicEntityExtensionHandler extends AbstractAdminAbstractControllerExtensionHandler {
    protected static final Log LOG = LogFactory.getLog(TypedEntityBasicEntityExtensionHandler.class);

    @Resource(name = "blAdminAbstractControllerExtensionManager")
    protected AdminAbstractControllerExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    /**
     * This allows us to set the Type on the entityForm before the entity is first persisted.
     * @param entityForm
     * @param cmd
     * @param pathVars
     * @return
     */
    @Override
    public ExtensionResultStatusType modifyPreAddEntityForm(EntityForm entityForm, ClassMetadata cmd, Map<String, String> pathVars) {
        Class<?> implClass = getDynamicEntityDao(cmd.getCeilingType()).getCeilingImplClass(cmd.getCeilingType());
        if (TypedEntity.class.isAssignableFrom(implClass)) {
            // Set the Type on the Add entity form
            String type = getDefaultType(implClass);

            String sectionKey = entityForm.getSectionKey();
            int typeIndex = sectionKey.indexOf(":");
            if (typeIndex > 0) {
                type = sectionKey.substring(typeIndex + 1).toUpperCase();
            }

            String typedFieldName = getTypeFieldName(implClass);
            if (typedFieldName != null) {
                entityForm.findField(typedFieldName).setValue(type);
            }
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    protected DynamicEntityDao getDynamicEntityDao(String className) {
        return PersistenceManagerFactory.getPersistenceManager(className).getDynamicEntityDao();
    }

    protected String getDefaultType(Class implClass) {
        try {
            return ((TypedEntity) implClass.newInstance()).getDefaultType();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getTypeFieldName(Class implClass) {
        try {
            return ((TypedEntity) implClass.newInstance()).getTypeFieldName();
        } catch (Exception e) {
            return null;
        }
    }
}
