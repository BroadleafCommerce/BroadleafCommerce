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
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Controller
@RequestMapping("offer")
public class BroadleafAdminOfferController extends BroadleafAdminBasicEntityController {

    @Override
    public String[] getSectionCustomCriteria() {
        return new String[]{"Offer"};
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, "offer", id);
        
        EntityForm entityForm = (EntityForm) model.asMap().get("entityForm");
        Entity entity = (Entity) model.asMap().get("entity");
        String additionalClasses = (String) model.asMap().get("additionalClasses");
        
        if (additionalClasses == null) {
            additionalClasses = "";
        }
        additionalClasses = additionalClasses.concat(" rulebuilder-form ");
        model.addAttribute("additionalClasses", additionalClasses);

        //TODO support i18N
        constructRuleBuilder(entityForm, entity, "appliesToOrderRules","Order Qualification",
                "rule-builder-simple","appliesToOrderRulesFieldService","appliesToOrderRulesJson");

        constructRuleBuilder(entityForm, entity, "appliesToCustomerRules","Customer Qualification",
                "rule-builder-simple","appliesToCustomerRulesFieldService","appliesToCustomerRulesJson");

        constructRuleBuilder(entityForm, entity, "appliesToFulfillmentGroupRules","Fulfillment Group Qualification",
                "rule-builder-simple","appliesToFulfillmentGroupRulesFieldService","appliesToFulfillmentGroupRulesJson");

        constructRuleBuilder(entityForm, entity, "qualifyingItemCriteria","Qualifying Item Criteria",
                "rule-builder-complex","qualifyingItemCriteriaFieldService","qualifyingItemCriteriaJson");

        constructRuleBuilder(entityForm, entity, "targetItemCriteria","Target Item Criteria",
                "rule-builder-complex","targetItemCriteriaFieldService","targetItemCriteriaJson");

        return view;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return super.viewEntityList(request, response, model, "offer");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        return super.saveEntity(request, response, model, "offer", id, entityForm, result, ra);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, "offer", id, collectionField);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.POST)
    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, "offer", id, collectionField, entityForm);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, "offer", id, collectionField, collectionItemId);
    }
    
    @Override
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
    }

    protected void constructRuleBuilder(EntityForm entityForm, Entity entity,
            String fieldName, String friendlyName, String styleClass, String fieldService, 
            String fieldJson) throws IOException {
        RuleBuilder ruleBuilder = new RuleBuilder();
        ruleBuilder.setFieldName(fieldName);
        ruleBuilder.setFriendlyName(friendlyName);
        ruleBuilder.setStyleClass(styleClass);
        ruleBuilder.setFieldBuilder(entity.getPMap().get(fieldService).getValue());
        ruleBuilder.setJsonFieldName(fieldJson);
        ruleBuilder.setDataWrapper(new DataWrapper());
        if (entity.getPMap().get(fieldJson) != null) {
            String json = entity.getPMap().get(fieldJson).getValue();
            ruleBuilder.setJson(json);
            DataWrapper dw = (convertJsonToDataWrapper(json) != null)? convertJsonToDataWrapper(json) : new DataWrapper();
            ruleBuilder.setDataWrapper(dw);
        }
        entityForm.addRuleBuilder(ruleBuilder, null, null);
    }

    /**
     * When using Thymeleaf, we need to convert the JSON string back to
     * a DataWrapper object because Thymeleaf escapes JSON strings.
     * Thymeleaf uses it's own object de-serializer
     * see: https://github.com/thymeleaf/thymeleaf/issues/84
     * see: http://forum.thymeleaf.org/Spring-Javascript-and-escaped-JSON-td4024739.html
     * @param json
     * @return DataWrapper
     * @throws IOException
     */
    protected DataWrapper convertJsonToDataWrapper(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DataDTODeserializer dtoDeserializer = new DataDTODeserializer();
        SimpleModule module = new SimpleModule("DataDTODeserializerModule", new Version(1, 0, 0, null));
        module.addDeserializer(DataDTO.class, dtoDeserializer);
        mapper.registerModule(module);
        return mapper.readValue(json, DataWrapper.class);
    }
}
