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

import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.processor.ConfigVariableProcessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This Thymeleaf variable expression class provides access to runtime configuration properties that are configured
 * in development.properties, development-shared.properties, etc, for the current environment.
 * 
 * <p>
 * This also includes properties that have been saved/overwritten in the database via {@link SystemProperty}.
 * 
 * @author Andre Azzolini (apazzolini)
 * @see {@link ConfigVariableProcessor}
 */
public class PropertiesVariableExpression implements BroadleafVariableExpression {
    
    @Override
    public String getName() {
        return "props";
    }
    
    public String get(String propertyName) {
        return BLCSystemProperty.resolveSystemProperty(propertyName);
    }

    public int getAsInt(String propertyName) {
        return BLCSystemProperty.resolveIntSystemProperty(propertyName);
    }
    
    public boolean getAsBoolean(String propertyName) {
        return BLCSystemProperty.resolveBooleanSystemProperty(propertyName); 
    }
    
    public long getAsLong(String propertyName) {
        return BLCSystemProperty.resolveLongSystemProperty(propertyName); 
    }
    
    /**
     * Returns true if the <b>listGrid.forceShowIdColumns</b> system property or a <b>showIds</b> request parameter is set
     * to true. Used in the admin to show ID columns when displaying list grids.
     */
    public boolean getForceShowIdColumns() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        boolean forceShow = BLCSystemProperty.resolveBooleanSystemProperty("listGrid.forceShowIdColumns");
        forceShow = forceShow || "true".equals(request.getParameter("showIds"));
        
        return forceShow;
    }
    
}
