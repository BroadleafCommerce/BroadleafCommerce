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
package org.broadleafcommerce.openadmin.security;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Jeff Fischer
 */
@Component("blAdminSandBoxFilter")
public class AdminSandBoxFilter extends OncePerRequestFilter {

    private static final String SANDBOX_ADMIN_ID_VAR = "blAdminCurrentSandboxId";
    private static String SANDBOX_ID_VAR = "blSandboxId";

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // this filter is not currently wired in
        HttpSession session = request.getSession();
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any sandbox
            session.removeAttribute(SANDBOX_ADMIN_ID_VAR);
            SandBoxContext.setSandBoxContext(null);
        } else {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            if (brc != null) {
                brc.getAdditionalProperties().put("adminUser", adminUser);
            }

            Long overrideSandBoxId = adminUser.getOverrideSandBox() == null ? null : adminUser.getOverrideSandBox().getId();
            SandBox sandBox = sandBoxService.retrieveUserSandBox(adminUser.getId(), overrideSandBoxId, adminUser.getLogin());
            session.setAttribute(SANDBOX_ADMIN_ID_VAR, sandBox.getId());
            session.removeAttribute(SANDBOX_ID_VAR);
            AdminSandBoxContext context = new AdminSandBoxContext();
            context.setSandBoxId(sandBox.getId());
            context.setSandBoxMode(SandBoxMode.IMMEDIATE_COMMIT);
            context.setAdminUser(adminUser);
            SandBoxContext.setSandBoxContext(context);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            SandBoxContext.setSandBoxContext(null);
        }
    }
}
