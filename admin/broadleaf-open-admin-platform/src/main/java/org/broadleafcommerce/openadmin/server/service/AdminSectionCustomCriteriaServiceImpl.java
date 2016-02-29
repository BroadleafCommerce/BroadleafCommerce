/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
