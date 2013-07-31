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

package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Page} entity. This entity has fields that are 
 * dependent on the value of the {@link Page#getPageTemplate()} field, and as such,
 * it deviates from the typical {@link AdminAbstractEntityController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminPageController")
@RequestMapping("/" + AdminPageController.SECTION_KEY)
public class AdminPageController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "pages";
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id) throws Exception {
        // Get the normal entity form for this item
        String returnPath = super.viewEntityForm(request, response, model, pathVars, id);
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        
        // Attach the dynamic fields to the form
        DynamicEntityFormInfo info = new DynamicEntityFormInfo()
            .withCeilingClassName(PageTemplate.class.getName())
            .withCriteriaName("constructForm")
            .withPropertyName("pageTemplate")
            .withPropertyValue(ef.findField("pageTemplate").getValue());
        EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id);
        ef.putDynamicFormInfo("pageTemplate", info);
        ef.putDynamicForm("pageTemplate", dynamicForm);
        
        // Mark the field that will drive this dynamic form
        ef.findField("pageTemplate").setOnChangeTrigger("dynamicForm-pageTemplate");
        
        return returnPath;
    }
    
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        // Attach the dynamic form info so that the update service will know how to split up the fields
        DynamicEntityFormInfo info = new DynamicEntityFormInfo()
            .withCeilingClassName(PageTemplate.class.getName())
            .withCriteriaName("constructForm")
            .withPropertyName("pageTemplate");
        entityForm.putDynamicFormInfo("pageTemplate", info);
        
        String returnPath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);
        if (result.hasErrors()) {
            info = entityForm.getDynamicFormInfo("pageTemplate");
            info.setPropertyValue(entityForm.findField("pageTemplate").getValue());
            EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id);
            entityForm.putDynamicForm("pageTemplate", dynamicForm);
        }
        
        return returnPath;
    }
    
    @RequestMapping(value = "/{propertyName}/dynamicForm", method = RequestMethod.GET)
    public String getDynamicForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("propertyTypeId") String propertyTypeId) throws Exception {
        DynamicEntityFormInfo info = new DynamicEntityFormInfo()
            .withCeilingClassName(PageTemplate.class.getName())
            .withCriteriaName("constructForm")
            .withPropertyName(propertyName)
            .withPropertyValue(propertyTypeId);
        
        return super.getDynamicForm(request, response, model, pathVars, info);
    }
    
}
