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

import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for returning the theme used by Broadleaf Commerce for the current request.
 * For a single site installation, this should return a theme whose path and name are empty string.
 *
 * @author bpolster
 */
public interface BroadleafThemeResolver {
    
    /**
     * 
     * @deprecated Use {@link #resolveTheme(WebRequest)} instead
     */
    @Deprecated
    public Theme resolveTheme(HttpServletRequest request, Site site);
    
    public Theme resolveTheme(WebRequest request);
}
