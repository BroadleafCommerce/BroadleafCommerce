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
package org.broadleafcommerce.openadmin.processor;


import org.broadleafcommerce.common.web.dialect.AbstractBroadleafModelVariableModifierProcessor;
import org.broadleafcommerce.common.web.dialect.BroadleafDialectPrefix;
import org.broadleafcommerce.common.web.domain.BroadleafTemplateContext;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add the appropriate AdminUser to the model.
 *
 * @author elbertbautista
 */
@Component("blAdminUserProcessor")
public class AdminUserProcessor extends AbstractBroadleafModelVariableModifierProcessor {

    private static final String ANONYMOUS_USER_NAME = "anonymousUser";
    
    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService securityService;

    @Override
    public String getName() {
        return "admin_user";
    }
    
    @Override
    public BroadleafDialectPrefix getPrefix() {
        return BroadleafDialectPrefix.BLC_ADMIN;
    }
    
    @Override
    public int getPrecedence() {
        return 10000; 
    }

    @Override
    public void populateModelVariables(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafTemplateContext context) {
        String resultVar = tagAttributes.get("resultVar");

        AdminUser user = getPersistentAdminUser();
        if (user != null) {
            newModelVars.put(resultVar, user);
        }
    }
    
    protected AdminUser getPersistentAdminUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            if (auth != null && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                UserDetails temp = (UserDetails) auth.getPrincipal();

                return securityService.readAdminUserByUserName(temp.getUsername());
            }
        }

        return null;
    }

}
