/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.util.List;

/**
 * An abstract controller that provides convenience methods and resource declarations for the Admin
 * All Admin controllers must extend this class in order to be registered with the Left-hand Navigation.
 *
 * Operations that are shared between all admin controllers belong here.
 *
 * @see org.broadleafcommerce.openadmin.web.handler.AdminNavigationHandlerMapping
 * @author elbertbautista
 * @author apazzolini
 */
public abstract class BroadleafAdminAbstractController extends BroadleafAbstractController {

    protected String currentSectionKey;

    @Resource(name = "blAdminNavigationService")
    private AdminNavigationService adminNavigationService;

    @ModelAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_MODULE_ATTRIBUTE_NAME)
    public AdminModule getCurrentAdminModule() {
        AdminSection section = getCurrentAdminSection();

        if (section != null) {
            return section.getModule();
        }

        return null;
    }

    @ModelAttribute(AdminNavigationHandlerMapping.CURRENT_ADMIN_SECTION_ATTRIBUTE_NAME)
    public AdminSection getCurrentAdminSection() {
        return adminNavigationService.findAdminSectionBySectionKey(getCurrentSectionKey());
    }

    public abstract String getCurrentSectionKey();

}
