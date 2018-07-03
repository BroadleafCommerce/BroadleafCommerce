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
package org.broadleafcommerce.admin.web.controller.entity;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.admin.server.service.handler.ProductCustomPersistenceHandler;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.controller.modal.ModalHeaderType;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridAction;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles admin operations for the {@link Product} entity. Editing a product requires custom criteria in order to properly
 * invoke the {@link ProductCustomPersistenceHandler}
 * 
 * @author Andre Azzolini (apazzolini)
 * @see {@link ProductCustomPersistenceHandler}
 */
@Controller("blAdminProductController")
@RequestMapping("/" + AdminProductController.SECTION_KEY)
public class AdminProductController extends AdminBasicEntityController {
    
    public static final String SECTION_KEY = "product";
    public static final String DEFAULT_SKU_NAME = "defaultSku.name";
    public static final String SELECTIZE_NAME_PROPERTY = "name";

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    @Override
    protected void modifyAddEntityForm(EntityForm ef, Map<String, String> pathVars) {
        String defaultCategoryUrlPrefix = null;
        Field defaultCategory = ef.findField("defaultCategory");
        if (defaultCategory != null && StringUtils.isNotBlank(defaultCategory.getValue())) {
            Category cat = catalogService.findCategoryById(Long.parseLong(defaultCategory.getValue()));
            defaultCategoryUrlPrefix = cat.getUrl();
        }
                
        Field overrideGeneratedUrl = ef.findField("overrideGeneratedUrl");
        if (overrideGeneratedUrl != null) {
            overrideGeneratedUrl.setFieldType(SupportedFieldType.HIDDEN.toString().toLowerCase());
            boolean overriddenUrl = Boolean.parseBoolean(overrideGeneratedUrl.getValue());
            Field fullUrl = ef.findField("url");
            if (fullUrl != null) {
                fullUrl.withAttribute("overriddenUrl", overriddenUrl)
                        .withAttribute("sourceField", "defaultSku--name")
                        .withAttribute("toggleField", "overrideGeneratedUrl")
                        .withAttribute("prefix-selector", "#field-defaultCategory")
                        .withAttribute("prefix", defaultCategoryUrlPrefix)
                        .withFieldType(SupportedFieldType.GENERATED_URL.toString().toLowerCase());
            }
        }
    }
    
    protected String showAddAdditionalSku(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, Map<String, String> pathVars) throws Exception {
        String collectionField = "additionalSkus";
        String mainClassName = getClassNameForSection(SECTION_KEY);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, SECTION_KEY, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();
        
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs)
                .withCustomCriteria(new String[] { id });
        BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        // If the entity type isn't specified, we need to determine if there are various polymorphic types
        // for this entity.
        String entityType = null;
        if (request.getParameter("entityType") != null) {
            entityType = request.getParameter("entityType");
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
            model.addAttribute("modalHeaderType", ModalHeaderType.ADD_ENTITY.getType());
            setModelAttributes(model, SECTION_KEY);
            return "modules/modalContainer";
        } else {
            ppr = ppr.withCeilingEntityClassname(entityType);
        }

        ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        EntityForm entityForm = formService.createEntityForm(collectionMetadata, sectionCrumbs);
        entityForm.setCeilingEntityClassname(ppr.getCeilingEntityClassname());
        entityForm.setEntityType(ppr.getCeilingEntityClassname());
        formService.removeNonApplicableFields(collectionMetadata, entityForm, ppr.getCeilingEntityClassname());

        entityForm.removeAction(DefaultEntityFormActions.DELETE);
        
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/simpleAddEntity");
                
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", ModalHeaderType.ADD_COLLECTION_ITEM.getType());
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, SECTION_KEY);
        return "modules/modalContainer";
    }
    
    @Override
    protected String buildAddCollectionItemModel(HttpServletRequest request, HttpServletResponse response,
            Model model,
            String id,
            String collectionField,
            String sectionKey,
            Property collectionProperty,
            FieldMetadata md, PersistencePackageRequest ppr, EntityForm entityForm, Entity entity) throws ServiceException {
        if ("additionalSkus".equals(collectionField) && ppr.getCustomCriteria().length == 0) {
            ppr.withCustomCriteria(new String[] { id });
        }
        return super.buildAddCollectionItemModel(request, response, model, id, collectionField, sectionKey, collectionProperty, md, ppr, entityForm, entity);
    }
    
    protected String showUpdateAdditionalSku(HttpServletRequest request, Model model,
                                             String id, String collectionItemId, Map<String, String> pathVars, EntityForm entityForm) throws Exception {
        String collectionField = "additionalSkus";
        
        // Find out metadata for the additionalSkus property
        String mainClassName = getClassNameForSection(SECTION_KEY);
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, SECTION_KEY, id);
        ClassMetadata mainMetadata = service.getClassMetadata(getSectionPersistencePackageRequest(mainClassName, sectionCrumbs, pathVars)).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        // Find the metadata and the entity for the selected sku
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs)
                .withCustomCriteria(new String[] { id });
        ClassMetadata collectionMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        if (collectionMetadata.getCeilingType().equals(SkuImpl.class.getName())) {
            collectionMetadata.setCeilingType(Sku.class.getName());
        }

        Entity entity = service.getRecord(ppr, collectionItemId, collectionMetadata, true).getDynamicResultSet().getRecords()[0];

        String currentTabName = getCurrentTabName(pathVars, collectionMetadata);
        Map<String, DynamicResultSet> subRecordsMap = service.getRecordsForSelectedTab(collectionMetadata, entity, sectionCrumbs, currentTabName);
        if (entityForm == null) {
            entityForm = formService.createEntityForm(collectionMetadata, entity, subRecordsMap, sectionCrumbs);
        } else {
            entityForm.clearFieldsMap();
            formService.populateEntityForm(collectionMetadata, entity, subRecordsMap, entityForm, sectionCrumbs);
            //remove all the actions since we're not trying to redisplay them on the form
            entityForm.removeAllActions();
        }
        
        entityForm.removeAction(DefaultEntityFormActions.DELETE);
        
        // Ensure that operations on the Sku subcollections go to the proper URL
        for (ListGrid lg : entityForm.getAllListGrids()) {
            lg.setSectionKey("org.broadleafcommerce.core.catalog.domain.Sku");
            lg.setSectionCrumbs(sectionCrumbs);
        }
        
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/simpleEditEntity");

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", ModalHeaderType.UPDATE_COLLECTION_ITEM.getType());
        model.addAttribute("collectionProperty", collectionProperty);
        setModelAttributes(model, SECTION_KEY);
        return "modules/modalContainer";
    }

    @Override
    @RequestMapping(value = "/selectize", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> viewEntityListSelectize(HttpServletRequest request,
                                                HttpServletResponse response, Model model,
                                                @PathVariable Map<String, String> pathVars,
                                                @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> crumbs = getSectionCrumbs(request, null, null);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, requestParams, crumbs, pathVars)
                .withStartIndex(getStartIndex(requestParams))
                .withMaxIndex(getMaxIndex(requestParams))
                .withCustomCriteria(getCustomCriteria(requestParams));

        FilterAndSortCriteria[] fascs = getCriteria(requestParams);
        for(FilterAndSortCriteria fasc : fascs) {
            if (SELECTIZE_NAME_PROPERTY.equals(fasc.getPropertyId())) {
                fasc.setPropertyId(DEFAULT_SKU_NAME);
                break;
            }
        }
        ppr.withFilterAndSortCriteria(fascs);

        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        DynamicResultSet drs =  service.getRecords(ppr).getDynamicResultSet();

        return constructSelectizeOptionMap(drs, cmd);
    }

    public Map<String, Object> constructSelectizeOptionMap(DynamicResultSet drs, ClassMetadata cmd) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> options = new ArrayList<>();

        for (Entity e : drs.getRecords()) {
            Map<String, String> selectizeOption = new HashMap<>();

            Property p = e.findProperty("MAIN_ENTITY_NAME");
            if (p != null) {
                selectizeOption.put("name", p.getValue());
                selectizeOption.put("id", p.getValue());
            }
            if (e.findProperty(ALTERNATE_ID_PROPERTY) != null) {
                selectizeOption.put("alternateId", e.findProperty(ALTERNATE_ID_PROPERTY).getValue());
            }
            options.add(selectizeOption);
        }
        result.put("options", options);

        return result;
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        if ("additionalSkus".equals(collectionField)) {
            return showUpdateAdditionalSku(request, model, id, collectionItemId, pathVars, null);
        }
        return super.showUpdateCollectionItem(request, response, model, pathVars, id, collectionField, collectionItemId);
    }

    @Override
    protected String showViewUpdateCollection(HttpServletRequest request, Model model, Map<String, String> pathVars,
                                              String id, String collectionField, String collectionItemId, String alternateId, String modalHeaderType, EntityForm entityForm, Entity entity) throws ServiceException {
        try {
            if ("additionalSkus".equals(collectionField)) {
                return showUpdateAdditionalSku(request, model, id, collectionItemId, pathVars, entityForm);
            } else {
                return super.showViewUpdateCollection(request, model, pathVars, id, collectionField, collectionItemId, alternateId,
                        modalHeaderType, entityForm, entity);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        if ("additionalSkus".equals(collectionField)) {
            return showAddAdditionalSku(request, response, model, id, pathVars);
        } 
        return super.showAddCollectionItem(request, response, model, pathVars, id, collectionField, requestParams);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value = "id") String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, pathVars, id);
        
        //Skus have a specific toolbar action to generate Skus based on permutations
        EntityForm form = (EntityForm) model.asMap().get("entityForm");
        ListGridAction generateSkusAction = new ListGridAction(ListGridAction.GEN_SKUS).withDisplayText("Generate_Skus")
                .withIconClass("icon-fighter-jet")
                .withButtonClass("generate-skus")
                .withUrlPostfix("/generate-skus")
                .withActionUrlOverride("/product/" + id + "/additionalSkus/generate-skus");

        ListGrid skusGrid = form.findListGrid("additionalSkus");
        if (skusGrid != null) {
            skusGrid.setCanFilterAndSort(false);
        }

        ListGrid productOptionsGrid = form.findListGrid("productOptions");
        if (productOptionsGrid != null) {
            productOptionsGrid.addToolbarAction(generateSkusAction);
        }
        
        // When we're dealing with product bundles, we don't want to render the product options and additional skus
        // list grids. Remove them from the form.
        if (ProductBundle.class.isAssignableFrom(Class.forName(form.getEntityType()))) {
            form.removeListGrid("additionalSkus");
            form.removeListGrid("productOptions");
            form.removeField("canSellWithoutOptions");
        }
        
        form.removeListGrid("defaultSku.skuAttributes");
        
        return view;
    }
}
