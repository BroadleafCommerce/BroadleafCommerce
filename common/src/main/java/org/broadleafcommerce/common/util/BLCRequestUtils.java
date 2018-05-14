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
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;


/**
 * Convenience methods for interacting with the request
 * 
 * @author bpolster
 */
public class BLCRequestUtils {
    
    private static String OK_TO_USE_SESSION = "blOkToUseSession";
    private static String URI_IS_FILTER_IGNORED = "blUriIsFilterIgnored";
    
    /**
     * Broadleaf "Resolver" and "Filter" classes may need to know if they are allowed to utilize the session.
     * BLC uses a pattern where we will store an attribute in the request indicating whether or not the 
     * session can be used.   For example, when using the REST APIs, we typically do not want to utilize the
     * session.
     *
     */
    public static boolean isOKtoUseSession(WebRequest request) {
        Boolean useSessionForRequestProcessing = (Boolean) request.getAttribute(OK_TO_USE_SESSION, WebRequest.SCOPE_REQUEST);
        if (useSessionForRequestProcessing == null) {
            // by default we will use the session
            return true;
        } else {
            return useSessionForRequestProcessing.booleanValue();
        }
    }

    public static boolean isFilteringIgnoredForUri(WebRequest request) {
        Boolean ignoreUri = (Boolean) request.getAttribute(URI_IS_FILTER_IGNORED, WebRequest.SCOPE_REQUEST);
        if (ignoreUri == null) {
            // by default we will use the session
            return false;
        } else {
            return ignoreUri.booleanValue();
        }
    }
    
    /**
     * Takes {@link #isOKtoUseSession(WebRequest)} into account when retrieving session attributes. If it's not ok, this
     * will return null
     */
    public static Object getSessionAttributeIfOk(WebRequest request, String attribute) {
        if (isOKtoUseSession(request)) {
            return request.getAttribute(attribute, WebRequest.SCOPE_SESSION);
        }
        return null;
    }
    
    /**
     * Takes {@link #isOKtoUseSession(WebRequest)} into account when setting a session attribute
     * 
     * @return <b>true</b> if this set the session attribute, <b>false</b> otherwise
     */
    public static boolean setSessionAttributeIfOk(WebRequest request, String attribute, Object value) {
        if (isOKtoUseSession(request)) {
            request.setAttribute(attribute, value, WebRequest.SCOPE_SESSION);
            return true;
        }
        return false;
    }
    
    /**
     * Sets whether or not Broadleaf can utilize the session in request processing.   Used by the REST API
     * flow so that RESTful calls do not utilize the session.
     *
     */
    public static void setOKtoUseSession(WebRequest request, Boolean value) {
        request.setAttribute(OK_TO_USE_SESSION, value, WebRequest.SCOPE_REQUEST);
    }

    public static void setIsFilteringIgnoredForUri(WebRequest request, Boolean value) {
        request.setAttribute(URI_IS_FILTER_IGNORED, value, WebRequest.SCOPE_REQUEST);
    }

    /**
     * Get header or url parameter.    Will obtain the parameter from a header variable or a URL parameter, preferring
     * header values if they are set.
     *
     */
    public static String getURLorHeaderParameter(WebRequest request, String varName) {
        String returnValue = request.getHeader(varName);
        if (returnValue == null) {
            returnValue = request.getParameter(varName);
        }
        return returnValue;
    }

    /**
     * Convenience method to obtain the server prefix of the current request.
     * Useful for many modules that configure Relative URL's and need to send absolute URL's
     * to Third Party Gateways.
     */
    public static String getRequestedServerPrefix() {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        String scheme = request.getScheme();
        StringBuilder serverPrefix = new StringBuilder(scheme);
        serverPrefix.append("://");
        serverPrefix.append(request.getServerName());
        if ((scheme.equalsIgnoreCase("http") && request.getServerPort() != 80) || (scheme.equalsIgnoreCase("https") && request.getServerPort() != 443)) {
            serverPrefix.append(":");
            serverPrefix.append(request.getServerPort());
        }
        return serverPrefix.toString();
    }

    public static String getRequestURIWithoutContext(HttpServletRequest request) {
        String requestURIWithoutContext = null;

        if (request != null && request.getRequestURI() != null) {
            if (request.getContextPath() != null) {
                requestURIWithoutContext = request.getRequestURI().substring(request.getContextPath().length());
            } else {
                requestURIWithoutContext = request.getRequestURI();
            }

            // Remove JSESSION-ID or other modifiers
            int pos = requestURIWithoutContext.indexOf(";");
            if (pos >= 0) {
                requestURIWithoutContext = requestURIWithoutContext.substring(0,pos);
            }
        }

        return requestURIWithoutContext;
    }
}
