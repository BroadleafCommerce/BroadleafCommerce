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
package org.broadleafcommerce.common.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;


/**
 * Commonly used Broadleaf Controller operations.
 * - ajaxRedirects
 * - isAjaxRequest
 * - ajaxRender   
 * 
 * BroadleafAbstractController provides convenience methods for this functionality.
 * Implementors who are not able (or willing) to have their Controllers extend
 * BroadleafAbstractController can utilize this utility class to achieve some of
 * the same benefits.
 * 
 * 
 * @author bpolster
 */
public class BroadleafControllerUtility {
    protected static final Log LOG = LogFactory.getLog(BroadleafControllerUtility.class);
    
    public static final String BLC_REDIRECT_ATTRIBUTE = "blc_redirect";
    public static final String BLC_AJAX_PARAMETER = "blcAjax";
    
    /**
     * A helper method that returns whether or not the given request was invoked via an AJAX call
     * 
     * Returns true if the request contains the XMLHttpRequest header or a blcAjax=true parameter.
     * 
     * @param request
     * @return - whether or not it was an AJAX request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return isAjaxRequest(new ServletWebRequest(request));
    }
    
    public static boolean isAjaxRequest(WebRequest request) {
        String ajaxParameter = request.getParameter(BLC_AJAX_PARAMETER);
        String requestedWithHeader = request.getHeader("X-Requested-With");
        boolean result = (ajaxParameter != null && "true".equals(ajaxParameter))
                || "XMLHttpRequest".equals(requestedWithHeader);
        
        if (LOG.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder()
                .append("Request URL: [").append(request.getContextPath()).append("]")
                .append(" - ")
                .append("ajaxParam: [").append(String.valueOf(ajaxParameter)).append("]")
                .append(" - ")
                .append("X-Requested-With: [").append(requestedWithHeader).append("]")
                .append(" - ")
                .append("Returning: [").append(result).append("]");
            LOG.trace(sb.toString());
        }
        
        return result;
    }
    
}
