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
package org.broadleafcommerce.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * Adds some convenience methods to the Spring AbstractHandlerMapping for
 * BLC specific HandlerMappings.
 * 
 * Always returns null from defaultHandlerMapping 
 * 
 * @author bpolster
 */
public abstract class BLCAbstractHandlerMapping extends AbstractHandlerMapping {

    protected String controllerName;

    @Autowired
    protected Environment env;

    /**
     * This handler mapping does not provide a default handler.   This method
     * has been coded to always return null.
     */
    @Override
    public Object getDefaultHandler() {
        return null;        
    }
    
    /**
     * Returns the controllerName if set or "blPageController" by default.
     * @return
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * Sets the name of the bean to use as the Handler.  Typically the name of
     * a controller bean.
     * 
     * @param controllerName
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public boolean allowProductResolutionUsingIdParam() {
        return env.getProperty("allowProductResolutionUsingIdParam", boolean.class, false);
    }

    public boolean allowCategoryResolutionUsingIdParam() {
        return env.getProperty("allowCategoryResolutionUsingIdParam", boolean.class, false);
    }
}
