/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.handler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;

/**
 * This handler mapping works with the AdminSection entity to determine if a section has been configured for
 * the passed in URL.
 *
 * If the URL matches a valid AdminSection then this mapping returns the handler configured via the
 * controllerName property or blAdminModulesController by default.
 *
 * @author elbertbautista
 * @since 2.1
 * @deprecated no longer necessary after GWT removal
 */
@Deprecated
public class AdminNavigationHandlerMapping extends BLCAbstractHandlerMapping {

    private final String controllerName="blAdminModulesController";

    @Resource(name = "blAdminNavigationService")
    private AdminNavigationService adminNavigationService;

    public static final String CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME = "currentAdminModule";
    public static final String CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME = "currentAdminSection";

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        //TODO: BLCRequestContext should be refactored to be used in the admin as well
        AdminSection adminSection = adminNavigationService.findAdminSectionByURI(getRequestURIWithoutPreamble(request));
        if (adminSection != null && adminSection.getUseDefaultHandler()) {
            request.setAttribute(CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME, adminSection);
            request.setAttribute(CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME, adminSection.getModule());
            if (adminSection.getDisplayController() != null) {
                return adminSection.getDisplayController();
            }

            return controllerName;
        } else {
            return null;
        }

    }

    public String getRequestURIWithoutPreamble(HttpServletRequest request) {
        int lastPos = request.getRequestURI().lastIndexOf("/");
        String requestURIWithoutContext;

        if (lastPos >= 0) {
            requestURIWithoutContext = request.getRequestURI().substring(lastPos, request.getRequestURI().length());
        } else {
            requestURIWithoutContext = request.getRequestURI();
        }

        // Remove JSESSION-ID or other modifiers
        int pos = requestURIWithoutContext.indexOf(";");
        if (pos >= 0) {
            requestURIWithoutContext = requestURIWithoutContext.substring(0,pos);
        }

        return requestURIWithoutContext;
    }

}
