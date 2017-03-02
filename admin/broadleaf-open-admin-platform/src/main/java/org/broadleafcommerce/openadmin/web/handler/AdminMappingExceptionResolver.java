/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.broadleafcommerce.openadmin.exception.EntityNotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
                String servletPath = request.getServletPath();

                // Remove erroneous entity Id from servletPath
                servletPath = servletPath.substring(0, servletPath.lastIndexOf('/'));
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
