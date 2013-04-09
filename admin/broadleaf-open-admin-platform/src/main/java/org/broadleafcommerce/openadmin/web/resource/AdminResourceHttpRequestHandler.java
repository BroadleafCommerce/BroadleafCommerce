/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */

package org.broadleafcommerce.openadmin.web.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * A specialized resource request handler that supports special admin resources
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class AdminResourceHttpRequestHandler extends ResourceHttpRequestHandler {
    private static final Log LOG = LogFactory.getLog(AdminResourceHttpRequestHandler.class);
    
    protected List<AbstractGeneratedResourceHandler> handlers;
    
    /**
     * Checks to see if any of the configured GeneratedResourceHandlers can handle the given request.
     * If not, delegates to the normal ResourceHttpRequestHandler
     */
    @Override
	protected Resource getResource(HttpServletRequest request) {
        for (AbstractGeneratedResourceHandler handler : handlers) {
            if (handler.getHandledFileName().equals(request.getServletPath())) {
                return handler.getResource(request);
            }
        }
        return super.getResource(request);
    }
    
    public List<AbstractGeneratedResourceHandler> getHandlers() {
        return handlers;
    }
    
    public void setHandlers(List<AbstractGeneratedResourceHandler> handlers) {
        this.handlers = handlers;
    }
    
}
