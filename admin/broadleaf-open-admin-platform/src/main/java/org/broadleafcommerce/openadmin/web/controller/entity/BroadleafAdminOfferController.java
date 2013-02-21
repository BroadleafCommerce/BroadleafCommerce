/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.controller.entity;

import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafAdminOfferController extends BroadleafAdminBasicEntityController {

    public static final String ITEM_DISCOUNT_TARGET_FIELD_NAME = "targetItemCriteria";
    public static final String ITEM_DISCOUNT_TARGET_CONFIG_KEY = "promotionOrderItem";
    public static final String ITEM_DISCOUNT_TARGET_CEILING_ENTITY = "org.broadleafcommerce.core.order.domain.OrderItem";
    public static final String ITEM_DISCOUNT_TARGET_MVEL = "orderItemMatchRule";

    public static final String ITEM_QUALIFICATION_FIELD_NAME = "qualifyingItemCriteria";
    public static final String ITEM_QUALIFICATION_CONFIG_KEY = "promotionOrderItem";
    public static final String ITEM_QUALIFICATION_CEILING_ENTITY = "org.broadleafcommerce.core.order.domain.OrderItem";
    public static final String ITEM_QUALIFICATION_MVEL = "orderItemMatchRule";

    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
             String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, "offer", id);
        EntityForm entityForm = (EntityForm) model.asMap().get("entityForm");

        List<RuleBuilder> ruleBuilders = entityForm.getCollectionRuleBuilders();
        for (RuleBuilder builder : ruleBuilders) {
            if (ITEM_DISCOUNT_TARGET_FIELD_NAME.equals(builder.getFieldName())){
                builder.setConfigKey(ITEM_DISCOUNT_TARGET_CONFIG_KEY);
                builder.setCeilingEntity(ITEM_DISCOUNT_TARGET_CEILING_ENTITY);
                builder.setMvelProperty(ITEM_DISCOUNT_TARGET_MVEL);
            }
            if (ITEM_QUALIFICATION_FIELD_NAME.equals(builder.getFieldName())){
                builder.setConfigKey(ITEM_QUALIFICATION_CONFIG_KEY);
                builder.setCeilingEntity(ITEM_QUALIFICATION_CEILING_ENTITY);
                builder.setMvelProperty(ITEM_QUALIFICATION_MVEL);
            }
        }

        return view;
    }

    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return super.viewEntityList(request, response, model, "offer");
    }

    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, EntityForm entityForm, BindingResult result) throws Exception {
        return super.saveEntity(request, response, model, "offer", id, entityForm, result);
    }

    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, "offer", id, collectionField);
    }

    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, String collectionField, EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, "offer", id, collectionField, entityForm);
    }

    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            String id, String collectionField, String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, "offer", id, collectionField, collectionItemId);
    }
}
