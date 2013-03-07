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

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafAdminOfferController extends BroadleafAdminBasicEntityController {

    public static final String ITEM_DISCOUNT_TARGET_FIELD_NAME = "targetItemCriteria";
    public static final String ITEM_DISCOUNT_TARGET_FIELD_BUILDER = "ORDER_ITEM_FIELDS";

    @Override
    public String getSectionCustomCriteria() {
        return "Offer";
    }

    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
             String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, "offer", id);
        EntityForm entityForm = (EntityForm) model.asMap().get("entityForm");
        Entity entity = (Entity) model.asMap().get("entity");

        for (Tab tab : entityForm.getTabs()) {
            Set<RuleBuilder> ruleBuilders = tab.getRuleBuilders();
            for (RuleBuilder builder : ruleBuilders) {
                if (ITEM_DISCOUNT_TARGET_FIELD_NAME.equals(builder.getFieldName())){
                    builder.setFieldBuilder(ITEM_DISCOUNT_TARGET_FIELD_BUILDER);
                    String json = entity.getPMap().get("targetItemCriteriaJson").getValue();
                    builder.setJson(json);

                    //When using Thymeleaf, we need to convert it back to
                    //a DataWrapper object because Thymeleaf escapes JSON strings.
                    //Thymeleaf uses it's own object de-serializer
                    //see: https://github.com/thymeleaf/thymeleaf/issues/84
                    //see: http://forum.thymeleaf.org/Spring-Javascript-and-escaped-JSON-td4024739.html
                    ObjectMapper mapper = new ObjectMapper();
                    DataDTODeserializer dtoDeserializer = new DataDTODeserializer();
                    SimpleModule module = new SimpleModule("DataDTODeserializerModule", new Version(1, 0, 0, null));
                    module.addDeserializer(DataDTO.class, dtoDeserializer);
                    mapper.registerModule(module);
                    builder.setDataWrapper(mapper.readValue(json, DataWrapper.class));
                }
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
