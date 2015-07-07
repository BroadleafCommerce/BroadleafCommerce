/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(PreviewTemplateController.REQUEST_MAPPING_PREFIX + "**")
public class PreviewTemplateController {
    private String templatePathPrefix = "templates";
    public static final String REQUEST_MAPPING_PREFIX = "/preview/";
    
    @RequestMapping
    public String displayPreview(HttpServletRequest httpServletRequest) {
        String requestURIPrefix = httpServletRequest.getContextPath() + REQUEST_MAPPING_PREFIX;
        String templatePath = httpServletRequest.getRequestURI().substring(requestURIPrefix.length() - 1);
        return templatePathPrefix + templatePath;
    }
    
}
