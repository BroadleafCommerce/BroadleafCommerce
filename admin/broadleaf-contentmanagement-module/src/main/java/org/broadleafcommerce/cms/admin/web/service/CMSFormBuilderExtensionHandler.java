/*-
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.admin.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.AbstractFormBuilderExtensionHandler;
import org.broadleafcommerce.openadmin.web.service.FormBuilderExtensionManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Handle admin form UI modification for CMS related entitites
 *
 * @author Jeff Fischer
 */
@Service("blCMSFormBuilderExtensionHandler")
public class CMSFormBuilderExtensionHandler extends AbstractFormBuilderExtensionHandler {

    private static final Log LOG = LogFactory.getLog(CMSFormBuilderExtensionHandler.class);

    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyDetailEntityForm(EntityForm ef) {
        try {
            // These fields on page and structured content are really only modified under the covers
            // in custom persistence handlers and are not designed to be modified on the frontend
            if (Page.class.isAssignableFrom(Class.forName(ef.getCeilingEntityClassname()))) {
                ef.removeListGrid("pageFields");
            } else if (StructuredContent.class.isAssignableFrom(Class.forName(ef.getCeilingEntityClassname()))) {
                ef.removeListGrid("structuredContentFields");
            }

            return ExtensionResultStatusType.HANDLED;
        } catch (ClassNotFoundException e) {
            LOG.warn("No class found for the given entity form, not modifying grid");
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
