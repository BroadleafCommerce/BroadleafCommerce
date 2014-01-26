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
package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.template.TemplateType;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Adds some convenience methods to the Spring AbstractHandlerMapping for
 * BLC specific HandlerMappings.
 * 
 * Always returns null from defaultHandlerMapping 
 * 
 * @author bpolster
 */
public abstract class BLCAbstractHandlerMapping extends AbstractHandlerMapping implements TemplateTypeAware {
    protected String controllerName;

    @Override
    /**
     * This handler mapping does not provide a default handler.   This method
     * has been coded to always return null.
     */
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

    @Override
    /**
     * Often, the HandlerMapping knows the template that will be served.   For example, the ProductController in the 
     * core framework knows that the desired template is product.getDisplayTemplate().
     * 
     */
    public String getExpectedTemplateName(HttpServletRequest request) {
        return null;
    }

    @Override
    /**
     * Override to extend the template type for customer types.   BLC community returns
     * TemplateType.PRODUCT, PAGE, CATEGORY, etc.
     * 
     * If not overridden in subclass, returns TemplateType.OTHER.
     */
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.OTHER;
    }

}
