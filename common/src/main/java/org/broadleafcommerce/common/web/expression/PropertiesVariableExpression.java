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
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * This Thymeleaf variable expression class provides access to runtime configuration properties that are configured
 * in development.properties, development-shared.properties, etc, for the current environment.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PropertiesVariableExpression implements BroadleafVariableExpression {
    
    @Autowired
    protected SystemPropertiesService service;
    
    @Override
    public String getName() {
        return "props";
    }
    
    public String get(String propertyName) {
        return service.resolveSystemProperty(propertyName);
    }

    public int getAsInt(String propertyName) {
        return service.resolveIntSystemProperty(propertyName);
    }
    
    public boolean getForceShowIdColumns() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        boolean forceShow = service.resolveBooleanSystemProperty("listGrid.forceShowIdColumns");
        forceShow = forceShow || "true".equals(request.getParameter("showIds"));
        
        return forceShow;
    }
    
    public boolean getForceShowIdColumns() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        boolean forceShow = Boolean.parseBoolean(propMgr.getProperty("listGrid.forceShowIdColumns"));
        forceShow = forceShow || "true".equals(request.getParameter("showIds"));
        
        return forceShow;
    }

}
