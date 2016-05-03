/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author by reginaldccole
 */
@Controller("blAdminUserManagementController")
@RequestMapping("/" + AdminUserManagementController.SECTION_KEY)
public class AdminUserManagementController extends AdminBasicEntityController {

    public static final String SECTION_KEY = "user-management";

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
                                 @PathVariable Map<String, String> pathVars,
                                 @PathVariable(value="id") String id) throws Exception {
        // Get the normal entity form for this item
        String returnPath = super.viewEntityForm(request, response, model, pathVars, id);
        EntityForm ef = (EntityForm) model.asMap().get("entityForm");
        // Remove List Grid for Additional Fields
        ef.removeListGrid("additionalFields");

        return returnPath;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
                             @PathVariable  Map<String, String> pathVars,
                             @PathVariable(value="id") String id,
                             @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
                             RedirectAttributes ra) throws Exception {

        // Get the normal entity form for this item
        String returnPath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);
        // Remove List Grid for Additional Fields
        entityForm.removeListGrid("additionalFields");

        return returnPath;
    }

    @Override
    protected void modifyEntityForm(EntityForm ef, Map<String, String> pathVars) {
        // Remove password/confirm password field for EntityForm edit pages if it has been previously set.
        // Password changes should be done through the "Forgot Password" flow before the user has logged in,
        //      or "Change Password" flow after the user is logged in.
        Field password = ef.findField("password");
        Field passwordConfirm = ef.findField("passwordConfirm");
        if (password != null && password.getValue() != null && !password.getValue().isEmpty()) {
            password.setIsVisible(false);
            passwordConfirm.setIsVisible(false);
        }
    }

}
