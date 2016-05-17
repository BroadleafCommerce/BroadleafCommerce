/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Offer} entity. Certain Offer fields should only render when specific values
 * are set for other fields; we provide the support for that in this controller.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminOfferController")
@RequestMapping("/" + AdminOfferController.SECTION_KEY)
public class AdminOfferController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "offer";
    
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
        String view = super.viewEntityForm(request, response, model, pathVars, id);
        modifyModelAttributes(model);
        return view;
    }
    
    @Override
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String viewAddEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @RequestParam(defaultValue = "") String entityType) throws Exception {
        String view = super.viewAddEntityForm(request, response, model, pathVars, entityType);
        modifyModelAttributes(model);
        return view;
    }
    
    @Override
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        String view = super.addEntity(request, response, model, pathVars, entityForm, result);
        if (result.hasErrors()) {
            modifyModelAttributes(model);
        }
        return view;
    }
    
    /**
     * Offer field visibility is dependent on other fields in the entity. Mark the form with the appropriate class
     * so that the Javascript will know to handle this form.
     * 
     * We also want to tell the UI to make item target criteria required. We cannot manage this at the entity level via an
     * @AdminPresentation annotation as it is only required when the offer type has a type of {@link OfferType#ORDER_ITEM}.
     */
    protected void modifyModelAttributes(Model model) {
        model.addAttribute("additionalControllerClasses", "offer-form");
        if (model.asMap().containsKey("entityForm")) {
            EntityForm form = (EntityForm) model.asMap().get("entityForm");
            form.findField("targetItemCriteria").setRequired(true);
        }
    }
    
}
