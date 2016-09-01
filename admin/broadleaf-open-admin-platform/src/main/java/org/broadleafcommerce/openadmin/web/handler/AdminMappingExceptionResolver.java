/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.broadleafcommerce.openadmin.exception.EntityNotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;


public class AdminMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private static final Log LOG = LogFactory.getLog(AdminMappingExceptionResolver.class);
    
    protected boolean showDebugMessage = false;
    
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, 
            Exception ex) {
        if (BroadleafControllerUtility.isAjaxRequest(request)) {
            // Set up some basic response attributes
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ModelAndView mav = new ModelAndView("utility/blcException");
            
            // Friendly message
            mav.addObject("exceptionMessage", ex.getMessage());
            
            mav.addObject("showDebugMessage", showDebugMessage);
            if (showDebugMessage) {
                StringBuilder sb2 = new StringBuilder();
                appendStackTrace(ex, sb2);
                mav.addObject("debugMessage", sb2.toString());
                LOG.error("Unhandled error processing ajax request", ex);
            }
            
            // Add the message to the model so we can render it 
            return mav;
        } else {
            // If the exception is "Entity not found" redirect to main listgrid view
            if (ex.getClass().equals(EntityNotFoundException.class)) {
                String originatingUri = new UrlPathHelper().getOriginatingRequestUri(request);
                int startIndex = request.getContextPath().length();

                // Remove erroneous entity Id from servletPath
                String servletPath = originatingUri.substring(startIndex, originatingUri.lastIndexOf('/'));
                return new ModelAndView("redirect:" + servletPath);
            }
            return super.resolveException(request, response, handler, ex);
        }
    }
    
    /**
     * By default, appends the exception and its message followed by the file location that triggered this exception.
     * Recursively builds this out for each cause of the given exception.
     * 
     * @param throwable
     * @param sb
     */
    protected void appendStackTrace(Throwable throwable, StringBuilder sb) {
        if (throwable == null) {
            return;
        }
        
        StackTraceElement[] st = throwable.getStackTrace();
        if (st != null && st.length > 0) {
            sb.append("\r\n\r\n");
            sb.append(throwable.toString());
            sb.append("\r\n");
            sb.append(st[0].toString());
        }
        
        appendStackTrace(throwable.getCause(), sb);
    }

    public boolean isShowDebugMessage() {
        return showDebugMessage;
    }
    
    public void setShowDebugMessage(boolean showDebugMessage) {
        this.showDebugMessage = showDebugMessage;
    }

}
