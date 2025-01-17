/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("blAdminBaseProductController")
@RequestMapping("/"+ AdminProductController.SECTION_KEY + ":" + AdminBaseProductController.SECTION_KEY)
public class AdminBaseProductController extends AdminProductController {

    public static final String SECTION_KEY = "product";

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
                                 @PathVariable Map<String, String> pathVars,
                                 @PathVariable(value = "id") String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, pathVars, id);
        return view;
    }

    @Override
    @RequestMapping(value = {""}, method = {RequestMethod.GET})
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable Map<String, String> pathVars, @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String sectionKey = this.getSectionKey(pathVars);
        String sectionClassName = this.getClassNameForSection(sectionKey);
        List<SectionCrumb> crumbs = this.getSectionCrumbs(request, (String)null, (String)null);
        PersistencePackageRequest ppr = this.getSectionPersistencePackageRequest(sectionClassName, requestParams, crumbs, pathVars);
        ppr.addCustomCriteria("ProductList");
        ClassMetadata cmd = this.service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        DynamicResultSet drs = this.service.getRecords(ppr).getDynamicResultSet();
        ListGrid listGrid = this.formService.buildMainListGrid(drs, cmd, sectionKey, crumbs);
        listGrid.setSelectType(ListGrid.SelectType.NONE);
        Set<Field> headerFields = listGrid.getHeaderFields();
        if (CollectionUtils.isNotEmpty(headerFields)) {
            Field firstField = (Field)headerFields.iterator().next();
            if (requestParams.containsKey(firstField.getName())) {
                model.addAttribute("mainSearchTerm", ((List)requestParams.get(firstField.getName())).get(0));
            }
        }

        model.addAttribute("viewType", "entityList");
        this.setupViewEntityListBasicModel(request, cmd, sectionKey, sectionClassName, model, requestParams);
        model.addAttribute("listGrid", listGrid);
        return "modules/defaultContainer";
    }
}
