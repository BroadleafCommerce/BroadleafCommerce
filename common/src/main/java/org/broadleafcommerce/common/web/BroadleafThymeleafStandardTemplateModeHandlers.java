/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
