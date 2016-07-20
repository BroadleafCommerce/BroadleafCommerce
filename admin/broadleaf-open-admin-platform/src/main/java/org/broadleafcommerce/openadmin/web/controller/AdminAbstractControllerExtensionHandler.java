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

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * Extension handler for methods present in {@link AdminAbstractController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminAbstractControllerExtensionHandler extends ExtensionHandler {
    
    public static final String NEW_CLASS_NAME = "newClassName";

    /**
     * Invoked every time {@link AdminBasicEntityController#viewEntityList()} is invoked to allow additional
     * main form actions to be contributed.
     * 
     * @param model
     * @param sectionKey
     * @return
     */
    public ExtensionResultStatusType addAdditionalMainActions(String sectionClassName, List<EntityFormAction> actions);

    /**
     * Extension point to override the actions that are added by default when viewing a ceiling entity for a particular
     * section (for instance, a list of Products in the 'Product' section). Assuming that the user has proper permissions,
     * the mainActions list would have {@link org.broadleafcommerce.openadmin.web.form.entity.DefaultMainActions#ADD}
     *
     * @param cmd the metadata for the ceiling entity that is being displayed
     * @param mainActions the actions that are added to the main form by default. Use this list to add more actions
     */
    public ExtensionResultStatusType modifyMainActions(ClassMetadata cmd, List<EntityFormAction> mainActions);

    /**
     * Invoked every time {@link AdminAbstractController#setModelAttributes(Model, String)} is invoked.
     * 
     * @param model
     * @param sectionKey
     * @return the extension result status
     */
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey);

    /**
     * Invoked whenever {@link AdminAbstractController#getBlankDynamicFieldTemplateForm} or 
     * {@link AdminAbstractController#getDynamicFieldTemplateForm} is invoked. This method provides the ability to modify
     * the dynamic form that is created as a result of those two methods.
     * 
     * @param form
     * @param parentEntityId
     * @return
     */
    public ExtensionResultStatusType modifyDynamicForm(EntityForm form, String parentEntityId);

    /**
     * Extension point to modify the model for specific collection types
     *
     * @param md
     * @return
     */
    public ExtensionResultStatusType modifyModelForAddCollectionType(HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     Model model,
                                                                     String sectionKey,
                                                                     String id,
                                                                     MultiValueMap<String, String> requestParams, MapMetadata md);

    /**
     * Extension point to determine if the entity requested is a new empty add request.
     *
     * @param entity
     * @return
     */
    public ExtensionResultStatusType isAddRequest(Entity entity, ExtensionResultHolder<Boolean> resultHolder);

    /**
     * Extension point to determine if the current save transaction is the first valid save.
     *
     *
     * @param response
     * @param hasValidationErrors
     * @param sectionKey
     * @param id
     * @return
     */
    public ExtensionResultStatusType overrideSaveEntityJsonResponse(HttpServletResponse response, boolean hasValidationErrors, String sectionKey, String id, ExtensionResultHolder<String> resultHolder);

    /**
     * Extension point for setting values on an EntityForm before the initial object is persisted.
     *
     * @param entityForm
     * @param pathVars
     * @return
     */
    public ExtensionResultStatusType modifyPreAddEntityForm(EntityForm entityForm, ClassMetadata cmd, Map<String, String> pathVars);
}
