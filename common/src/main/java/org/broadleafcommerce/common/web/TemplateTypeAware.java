/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
