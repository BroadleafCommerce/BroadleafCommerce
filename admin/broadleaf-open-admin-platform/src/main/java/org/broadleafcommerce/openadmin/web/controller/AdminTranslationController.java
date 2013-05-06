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

package org.broadleafcommerce.openadmin.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.web.form.TranslationForm;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.TranslationFormBuilderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller("blAdminTranslationController")
@RequestMapping("/translation")
public class AdminTranslationController extends AdminAbstractController {
    
    @Resource(name = "blTranslationService")
    protected TranslationService translationService;
    
    @Resource(name = "blTranslationFormBuilderService")
    protected TranslationFormBuilderService formService;
    
    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    /**
     * Invoked when the translation button is clicked on a given translatable field
     * 
     * @param request
     * @param response
     * @param model
     * @param form
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute TranslationForm form, BindingResult result) throws Exception {
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.FETCH);

        List<Translation> translations = 
                translationService.getTranslations(form.getCeilingEntity(), form.getEntityId(), form.getPropertyName());
        ListGrid lg = formService.buildListGrid(translations);
        
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("form", form);
        model.addAttribute("listGrid", lg);
        model.addAttribute("viewType", "modal/translationListGrid");
        model.addAttribute("modalHeaderType", "translation");
        return "modules/modalContainer";
    }
    
    /**
     * Renders a modal dialog that has a list grid of translations for the specified field
     * 
     * @param request
     * @param response
     * @param model
     * @param form
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute TranslationForm form, BindingResult result) throws Exception {
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.FETCH);
        
        EntityForm entityForm = formService.buildTranslationForm(form);
        
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/translationAdd");
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "addTranslation");
        return "modules/modalContainer";
    }
    
    /**
     * Saves a new translation to the database. 
     * 
     * Note that if the ceiling entity, entity id, property name, and locale code match a previously existing translation,
     * this method will update that translation.
     * 
     * @param request
     * @param response
     * @param model
     * @param entityForm
     * @param result
     * @return the result of a call to {@link #viewTranslation}, which renders the list grid
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        TranslationForm form = getTranslationForm(entityForm);
        
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.UPDATE);
        
        translationService.save(form.getCeilingEntity(), form.getEntityId(), form.getPropertyName(), form.getLocaleCode(), 
                form.getTranslatedValue());
        return viewTranslation(request, response, model, form, result);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String showUpdateTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute TranslationForm form, BindingResult result) throws Exception {
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.FETCH);
        
        EntityForm entityForm = formService.buildTranslationForm(form);
        entityForm.setId(String.valueOf(form.getTranslationId()));
        
        model.addAttribute("entityForm", entityForm);
        model.addAttribute("viewType", "modal/translationAdd");
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "updateTranslation");
        return "modules/modalContainer";
    }
    
    /**
     * Updates the given translation id to the new locale code and translated value
     * 
     * @param request
     * @param response
     * @param model
     * @param entityForm
     * @param result
     * @return the result of a call to {@link #viewTranslation}, which renders the list grid
     * @throws Exception
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute EntityForm entityForm, BindingResult result) throws Exception {
        TranslationForm form = getTranslationForm(entityForm);
        
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.UPDATE);
        
        translationService.update(form.getTranslationId(), form.getLocaleCode(), form.getTranslatedValue());
        return viewTranslation(request, response, model, form, result);
    }
    
    /**
     * Deletes the translation specified by the translation id
     * 
     * @param request
     * @param response
     * @param model
     * @param id
     * @param form
     * @param result
     * @return the result of a call to {@link #viewTranslation}, which renders the list grid
     * @throws Exception
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteTranslation(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute TranslationForm form, BindingResult result) throws Exception {
        adminRemoteSecurityService.securityCheck(form.getCeilingEntity(), EntityOperationType.UPDATE);
        
        translationService.deleteTranslationById(form.getTranslationId());
        return viewTranslation(request, response, model, form, result);
    }
    
    /**
     * Converts an EntityForm into a TranslationForm
     * 
     * @param entityForm
     * @return the converted translation form
     */
    protected TranslationForm getTranslationForm(EntityForm entityForm) {
        TranslationForm form = new TranslationForm();
        form.setCeilingEntity(entityForm.getFields().get("ceilingEntity").getValue());
        form.setEntityId(entityForm.getFields().get("entityId").getValue());
        form.setLocaleCode(entityForm.getFields().get("localeCode").getValue());
        form.setPropertyName(entityForm.getFields().get("propertyName").getValue());
        form.setTranslatedValue(entityForm.getFields().get("translatedValue").getValue());
        if (StringUtils.isNotBlank(entityForm.getId())) {
            form.setTranslationId(Long.parseLong(entityForm.getId()));
        }
        return form;
    }

}