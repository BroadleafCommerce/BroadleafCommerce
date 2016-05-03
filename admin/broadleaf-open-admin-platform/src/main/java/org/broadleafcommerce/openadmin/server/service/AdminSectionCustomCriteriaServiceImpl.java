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
package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.openadmin.server.service.extension.AdminSectionCustomCriteriaExtensionManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Jon on 1/27/16.
 */
@Service("blAdminSectionCustomCriteriaService")
public class AdminSectionCustomCriteriaServiceImpl implements AdminSectionCustomCriteriaService {

    @Resource(name="blSectionCustomCriteriaMap")
    protected Map<String, ArrayList<String>> customCriteria;

    @Resource(name = "blAdminSectionCustomCriteriaExtensionManager")
    protected AdminSectionCustomCriteriaExtensionManager extensionManager;

    @Override
    public String[] mergeSectionCustomCriteria(String ceilingEntityClassName, String[] controllerCriteria) {
        ArrayList<String> sectionCriteria = new ArrayList<String>();
        if (controllerCriteria != null) {
            sectionCriteria = new ArrayList<>(Arrays.asList(controllerCriteria));
        }

        // Find any section custom criteria defined in XML
        ArrayList<String> xmlCriteria = customCriteria.get(ceilingEntityClassName);

        if (xmlCriteria != null) {
            // Merge custom criteria from the controller and XML
            sectionCriteria.removeAll(xmlCriteria);
            sectionCriteria.addAll(xmlCriteria);
        }

        // Some sections require additional logic to apply section custom criteria.
        // This is an extension point for more complex criteria assignment.
        // Some controllers do not have a specific ceilingEntity, only perform if ceilingEntity is provided
        if (ceilingEntityClassName != null) {
            ArrayList<String> extensionCriteria = new ArrayList<String>();
            extensionManager.getProxy().addAdditionalSectionCustomCriteria(extensionCriteria, ceilingEntityClassName);
            sectionCriteria.removeAll(extensionCriteria);
            sectionCriteria.addAll(extensionCriteria);
        }
        return sectionCriteria.toArray(new String[sectionCriteria.size()]);
    }
}
