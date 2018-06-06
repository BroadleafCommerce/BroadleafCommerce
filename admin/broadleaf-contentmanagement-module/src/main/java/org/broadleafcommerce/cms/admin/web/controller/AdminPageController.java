/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Page} entity. This entity has fields that are 
 * dependent on the value of the {@link Page#getPageTemplate()} field, and as such,
 * it deviates from the typical {@link AdminBasicEntityController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminPageController")
@RequestMapping("/" + AdminPageController.SECTION_KEY)
public class AdminPageController extends AdminBasicEntityController {
    
    public static final String SECTION_KEY = "pages";
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    protected DynamicEntityFormInfo getDynamicForm(EntityForm ef, String id) {
        return new DynamicEntityFormInfo()
            .withCeilingClassName(PageTemplate.class.getName())
            .withSecurityCeilingClassName(Page.class.getName())
            .withCriteriaName("constructForm")
            .withPropertyName("pageTemplate")
            .withPropertyValue(ef.findField("pageTemplate").getValue());
    }
    
    protected void addOnChangeTrigger(EntityForm ef) {
        ef.findField("pageTemplate").setOnChangeTrigger("dynamicForm-pageTemplate");
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id) throws Exception {
        // Get the normal entity form for this item
        String returnPath = super.viewEntityForm(request, response, model, pathVars, id);
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        
        DynamicEntityFormInfo info = getDynamicForm(ef, id);
        EntityForm dynamicForm;
        if (info.getPropertyValue() != null) {
            dynamicForm = getDynamicFieldTemplateForm(info, id, null);
        } else {
            dynamicForm = getEntityForm(info, null);
        }
        if (dynamicForm.getCeilingEntityClassname().equals(PageTemplate.class.getName())) {
            dynamicForm.setTranslationCeilingEntity(Page.class.getName());
            dynamicForm.setTranslationId(id);
        }
        ef.putDynamicFormInfo("pageTemplate", info);
        ef.putDynamicForm("pageTemplate", dynamicForm);

        // Mark the field that will drive this dynamic form
        addOnChangeTrigger(ef);
        
        ef.removeListGrid("additionalAttributes");
        
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
                .withSecurityCeilingClassName(Page.class.getName())
                .withCriteriaName("constructForm")
                .withPropertyName("pageTemplate");
        entityForm.putDynamicFormInfo("pageTemplate", info);
        
        String returnPath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);
        if (result.hasErrors()) {
            info = entityForm.getDynamicFormInfo("pageTemplate");
            if (entityForm.getFields().containsKey("pageTemplate")) {
                info.setPropertyValue(entityForm.findField("pageTemplate").getValue());
            }
            
            //grab back the dynamic form that was actually put in
            EntityForm inputDynamicForm = entityForm.getDynamicForm("pageTemplate");
            if (inputDynamicForm != null) {
                List<Field> fieldsToChange = new ArrayList<Field>();
                String prefix = "pageTemplate" + DynamicEntityFormInfo.FIELD_SEPARATOR;
                for (Entry<String, Field> entry : inputDynamicForm.getFields().entrySet()) {
                    if (entry.getKey().startsWith(prefix)) {
                        fieldsToChange.add(entry.getValue());
                    }
                }
                for (Field f : fieldsToChange) {
                    inputDynamicForm.getFields().remove(f.getName());
                    f.setName(f.getName().substring(prefix.length()));
                    inputDynamicForm.getFields().put(f.getName(), f);
                }
            }
            EntityForm dynamicForm;
            if (info.getPropertyValue() != null) {
                dynamicForm = getDynamicFieldTemplateForm(info, id, inputDynamicForm);
            } else {
                dynamicForm = getEntityForm(info, inputDynamicForm);
            }
            entityForm.putDynamicForm("pageTemplate", dynamicForm);

            entityForm.removeListGrid("additionalAttributes");
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
                .withSecurityCeilingClassName(Page.class.getName())
                .withCriteriaName("constructForm")
                .withPropertyName(propertyName)
                .withPropertyValue(propertyTypeId);
        
        return super.getDynamicForm(request, response, model, pathVars, info);
    }

    @Override
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr, Map<String, String> pathVars) {
        ppr.setSecurityCeilingEntityClassname(Page.class.getName());
    }
}
