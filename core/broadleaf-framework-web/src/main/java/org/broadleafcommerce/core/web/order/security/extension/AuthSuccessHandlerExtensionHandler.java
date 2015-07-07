/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order.security.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Extension handler for actions that should take place after a user has authenticated on the front-end site.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface AuthSuccessHandlerExtensionHandler extends ExtensionHandler {
    
    /**
     * Perform any necessary tasks before the merge cart processor executes.
     * 
     * @param request
     * @param response
     * @param authentication
     * @return
     */
    public ExtensionResultStatusType preMergeCartExecution(HttpServletRequest request, HttpServletResponse response, 
            Authentication authentication);

}
