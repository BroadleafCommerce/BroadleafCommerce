/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.common.persistence.EntityDuplicator;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.JsonResponse;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
    
    public static final String SECTION_KEY = "offer";
    public static String[] customCriteria = {};

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name="blEntityDuplicator")
    protected EntityDuplicator duplicator;

    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }
    
    @Override
    public String[] getSectionCustomCriteria() {
        return customCriteria;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
                                 @PathVariable Map<String, String> pathVars,
                                 @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        customCriteria = new String[]{"listGridView"};
        String view = super.viewEntityList(request, response, model, pathVars, requestParams);
        return view;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id) throws Exception {
        customCriteria = new String[]{};
        String view = super.viewEntityForm(request, response, model, pathVars, id);
        modifyModelAttributes(model);
        addDuplicateOption(model, id);
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

    @RequestMapping(value = "/{id}/duplicate", method = RequestMethod.POST)
    public String duplicateEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars, @PathVariable(value="id") String id,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result) throws Exception {
        if (duplicator.validate(OfferImpl.class, Long.parseLong(id))) {
            String sectionKey = getSectionKey(pathVars);
            Offer duplicate;
            try {
                duplicate = offerService.duplicate(Long.parseLong(id));
            } catch (Exception e) {
                return getErrorDuplicatingResponse(response, "Duplication_Failure");
            }

            // Note that AJAX Redirects need the context path prepended to them
            return "ajaxredirect:" + getContextPath(request) + sectionKey + "/" + duplicate.getId();
        } else {
            return getErrorDuplicatingResponse(response, "Validation_Failure");
        }
    }

    protected String getErrorDuplicatingResponse(HttpServletResponse response, String code) {
        List<Map<String, Object>> errors = new ArrayList<>();
        String message;
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getMessageSource() != null) {
            message = context.getMessageSource().getMessage(code, null, code, context.getJavaLocale());
        } else {
            LOG.warn("Could not find the MessageSource on the current request, not translating the message key");
            message = "Duplication_Failure";
        }

        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("errorType", "global");
        errorMap.put("code", code);
        errorMap.put("message", message);
        errors.add(errorMap);
        return new JsonResponse(response).with("errors", errors).done();
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
        EntityForm form = (EntityForm) model.asMap().get("entityForm");
        if (form != null && form.findField("targetItemCriteria") != null) {
            form.findField("targetItemCriteria").setRequired(true);
        }
    }

    protected void addDuplicateOption(Model model, String id) {
        if (duplicator.validate(OfferImpl.class, Long.parseLong(id))) {
            EntityForm form = (EntityForm) model.asMap().get("entityForm");
            EntityFormAction duplicate = new EntityFormAction("duplicate")
                    .withButtonClass("duplicate-button")
                    .withDisplayText("Duplicate");
            form.addAction(duplicate);
        }
    }
}
