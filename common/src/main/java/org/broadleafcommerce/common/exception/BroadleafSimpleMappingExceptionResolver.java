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
package org.broadleafcommerce.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This exception resolver can be used to handle exceptions in a user friendly way by displaying an error template.  
 * It also serves a security purpose of not showing stack traces to users.  This is disabled by default and should 
 * only be enabled when there is not already a way to handle exceptions in the current project.  
 * 
 * This can be enabled by setting exception.handler.enabled=true in your properties file.  You will need to create 
 * a template file at path "utility/error" or override the method getDefaultErrorView() to return a different path 
 * to an error file.
 * 
 * @author Chad Harchar (charchar)
 */
@Component("blSimpleMappingExceptionResolver")
public class BroadleafSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private static final Log LOG = LogFactory.getLog(BroadleafSimpleMappingExceptionResolver.class);

    @Value("${exception.handler.enabled:false}")
    protected boolean exceptionHandlerEnabled;

    protected String DEFAULT_ERROR_VIEW = "utility/error";

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {

        String viewName = getDefaultErrorView();

        Integer statusCode = super.determineStatusCode(request, viewName);
        if (statusCode != null) {
            applyStatusCodeIfPossible(request, response, statusCode);
        }

        ModelAndView modelAndView = getModelAndView(viewName, ex, request);
        String uuid = "";
        if (request.getAttribute("exceptionUUID") != null) {
            uuid = request.getAttribute("exceptionUUID").toString();
        }
        modelAndView.getModel().put("exceptionUUID", uuid);
        return modelAndView;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {

        if (exceptionHandlerEnabled) {
            String uuid = UUID.randomUUID().toString();
            if (LOG.isErrorEnabled()) {
                LOG.error(String.format("Error caught and handled.:%s", uuid), ex);
            }
            request.setAttribute("exceptionUUID", uuid);
            return doResolveException(request, response, handler, ex);
        } else {
            return null;
        }
    }

    public String getDefaultErrorView(){
        return DEFAULT_ERROR_VIEW;
    }

}
