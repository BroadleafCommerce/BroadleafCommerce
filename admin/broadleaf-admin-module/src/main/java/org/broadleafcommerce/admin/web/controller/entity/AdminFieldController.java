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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
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

@Controller("blAdminFieldController")
@RequestMapping("/" + AdminFieldController.SECTION_KEY)
public class AdminFieldController extends AdminBasicEntityController {

    public static final String SECTION_KEY = "org.broadleafcommerce.core.search.domain.FieldImpl";

    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    @RequestMapping(value = "/selectize", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> viewEntityListSelectize(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @PathVariable Map<String, String> pathVars,
            @RequestParam MultiValueMap<String, String> requestParams
    ) throws Exception {
        String sectionKey = getSectionKey(pathVars);
        String sectionClassName = getClassNameForSection(sectionKey);
        List<SectionCrumb> crumbs = getSectionCrumbs(request, null, null);
        PersistencePackageRequest ppr = getSectionPersistencePackageRequest(sectionClassName, requestParams, crumbs, pathVars)
                .withCustomCriteria(getCustomCriteria(requestParams));

        ppr.addCustomCriteria(buildSelectizeCustomCriteria());

        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        DynamicResultSet drs = service.getRecords(ppr).getDynamicResultSet();

        return constructSelectizeOptionMap(drs, cmd);
    }

    public Map<String, Object> constructSelectizeOptionMap(DynamicResultSet drs, ClassMetadata cmd) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> options = new ArrayList<>();

        for (Entity e : drs.getRecords()) {
            Map<String, String> selectizeOption = new HashMap<>();

            Property p = e.findProperty(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
            if (p != null) {
                selectizeOption.put("name", p.getValue());
                selectizeOption.put("id", p.getValue());
            }
            options.add(selectizeOption);
        }
        result.put("options", options);

        return result;
    }

}
