/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This Thymeleaf variable expression class provides access to runtime configuration properties that are configured
 * in development.properties, development-shared.properties, etc, for the current environment.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PropertiesVariableExpression implements BroadleafVariableExpression {
    
    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;
    
    @Override
    public String getName() {
        return "props";
    }
    
    public String get(String propertyName) {
        return propMgr.getProperty(propertyName);
    }

    public int getAsInt(String propertyName) {
        return Integer.parseInt(propMgr.getProperty(propertyName));
    }

}
