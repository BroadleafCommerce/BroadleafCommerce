/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
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
 * Handles admin operations for the {@link StructuredContent} entity. This entity has fields that are 
 * dependent on the value of the {@link StructuredContent#getStructuredContentType()} field, and as such,
 * it deviates from the typical {@link AdminAbstractEntityController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@RequestMapping("/" + AdminStructuredContentController.SECTION_KEY)
public class AdminStructuredContentController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "structured-content";
    
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
                .withCeilingClassName(StructuredContentType.class.getName())
                .withSecurityCeilingClassName(StructuredContent.class.getName())
                .withCriteriaName("constructForm")
                .withPropertyName("structuredContentType")
                .withPropertyValue(ef.findField("structuredContentType").getValue());
        EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id, null);
        ef.putDynamicFormInfo("structuredContentType", info);
        ef.putDynamicForm("structuredContentType", dynamicForm);
        
        // We don't want to allow changing types once a structured content item exists
        ef.findField("structuredContentType").setReadOnly(true);
        
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
                .withCeilingClassName(StructuredContentType.class.getName())
                .withSecurityCeilingClassName(StructuredContent.class.getName())
                .withCriteriaName("constructForm")
                .withPropertyName("structuredContentType");
        entityForm.putDynamicFormInfo("structuredContentType", info);
        
        String returnPath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);
        
        if (result.hasErrors()) {
            info = entityForm.getDynamicFormInfo("structuredContentType");
            info.setPropertyValue(entityForm.findField("structuredContentType").getValue());
            
            //grab back the dynamic form that was actually put in
            EntityForm inputDynamicForm = entityForm.getDynamicForm("structuredContentType");
            
            EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id, inputDynamicForm);
            entityForm.putDynamicForm("structuredContentType", dynamicForm);
        }
        
        return returnPath;
    }
    
    @RequestMapping(value = "/{propertyName}/dynamicForm", method = RequestMethod.GET)
    public String getDynamicForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("propertyTypeId") String propertyTypeId) throws Exception {
        DynamicEntityFormInfo info = new DynamicEntityFormInfo()
                .withCeilingClassName(StructuredContentType.class.getName())
                .withSecurityCeilingClassName(StructuredContent.class.getName())
                .withCriteriaName("constructForm")
                .withPropertyName(propertyName)
                .withPropertyValue(propertyTypeId);
        
        return super.getDynamicForm(request, response, model, pathVars, info);
    }
    
}
