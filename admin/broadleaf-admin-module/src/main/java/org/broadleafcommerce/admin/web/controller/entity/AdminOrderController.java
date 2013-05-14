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

package org.broadleafcommerce.admin.web.controller.entity;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Order} entity. 
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminOrderController")
@RequestMapping("/" + AdminOrderController.SECTION_KEY)
public class AdminOrderController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "order";
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    /**
     * This method provides standard operations for all collection fields other than orderItems. As part of 2.3, 
     * Broadleaf deprecated the old orderItemAdjustments collection in favor of the more robust orderItemPriceDetails.
     * 
     * However, to maintain legacy compability and not force a database migration for Orders, the legacy style is still 
     * supported. This method will hide the appropriate collection based on the type of the order (legacy vs non-legacy).
     */
    @Override
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        String returnPath = super.showUpdateCollectionItem(request, response, model, pathVars, id, collectionField,
                        collectionItemId);
        showViewOrderItemCollectionItem(request, response, model, pathVars, id, collectionField,
                collectionItemId);
        return returnPath;
    }

    @Override
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/view", method = RequestMethod.GET)
    public String showViewCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @PathVariable(value="collectionField") String collectionField,
            @PathVariable(value="collectionItemId") String collectionItemId) throws Exception {
        String returnPath = super.showViewCollectionItem(request, response, model, pathVars, id, collectionField,
                        collectionItemId);
        showViewOrderItemCollectionItem(request, response, model, pathVars, id, collectionField,
                collectionItemId);

        return returnPath;
    }

    protected void showViewOrderItemCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model, Map<String, String> pathVars, String id, String collectionField, String collectionItemId) throws Exception {
        if ("orderItems".equals(collectionField)) {
            EntityForm ef = (EntityForm) model.asMap().get("entityForm");

            ListGrid adjustmentsGrid = ef.findListGrid("orderItemAdjustments");
            if (adjustmentsGrid != null && CollectionUtils.isEmpty(adjustmentsGrid.getRecords())) {
                ef.removeListGrid("orderItemAdjustments");
            }

            ListGrid priceDetailsGrid = ef.findListGrid("orderItemPriceDetails");
            if (priceDetailsGrid != null && CollectionUtils.isEmpty(priceDetailsGrid.getRecords())) {
                ef.removeListGrid("orderItemPriceDetails");
            }
        }
    }

}
