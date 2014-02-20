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
}
