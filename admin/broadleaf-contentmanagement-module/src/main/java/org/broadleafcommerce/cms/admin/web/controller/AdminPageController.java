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
 * it deviates from the typical {@link AdminAbstractEntityController}.
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
        EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id, null);
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
                info.setPropertyValue(entityForm.getFields().get("pageTemplate").getValue());
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
            
            EntityForm dynamicForm = getDynamicFieldTemplateForm(info, id, inputDynamicForm);
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
                
        if (propertyName.equals("pageTemplate")){
    		String[] customCriteriaForPageTemplate = {request.getParameter("pagesId")};
    		info.setCustomCriteriaOverride(customCriteriaForPageTemplate);
    	}
        
        return super.getDynamicForm(request, response, model, pathVars, info);
    }

    @Override
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr, Map<String, String> pathVars) {
        ppr.setSecurityCeilingEntityClassname(Page.class.getName());
    }
    
    /**
     * override method and add some code.
     * Added by Kunner, 2015/08/01, kunner@kunner.com
     * only 197~203 line added.
    */
    @Override
	protected EntityForm getBlankDynamicFieldTemplateForm(DynamicEntityFormInfo info, EntityForm dynamicFormOverride) 
            throws ServiceException {
        // We need to inspect with the second custom criteria set to the id of
        // the desired structured content type
    	/*  Original Source code
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withSecurityCeilingEntityClassname(info.getSecurityCeilingClassName())
                .withCustomCriteria(new String[] { info.getCriteriaName(), null, info.getPropertyName(), info.getPropertyValue() });
        */
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(info.getCeilingClassName())
                .withSecurityCeilingEntityClassname(info.getSecurityCeilingClassName());
        
        // code added
        if (info.getPropertyName().equals("pageTemplate")&& info.getCustomCriteriaOverride()!=null){
        	ppr.setCustomCriteria(new String[] { info.getCriteriaName(), info.getCustomCriteriaOverride()[0], info.getPropertyName(), info.getPropertyValue() });
        }else{
        	ppr.setCustomCriteria(new String[] { info.getCriteriaName(), null, info.getPropertyName(), info.getPropertyValue() });
        }
        // end of added code.
        
        ClassMetadata cmd = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        
        EntityForm dynamicForm = formService.createEntityForm(cmd, null);
        dynamicForm.clearFieldsMap();

        if (dynamicFormOverride != null) {
            dynamicFormOverride.clearFieldsMap();
            Map<String, Field> fieldOverrides = dynamicFormOverride.getFields();
            for (Entry<String, Field> override : fieldOverrides.entrySet()) {
                if (dynamicForm.getFields().containsKey(override.getKey())) {
                    dynamicForm.getFields().get(override.getKey()).setValue(override.getValue().getValue());
                }
            }
        }
        
        // Set the specialized name for these fields - we need to handle them separately
        dynamicForm.clearFieldsMap();
        for (Tab tab : dynamicForm.getTabs()) {
            for (FieldGroup group : tab.getFieldGroups()) {
                for (Field field : group.getFields()) {
                    field.setName(info.getPropertyName() + DynamicEntityFormInfo.FIELD_SEPARATOR + field.getName());
                }
            }
        }

        //extensionManager.getProxy().modifyDynamicForm(dynamicForm, );

        return dynamicForm;
    }
}
