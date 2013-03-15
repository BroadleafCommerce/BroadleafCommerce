/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.BroadleafAdminAbstractEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminStructuredContentController")
@RequestMapping("/structured-content")
public class BroadleafAdminStructuredContentController extends BroadleafAdminAbstractEntityController {
    
    protected static final String SECTION_KEY = "structured-content";

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return super.viewEntityList(request, response, model, SECTION_KEY);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam(defaultValue = "") String entityType) throws Exception {
        return super.viewAddEntityForm(request, response, model, SECTION_KEY, entityType);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.addEntity(request, response, model, SECTION_KEY, entityForm, result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id) throws Exception {
        // Hardcoded values - may be extracted to annotation eventually
        String criteriaName = "constructForm";
        String propertyName = "structuredContentType";
        String ceilingClassName = StructuredContentType.class.getName();
        
        String returnPath = super.viewEntityForm(request, response, model, SECTION_KEY, id);
        
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        
        EntityForm dynamicForm = getDynamicFieldTemplateForm(criteriaName, propertyName, ceilingClassName, 
                ef.findField(propertyName).getValue(), id);
        ef.findField(propertyName).setOnChangeTrigger("dynamicForm-" + propertyName);

        ef.putDynamicForm(propertyName, dynamicForm);
        
        return returnPath;
    }
    
    @RequestMapping(value = "/{id}/dynamicForm", method = RequestMethod.GET)
    public String getDynamicForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @RequestParam("propertyName") String propertyName,
            @RequestParam("propertyTypeId") String propertyTypeId) throws Exception {
        // Hardcoded values - may be extracted to annotation eventually
        String criteriaName = "constructForm";
        String ceilingClassName = StructuredContentType.class.getName();
        
        EntityForm blankFormContainer = new EntityForm();
        EntityForm dynamicForm = getBlankDynamicFieldTemplateForm(criteriaName, propertyName, ceilingClassName, 
                propertyTypeId);

        blankFormContainer.putDynamicForm(propertyName, dynamicForm);
        model.addAttribute("entityForm", blankFormContainer);
        
        String reqUrl = request.getRequestURL().toString();
        reqUrl = reqUrl.substring(0, reqUrl.indexOf("/dynamicForm"));
        model.addAttribute("currentUrl", reqUrl);
        
        return "views/dynamicFormPartial";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        // Update the normal form
        String returnPath = super.saveEntity(request, response, model, SECTION_KEY, id, entityForm, result, ra);
        
        // Hardcoded values - may be extracted to annotation eventually
        String criteriaName = "constructForm";
        String propertyName = "structuredContentType";
        String ceilingClassName = StructuredContentType.class.getName();
        
        Map<String, Field> dynamicFields = new HashMap<String, Field>();
        
        // Find all of the dynamic form fields
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            if (entry.getKey().contains("|")) { 
                dynamicFields.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Remove the dynamic form fields from the main entity - they are persisted separately
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            entityForm.removeField(entry.getKey());
        }
        
        // Find the appropriate type that we need for this dynamic form field
        // TODO APA - this is how to get it dynamically. but we need the interface anyways and not the impl.
        //PersistencePackageRequest ppr = getSectionPersistencePackageRequest(getClassNameForSection(SECTION_KEY));
        //ClassMetadata cmd = service.getClassMetadata(ppr);
        //BasicFieldMetadata fmd = (BasicFieldMetadata) cmd.getPMap().get(propertyName).getMetadata();
        //String dynamicFieldClass = fmd.getForeignKeyClass();
        
        // Create the entity form for the dynamic form, as it needs to be persisted separately
        EntityForm dynamicForm = new EntityForm();
        dynamicForm.setEntityType(ceilingClassName);
        for (Entry<String, Field> entry : dynamicFields.entrySet()) {
            String[] fieldName = entry.getKey().split("\\|");
            if (!fieldName[0].equals(propertyName)) {
                throw new RuntimeException("Unknown field - we only support one dynamic form per page currently.");
            }
            entry.getValue().setName(fieldName[1]);
            dynamicForm.addField(entry.getValue());
        }
        
        // Update the dynamic form
        service.updateEntity(dynamicForm, new String[] { criteriaName,  id });
        
        return returnPath;
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String removeEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        return super.removeEntity(request, response, model, SECTION_KEY, id, entityForm, result);
    }

    @RequestMapping(value = "/{collectionField}/select", method = RequestMethod.GET)
    public String showSelectCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String collectionField) throws Exception {
        return super.showSelectCollectionItem(request, response, model, SECTION_KEY, collectionField);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.GET)
    public String showAddCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField) throws Exception {
        return super.showAddCollectionItem(request, response, model, SECTION_KEY, id, collectionField);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.GET)
    public String showUpdateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.showUpdateCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId);
    }

    @RequestMapping(value = "/{id}/{collectionField}/add", method = RequestMethod.POST)
    public String addCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.addCollectionItem(request, response, model, SECTION_KEY, id, collectionField, entityForm);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}", method = RequestMethod.POST)
    public String updateCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId,
            @ModelAttribute EntityForm entityForm) throws Exception {
        return super.updateCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId, 
                entityForm);
    }

    @RequestMapping(value = "/{id}/{collectionField}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String removeCollectionItem(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String id,
            @PathVariable String collectionField,
            @PathVariable String collectionItemId) throws Exception {
        return super.removeCollectionItem(request, response, model, SECTION_KEY, id, collectionField, collectionItemId);
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
    }
    
    @Override
    protected void attachSectionSpecificInfo(PersistencePackageRequest ppr) {
        ppr.addAdditionalForeignKey(new ForeignKey("structuredContentType", EntityImplementations.STRUCTUREDCONTENTTYPEIMPL, null, ForeignKeyRestrictionType.ID_EQ, "name"));
        ppr.addAdditionalForeignKey(new ForeignKey("locale", EntityImplementations.LOCALEIMPL, null, ForeignKeyRestrictionType.ID_EQ, "friendlyName"));
    }
    
}
