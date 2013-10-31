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

package org.broadleafcommerce.openadmin.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
        HttpSession session = request.getSession();
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any sandbox
            session.removeAttribute(SANDBOX_ADMIN_ID_VAR);
            SandBoxContext.setSandBoxContext(null);
        } else {
            SandBox sandBox = sandBoxService.retrieveUserSandBox(adminUser);
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
