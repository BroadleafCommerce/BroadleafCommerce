/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.web.controller.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.util.BLCArrayUtils;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.JsonResponse;
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
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
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
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    protected static final Log LOG = LogFactory.getLog(AdminBasicEntityController.class);

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Value("${admin.form.validation.errors.hideTopLevelErrors}")
    protected boolean hideTopLevelErrors = false;

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
     * @param requestParams a Map of property name -> list critiera values
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> crumbs = getSectionCrumbs(request, null, null);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, requestParams, crumbs, pathVars);

        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        DynamicResultSet drs =  service.getRecords(ppr).getDynamicResultSet();

        ListGrid listGrid = formService.buildMainListGrid(drs, cmd, sectionKey, crumbs);
        List<EntityFormAction> mainActions = new ArrayList<EntityFormAction>();
        addAddActionIfAllowed(sectionClassName, cmd, mainActions);
        extensionManager.getProxy().addAdditionalMainActions(sectionClassName, mainActions);
        extensionManager.getProxy().modifyMainActions(cmd, mainActions);
        
        Field firstField = listGrid.getHeaderFields().iterator().next();
        if (requestParams.containsKey(firstField.getName())) {
            model.addAttribute("mainSearchTerm", requestParams.get(firstField.getName()).get(0));
        }
        
        // If this came from a delete save, we'll have a headerFlash request parameter to take care of
        if (requestParams.containsKey("headerFlash")) {
            model.addAttribute("headerFlash", requestParams.get("headerFlash").get(0));
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
        if (isAddActionAllowed(sectionClassName, cmd)) {
            mainActions.add(DefaultMainActions.ADD);
        }

        mainEntityActionsExtensionManager.getProxy().modifyMainActions(cmd, mainActions);
    }
    
    protected boolean isAddActionAllowed(String sectionClassName, ClassMetadata cmd) {
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
        
        return canCreate;
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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, null, null);
        ClassMetadata cmd = service.getClassMetadata(getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, pathVars))
                .getDynamicResultSet().getClassMetaData();

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
            String requestUri = request.getRequestURI();
            if (!request.getContextPath().equals("/") && requestUri.startsWith(request.getContextPath())) {
                requestUri = requestUri.substring(request.getContextPath().length() + 1, requestUri.length());
            }
            model.addAttribute("currentUri", requestUri);
        } else {
            EntityForm entityForm = formService.createEntityForm(cmd, sectionCrumbs);
            
            // We need to make sure that the ceiling entity is set to the interface and the specific entity type
            // is set to the type we're going to be creating.
            entityForm.setCeilingEntityClassname(cmd.getCeilingType());
            entityForm.setEntityType(entityType);
            
            // When we initially build the class metadata (and thus, the entity form), we had all of the possible
            // polymorphic fields built out. Now that we have a concrete entity type to render, we can remove the
            // fields that are not applicable for this given entity type.
            formService.removeNonApplicableFields(cmd, entityForm, entityType);

            modifyAddEntityForm(entityForm, pathVars);

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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, null, null);
        Entity entity = service.addEntity(entityForm, getSectionCustomCriteria(), sectionCrumbs).getEntity();
        entityFormValidator.validate(entityForm, entity, result);

        if (result.hasErrors()) {
            String sectionClassName = getClassNameForSection(sectionKey);
            ClassMetadata cmd = service.getClassMetadata(getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
            entityForm.clearFieldsMap();
            formService.populateEntityForm(cmd, entity, entityForm, sectionCrumbs);

            formService.removeNonApplicableFields(cmd, entityForm, entityForm.getEntityType());

            modifyAddEntityForm(entityForm, pathVars);

            model.addAttribute("viewType", "modal/entityAdd");
            model.addAttribute("currentUrl", request.getRequestURL().toString());
            model.addAttribute("modalHeaderType", "addEntity");
            model.addAttribute("hideTopLevelErrors", hideTopLevelErrors);
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
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable("id") String id) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> crumbs = getSectionCrumbs(request, sectionKey, id);

        Integer startIndex = ServletRequestUtils.getIntParameter(request, "startIndex");
        Integer maxIndex = ServletRequestUtils.getIntParameter(request, "maxIndex");

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, crumbs, pathVars);

        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        Entity entity = service.getRecord(ppr, id, cmd, false).getDynamicResultSet().getRecords()[0];
        
        Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity, startIndex, maxIndex, crumbs);

        EntityForm entityForm = formService.createEntityForm(cmd, entity, subRecordsMap, crumbs);
        
        modifyEntityForm(entityForm, pathVars);
        
        model.addAttribute("entity", entity);
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("currentUrl", request.getRequestURL().toString());

        setModelAttributes(model, sectionKey);
        
        if (sandBoxHelper.isSandBoxable(entityForm.getEntityType())) {
            Tab auditTab = new Tab();
            auditTab.setTitle("Audit");
            auditTab.setOrder(Integer.MAX_VALUE);
            auditTab.setTabClass("audit-tab");
            entityForm.getTabs().add(auditTab);
        }

        if (isAjaxRequest(request)) {
            entityForm.setReadOnly();
            model.addAttribute("viewType", "modal/entityView");
            model.addAttribute("modalHeaderType", "viewEntity");
            return "modules/modalContainer";
        } else {
            model.addAttribute("useAjaxUpdate", true);
            model.addAttribute("viewType", "entityEdit");
            return "modules/defaultContainer";
        }
    }

    /**
     * Builds JSON that looks like this:
     * 
     * {"errors":
     *      [{"message":"This field is Required",
     *        "code": "requiredValidationFailure"
     *        "field":"defaultSku--name",
     *        "errorType", "field",
     *        "tab": "General"
     *        },
     *        {"message":"This field is Required",
     *        "code": "requiredValidationFailure"
     *        "field":"defaultSku--name",
     *        "errorType", "field",
     *        "tab": "General"
     *        }]
     * }
     * 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "application/json")
    public String saveEntityJson(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value = "id") String id,
            @ModelAttribute(value = "entityForm") EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        
        saveEntity(request, response, model, pathVars, id, entityForm, result, ra);

        JsonResponse json = new JsonResponse(response);
        if (result.hasErrors()) {
            populateJsonValidationErrors(entityForm, result, json);
        }
        List<String> dirtyList = buildDirtyList(pathVars, request, id);
        if (CollectionUtils.isNotEmpty(dirtyList)) {
            json.with("dirty", dirtyList);
        }

        return json.done();
    }
    
    public List<String> buildDirtyList(Map<String, String> pathVars, HttpServletRequest request, String id) throws ServiceException {
        List<String> dirtyList = new ArrayList<>();
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, pathVars);
        ClassMetadata cmd = null;
        Entity entity = null;
        cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        entity = service.getRecord(ppr, id, cmd, false).getDynamicResultSet().getRecords()[0];
        
        for (Property p: entity.getProperties()) {
            if (p.getIsDirty()) {
                dirtyList.add(p.getName());
            }
        }
        return dirtyList;
    }
    
    /**
     * Populates the given <b>json</b> response object based on the given <b>form</b> and <b>result</b>
     * @return the same <b>result</b> that was passed in
     */
    protected JsonResponse populateJsonValidationErrors(EntityForm form, BindingResult result, JsonResponse json) {
        List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();
        for (FieldError e : result.getFieldErrors()){
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("errorType", "field");
            String fieldName = e.getField().substring(e.getField().indexOf("[") + 1, e.getField().indexOf("]")).replace("_", "-");
            errorMap.put("field", fieldName);
            
            errorMap.put("message", translateErrorMessage(e));
            errorMap.put("code", e.getCode());
            String tabFieldName = fieldName.replaceAll("-+", ".");
            Tab errorTab = form.findTabForField(tabFieldName);
            if (errorTab != null) {
                errorMap.put("tab", errorTab.getTitle());
            }
            errors.add(errorMap);
        }
        for (ObjectError e : result.getGlobalErrors()) {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("errorType", "global");
            errorMap.put("code", e.getCode());
            errorMap.put("message", translateErrorMessage(e));
            errors.add(errorMap);
        }
        json.with("errors", errors);
        
        return json;
    }
    
    protected String translateErrorMessage(ObjectError error) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getMessageSource() != null) {
            return context.getMessageSource().getMessage(error.getCode(), null, error.getCode(), context.getJavaLocale());
        } else {
            LOG.warn("Could not find the MessageSource on the current request, not translating the message key");
            return error.getCode();
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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, pathVars);

        extractDynamicFormFields(entityForm);
        
        Entity entity = service.updateEntity(entityForm, getSectionCustomCriteria(), sectionCrumbs).getEntity();

        entityFormValidator.validate(entityForm, entity, result);
        if (result.hasErrors()) {
            model.addAttribute("headerFlash", "save.unsuccessful");
            model.addAttribute("headerFlashAlert", true);
            
            Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity, sectionCrumbs);
            ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            entityForm.clearFieldsMap();
            formService.populateEntityForm(cmd, entity, subRecordsMap, entityForm, sectionCrumbs);
            
            modifyEntityForm(entityForm, pathVars);
            
            model.addAttribute("entity", entity);
            model.addAttribute("currentUrl", request.getRequestURL().toString());

            model.addAttribute("hideTopLevelErrors", hideTopLevelErrors);
            setModelAttributes(model, sectionKey);
            
            if (isAjaxRequest(request)) {
                entityForm.setReadOnly();
                model.addAttribute("viewType", "modal/entityView");
                model.addAttribute("modalHeaderType", "viewEntity");
                return "modules/modalContainer";
            } else {
                model.addAttribute("useAjaxUpdate", true);
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
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);

        Entity entity = service.removeEntity(entityForm, getSectionCustomCriteria(), sectionCrumbs).getEntity();
        // Removal does not normally return an Entity unless there is some validation error
        if (entity != null) {
            entityFormValidator.validate(entityForm, entity, result);
            if (result.hasErrors()) {
                // Create a flash attribute for the unsuccessful delete
                FlashMap fm = new FlashMap();
                fm.put("headerFlash", "delete.unsuccessful");
                fm.put("headerFlashAlert", true);
                request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, fm);
                
                // Re-look back up the entity so that we can return something populated
                String sectionClassName = getClassNameForSection(sectionKey);
                PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, pathVars);
                ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
                entity = service.getRecord(ppr, id, cmd, false).getDynamicResultSet().getRecords()[0];
                Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity, sectionCrumbs);
                entityForm.clearFieldsMap();
                formService.populateEntityForm(cmd, entity, subRecordsMap, entityForm, sectionCrumbs);
                modifyEntityForm(entityForm, pathVars);
    
                return populateJsonValidationErrors(entityForm, result, new JsonResponse(response))
                        .done();
            }
        }
        
        ra.addFlashAttribute("headerFlash", "delete.successful");
        ra.addFlashAttribute("headerFlashAlert", true);
        
        if (isAjaxRequest(request)) {
            // redirect attributes won't work here since ajaxredirect actually makes a new request
            return "ajaxredirect:" + getContextPath(request) + sectionKey + "?headerFlash=delete.successful";
        } else {
            return "redirect:/" + sectionKey;
        }
    }

    @RequestMapping(value = "/{collectionField:.*}/details", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getCollectionValueDetails(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam String ids,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, null, null);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, requestParams, sectionCrumbs, pathVars);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
        ppr.setStartIndex(getStartIndex(requestParams));
        ppr.setMaxIndex(getMaxIndex(requestParams));
        
        if (md instanceof BasicFieldMetadata) {
            String idProp = ((BasicFieldMetadata) md).getForeignKeyProperty();
            String displayProp = ((BasicFieldMetadata) md).getForeignKeyDisplayValueProperty();

            List<String> filterValues = BLCArrayUtils.asList(ids.split(FILTER_VALUE_SEPARATOR_REGEX));
            ppr.addFilterAndSortCriteria(new FilterAndSortCriteria(idProp, filterValues));
            
            DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();
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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        BasicFieldMetadata md = (BasicFieldMetadata) collectionProperty.getMetadata();

        AdminSection section = adminNavigationService.findAdminSectionByClassAndSectionId(md.getForeignKeyClass(), sectionKey);
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
     * @param requestParams
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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, requestParams, sectionCrumbs, pathVars);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        
        ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, requestParams, sectionKey, sectionCrumbs);
        model.addAttribute("listGrid", listGrid);

        model.addAttribute("currentParams", new ObjectMapper().writeValueAsString(requestParams));

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
     * @param pathVars
     * @param id
     * @param collectionField
     * @param requestParams
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value = "id") String id,
            @PathVariable(value = "collectionField") String collectionField,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName,
                sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs)
                .withFilterAndSortCriteria(getCriteria(requestParams))
                .withStartIndex(getStartIndex(requestParams))
                .withMaxIndex(getMaxIndex(requestParams));

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
                // If the entity type isn't specified, we need to determine if there are various polymorphic types
                // for this entity.
                String entityType = null;
                if (requestParams.containsKey("entityType")) {
                    entityType = requestParams.get("entityType").get(0);
                }
                if (StringUtils.isBlank(entityType)) {
                    if (cmd.getPolymorphicEntities().getChildren().length == 0) {
                        entityType = cmd.getPolymorphicEntities().getFullyQualifiedClassname();
                    } else {
                        entityType = getDefaultEntityType();
                    }
                } else {
                    entityType = URLDecoder.decode(entityType, "UTF-8");
                }

                if (StringUtils.isBlank(entityType)) {
                    List<ClassTree> entityTypes = getAddEntityTypes(cmd.getPolymorphicEntities());
                    model.addAttribute("entityTypes", entityTypes);
                    model.addAttribute("viewType", "modal/entityTypeSelection");
                    model.addAttribute("entityFriendlyName", cmd.getPolymorphicEntities().getFriendlyName());
                    String requestUri = request.getRequestURI();
                    if (!request.getContextPath().equals("/") && requestUri.startsWith(request.getContextPath())) {
                        requestUri = requestUri.substring(request.getContextPath().length() + 1, requestUri.length());
                    }
                    model.addAttribute("currentUri", requestUri);
                    model.addAttribute("modalHeaderType", "addEntity");
                    setModelAttributes(model, sectionKey);
                    return "modules/modalContainer";
                } else {
                    ppr = ppr.withCeilingEntityClassname(entityType);
                }
            }
        }

        //service.getContextSpecificRelationshipId(mainMetadata, entity, prefix);

        model.addAttribute("currentParams", new ObjectMapper().writeValueAsString(requestParams));

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
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        
        if (StringUtils.isBlank(entityForm.getEntityType())) {
            FieldMetadata fmd = collectionProperty.getMetadata();
            if (fmd instanceof BasicCollectionMetadata) {
                entityForm.setEntityType(((BasicCollectionMetadata) fmd).getCollectionCeilingEntity());
            }
        }

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];
        
        // First, we must save the collection entity
        PersistenceResponse persistenceResponse = service.addSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, sectionCrumbs);
        Entity savedEntity = persistenceResponse.getEntity();
        entityFormValidator.validate(entityForm, savedEntity, result);
        
        if (result.hasErrors()) {
            FieldMetadata md = collectionProperty.getMetadata();
            ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
            return buildAddCollectionItemModel(request, response, model, id, collectionField, sectionKey, collectionProperty,
                    md, ppr, entityForm, savedEntity);
        }

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey, persistenceResponse, sectionCrumbs);
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
            Model model, String id, String collectionField, String sectionKey, Property collectionProperty,
            FieldMetadata md, PersistencePackageRequest ppr, EntityForm entityForm, Entity entity) throws ServiceException {
        
        // For requests to add a new collection item include the main class that the subsequent request comes from.
        // For instance, with basic collections we know the main associated class for a fetch through the ForeignKey
        // persistence item but map and adorned target lookups make a standard persistence request. This solution
        // fixes all cases.
        String mainClassName = getClassNameForSection(sectionKey);
        ppr.addCustomCriteria("owningClass=" + mainClassName);
        
        if (entityForm != null) {
            entityForm.clearFieldsMap();
        }
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            // When adding items to basic collections, we will sometimes show a form to persist a new record
            // and sometimes show a list grid to allow the user to associate an existing record.
            if (fmd.getAddMethodType().equals(AddMethodType.PERSIST)) {
                ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
                if (entityForm == null) {
                    entityForm = formService.createEntityForm(collectionMetadata,sectionCrumbs);
                    entityForm.setCeilingEntityClassname(ppr.getCeilingEntityClassname());
                    entityForm.setEntityType(ppr.getCeilingEntityClassname());
                } else {
                    formService.populateEntityForm(collectionMetadata, entityForm, sectionCrumbs);
                    formService.populateEntityFormFieldValues(collectionMetadata, entity, entityForm);
                }
                formService.removeNonApplicableFields(collectionMetadata, entityForm, ppr.getCeilingEntityClassname());
                entityForm.getTabs().iterator().next().getIsVisible();

                model.addAttribute("entityForm", entityForm);
                model.addAttribute("viewType", "modal/simpleAddEntity");
            } else {
                DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();
                ListGrid listGrid = formService.buildCollectionListGrid(id, drs, collectionProperty, sectionKey, sectionCrumbs);
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
            ppr.setSectionEntityField(collectionField);

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

            DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();
            ListGrid listGrid = formService.buildMainListGrid(drs, collectionMetadata, sectionKey, sectionCrumbs);
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
            ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            
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
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/{alternateId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @PathVariable(value="alternateId") String alternateId) throws Exception {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, alternateId,
                "updateCollectionItem");
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, null,
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
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/{alternateId}/view", method = RequestMethod.GET)
    public String showViewCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @PathVariable(value="alternateId") String alternateId) throws Exception {
        String returnPath = showViewUpdateCollection(request, model, pathVars, id, collectionField, alternateId, collectionItemId,
                "viewCollectionItem");
        
        // Since this is a read-only view, actions don't make sense in this context
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        ef.removeAllActions();
        
        return returnPath;
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/view", method = RequestMethod.GET)
    public String showViewCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        String returnPath = showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, null,
                "viewCollectionItem");

        // Since this is a read-only view, actions don't make sense in this context
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        ef.removeAllActions();

        return returnPath;
    }
    
    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
            String id, String collectionField, String collectionItemId, String alternateId, String modalHeaderType) throws ServiceException {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, alternateId, modalHeaderType, null, null);
    }

    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
            String id, String collectionField, String collectionItemId, String modalHeaderType) throws ServiceException {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, null, modalHeaderType, null, null);
    }

    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
                String id, String collectionField, String collectionItemId, String modalHeaderType, EntityForm entityForm, Entity entity) throws ServiceException {
        return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, null, modalHeaderType, entityForm, entity);
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
            String id, String collectionField, String collectionItemId, String alternateId, String modalHeaderType, EntityForm entityForm, Entity entity) throws ServiceException {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        SectionCrumb nextCrumb = new SectionCrumb();
        if (md instanceof MapMetadata) {
            nextCrumb.setSectionIdentifier(((MapMetadata) md).getValueClassName());
        } else {
            nextCrumb.setSectionIdentifier(((CollectionMetadata) md).getCollectionCeilingEntity());
        }
        nextCrumb.setSectionId(collectionItemId);
        if (!sectionCrumbs.contains(nextCrumb)) {
            sectionCrumbs.add(nextCrumb);
        }

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];

        ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
        
        if (md instanceof BasicCollectionMetadata &&
                ((BasicCollectionMetadata) md).getAddMethodType().equals(AddMethodType.PERSIST)) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            if (entity == null) {
                entity = service.getRecord(ppr, collectionItemId, collectionMetadata, true).getDynamicResultSet().getRecords()[0];
            }

            Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity, sectionCrumbs);
            if (entityForm == null) {
                entityForm = formService.createEntityForm(collectionMetadata, entity, subRecordsMap, sectionCrumbs);
            } else {
                entityForm.clearFieldsMap();
                formService.populateEntityForm(collectionMetadata, entity, subRecordsMap, entityForm, sectionCrumbs);
                //remove all the actions since we're not trying to redisplay them on the form
                entityForm.removeAllActions();
            }
            entityForm.removeAction(DefaultEntityFormActions.DELETE);

            model.addAttribute("entityForm", entityForm);
            model.addAttribute("viewType", "modal/simpleEditEntity");
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;

            if (entity == null) {
                entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId, sectionCrumbs, alternateId).getDynamicResultSet().getRecords()[0];
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

            ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            for (String field : fmd.getMaintainedAdornedTargetFields()) {
                Property p = cmd.getPMap().get(field);
                if (p != null && p.getMetadata() instanceof AdornedTargetCollectionMetadata) {
                    // Because we're dealing with a nested adorned target collection, this particular request must act
                    // directly on the first adorned target collection. Because of this, we need the actual id property
                    // from the entity that models the adorned target relationship, and not the id of the target object.
                    Property alternateIdProperty = entity.getPMap().get(BasicPersistenceModule.ALTERNATE_ID_PROPERTY);
                    DynamicResultSet drs = service.getRecordsForCollection(cmd, entity, p, null, null, null,
                            alternateIdProperty.getValue(), sectionCrumbs).getDynamicResultSet();
                    
                    ListGrid listGrid = formService.buildCollectionListGrid(alternateIdProperty.getValue(), drs, p,
                            ppr.getAdornedList().getAdornedTargetEntityClassname(), sectionCrumbs);
                    listGrid.setListGridType(ListGrid.Type.INLINE);
                    listGrid.getToolbarActions().add(DefaultListGridActions.ADD);
                    entityForm.addListGrid(listGrid, EntityForm.DEFAULT_TAB_NAME, EntityForm.DEFAULT_TAB_ORDER);
                } else if (p != null && p.getMetadata() instanceof MapMetadata) {
                    // See above comment for AdornedTargetCollectionMetadata
                    MapMetadata mmd = (MapMetadata) p.getMetadata();

                    Property alternateIdProperty = entity.getPMap().get(BasicPersistenceModule.ALTERNATE_ID_PROPERTY);
                    DynamicResultSet drs = service.getRecordsForCollection(cmd, entity, p, null, null, null,
                            alternateIdProperty.getValue(), sectionCrumbs).getDynamicResultSet();

                    ListGrid listGrid = formService.buildCollectionListGrid(alternateIdProperty.getValue(), drs, p,
                            mmd.getTargetClass(), sectionCrumbs);
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

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            if (entity == null) {
                entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty,
                    collectionItemId, sectionCrumbs, null).getEntity();
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
     * @param collectionItemId the collection primary key value (in the case of adorned target collection, this is the primary key value of the target entity)
     * @param entityForm
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @ModelAttribute(value="entityForm") EntityForm entityForm,
            BindingResult result) throws Exception {
        return updateCollectionItem(request, response, model, pathVars, id, collectionField, collectionItemId, entityForm, null, result);
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
     * @param collectionItemId the collection primary key value (in the case of adorned target collection, this is the primary key value of the target entity)
     * @param entityForm
     * @param alternateId in the case of adorned target collections, this is the primary key value of the collection member
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/{alternateId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @ModelAttribute(value="entityForm") EntityForm entityForm,
            @PathVariable(value="alternateId") String alternateId,
            BindingResult result) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];
        
        // First, we must save the collection entity
        PersistenceResponse persistenceResponse = service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, collectionItemId, alternateId, sectionCrumbs);
        Entity savedEntity = persistenceResponse.getEntity();
        entityFormValidator.validate(entityForm, savedEntity, result);

        if (result.hasErrors()) {
            return showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, alternateId,
                    "updateCollectionItem", entityForm, savedEntity); 
        }
        
        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey, persistenceResponse, sectionCrumbs);
        model.addAttribute("listGrid", listGrid);

        // We return the new list grid so that it can replace the currently visible one
        setModelAttributes(model, sectionKey);
        return "views/standaloneListGrid";
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/sequence", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> updateCollectionItemSequence(HttpServletRequest request,
            HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @RequestParam(value="newSequence") String newSequence) throws Exception {
        return updateCollectionItemSequence(request, response, model, pathVars, id, collectionField, collectionItemId, newSequence, null);
    }
    
    /**
     * Updates the given collection item's sequence. This should only be triggered for adorned target collections
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
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/{alternateId}/sequence", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> updateCollectionItemSequence(HttpServletRequest request, 
            HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @RequestParam(value="newSequence") String newSequence,
            @PathVariable(value="alternateId") String alternateId) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity parentEntity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];
        
        ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);

        if (md instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata fmd = (AdornedTargetCollectionMetadata) md;
            AdornedTargetList atl = ppr.getAdornedList();
            
            // Get an entity form for the entity
            EntityForm entityForm = formService.buildAdornedListForm(fmd, ppr.getAdornedList(), id);
            Entity entity = service.getAdvancedCollectionRecord(mainMetadata, parentEntity, collectionProperty, 
                    collectionItemId, sectionCrumbs, alternateId).getDynamicResultSet().getRecords()[0];
            formService.populateEntityFormFields(entityForm, entity);
            formService.populateAdornedEntityFormFields(entityForm, entity, ppr.getAdornedList());
            
            // Set the new sequence (note that it will come in 0-indexed but the persistence module expects 1-indexed)
            int sequenceValue = Integer.parseInt(newSequence) + 1;
            Field field = entityForm.findField(atl.getSortField());
            field.setValue(String.valueOf(sequenceValue));
            
            Map<String, Object> responseMap = new HashMap<String, Object>();
            service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, entity, collectionItemId, alternateId, sectionCrumbs);
            responseMap.put("status", "ok");
            responseMap.put("field", collectionField);
            return responseMap;
        } else if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata cd = (BasicCollectionMetadata) md;
            Map<String, Object> responseMap = new HashMap<String, Object>();
            Entity entity = service.getRecord(ppr, collectionItemId, mainMetadata, false).getDynamicResultSet().getRecords()[0];

            ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            EntityForm entityForm = formService.createEntityForm(collectionMetadata, sectionCrumbs);
            if (!StringUtils.isEmpty(cd.getSortProperty())) {
                Field f = new Field()
                        .withName(cd.getSortProperty())
                        .withFieldType(SupportedFieldType.HIDDEN.toString());
                entityForm.addHiddenField(f);
            }
            formService.populateEntityFormFields(entityForm, entity);

            if (!StringUtils.isEmpty(cd.getSortProperty())) {
                int sequenceValue = Integer.parseInt(newSequence) + 1;
                Field field = entityForm.findField(cd.getSortProperty());
                field.setValue(String.valueOf(sequenceValue));
            }

            service.updateSubCollectionEntity(entityForm, mainMetadata, collectionProperty, parentEntity, collectionItemId, sectionCrumbs);

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
        return removeCollectionItem(request, response, model, pathVars, id, collectionField, collectionItemId, null);
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
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/{alternateId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId,
            @PathVariable(value="alternateId") String alternateId) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String mainClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, sectionKey, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);

        String priorKey = request.getParameter("key");
        
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars);
        Entity entity = service.getRecord(ppr, id, mainMetadata, false).getDynamicResultSet().getRecords()[0];

        // First, we must remove the collection entity
        PersistenceResponse persistenceResponse = service.removeSubCollectionEntity(mainMetadata, collectionProperty, entity, collectionItemId, alternateId, priorKey, sectionCrumbs);
        if (persistenceResponse.getEntity() != null && persistenceResponse.getEntity().isValidationFailure()) {
            String error = "There was an error removing the whatever";
            if (MapUtils.isNotEmpty(persistenceResponse.getEntity().getPropertyValidationErrors())) {
                // If we failed, we'll return some JSON with the first error
                error = persistenceResponse.getEntity().getPropertyValidationErrors().values().iterator().next().get(0);
            } else if (CollectionUtils.isNotEmpty(persistenceResponse.getEntity().getGlobalValidationErrors())) {
                error = persistenceResponse.getEntity().getGlobalValidationErrors().get(0);
            }
            return new JsonResponse(response)
                .with("status", "error")
                .with("message", BLCMessageUtils.getMessage(error))
                .done();
        }

        // Next, we must get the new list grid that represents this collection
        ListGrid listGrid = getCollectionListGrid(mainMetadata, entity, collectionProperty, null, sectionKey, persistenceResponse, sectionCrumbs);
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
