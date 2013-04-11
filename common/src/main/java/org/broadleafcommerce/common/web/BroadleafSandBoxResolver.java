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

package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for determining the SandBox to use for the current request. 
 * SandBox's are used to store a user's changes to products, content-items, etc. 
 * until they are ready to be pushed to production.  
 * 
 * If a request is being served with a SandBox parameter, it indicates that the user
 * wants to see the site as if their changes were applied.
 *
 * @author bpolster
 */
public interface BroadleafSandBoxResolver  {

    /**
     * @deprecated use {@link #resolveSandBox(WebRequest, Site)} instead
     */
    @Deprecated
    public SandBox resolveSandBox(HttpServletRequest request, Site site);

    /**
     * Resolve the sandbox for the given site and request
     * 
     * @param request
     * @param site
     * @return the sandbox for the current request
     */
    public SandBox resolveSandBox(WebRequest request, Site site);

}
