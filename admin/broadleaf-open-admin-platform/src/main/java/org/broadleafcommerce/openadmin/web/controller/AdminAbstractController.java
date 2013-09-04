/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormValidator;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.FieldGroup;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An abstract controller that provides convenience methods and resource declarations for the Admin
 *
 * Operations that are shared between all admin controllers belong here.
 *
 * @see org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping
 * @author elbertbautista
 * @author apazzolini
 */
public abstract class AdminAbstractController extends BroadleafAbstractController {

    // ***********************
    // RESOURCE DECLARATIONS *
    // ***********************

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService service;

    @Resource(name = "blFormBuilderService")
    protected FormBuilderService formService;
    
    @Resource(name = "blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;
    
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blEntityFormValidator")
    protected EntityFormValidator entityFormValidator;
    
    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    @Resource(name = "blMainEntityActionsExtensionManager")
    protected MainEntityActionsExtensionManager mainEntityActionsExtensionManager;
    
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
     * @see #getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo, EntityForm)
     * @param info
     * @throws ServiceException
     */
    protected EntityForm getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo info) throws ServiceException {
        return getBlankDynamicFieldTemplateForm(info, null);
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
    protected EntityForm getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo info, EntityForm dynamicFormOverride) 
            throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(), null, info.getPropertyName(), info.getPropertyValue() });

        ClassMetadata cmd = service.getClassMetadata(ppr);
        
        EntityForm dynamicForm = formService.createEntityForm(cmd);
        dynamicForm.clearFieldsMap();

        if (dynamicFormOverride != null) {
            dynamicFormOverride.clearFieldsMap();
            Map<String, Field> fieldOverrides = dynamicFormOverride.getFields();
            for (Entry<String, Field> override : fieldOverrides.entrySet()) {
                if (dynamicForm.getFields().containsKey(override.getKey())) {
                    dynamicForm.getFields().get(override.getKey()).setValue(override.getValue().getValue());
                }
            }
        }
        
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + DynamicEntityFormInfo.FIELD_SEPARATOR + field.getName());
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
     * Optionally, you can pass in a pre-existing dynamic form to this method that already has updated values. Example usage
     * would be for after validation has failed and you do not want to lookup old values from the database again.
     * 
     * @param info
     * @param entityId
     * @param dynamicForm optional dynamic form that already has values to fill out
     * @return the entity form
     * @throws ServiceException
     */
    protected EntityForm getDynamicFieldTemplateForm(DynamicEntityFormInfo info, String entityId, EntityForm dynamicFormOverride) 
            throws ServiceException {
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(), entityId, info.getPropertyName(), info.getPropertyValue() });
        ClassMetadata cmd = service.getClassMetadata(ppr);
        Entity entity = service.getRecord(ppr, entityId, cmd, true);        
        
        List<Field> fieldsToMove = new ArrayList<Field>();
        // override the results of the entity with the dynamic form passed in
        if (dynamicFormOverride != null) {
            dynamicFormOverride.clearFieldsMap();
            Map<String, Field> fieldOverrides = dynamicFormOverride.getFields();
            for (Entry<String, Field> override : fieldOverrides.entrySet()) {
                if (entity.getPMap().containsKey(override.getKey())) {
                    entity.getPMap().get(override.getKey()).setValue(override.getValue().getValue());
                } else {
                    fieldsToMove.add(override.getValue());
                }
            }
        }
        
        // Assemble the dynamic form for structured content type
        EntityForm dynamicForm = formService.createEntityForm(cmd, entity);
        
        for (Field field : fieldsToMove) {
            FieldMetadata fmd = cmd.getPMap().get(field.getName()).getMetadata();
            if (fmd instanceof BasicFieldMetadata) {
                BasicFieldMetadata bfmd = (BasicFieldMetadata) fmd;
                field.setFieldType(bfmd.getFieldType().toString());
                field.setFriendlyName(bfmd.getFriendlyName());
                field.setRequired(bfmd.getRequired());
            }
            dynamicForm.addField(field);
        }
        
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + DynamicEntityFormInfo.FIELD_SEPARATOR + field.getName());
                }
            }
        }
    
        return dynamicForm;
    }

    /**
     * This method will scan the entityForm for all dynamic form fields and pull them out
     * as appropriate.
     * 
     * @param entityForm
     */
    protected void extractDynamicFormFields(EntityForm entityForm) {
        Map<String, Field> dynamicFields = new HashMap<String, Field>();
        
        // Find all of the dynamic form fields
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            if (entry.getKey().contains(DynamicEntityFormInfo.FIELD_SEPARATOR)) { 
                dynamicFields.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Remove the dynamic form fields from the main entity - they are persisted separately
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            entityForm.removeField(entry.getKey());
        }
        
        // Create the entity form for the dynamic form, as it needs to be persisted separately
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            String[] fieldName = entry.getKey().split("\\" + DynamicEntityFormInfo.FIELD_SEPARATOR);
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
     * @return a List<ClassTree> representing all potentially avaialble entity types to create
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
