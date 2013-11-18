/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.resource;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


public class BroadleafResourceHttpRequestHandler extends ResourceHttpRequestHandler {
    private static final Log LOG = LogFactory.getLog(BroadleafResourceHttpRequestHandler.class);
    
    // XML Configured generated resource handlers
    protected List<AbstractGeneratedResourceHandler> handlers;
    
    @javax.annotation.Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;
    
    /**
     * Checks to see if the requested path corresponds to a registered bundle. If so, returns the generated bundle.
     * Otherwise, checks to see if any of the configured GeneratedResourceHandlers can handle the given request.
     * If neither of those cases match, delegates to the normal ResourceHttpRequestHandler
     */
    @Override
	protected Resource getResource(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (bundlingService.hasBundle(path)) {
            return bundlingService.getBundle(path);
        }
        
        if (handlers != null) {
            for (AbstractGeneratedResourceHandler handler : handlers) {
                if (handler.canHandle(path)) {
                    return handler.getResource(path, getLocations());
                }
            }
        }
        
        return super.getResource(request);
    }
    
    public boolean isBundleRequest(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return bundlingService.hasBundle(path);
    }
        
    /**
     * @return a clone of the locations list that is in {@link ResourceHttpRequestHandler}. Note that we must use
     * reflection to access this field as it is marked private.
     */
    @SuppressWarnings("unchecked")
    public List<Resource> getLocations() {
        try {
            List<Resource> locations = (List<Resource>) FieldUtils.readField(this, "locations", true);
            return new ArrayList<Resource>(locations);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* *********** */
    /* BOILERPLATE */
    /* *********** */
    
    public List<AbstractGeneratedResourceHandler> getHandlers() {
        return handlers;
    }
    
    public void setHandlers(List<AbstractGeneratedResourceHandler> handlers) {
        this.handlers = handlers;
    }

}