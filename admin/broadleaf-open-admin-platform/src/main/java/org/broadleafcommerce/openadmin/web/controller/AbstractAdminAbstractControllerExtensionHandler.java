/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Abstract implementatino of {@link AdminAbstractControllerExtensionHandler}.
 * 
 * Individual implementations of this extension handler should subclass this class as it will allow them to 
 * only override the methods that they need for their particular scenarios.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class AbstractAdminAbstractControllerExtensionHandler extends AbstractExtensionHandler implements AdminAbstractControllerExtensionHandler {

    @Override
    public ExtensionResultStatusType addAdditionalMainActions(String sectionClassName, List<EntityFormAction> actions) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyMainActions(ClassMetadata cmd, List<EntityFormAction> mainActions) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyDynamicForm(EntityForm form, String parentEntityId) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyModelForAddCollectionType(HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     Model model,
                                                                     String sectionKey,
                                                                     String id,
                                                                     MultiValueMap<String, String> requestParams, MapMetadata md) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType isAddRequest(Entity entity, ExtensionResultHolder<Boolean> resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType overrideSaveEntityJsonResponse(HttpServletResponse response, boolean hasValidationErrors, String sectionKey, String id, ExtensionResultHolder<String> resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyPreAddEntityForm(EntityForm entityForm, ClassMetadata cmd, Map<String, String> pathVars) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
