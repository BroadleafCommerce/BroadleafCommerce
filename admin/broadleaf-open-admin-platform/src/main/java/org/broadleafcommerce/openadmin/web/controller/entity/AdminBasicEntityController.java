/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.controller.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.broadleafcommerce.openadmin.web.editor.NonNullBooleanEditor;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultMainActions;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormValidator;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.FieldGroup;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The default implementation of the {@link #BroadleafAdminAbstractEntityController}. This delegates every call to 
 * super and does not provide any custom-tailored functionality. It is responsible for rendering the admin for every
 * entity that is not explicitly customized by its own controller.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminBasicEntityController")
@RequestMapping("/{sectionKey:.+}")
public class AdminBasicEntityController extends AdminAbstractController {
    
    // ***********************
    // RESOURCE DECLARATIONS *
    // ***********************

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService service;

    @Resource(name = "blFormBuilderService")
    protected FormBuilderService formService;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blEntityFormValidator")
    protected EntityFormValidator entityValidator;
    
    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    // ******************************************
    // REQUEST-MAPPING BOUND CONTROLLER METHODS *
    // ******************************************

    /**
     * Renders the main entity listing for the specified class, which is based on the current sectionKey with some optional
     * criteria.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param criteria a Map of property name -> list critiera values
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, requestParams);

        ClassMetadata cmd = service.getClassMetadata(ppr);
        DynamicResultSet drs =  service.getRecords(ppr);

        ListGrid listGrid = formService.buildMainListGrid(drs, cmd, sectionKey);
        
        List<EntityFormAction> mainActions = new ArrayList<EntityFormAction>();
        // If the user does not have create permissions, we will not add the "Add New" button
        boolean canCreate = true;
        try {
            adminRemoteSecurityService.securityCheck(sectionClassName, EntityOperationType.ADD);
        } catch (ServiceException e) {
            if (e instanceof SecurityServiceException) {
                canCreate = false;
            }
        }

        if (canCreate) {
            mainActions.add(DefaultMainActions.ADD);
        }
        
        model.addAttribute("entityFriendlyName", cmd.getPolymorphicEntities().getFriendlyName());
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("listGrid", listGrid);
        model.addAttribute("mainActions", mainActions);
        model.addAttribute("viewType", "entityList");

        setModelAttributes(model, sectionKey);
        return "modules/defaultContainer";
    }

    /**
     * Renders the modal form that is used to add a new parent level entity. Note that this form cannot render any
     * subcollections as operations on those collections require the parent level entity to first be saved and have 
     * and id. Once the entity is initially saved, we will redirect the user to the normal manage entity screen where 
     * they can then perform operations on sub collections.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param entityType
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @RequestParam(defaultValue = "") String entityType) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);

        ClassMetadata cmd = service.getClassMetadata(getSectionPersistencePackageRequest(sectionClassName));

        // If the entity type isn't specified, we need to determine if there are various polymorphic types for this entity.
        if (StringUtils.isBlank(entityType)) {
            if (cmd.getPolymorphicEntities().getChildren().length == 0) {
                entityType = cmd.getPolymorphicEntities().getFullyQualifiedClassname();
            } else {
                entityType = getDefaultEntityType();
            }
        } else {
            entityType = URLDecoder.decode(entityType, "UTF-8");
        }

        // If we still don't have a type selected, that means that there were indeed multiple possible types and we 
        // will be allowing the user to pick his desired type.
        if (StringUtils.isBlank(entityType)) {
            List<ClassTree> entityTypes = getAddEntityTypes(cmd.getPolymorphicEntities());
            model.addAttribute("entityTypes", entityTypes);
            model.addAttribute("viewType", "modal/entityTypeSelection");
        } else {
            EntityForm entityForm = formService.createEntityForm(cmd);
            
            // We need to make sure that the ceiling entity is set to the interface and the specific entity type
            // is set to the type we're going to be creating.
            entityForm.setCeilingEntityClassname(cmd.getCeilingType());
            entityForm.setEntityType(entityType);
            
            // When we initially build the class metadata (and thus, the entity form), we had all of the possible
            // polymorphic fields built out. Now that we have a concrete entity type to render, we can remove the
            // fields that are not applicable for this given entity type.
            formService.removeNonApplicableFields(cmd, entityForm, entityType);

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/entityAdd");
        }

        model.addAttribute("entityFriendlyName", cmd.getPolymorphicEntities().getFriendlyName());
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "addEntity");
        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    /**
     * Processes the request to add a new entity. If successful, returns a redirect to the newly created entity.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param entityForm
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        String sectionKey = getSectionKey(pathVars);

        Entity entity = service.addEntity(entityForm, getSectionCustomCriteria());
        entityValidator.validate(entityForm, entity, result);

        if (result.hasErrors()) {
            ClassMetadata cmd = service.getClassMetadata(getSectionPersistencePackageRequest(entityForm.getEntityType()));
            entityForm.clearFieldsMap();
            formService.populateEntityForm(cmd, entity, entityForm);

            formService.removeNonApplicableFields(cmd, entityForm, entityForm.getEntityType());

            model.addAttribute("viewType", "modal/entityAdd");
            model.addAttribute("currentUrl", request.getRequestURL().toString());
            model.addAttribute("modalHeaderType", "addEntity");
            setModelAttributes(model, sectionKey);
            return "modules/modalContainer";
        }
        
        // Note that AJAX Redirects need the context path prepended to them
        return "ajaxredirect:" + getContextPath(request) + sectionKey + "/" + entity.getPMap().get("id").getValue();
    }

    /**
     * Renders the main entity form for the specified entity
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param modal - whether or not to show the entity in a read-only modal
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable("id") String id) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName);

        ClassMetadata cmd = service.getClassMetadata(ppr);
        Entity entity = service.getRecord(ppr, id, cmd);
        
        Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity);

        EntityForm entityForm = formService.createEntityForm(cmd, entity, subRecordsMap);
        
        model.addAttribute("entity", entity);
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("currentUrl", request.getRequestURL().toString());

        setModelAttributes(model, sectionKey);

        boolean readable = false;
        for (Property property : cmd.getProperties()) {
            FieldMetadata fieldMetadata = property.getMetadata();
            if (fieldMetadata instanceof BasicFieldMetadata) {
                if (!((BasicFieldMetadata) fieldMetadata).getReadOnly()) {
                    readable = true;
                    break;
                }
            } else {
                if (((CollectionMetadata) fieldMetadata).isMutable()) {
                    readable = true;
                    break;
                }
            }
        }
        if (!readable) {
            entityForm.setReadOnly();
        }

        // If the user does not have edit permissions, we will go ahead and make the form read only to prevent confusion
        try {
            adminRemoteSecurityService.securityCheck(sectionClassName, EntityOperationType.UPDATE);
        } catch (ServiceException e) {
            if (e instanceof SecurityServiceException) {
                entityForm.setReadOnly();
            }
        }

        if (isAjaxRequest(request)) {
            entityForm.setReadOnly();
            model.addAttribute("viewType", "modal/entityView");
            model.addAttribute("modalHeaderType", "viewEntity");
            return "modules/modalContainer";
        } else {
            model.addAttribute("viewType", "entityEdit");
            return "modules/defaultContainer";
        }
    }

    /**
     * Attempts to save the given entity. If validation is unsuccessful, it will re-render the entity form with
     * error fields highlighted. On a successful save, it will refresh the entity page.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param entityForm
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName);

        Map<String, Field> dynamicFields = new HashMap<String, Field>();
        
        // Find all of the dynamic form fields
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            if (entry.getKey().contains("|")) { 
                dynamicFields.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Remove the dynamic form fields from the main entity - they are persisted separately
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            entityForm.removeField(entry.getKey());
        }
        
        // Create the entity form for the dynamic form, as it needs to be persisted separately
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            String[] fieldName = entry.getKey().split("\\|");
            DynamicEntityFormInfo info = entityForm.getDynamicFormInfo(fieldName[0]);
                    
            EntityForm dynamicForm = entityForm.getDynamicForm(fieldName[0]);
            if (dynamicForm == null) {
                dynamicForm = new EntityForm();
                dynamicForm.setCeilingEntityClassname(info.getCeilingClassName());
                entityForm.putDynamicForm(fieldName[0], dynamicForm);
            }
            
            entry.getValue().setName(fieldName[1]);
            dynamicForm.addField(entry.getValue());
        }

        Entity entity = service.updateEntity(entityForm, getSectionCustomCriteria());
        
        entityValidator.validate(entityForm, entity, result);
        if (result.hasErrors()) {
            Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity);
            ClassMetadata cmd = service.getClassMetadata(ppr);
            entityForm.clearFieldsMap();
            formService.populateEntityForm(cmd, entity, subRecordsMap, entityForm);
            
            model.addAttribute("entity", entity);
            model.addAttribute("currentUrl", request.getRequestURL().toString());

            setModelAttributes(model, sectionKey);
            
            if (isAjaxRequest(request)) {
                entityForm.setReadOnly();
                model.addAttribute("viewType", "modal/entityView");
                model.addAttribute("modalHeaderType", "viewEntity");
                return "modules/modalContainer";
            } else {
                model.addAttribute("viewType", "entityEdit");
                return "modules/defaultContainer";
            }
        }
        
        ra.addFlashAttribute("headerFlash", "save.successful");
        
        return "redirect:/" + sectionKey + "/" + id;
    }

    /**
     * Attempts to remove the given entity.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String removeEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        service.removeEntity(entityForm, getSectionCustomCriteria());

        return "redirect:/" + sectionKey;
    }

    /**
     * Shows the modal dialog that is used to select a "to-one" collection item. For example, this could be used to show
     * a list of categories for the ManyToOne field "defaultCategory" in Product.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param owningClass
     * @param collectionField
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{owningClass:.*}/{collectionField:.*}/select", method = RequestMethod.GET)
    public String showSelectCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value = "owningClass") String owningClass,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(owningClass, requestParams);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        ppr = PersistencePackageRequest.fromMetadata(md);
        
        ppr.addFilterAndSortCriteria(getCriteria(requestParams));
        ppr.setStartIndex(getStartIndex(requestParams));
        ppr.setMaxIndex(getMaxIndex(requestParams));
        
        if (md instanceof BasicFieldMetadata) {
            DynamicResultSet drs = service.getRecords(ppr);
            ListGrid listGrid = formService.buildCollectionListGrid(null, drs, collectionProperty, owningClass);

            model.addAttribute("listGrid", listGrid);
            model.addAttribute("viewType", "modal/simpleSelectEntity");
        }

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "selectCollectionItem");
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, owningClass);
        return "modules/modalContainer";
    }
    
    /**
     * Shows the modal popup for the current selected "to-one" field. For instance, if you are viewing a list of products
     * then this method is invoked when a user clicks on the name of the default category field.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param collectionField
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{collectionField:.*}/{id}/view", method = RequestMethod.GET)
    public String viewCollectionItemDetails(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="id") String id) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        BasicFieldMetadata md = (BasicFieldMetadata) collectionProperty.getMetadata();

        AdminSection section = adminNavigationService.findAdminSectionByClass(md.getForeignKeyClass());
        String sectionUrlKey = (section.getUrl().startsWith("/")) ? section.getUrl().substring(1) : section.getUrl();
        Map<String, String> varsForField = new HashMap<String, String>();
        varsForField.put("sectionKey", sectionUrlKey);
        return viewEntityForm(request, response, model, varsForField, id);
    }

    /**
     * Returns the records for a given collectionField filtered by a particular criteria
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param collectionField
     * @param criteriaForm
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}", method = RequestMethod.GET)
    public String getCollectionFieldRecords(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, requestParams);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        
        ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, requestParams, sectionKey);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }

    /**
     * Shows the modal dialog that is used to add an item to a given collection. There are several possible outcomes
     * of this call depending on the type of the specified collection field.
     * 
     * <ul>
     *  <li>
     *    <b>Basic Collection (Persist)</b> - Renders a blank form for the specified target entity so that the user may
     *    enter information and associate the record with this collection. Used by fields such as ProductAttribute.
     *  </li>
     *  <li>
     *    <b>Basic Collection (Lookup)</b> - Renders a list grid that allows the user to click on an entity and select it. 
     *    Used by fields such as "allParentCategories".
     *  </li>
     *  <li>
     *    <b>Adorned Collection (without form)</b> - Renders a list grid that allows the user to click on an entity and 
     *    select it. The view rendered by this is identical to basic collection (lookup), but will perform the operation
     *    on an adorned field, which may carry extra meta-information about the created relationship, such as order.
     *  </li>
     *  <li>
     *    <b>Adorned Collection (with form)</b> - Renders a list grid that allows the user to click on an entity and 
     *    select it. Once the user selects the entity, he will be presented with an empty form based on the specified
     *    "maintainedAdornedTargetFields" for this field. Used by fields such as "crossSellProducts", which in addition
     *    to linking an entity, provide extra fields, such as a promotional message.
     *  </li>
     *  <li>
     *    <b>Map Collection</b> - Renders a form for the target entity that has an additional key field. This field is
     *    populated either from the configured map keys, or as a result of a lookup in the case of a key based on another
     *    entity. Used by fields such as the mediaMap on a Sku.
     *  </li>
     *  
     * @param request
     * @param response
     * @param model
     * @param sectionKey
     * @param id
     * @param collectionField
     * @param requestParams
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        //service.getContextSpecificRelationshipId(mainMetadata, entity, prefix);

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md)
                .withFilterAndSortCriteria(getCriteria(requestParams))
                .withStartIndex(getStartIndex(requestParams))
                .withMaxIndex(getMaxIndex(requestParams));

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            // When adding items to basic collections, we will sometimes show a form to persist a new record
            // and sometimes show a list grid to allow the user to associate an existing record.
            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
                EntityForm entityForm = formService.createEntityForm(collectionMetadata);

                entityForm.getTabs().iterator().next().getIsVisible();

                model.addAttribute("entityForm", entityForm);
                model.addAttribute("viewType", "modal/simpleAddEntity");
            } else {
                DynamicResultSet drs = service.getRecords(ppr);
                ListGrid listGrid = formService.buildCollectionListGrid(id, drs, collectionProperty, sectionKey);
                listGrid.setPathOverride(request.getRequestURL().toString());

                model.addAttribute("listGrid", listGrid);
                model.addAttribute("viewType", "modal/simpleSelectEntity");
            }
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;

            // Even though this field represents an adorned target collection, the list we want to show in the modal
            // is the standard list grid for the target entity of this field
            ppr.setOperationTypesOverride(null);
            ppr.setType(PersistencePackageRequest.Type.STANDARD);

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);

            DynamicResultSet drs = service.getRecords(ppr);
            ListGrid listGrid = formService.buildMainListGrid(drs, collectionMetadata, sectionKey);
            listGrid.setSubCollectionFieldName(collectionField);
            listGrid.setPathOverride(request.getRequestURL().toString());
            listGrid.setFriendlyName(collectionMetadata.getPolymorphicEntities().getFriendlyName());
            EntityForm entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);

            if (fmd.getMaintainedAdornedTargetFields().length > 0) {
                listGrid.setListGridType(ListGrid.Type.ADORNED_WITH_FORM);
            } else {
                listGrid.setListGridType(ListGrid.Type.ADORNED);
            }

            model.addAttribute("listGrid", listGrid);
            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/adornedSelectEntity");
        } else if (md instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) md;
            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);

            EntityForm entityForm = formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id);
            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/mapAddEntity");
        }

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "addCollectionItem");
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    /**
     * Shows the appropriate modal dialog to edit the selected collection item
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param collectionItemId
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId,"updateCollectionItem");
    }

    /**
     * Shows the appropriate modal dialog to edit the selected collection item
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param collectionItemId
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/view", method = RequestMethod.GET)
    public String showViewCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId,"viewCollectionItem");
    }

    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
                                              String id, String collectionField, String collectionItemId,
                                              String modalHeaderType) throws ServiceException {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata);

        ppr = PersistencePackageRequest.fromMetadata(md);

        if (md instanceof BasicCollectionMetadata &&
                ((BasicCollectionMetadata) md).getAddMethodType().equals(AddMethodType.PERSIST)) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
            Entity entity = service.getRecord(ppr, collectionItemId, collectionMetadata);

            Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity);

            EntityForm entityForm = formService.createEntityForm(collectionMetadata, entity, subRecordsMap);

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/simpleEditEntity");
        } else if (md instanceof AdornedTargetCollectionMetadata &&
                ((AdornedTargetCollectionMetadata) md).getMaintainedAdornedTargetFields().length > 0) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;

            EntityForm entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);
            Entity entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId);

            formService.populateEntityFormFields(entityForm, entity);
            formService.populateAdornedEntityFormFields(entityForm, entity, ppr.getAdornedList());

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/adornedEditEntity");
        } else if (md instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) md;

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
            Entity entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId);
            EntityForm entityForm = formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id);

            formService.populateEntityFormFields(entityForm, entity);
            formService.populateMapEntityFormFields(entityForm, entity);

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/mapEditEntity");
        }

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", modalHeaderType);
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, sectionKey);
        return "modules/modalContainer";
    }

    /**
     * Adds the requested collection item
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param entityForm
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/add", method = RequestMethod.POST)
    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @ModelAttribute(value="entityForm") EntityForm entityForm) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata);
        
        // First, we must save the collection entity
        service.addSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }

    /**
     * Updates the specified collection item
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param entityForm
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @ModelAttribute(value="entityForm") EntityForm entityForm) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata);
        
        // First, we must save the collection entity
        service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, collectionItemId);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }
    
    /**
     * Updates the given colleciton item's sequence. This should only be triggered for adorned target collections
     * where a sort field is specified -- any other invocation is incorrect and will result in an exception.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param collectionItemId
     * @return an object explaining the state of the operation
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/sequence", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> updateCollectionItemSequence(HttpServletRequest request, 
            HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @RequestParam(value="newSequence") String newSequence) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata);
        
        ppr = PersistencePackageRequest.fromMetadata(md);
        
        if (md instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;
            AdornedTargetList atl = ppr.getAdornedList();
            
            // Get an entity form for the entity
            EntityForm entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);
            Entity entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty, 
                    collectionItemId);
            formService.populateEntityFormFields(entityForm, entity);
            formService.populateAdornedEntityFormFields(entityForm, entity, ppr.getAdornedList());
            
            // Set the new sequence (note that it will come in 0-indexed but the persistence module expects 1-indexed)
            int sequenceValue = Integer.parseInt(newSequence) + 1;
            Field field = entityForm.findField(atl.getSortField());
            field.setValue(String.valueOf(sequenceValue));
            
            Map<String, Object> responseMap = new HashMap<String, Object>();
            service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, collectionItemId);
            responseMap.put("status", "ok");
            responseMap.put("field", collectionField);
            return responseMap;
        } else {
            throw new UnsupportedOperationException("Cannot handle sequencing for non adorned target collection fields.");
        }
    }

    /**
     * Removes the requested collection item
     * 
     * Note that the request must contain a parameter called "key" when attempting to remove a collection item from a 
     * map collection.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param collectionItemId
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        String priorKey = request.getParameter("key");
        
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata);

        // First, we must remove the collection entity
        service.removeSubCollectionEntity(mainMetadata, collectionProperty, entity, collectionItemId, priorKey);

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }
    
    // *********************************
    // ADDITIONAL SPRING-BOUND METHODS *
    // *********************************
    
    /**
     * Invoked on every request to provide the ability to register specific binders for Spring's binding process.
     * By default, we register a binder that treats empty Strings as null and a Boolean editor that supports either true
     * or false. If the value is passed in as null, it will treat it as false.
     * 
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Boolean.class, new NonNullBooleanEditor());
    }
    
    // *********************************************************
    // UNBOUND CONTROLLER METHODS (USED BY DIFFERENT SECTIONS) *
    // *********************************************************
    
    /**
     * Returns a partial representing a dynamic form. An example of this is the dynamic fields that render
     * on structured content, which are determined by the currently selected structured content type. This 
     * method is typically only invoked through Javascript and used to replace the current dynamic form with
     * the one for the newly selected type.
     * 
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param info
     * @return the return view path
     * @throws Exception
     */
    protected String getDynamicForm(HttpServletRequest request, HttpServletResponse response, Model model,
            Map<String, String> pathVars,
            DynamicEntityFormInfo info) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        EntityForm blankFormContainer = new EntityForm();
        EntityForm dynamicForm = getBlankDynamicFieldTemplateForm(info);

        blankFormContainer.putDynamicForm(info.getPropertyName(), dynamicForm);
        model.addAttribute("entityForm", blankFormContainer);
        model.addAttribute("dynamicPropertyName", info.getPropertyName());
        
        String reqUrl = request.getRequestURL().toString();
        reqUrl = reqUrl.substring(0, reqUrl.indexOf("/dynamicForm"));
        model.addAttribute("currentUrl", reqUrl);
        
        setModelAttributes(model, sectionKey);
        return "views/dynamicFormPartial";
    }
    
    // **********************************
    // HELPER METHODS FOR BUILDING DTOS *
    // **********************************

    /**
     * Convenience method for obtaining a ListGrid DTO object for a collection. Note that if no <b>criteria</b> is
     * available, then this should be null (or empty)
     * 
     * @param mainMetadata class metadata for the root entity that this <b>collectionProperty</b> relates to
     * @param id foreign key from the root entity for <b>collectionProperty</b>
     * @param collectionProperty property that this collection should be based on from the root entity
     * @param form the criteria form model attribute
     * @param sectionKey the current main section key
     * @return the list grid
     * @throws ServiceException
     */
    protected ListGrid getCollectionListGrid(ClassMetadata mainMetadata, Entity entity, Property collectionProperty,
            MultiValueMap<String, String> requestParams, String sectionKey)
            throws ServiceException {
        DynamicResultSet drs = service.getRecordsForCollection(mainMetadata, entity, collectionProperty,
                getCriteria(requestParams), getStartIndex(requestParams), getMaxIndex(requestParams));

        String idProperty = service.getIdProperty(mainMetadata);
        ListGrid listGrid = formService.buildCollectionListGrid(entity.findProperty(idProperty).getValue(), drs, 
                collectionProperty, sectionKey);
        listGrid.setListGridType(ListGrid.Type.INLINE);

        return listGrid;
    }
    
    /**
     * Convenience method for obtaining a blank dynamic field template form. For example, if the main entity form should 
     * render different fields depending on the value of a specific field in that main form itself, the "dynamic" fields
     * are generated by this method. Because this is invoked when a new value is chosen, the form generated by this method
     * will never have values set.
     * 
     * @param info
     * @return the entity form
     * @throws ServiceException
     */
    protected EntityForm getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo info) 
            throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(),  info.getPropertyValue() });
        ClassMetadata cmd = service.getClassMetadata(ppr);
        
        EntityForm dynamicForm = formService.createEntityForm(cmd);
        
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + "|" + field.getName());
                }
            }
        }
    
        return dynamicForm;
    }
    
    /**
     * Convenience method for obtaining a dynamic field template form for a particular entity. This method differs from
     * {@link #getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo)} in that it will fill out the current values for 
     * the fields in this dynamic form from the database. This method is invoked when the initial view of a page containing
     * a dynamic form is triggered.
     * 
     * @param info
     * @param entityId
     * @return the entity form
     * @throws ServiceException
     */
    protected EntityForm getDynamicFieldTemplateForm(DynamicEntityFormInfo info, String entityId) 
            throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(),  info.getPropertyValue() });
        ClassMetadata cmd = service.getClassMetadata(ppr);
        
        // However, when we fetch, the second custom criteria needs to be the id
        // of this particular structured content entity
        ppr.setCustomCriteria(new String[] { info.getCriteriaName(), entityId });
        Entity entity = service.getRecord(ppr, entityId, cmd);
        
        // Assemble the dynamic form for structured content type
        EntityForm dynamicForm = formService.createEntityForm(cmd, entity);
        
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + "|" + field.getName());
                }
            }
        }
    
        return dynamicForm;
    }
    
    // ***********************************************
    // HELPER METHODS FOR SECTION-SPECIFIC OVERRIDES *
    // ***********************************************
    
    /**
     * This method is used to determine the current section key. For this default implementation, the sectionKey is pulled
     * from the pathVariable, {sectionKey}, as defined by the request mapping on this controller. To support controller
     * inheritance and allow more specialized controllers to delegate some methods to this basic controller, overridden
     * implementations of this method could return a hardcoded value instead of reading the map
     * 
     * @param pathVars - the map of all currently bound path variables for this request
     * @return the sectionKey for this request
     */
    protected String getSectionKey(Map<String, String> pathVars) {
        return pathVars.get("sectionKey");
    }
    
    /**
     * <p>Helper method to return an array of {@link org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria} based on a map of propertyName -> list of criteria
     * value. This will also grab the sorts off of the request parameters, if any.</p>
     * 
     * <p>The multi-valued map allows users to specify multiple criteria values per property, as well as multiple sort
     * properties and sort directions. For multiple sort properties and sort directions, these would usually come in as
     * request parameters like:
     * <br />
     * <br />
     * ....?sortProperty=defaultSku.name&sortProperty=manufacturer&sortDirection=ASCENDING&sortDirection=DESCENDING
     * <br />
     * <br />
     * This would attach criteria such that defaultSku.name was sorted ascending, and manufacturer was sorted descending</p>
     * 
     * @param requestParams usually a {@link MultiValueMap} that has been bound by a controller to receive all of the
     * request parameters that are not explicitly named
     * @return the final array of {@link org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria} to pass to the fetch
     * 
     * @see {@link #getSortPropertyNames(Map)}
     * @see {@link #getSortDirections(Map)}
     */
    protected FilterAndSortCriteria[] getCriteria(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }
        
        List<FilterAndSortCriteria> result = new ArrayList<FilterAndSortCriteria>();
        for (Entry<String, List<String>> entry : requestParams.entrySet()) {
            if (!entry.getKey().equals(FilterAndSortCriteria.SORT_PROPERTY_PARAMETER) &&
                    !entry.getKey().equals(FilterAndSortCriteria.SORT_DIRECTION_PARAMETER)) {
                FilterAndSortCriteria fasCriteria = new FilterAndSortCriteria(entry.getKey(), entry.getValue());
                result.add(fasCriteria);
            }
        }

        List<String> sortProperties = getSortPropertyNames(requestParams);
        List<String> sortDirections = getSortDirections(requestParams);
        if (CollectionUtils.isNotEmpty(sortProperties)) {
            //set up a map to determine if there is already some criteria set for the sort property
            Map<String, FilterAndSortCriteria> fasMap = BLCMapUtils.keyedMap(result, new TypedClosure<String, FilterAndSortCriteria>() {
    
                @Override
                public String getKey(FilterAndSortCriteria value) {
                    return value.getPropertyId();
                }
            });
            for (int i = 0; i < sortProperties.size(); i++) {
                boolean sortAscending = SortDirection.ASCENDING.toString().equals(sortDirections.get(i));
                FilterAndSortCriteria propertyCriteria = fasMap.get(sortProperties.get(i));
                //If there is already criteria for this property, attach the sort to that. Otherwise, create some new
                //FilterAndSortCriteria for the sort
                if (propertyCriteria != null) {
                    propertyCriteria.setSortAscending(sortAscending);
                } else {
                    FilterAndSortCriteria fasc = new FilterAndSortCriteria(sortProperties.get(i));
                    fasc.setSortAscending(sortAscending);
                    result.add(fasc);
                }
            }
        }
        
        return result.toArray(new FilterAndSortCriteria[result.size()]);
    }
    
    /**
     * Obtains the requested start index parameter
     * 
     * @param requestParams
     * @return
     */
    protected Integer getStartIndex(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }
        
        List<String> startIndex = requestParams.get(FilterAndSortCriteria.START_INDEX_PARAMETER);
        return CollectionUtils.isEmpty(startIndex) ? null : Integer.parseInt(startIndex.get(0));
    }
    
    /**
     * Obtains the requested max index parameter
     * 
     * @param requestParams
     * @return
     */
    protected Integer getMaxIndex(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }
        
        List<String> maxIndex = requestParams.get(FilterAndSortCriteria.MAX_INDEX_PARAMETER);
        return CollectionUtils.isEmpty(maxIndex) ? null : Integer.parseInt(maxIndex.get(0));
    }

    /**
     * Obtains the list of sort directions from the bound request parameters. Note that these should appear in the same
     * relative order as {@link #getSortPropertyNames(Map)}
     * 
     * @param requestParams
     * @return
     */
    protected List<String> getSortDirections(Map<String, List<String>> requestParams) {
        List<String> sortTypes = requestParams.get(FilterAndSortCriteria.SORT_DIRECTION_PARAMETER);
        return sortTypes;
    }
    
    /**
     * Obtains the list of property names to sort on from the bound request parameters. Note that these should appear in the
     * same relative order as {@link #getSortDirections(Map)}.
     * 
     * @param requestParams
     * @return
     */
    protected List<String> getSortPropertyNames(Map<String, List<String>> requestParams) {
        return requestParams.get(FilterAndSortCriteria.SORT_PROPERTY_PARAMETER);
    }

    /**
     * Gets the fully qualified ceiling entity classname for this section. If this section is not explicitly defined in
     * the database, will return the value passed into this function. For example, if there is a mapping from "/myentity" to
     * "com.mycompany.myentity", both "http://localhost/myentity" and "http://localhost/com.mycompany.myentity" are valid
     * request paths.
     * 
     * @param sectionKey
     * @return the className for this sectionKey if found in the database or the sectionKey if not
     */
    protected String getClassNameForSection(String sectionKey) {
        AdminSection section = adminNavigationService.findAdminSectionByURI("/" + sectionKey);
        return (section == null) ? sectionKey : section.getCeilingEntity();
    }

    /**
     * If there are certain types of entities that should not be allowed to be created, an override of this method would be
     * able to specify that. It could also add additional types if desired.
     * 
     * @param classTree
     * @returna a List<ClassTree> representing all potentially avaialble entity types to create a product for.
     */
    protected List<ClassTree> getAddEntityTypes(ClassTree classTree) {
        return classTree.getCollapsedClassTrees();
    }

    /**
     * This method is called when attempting to add new entities that have a polymorphic tree. 
     * 
     * If this method returns null, there is no default type set for this particular entity type, and the user will be 
     * presented with a selection of possible types to utilize.
     * 
     * If it returns a non-null value, the returned fullyQualifiedClassname will be used and will bypass the selection step.
     * 
     * @return null if there is no default type, otherwise the default type
     */
    protected String getDefaultEntityType() {
        return null;
    }
    
    /**
     * This method is invoked for every request for this controller. By default, we do not want to specify a custom
     * criteria, but specialized controllers may want to.
     * 
     * @return the custom criteria for this section for all requests, if any
     */
    protected String[] getSectionCustomCriteria() {
        return null;
    }
    
    /**
     * A hook method that is invoked every time the {@link #getSectionPersistencePackageRequest(String)} method is invoked.
     * This allows specialized controllers to hook into every request and manipulate the persistence package request as
     * desired.
     * 
     * @param ppr
     */
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr) {
        
    }

    // ************************
    // GENERIC HELPER METHODS *
    // ************************
    
    /**
     * Attributes to add to the model on every request
     * 
     * @param model
     * @param sectionKey
     */
    protected void setModelAttributes(Model model, String sectionKey) {
        AdminSection section = adminNavigationService.findAdminSectionByURI("/" + sectionKey);

        if (section != null) {
            model.addAttribute("sectionKey", sectionKey);
            model.addAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME, section.getModule());
            model.addAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME, section);
        }
    }

    /**
     * Returns a PersistencePackageRequest for the given sectionClassName. Will also invoke the 
     * {@link #getSectionCustomCriteria()} and {@link #attachSectionSpecificInfo(PersistencePackageRequest)} to allow
     * specialized controllers to manipulate the request for every action in this controller.
     * 
     * @param sectionClassName
     * @return the PersistencePacakageRequest
     */
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName) {
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(sectionClassName)
                .withCustomCriteria(getSectionCustomCriteria());
        
        attachSectionSpecificInfo(ppr);
        
        return ppr;
    }

    /**
     * Returns the result of a call to {@link #getSectionPersistencePackageRequest(String)} with the additional filter
     * and sort criteria attached.
     * 
     * @param sectionClassName
     * @param filterAndSortCriteria
     * @return the PersistencePacakageRequest
     */
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName, 
            MultiValueMap<String, String> requestParams) {
        FilterAndSortCriteria[] fascs = getCriteria(requestParams);
        return getSectionPersistencePackageRequest(sectionClassName)
                .withFilterAndSortCriteria(fascs)
                .withStartIndex(getStartIndex(requestParams))
                .withMaxIndex(getMaxIndex(requestParams));
    }
    
}
