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

import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;

import java.util.HashSet;
import java.util.Set;


public class BroadleafThymeleafStandardTemplateModeHandlers {

    public static final Set<ITemplateModeHandler> ALL_BLC_TEMPLATE_MODE_HANDLERS = new HashSet<ITemplateModeHandler>();
    
    static {
        for (ITemplateModeHandler handler : StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS) {
            ALL_BLC_TEMPLATE_MODE_HANDLERS.add(wrapHandler(handler));
        }
    }
    
    protected static ITemplateModeHandler wrapHandler(ITemplateModeHandler handler) {
        return new BroadleafThymeleafTemplateModeHandler(handler);        
    }

    public Set<ITemplateModeHandler> getStandardTemplateModeHandlers() {
        return ALL_BLC_TEMPLATE_MODE_HANDLERS;
    }
}
