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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.JsonResponse;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.security.ClassNameRequestParamValidationService;
import org.broadleafcommerce.openadmin.server.domain.FetchPageRequest;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.AdminSectionCustomCriteriaService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.*;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An abstract controller that provides convenience methods and resource declarations for the Admin.
 * Operations that are shared between all admin controllers belong here.
 *
 * @author elbertbautista
 * @author apazzolini
 */
public abstract class AdminAbstractController extends BroadleafAbstractController {
    protected static final Log LOG = LogFactory.getLog(AdminAbstractController.class);

    public static final String FILTER_VALUE_SEPARATOR = "|";
    public static final String FILTER_VALUE_SEPARATOR_REGEX = "\\|";

    public static final String CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME = "currentAdminModule";
    public static final String CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME = "currentAdminSection";

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

    @Resource(name = "blAdminSectionCustomCriteriaService")
    protected AdminSectionCustomCriteriaService customCriteriaService;

    /**
     * Deprecated in favor of {@link org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionManager}
     */
    @Deprecated
    @Resource(name = "blMainEntityActionsExtensionManager")
    protected MainEntityActionsExtensionManager mainEntityActionsExtensionManager;

    @Resource(name = "blAdminAbstractControllerExtensionManager")
    protected AdminAbstractControllerExtensionManager extensionManager;

    @Resource(name="blClassNameRequestParamValidationService")
    protected ClassNameRequestParamValidationService validationService;

    // *********************************************************
    // UNBOUND CONTROLLER METHODS (USED BY DIFFERENT SECTIONS) *
    // *********************************************************
    
    /**
     * Convenience method for obtaining a fully built EntityForm for the given sectionKey, sectionClassName, and id.
     * 
     * @param sectionKey
     * @param sectionClassName
     * @param id
     * @return a fully composed EntityForm
     * @throws ServiceException
     */
    protected EntityForm getEntityForm(String sectionKey, String sectionClassName, String id) throws ServiceException {
        SectionCrumb sc = new SectionCrumb();
        sc.setSectionId(id);
        sc.setSectionIdentifier("structured-content/all");
        List<SectionCrumb> crumbs = new ArrayList<SectionCrumb>(1);
        crumbs.add(sc);

        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, crumbs, null);
        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        Entity entity = service.getRecord(ppr, id, cmd, false).getDynamicResultSet().getRecords()[0];

        Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForAllSubCollections(ppr, entity, crumbs);

        EntityForm entityForm = formService.createEntityForm(cmd, entity, subRecordsMap, crumbs);
        return entityForm;
    }
    
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
        EntityForm dynamicForm = getEntityForm(info);

        // Set the specialized name for these fields - we need to handle them separately
        setSpecializedNameForFields(info, dynamicForm);

        blankFormContainer.putDynamicForm(info.getPropertyName(), dynamicForm);
        model.addAttribute("dynamicForm", dynamicForm);
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
     * @param entity
     * @param collectionProperty property that this collection should be based on from the root entity
     * @param requestParams
     * @param sectionKey the current main section key
     * @param persistenceResponse
     * @param sectionCrumbs
     * @return the list grid
     * @throws ServiceException
     */
    protected ListGrid getCollectionListGrid(ClassMetadata mainMetadata, Entity entity, Property collectionProperty,
            MultiValueMap<String, String> requestParams, String sectionKey, PersistenceResponse persistenceResponse, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        String idProperty = service.getIdProperty(mainMetadata);
        if (persistenceResponse != null && persistenceResponse.getAdditionalData().containsKey(PersistenceResponse.AdditionalData.CLONEID)) {
            entity.findProperty(idProperty).setValue((String) persistenceResponse.getAdditionalData().get(PersistenceResponse.AdditionalData.CLONEID));
        }
        FetchPageRequest pageRequest = new FetchPageRequest()
            .withLastId(getLastId(requestParams))
            .withFirstId(getFirstId(requestParams))
            .withStartIndex(getStartIndex(requestParams))
            .withMaxIndex(getMaxIndex(requestParams))
            .withUpperCount(getUpperCount(requestParams))
            .withLowerCount(getLowerCount(requestParams))
            .withPageSize(getPageSize(requestParams));

        DynamicResultSet drs = service.getPagedRecordsForCollection(mainMetadata, entity, collectionProperty,
                getCriteria(requestParams), pageRequest, null, sectionCrumbs).getDynamicResultSet();

        ListGrid listGrid = formService.buildCollectionListGrid(entity.findProperty(idProperty).getValue(), drs,
                collectionProperty, sectionKey, sectionCrumbs);

        return listGrid;
    }

    /**
     * Convenience method for obtaining a ListGrid DTO object for a collection. Note that if no <b>criteria</b> is
     * available, then this should be null (or empty)
     *
     * @param mainMetadata class metadata for the root entity that this <b>collectionProperty</b> relates to
     * @param entity
     * @param collectionProperty property that this collection should be based on from the root entity
     * @param requestParams
     * @param sectionKey the current main section key
     * @param sectionCrumbs
     * @return the list grid
     * @throws ServiceException
     */
    protected ListGrid getCollectionListGrid(ClassMetadata mainMetadata, Entity entity, Property collectionProperty,
                MultiValueMap<String, String> requestParams, String sectionKey, List<SectionCrumb> sectionCrumbs)
                throws ServiceException {
        return getCollectionListGrid(mainMetadata, entity, collectionProperty, requestParams, sectionKey, null, sectionCrumbs);
    }

    protected EntityForm getEntityForm(DynamicEntityFormInfo info) throws ServiceException {
        return getEntityForm(info, null);
    }

    protected EntityForm getEntityForm(DynamicEntityFormInfo info, EntityForm dynamicFormOverride) throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withSecurityCeilingEntityClassname(info.getSecurityCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(), null, info.getPropertyName(), info.getPropertyValue() });
        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        EntityForm dynamicForm = formService.createEntityForm(cmd, null);
        dynamicForm.clearFieldsMap();

        if (dynamicFormOverride != null) {
            dynamicFormOverride.clearFieldsMap();
            Map<String, Field> fieldOverrides = dynamicFormOverride.getFields();
            for (Entry<String, Field> override : fieldOverrides.entrySet()) {
                if (dynamicForm.getFields().containsKey(override.getKey())) {
                    dynamicForm.findField(override.getKey()).setValue(override.getValue().getValue());
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
     * @param dynamicFormOverride optional dynamic form that already has values to fill out
     * @return the entity form
     * @throws ServiceException
     */
    protected EntityForm getDynamicFieldTemplateForm(DynamicEntityFormInfo info, String entityId, EntityForm dynamicFormOverride) 
            throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withSecurityCeilingEntityClassname(info.getSecurityCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(), entityId, info.getPropertyName(), info.getPropertyValue() });
        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        
        // However, when we fetch, the second custom criteria needs to be the id
        // of this particular structured content entity
        ppr.setCustomCriteria(new String[] { info.getCriteriaName(), entityId });
        Entity entity = service.getRecord(ppr, info.getPropertyValue(), cmd, true).getDynamicResultSet().getRecords()[0];
        
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
        EntityForm dynamicForm = formService.createEntityForm(cmd, entity, null, null);
        
        for (Field field : fieldsToMove) {
            FieldMetadata fmd = cmd.getPMap().get(field.getName()).getMetadata();
            if (fmd instanceof BasicFieldMetadata) {
                BasicFieldMetadata bfmd = (BasicFieldMetadata) fmd;
                field.setFieldType(bfmd.getFieldType().toString());
                field.setFriendlyName(bfmd.getFriendlyName());
                field.setRequired(bfmd.getRequired());
            }
            dynamicForm.addField(cmd, field);
        }
        setSpecializedNameForFields(info, dynamicForm);

        extensionManager.getProxy().modifyDynamicForm(dynamicForm, entityId);

        return dynamicForm;
    }

    protected void setSpecializedNameForFields(DynamicEntityFormInfo info, EntityForm dynamicForm) {
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + DynamicEntityFormInfo.FIELD_SEPARATOR + field.getName());
                }
            }
        }
    }

    /**
     * This method will scan the entityForm for all dynamic form fields and pull them out
     * as appropriate.
     *
     * @param cmd
     * @param entityForm
     */
    protected void extractDynamicFormFields(ClassMetadata cmd, EntityForm entityForm) {
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
            dynamicForm.addField(cmd, entry.getValue());
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
        String sectionKey = pathVars.get("sectionKey");

        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        AdminSection typedEntitySection = (AdminSection) request.getAttribute("typedEntitySection");
        if (typedEntitySection != null) {
            sectionKey = typedEntitySection.getUrl().substring(1);
        }

        return sectionKey;
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
        Map<String, FilterAndSortCriteria> fasMap = new HashMap<String, FilterAndSortCriteria>();

        List<FilterAndSortCriteria> result = new ArrayList<FilterAndSortCriteria>();
        for (Entry<String, List<String>> entry : requestParams.entrySet()) {
            if (!entry.getKey().equals(FilterAndSortCriteria.SORT_PROPERTY_PARAMETER)
                    && !entry.getKey().equals(FilterAndSortCriteria.SORT_DIRECTION_PARAMETER)
                    && !entry.getKey().equals(FilterAndSortCriteria.MAX_INDEX_PARAMETER)
                    && !entry.getKey().equals(FilterAndSortCriteria.START_INDEX_PARAMETER)) {
                List<String> values = entry.getValue();
                List<String> collapsedValues = new ArrayList<String>();
                for (String value : values) {
                    if (value.contains(FILTER_VALUE_SEPARATOR)) {
                        String[] vs = value.split(FILTER_VALUE_SEPARATOR_REGEX);
                        for (String v : vs) {
                            collapsedValues.add(v);
                        }
                    } else {
                        collapsedValues.add(value);
                    }
                }

                FilterAndSortCriteria fasCriteria = new FilterAndSortCriteria(entry.getKey(), collapsedValues, Integer.MIN_VALUE);
                fasMap.put(entry.getKey(),fasCriteria);
            }
        }

        List<String> sortProperties = getSortPropertyNames(requestParams);
        List<String> sortDirections = getSortDirections(requestParams);
        if (CollectionUtils.isNotEmpty(sortProperties)) {
            //set up a map to determine if there is already some criteria set for the sort property
            for (int i = 0; i < sortProperties.size(); i++) {
                boolean sortAscending = SortDirection.ASCENDING.toString().equals(sortDirections.get(i));
                FilterAndSortCriteria propertyCriteria = fasMap.get(sortProperties.get(i));
                //If there is already criteria for this property, attach the sort to that. Otherwise, create some new
                //FilterAndSortCriteria for the sort
                if (propertyCriteria != null) {
                    propertyCriteria.setSortAscending(sortAscending);
                } else {
                    propertyCriteria = new FilterAndSortCriteria(sortProperties.get(i));
                    propertyCriteria.setOrder(Integer.MIN_VALUE);
                    propertyCriteria.setSortAscending(sortAscending);
                    fasMap.put(sortProperties.get(i),propertyCriteria);
                }
            }
        }

        result.addAll(fasMap.values());
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
        return validationService.getClassNameForSection(sectionKey, "blPU");
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
     * @deprecated in favor of {@link #attachSectionSpecificInfo(PersistencePackageRequest, Map)}
     */
    @Deprecated
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr) {
        
    }
    
    /**
     * This method is invoked whenever an assembled EntityForm is rendered. This typically occurs when viewing an entity
     * in the admin or viewing an error state on a POST for that entity.
     * 
     * @param entityForm
     */
    protected void modifyEntityForm(EntityForm entityForm, Map<String, String> pathVars) throws Exception {
        
    }

    /**
     * This method is invoked whenever an assembled EntityForm is rendered for the add entity screen.
     * 
     * @param entityForm
     */
    protected void modifyAddEntityForm(EntityForm entityForm, Map<String, String> pathVars) {
        
    }

    /**
     * A hook method that is invoked every time the getSectionPersistencePackageRequest(..) method is invoked.
     * This allows specialized controllers to hook into every request and manipulate the persistence package request as
     * desired.
     * 
     * @param ppr
     */
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr, Map<String, String> pathVars) {
        attachSectionSpecificInfo(ppr);
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
     * Obtains the requested max index parameter
     *
     * @param requestParams
     * @return
     */
    protected Integer getMaxResults(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> maxResults = requestParams.get(FilterAndSortCriteria.MAX_RESULTS_PARAMETER);
        return CollectionUtils.isEmpty(maxResults) ? null : Integer.parseInt(maxResults.get(0));
    }


    protected Long getLastId(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> lastId = requestParams.get(FilterAndSortCriteria.LAST_ID_PARAMETER);
        return CollectionUtils.isEmpty(lastId) ? null : Long.parseLong(lastId.get(0));
    }

    protected Long getFirstId(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> firstId = requestParams.get(FilterAndSortCriteria.FIRST_ID_PARAMETER);
        return CollectionUtils.isEmpty(firstId) ? null : Long.parseLong(firstId.get(0));
    }

    protected Integer getUpperCount(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> upperCount = requestParams.get(FilterAndSortCriteria.UPPER_COUNT_PARAMETER);
        return CollectionUtils.isEmpty(upperCount) ? null : Integer.parseInt(upperCount.get(0));
    }

    protected Integer getLowerCount(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> lowerCount = requestParams.get(FilterAndSortCriteria.LOWER_COUNT_PARAMETER);
        return CollectionUtils.isEmpty(lowerCount) ? null : Integer.parseInt(lowerCount.get(0));
    }

    protected Integer getPageSize(Map<String, List<String>> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        List<String> pageSize = requestParams.get(FilterAndSortCriteria.PAGE_SIZE_PARAMETER);
        return CollectionUtils.isEmpty(pageSize) ? null : Integer.parseInt(pageSize.get(0));
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
            model.addAttribute(CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME, section.getModule());
            model.addAttribute(CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME, section);
        }
        
        extensionManager.getProxy().setAdditionalModelAttributes(model, sectionKey);
    }

    /**
     * @deprecated in favor of {@link #getSectionPersistencePackageRequest(String, List, Map)}
     */
    @Deprecated
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName, List<SectionCrumb> sectionCrumbs) {
        return getSectionPersistencePackageRequest(sectionClassName, sectionCrumbs, null);
    }

    /**
     * Returns a PersistencePackageRequest for the given sectionClassName. Will also invoke the 
     * {@link #getSectionCustomCriteria()} and {@link #attachSectionSpecificInfo(PersistencePackageRequest)} to allow
     * specialized controllers to manipulate the request for every action in this controller.
     * 
     * @param sectionClassName
     * @param sectionCrumbs
     * @param pathVars
     * @return
     */
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName, 
            List<SectionCrumb> sectionCrumbs, Map<String, String> pathVars) {
        String[] sectionCriteria = customCriteriaService.mergeSectionCustomCriteria(sectionClassName, getSectionCustomCriteria());
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(sectionClassName)
                .withCustomCriteria(sectionCriteria)
                .withSectionCrumbs(sectionCrumbs);

        attachSectionSpecificInfo(ppr, pathVars);
        
        return ppr;
    }

    /**
     * @deprecated in favor of {@link #getSectionPersistencePackageRequest(String, MultiValueMap, List, Map)}
     */
    @Deprecated
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName, 
            MultiValueMap<String, String> requestParams, List<SectionCrumb> sectionCrumbs) {
        return getSectionPersistencePackageRequest(sectionClassName, requestParams, sectionCrumbs, null);
    }

    /**
     * Returns the result of a call to getSectionPersistencePackageRequest(..) with the additional filter
     * and sort criteria attached.
     * 
     * @param sectionClassName
     * @param requestParams
     * @param sectionCrumbs
     * @param pathVars
     * @return the PersistencePacakageRequest
     */
    protected PersistencePackageRequest getSectionPersistencePackageRequest(String sectionClassName, 
            MultiValueMap<String, String> requestParams, List<SectionCrumb> sectionCrumbs, Map<String, String> pathVars) {
        FilterAndSortCriteria[] fascs = getCriteria(requestParams);
        String[] sectionCriteria = customCriteriaService.mergeSectionCustomCriteria(sectionClassName, getSectionCustomCriteria());
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(sectionClassName)
                .withCustomCriteria(sectionCriteria)
                .withFilterAndSortCriteria(fascs)
                .withStartIndex(getStartIndex(requestParams))
                .withMaxIndex(getMaxIndex(requestParams))
                .withSectionCrumbs(sectionCrumbs)
                .withLastId(getLastId(requestParams))
                .withFirstId(getFirstId(requestParams))
                .withUpperCount(getUpperCount(requestParams))
                .withLowerCount(getLowerCount(requestParams))
                .withPageSize(getPageSize(requestParams))
                .withPresentationFetch(true);

        attachSectionSpecificInfo(ppr, pathVars);

        return ppr;
    }

    protected List<SectionCrumb> getSectionCrumbs(HttpServletRequest request, String currentSection, String currentSectionId) {
        String crumbs = request.getParameter("sectionCrumbs");
        List<SectionCrumb> myCrumbs = validationService.getSectionCrumbs(crumbs, "blPU");
        if (currentSection != null && currentSectionId != null) {
            SectionCrumb crumb = createSectionCrumb(currentSection, currentSectionId);
            if (!myCrumbs.contains(crumb)) {
                myCrumbs.add(crumb);
            }
        }
        return myCrumbs;
    }

    protected SectionCrumb createSectionCrumb(String currentSection, String currentSectionId) {
        SectionCrumb crumb = new SectionCrumb();
        if (currentSection.startsWith("/")) {
            currentSection = currentSection.substring(1, currentSection.length());
        }
        crumb.setSectionIdentifier(currentSection);
        crumb.setSectionId(currentSectionId);
        return crumb;
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
}
