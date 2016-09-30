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

package org.broadleafcommerce.openadmin.web.controller.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.JsonResponse;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.broadleafcommerce.openadmin.web.controller.AdminBasicOperationsControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.controller.modal.ModalHeaderType;
import org.broadleafcommerce.openadmin.web.service.SearchFieldResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The operations in this controller are actions that do not necessarily depend on a section key being present.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminBasicOperationsController")
public class AdminBasicOperationsController extends AdminAbstractController {

    private static final Log LOG = LogFactory.getLog(AdminBasicOperationsController.class);

    @Resource(name = "blAdminBasicOperationsControllerExtensionManager")
    protected AdminBasicOperationsControllerExtensionManager extensionManager;

    @Resource(name = "blSearchFieldResolver")
    protected SearchFieldResolver searchFieldResolver;

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
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value = "owningClass") String owningClass,
            @PathVariable(value = "collectionField") String collectionField,
            @RequestParam(required = false) String requestingEntityId,
            @RequestParam(defaultValue = "false") boolean dynamicField,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, null, null);
        String validatedClass = getClassNameForSection(owningClass);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(validatedClass, requestParams, sectionCrumbs, pathVars);

        // We might need these fields in the initial inspect.
        ppr.addCustomCriteria("requestingEntityId=" + requestingEntityId);
        ppr.addCustomCriteria("owningClass=" + owningClass);
        ppr.addCustomCriteria("requestingField=" + collectionField);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        // Only get collection property metadata when there is a non-structured content field that I am looking for
        Property collectionProperty = null;
        FieldMetadata md = null;
        if (!collectionField.contains("|") && !dynamicField) {
            collectionProperty = mainMetadata.getPMap().get(collectionField);
            md = collectionProperty.getMetadata();
            ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
        } else {
            md = new BasicFieldMetadata();
            md.setFriendlyName(mainMetadata.getPolymorphicEntities().getFriendlyName());
            collectionProperty = new Property();
            collectionProperty.setMetadata(md);
        }

        ppr.addFilterAndSortCriteria(getCriteria(requestParams));
        ppr.setStartIndex(getStartIndex(requestParams));
        ppr.setMaxIndex(getMaxIndex(requestParams));
        ppr.removeFilterAndSortCriteria("requestingEntityId");
        ppr.addCustomCriteria("requestingEntityId=" + requestingEntityId);
        ppr.addCustomCriteria("owningClass=" + owningClass);
        ppr.addCustomCriteria("requestingField=" + collectionField);

        modifyFetchPersistencePackageRequest(ppr, pathVars);

        ClassMetadata targetClassMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        ExtensionResultStatusType extensionResultStatusType = extensionManager.getProxy().buildLookupListGrid(ppr, targetClassMetadata, ppr.getCeilingEntityClassname(), sectionCrumbs, model, requestParams);
        if (extensionResultStatusType.equals(ExtensionResultStatusType.NOT_HANDLED)) {
            DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();

            ListGrid listGrid = null;
            if (collectionField.contains("|") || dynamicField) {
                // If we're dealing with a lookup from a dynamic field, we need to build the list grid differently
                listGrid = formService.buildMainListGrid(drs, mainMetadata, "/" + owningClass, sectionCrumbs);
                listGrid.setListGridType(ListGrid.Type.TO_ONE);
                listGrid.setSubCollectionFieldName(collectionField);
                listGrid.setPathOverride("/" + owningClass + "/" + collectionField + "/select");
            } else if (md instanceof BasicFieldMetadata) {
                listGrid = formService.buildCollectionListGrid(null, drs, collectionProperty, owningClass, sectionCrumbs);
                listGrid.removeAllRowActions();
            }
            model.addAttribute("listGrid", listGrid);
        }

        model.addAttribute("viewType", "modal/simpleSelectEntity");

        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", ModalHeaderType.SELECT_COLLECTION_ITEM.getType());
        model.addAttribute("collectionProperty", collectionProperty);
        model.addAttribute("sectionCrumbs", request.getParameter("sectionCrumbs"));
        setModelAttributes(model, owningClass);
        return "modules/modalContainer";
    }

    @RequestMapping(value = "/{owningClass:.*}/{collectionField:.*}/typeahead", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> getTypeaheadResults(HttpServletRequest request,
            HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable(value = "owningClass") String owningClass,
            @PathVariable(value = "collectionField") String collectionField,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String requestingEntityId,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        List<SectionCrumb> sectionCrumbs = getSectionCrumbs(request, null, null);
        String validatedClass = getClassNameForSection(owningClass);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(validatedClass, requestParams, sectionCrumbs, pathVars);
        ClassMetadata mainMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        Property collectionProperty = mainMetadata.getPMap().get(collectionField);
        FieldMetadata md = collectionProperty.getMetadata();

        ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
        ppr.addFilterAndSortCriteria(getCriteria(requestParams));
        ppr.setStartIndex(getStartIndex(requestParams));
        ppr.setMaxIndex(getMaxIndex(requestParams));
        ppr.removeFilterAndSortCriteria("query");
        ppr.removeFilterAndSortCriteria("requestingEntityId");
        ppr.addCustomCriteria("requestingEntityId=" + requestingEntityId);

        // This list of datums will populate the typeahead suggestions.
        List<Map<String, String>> responses = new ArrayList<Map<String, String>>();
        if (md instanceof BasicFieldMetadata) {
            String searchField = searchFieldResolver.resolveField(((BasicFieldMetadata) md).getForeignKeyClass());
            ppr.addFilterAndSortCriteria(new FilterAndSortCriteria(searchField, query));

            DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();
            ClassMetadata lookupMetadata = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
            for (Entity e : drs.getRecords()) {
                Map<String, String> responseMap = new HashMap<String, String>();
                String idProperty = service.getIdProperty(lookupMetadata);
                responseMap.put("id", e.findProperty(idProperty).getValue());

                String displayKey = e.findProperty(searchField).getDisplayValue();
                if (StringUtils.isBlank(displayKey)) {
                    displayKey = e.findProperty(searchField).getValue();
                }
                responseMap.put("displayKey", displayKey);

                responses.add(responseMap);
            }
        }

        return responses;
    }

    /*
     * @return - JSON String containing the number of milliseconds before a session times out
     */
    @RequestMapping(value = "/sessionTimerReset", method = RequestMethod.GET)
    public @ResponseBody String sessionTimerReset(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long serverSessionTimeoutInterval = request.getSession().getMaxInactiveInterval() * 1000;
        return (new JsonResponse(response))
                .with("serverSessionTimeoutInterval", serverSessionTimeoutInterval)
                .done();
    }

    @RequestMapping(value = "/logJavaScriptError", method = RequestMethod.POST)
    public @ResponseBody String logJavaScriptError(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        // Grab the error information from the request params
        String url = requestParams.getFirst("url");
        String lineNumber = requestParams.getFirst("lineNumber");
        String message = requestParams.getFirst("message");

        // Log the error
        LOG.error("[JS] - (" + StringUtil.sanitize(url) + ":" + StringUtil.sanitize(lineNumber) + ") - " 
                + StringUtil.sanitize(message));

        // Return an errorLogged message to the client
        return (new JsonResponse(response))
                .with("errorLogged", true)
                .done();
    }

    @RequestMapping(value = "/update-navigation", method = RequestMethod.GET)
    public String updateAdminNavigation(HttpServletRequest request, HttpServletResponse response, Model model,
                                        @PathVariable Map<String, String> pathVars) throws Exception {
        return "layout/partials/secondLeftNav";
    }
    
    /**
     * Hook method to allow a user to modify the persistence package request for a fetch on a select lookup.
     * 
     * @param ppr
     * @param pathVars
     */
    protected void modifyFetchPersistencePackageRequest(PersistencePackageRequest ppr, Map<String, String> pathVars) {

    }
}
