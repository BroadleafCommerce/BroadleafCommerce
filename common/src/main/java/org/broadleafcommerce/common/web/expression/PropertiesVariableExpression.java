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
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.processor.ConfigVariableProcessor;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;
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
@Component("blPropertiesVariableExpression")
@ConditionalOnTemplating
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
