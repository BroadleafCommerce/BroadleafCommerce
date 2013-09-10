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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
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
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.broadleafcommerce.openadmin.web.editor.NonNullBooleanEditor;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultMainActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        addAddActionIfAllowed(sectionClassName, cmd, mainActions);
        
        Field firstField = listGrid.getHeaderFields().iterator().next();
        if (requestParams.containsKey(firstField.getName())) {
            model.addAttribute("mainSearchTerm", requestParams.get(firstField.getName()).get(0));
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
     * Adds the "Add" button to the main entity form if the current user has permissions to create new instances
     * of the entity and all of the fields in the entity aren't marked as read only.
     * 
     * @param sectionClassName
     * @param cmd
     * @param mainActions
     */
    protected void addAddActionIfAllowed(String sectionClassName, ClassMetadata cmd, List<EntityFormAction> mainActions) {
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
            checkReadOnly: {
                //check if all the metadata is read only
                for (Property property : cmd.getProperties()) {
                    if (property.getMetadata() instanceof BasicFieldMetadata) {
                        if (((BasicFieldMetadata) property.getMetadata()).getReadOnly() == null ||
                                !((BasicFieldMetadata) property.getMetadata()).getReadOnly()) {
                            break checkReadOnly;
                        }
                    }
                }
                canCreate = false;
            }
        }
        if (canCreate) {
            mainActions.add(DefaultMainActions.ADD);
        }
        
        mainEntityActionsExtensionManager.modifyMainActions(cmd, mainActions);
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

        extractDynamicFormFields(entityForm);
        
        Entity entity = service.addEntity(entityForm, getSectionCustomCriteria());
        entityFormValidator.validate(entityForm, entity, result);

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
        Entity entity = service.getRecord(ppr, id, cmd, false);
        
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

        extractDynamicFormFields(entityForm);
        
        Entity entity = service.updateEntity(entityForm, getSectionCustomCriteria());
        
        entityFormValidator.validate(entityForm, entity, result);
        if (result.hasErrors()) {
            model.addAttribute("headerFlash", "save.unsuccessful");
            model.addAttribute("headerFlashAlert", true);
            
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

    @RequestMapping(value = "/{owningClass:.*}/{collectionField:.*}/details", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getCollectionValueDetails(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value = "owningClass") String owningClass,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam String ids,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(owningClass, requestParams);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr);
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        ppr = PersistencePackageRequest.fromMetadata(md);
        ppr.setStartIndex(getStartIndex(requestParams));
        ppr.setMaxIndex(getMaxIndex(requestParams));
        
        if (md instanceof BasicFieldMetadata) {
            String idProp = ((BasicFieldMetadata) md).getForeignKeyProperty();
            String displayProp = ((BasicFieldMetadata) md).getForeignKeyDisplayValueProperty();

            List<String> filterValues = Arrays.asList(ids.split(FILTER_VALUE_SEPARATOR_REGEX));
            ppr.addFilterAndSortCriteria(new FilterAndSortCriteria(idProp, filterValues));
            
            DynamicResultSet drs = service.getRecords(ppr);
            Map<String, String> returnMap = new HashMap<String, String>();
            
            for (Entity e : drs.getRecords()) {
                String id = e.getPMap().get(idProp).getValue();
                String disp = e.getPMap().get(displayProp).getDisplayValue();
                
                if (StringUtils.isBlank(disp)) {
                    disp = e.getPMap().get(displayProp).getValue();
                }
                
                returnMap.put(id,  disp);
            }

            return returnMap;
        }

        return null;
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
        Entity entity = service.getRecord(ppr, id, mainMetadata, false);

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
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
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

        return buildAddCollectionItemModel(request, response, model, id, collectionField, sectionKey, collectionProperty, md, ppr, null, null);
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
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false);
        
        // First, we must save the collection entity
        Entity savedEntity = service.addSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity);
        entityFormValidator.validate(entityForm, savedEntity, result);
        
        if (result.hasErrors()) {
            FieldMetadata md = collectionProperty.getMetadata();
            ppr = PersistencePackageRequest.fromMetadata(md);
            return buildAddCollectionItemModel(request, response, model, id, collectionField, sectionKey, collectionProperty,
                    md, ppr, entityForm, savedEntity);
        }

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }

    /**
     * Builds out all of the model information needed for showing the add modal for collection items on both the initial GET
     * as well as after a POST with validation errors
     * 
     * @param request
     * @param model
     * @param id
     * @param collectionField
     * @param sectionKey
     * @param collectionProperty
     * @param md
     * @param ppr
     * @return the appropriate view to display for the modal
     * @see {@link #addCollectionItem(HttpServletRequest, HttpServletResponse, Model, Map, String, String, EntityForm, BindingResult)}
     * @see {@link #showAddCollectionItem(HttpServletRequest, HttpServletResponse, Model, Map, String, String, MultiValueMap)}
     * @throws ServiceException
     */
    protected String buildAddCollectionItemModel(HttpServletRequest request, HttpServletResponse response,
            Model model,
            String id,
            String collectionField,
            String sectionKey,
            Property collectionProperty,
            FieldMetadata md, PersistencePackageRequest ppr, EntityForm entityForm, Entity entity) throws ServiceException {
        
        if (entityForm != null) {
            entityForm.clearFieldsMap();
        }
        
        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            // When adding items to basic collections, we will sometimes show a form to persist a new record
            // and sometimes show a list grid to allow the user to associate an existing record.
            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
                if (entityForm == null) {
                    entityForm = formService.createEntityForm(collectionMetadata);
                } else {
                    formService.populateEntityForm(collectionMetadata, entityForm);
                    formService.populateEntityFormFieldValues(collectionMetadata, entity, entityForm);
                }
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
            if (entityForm == null) {
                entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);
            } else {
                formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id, entityForm);
                formService.populateEntityFormFieldValues(collectionMetadata, entity, entityForm);
            }
            
            listGrid.setListGridType(ListGrid.Type.ADORNED);
            for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
                if (entry.getValue().getIsVisible()) {
                    listGrid.setListGridType(ListGrid.Type.ADORNED_WITH_FORM);
                    break;
                }
            }

            model.addAttribute("listGrid", listGrid);
            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/adornedSelectEntity");
        } else if (md instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) md;
            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
            
            if (entityForm == null) {
                entityForm = formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id);
            } else {
                formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id, entityForm);
                formService.populateEntityFormFieldValues(collectionMetadata, entity, entityForm);
            }
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
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, 
                "updateCollectionItem");
    }

    /**
     * Shows the appropriate modal dialog to view the selected collection item. This will display the modal as readonly
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
        String returnPath = showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, 
                "viewCollectionItem");
        
        // Since this is a read-only view, actions don't make sense in this context
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        ef.removeAllActions();
        
        return returnPath;
    }
    
    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
            String id, String collectionField, String collectionItemId, String modalHeaderType) throws ServiceException {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, modalHeaderType, null, null);
    }

    /**
     * Shows the view and populates the model for updating a collection item. You can also pass in an entityform and entity
     * which are optional. If they are not passed in then they are automatically looked up
     * 
     * @param request
     * @param model
     * @param pathVars
     * @param id
     * @param collectionField
     * @param collectionItemId
     * @param modalHeaderType
     * @param ef
     * @param entity
     * @return
     * @throws ServiceException
     */
    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
            String id, String collectionField, String collectionItemId, String modalHeaderType, EntityForm entityForm, Entity entity) throws ServiceException {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata, false);

        ppr = PersistencePackageRequest.fromMetadata(md);
        
        if (md instanceof BasicCollectionMetadata &&
                ((BasicCollectionMetadata) md).getAddMethodType().equals(AddMethodType.PERSIST)) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
            if (entity == null) {
                entity = service.getRecord(ppr, collectionItemId, collectionMetadata, true);
            }

            Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity);
            if (entityForm == null) {
                entityForm = formService.createEntityForm(collectionMetadata, entity, subRecordsMap);
            } else {
                entityForm.clearFieldsMap();
                formService.populateEntityForm(collectionMetadata, entity, subRecordsMap, entityForm);
                //remove all the actions since we're not trying to redisplay them on the form
                entityForm.removeAllActions();
            }
            entityForm.removeAction(DefaultEntityFormActions.DELETE);

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/simpleEditEntity");
        } else if (md instanceof AdornedTargetCollectionMetadata &&
                ((AdornedTargetCollectionMetadata) md).getMaintainedAdornedTargetFields().length > 0) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;

            if (entity == null) {
                entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId);
            }
            
            boolean populateTypeAndId = true;
            if (entityForm == null) {
                entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);
            } else {
                entityForm.clearFieldsMap();
                String entityType = entityForm.getEntityType();
                formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id, entityForm);
                entityForm.setEntityType(entityType);
                populateTypeAndId = false;
            }

            ClassMetadata cmd = service.getClassMetadata(ppr);
            for (String field : fmd.getMaintainedAdornedTargetFields()) {
                Property p = cmd.getPMap().get(field);
                if (p != null && p.getMetadata() instanceof AdornedTargetCollectionMetadata) {
                    // Because we're dealing with a nested adorned target collection, this particular request must act
                    // directly on the first adorned target collection. Because of this, we need the actual id property
                    // from the entity that models the adorned target relationship, and not the id of the target object.
                    Property alternateIdProperty = entity.getPMap().get(BasicPersistenceModule.ALTERNATE_ID_PROPERTY);
                    DynamicResultSet drs = service.getRecordsForCollection(cmd, entity, p, null, null, null, alternateIdProperty.getValue());
                    
                    ListGrid listGrid = formService.buildCollectionListGrid(alternateIdProperty.getValue(), drs, p, ppr.getAdornedList().getAdornedTargetEntityClassname());
                    listGrid.setListGridType(ListGrid.Type.INLINE);
                    listGrid.getToolbarActions().add(DefaultListGridActions.ADD);
                    entityForm.addListGrid(listGrid, EntityForm.DEFAULT_TAB_NAME, EntityForm.DEFAULT_TAB_ORDER);
                }
            }
            
            formService.populateEntityFormFields(entityForm, entity, populateTypeAndId, populateTypeAndId);
            formService.populateAdornedEntityFormFields(entityForm, entity, ppr.getAdornedList());
            
            boolean atLeastOneBasicField = false;
            for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
                if (entry.getValue().getIsVisible()) {
                    atLeastOneBasicField = true;
                    break;
                }
            }
            if (!atLeastOneBasicField) {
                entityForm.removeAction(DefaultEntityFormActions.SAVE);
            }

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/adornedEditEntity");
        } else if (md instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) md;

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr);
            if (entity == null) {
                entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId);
            }
            
            boolean populateTypeAndId = true;
            if (entityForm == null) {
                entityForm = formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id);
            } else {
                //save off the prior key before clearing out the fields map as it will not appear
                //back on the saved entity
                String priorKey = entityForm.getFields().get("priorKey").getValue();
                entityForm.clearFieldsMap();
                formService.buildMapForm(fmd, ppr.getMapStructure(), collectionMetadata, id, entityForm);
                entityForm.getFields().get("priorKey").setValue(priorKey);
                populateTypeAndId = false;
            }

            formService.populateEntityFormFields(entityForm, entity, populateTypeAndId, populateTypeAndId);
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
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName));
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false);
        
        // First, we must save the collection entity
        Entity savedEntity = service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, collectionItemId);
        entityFormValidator.validate(entityForm, savedEntity, result);

        if (result.hasErrors()) {
            return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, 
                    "updateCollectionItem", entityForm, savedEntity); 
        }
        
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
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata, false);
        
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
        Entity entity = service.getRecord(ppr, id, mainMetadata, false);

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
    
}
