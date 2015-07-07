/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.template.TemplateType;

import javax.servlet.http.HttpServletRequest;


public interface TemplateTypeAware {

    /**
     * If a custom handler is written and it knows the eventual template name, then it should return the 
     * template name when this method is called.    This method will always be called after 
     * {@link #getBroadleafHandlerInternal(HttpServletRequest)} and only if the Handler was able to handle the 
     * request (e.g. it returns a non-null value from {@link #getBroadleafHandlerInternal(HttpServletRequest)}.  
     * 
     * Listed as expected because the HandlerMapping is making a call before the controller logic has 
     * been processed.   The controller may send the user somewhere else (e.g. an error page, etc.) in which 
     * case, the expected template won't be the actual destination.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public abstract String getExpectedTemplateName(HttpServletRequest request);

    /**
     * If a custom handler is written and it knows the eventual template name, then it should return the 
     * TemplateType when this method is called.    This method will always be called after 
     * {@link #getBroadleafHandlerInternal(HttpServletRequest)} and only if the Handler was able to handle the 
     * request (e.g. it returns a non-null value from {@link #getBroadleafHandlerInternal(HttpServletRequest)}.  
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public abstract TemplateType getTemplateType(HttpServletRequest request);

}
