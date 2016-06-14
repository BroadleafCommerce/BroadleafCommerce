/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handle page requests for the HTTP status 409 error in the admin.
 *
 * @see StaleStateProtectionService
 * @author Jeff Fischer
 */
@Controller("blStaleStateController")
public class StaleStateController {

    @RequestMapping(value = "/sc_conflict")
    public String viewConflictPage(Model model) throws Exception {
        model.addAttribute("customView", "sc_conflict");
        return "modules/emptyContainer";
    }

}
