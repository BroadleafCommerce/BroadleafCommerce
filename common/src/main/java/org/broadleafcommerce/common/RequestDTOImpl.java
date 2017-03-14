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

package org.broadleafcommerce.common;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bpolster.
 */
public class RequestDTOImpl implements RequestDTO, Serializable {

    private static final long serialVersionUID = 1L;

    @AdminPresentation(friendlyName = "RequestDTOImpl_Request_URI")
    private String requestURI;

    @AdminPresentation(friendlyName = "RequestDTOImpl_Full_Url")
    private String fullUrlWithQueryString;

    @AdminPresentation(friendlyName = "RequestDTOImpl_Is_Secure")
    private Boolean secure;

    Map<String, String> requestContextAttributes = new HashMap<String, String>();

    public RequestDTOImpl() {
    }

    public RequestDTOImpl(HttpServletRequest request) {
        requestURI = request.getRequestURI();
        fullUrlWithQueryString = request.getRequestURL().toString();
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            fullUrlWithQueryString += "?" + request.getQueryString();
        }
        secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        for(Object key : request.getParameterMap().keySet()) {
            String paramKey = key.toString();
            requestContextAttributes.put(paramKey, request.getParameter(paramKey));
        }
    }

    public RequestDTOImpl(WebRequest request) {
        // Page level targeting does not work for WebRequest.
        secure = request.isSecure();
        for(String key : request.getParameterMap().keySet()) {
            requestContextAttributes.put(key, request.getParameter(key));
        }
    }

    /**
     * @return  returns the request not including the protocol, domain, or query string
     */
    @Override
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * @return Returns the URL and parameters.
     */
    @Override
    public String getFullUrLWithQueryString() {
        return fullUrlWithQueryString;
    }

    /**
     * @return true if this request came in through HTTPS
     */
    @Override
    public Boolean isSecure() {
        return secure;
    }

    public Map<String, String> getRequestContextAttributes() {
        return requestContextAttributes;
    }

    public void setRequestContextAttributes(Map<String, String> requestContextAttributes) {
        this.requestContextAttributes = requestContextAttributes;
    }

    public String getFullUrlWithQueryString() {
        return fullUrlWithQueryString;
    }

    public void setFullUrlWithQueryString(String fullUrlWithQueryString) {
        this.fullUrlWithQueryString = fullUrlWithQueryString;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public Map<String, Object> getProperties() {
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            return BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties();
        } else {
            return null;
        }
    }
}
