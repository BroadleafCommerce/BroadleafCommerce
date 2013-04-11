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

import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for returning the site used by Broadleaf Commerce for the current request.
 * For a single site installation, this will typically return null.
 *
 * @author bpolster
 */
public interface BroadleafSiteResolver  {

    /**
     * 
     * @deprecated Use {@link #resolveSite(WebRequest)} instead
     */
    @Deprecated
    public Site resolveSite(HttpServletRequest request) throws SiteNotFoundException;

    public Site resolveSite(WebRequest request) throws SiteNotFoundException;
}
