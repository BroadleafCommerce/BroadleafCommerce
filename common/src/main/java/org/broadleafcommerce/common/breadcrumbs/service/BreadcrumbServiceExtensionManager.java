/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.breadcrumbs.service;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("blBreadcrumbServiceExtensionManager")
/**
 * Provides an extension point for building breadcrumbs.   Handlers participate in reverse priority order.
 * @author bpolster
 *
 */
public class BreadcrumbServiceExtensionManager extends ExtensionManager<BreadcrumbServiceExtensionHandler> {

    /**
     * As each handler runs, it can work with the ContextMap from the ExtensionResultHolder 
     * to get the URL as other handlers have modified it.
     * 
     * Given that handlers run in reverse priority order
     * 
     * Handlers may use the fullUrl passed into the method or work with these URLs.
     */
    public static String CONTEXT_PARAM_STRIPPED_URL = "STRIPPED_URL";
    public static String CONTEXT_PARAM_STRIPPED_PARAMS = "STRIPPED_PARAMS";

    public BreadcrumbServiceExtensionManager() {
        super(BreadcrumbServiceExtensionHandler.class);
    }

    /**
     * This extension manager works the handlers in reverse priority order.   It starts with the
     * last crumb and works its way back.
     */
    @Override
    protected void sortHandlers() {
        super.sortHandlers();
        Collections.reverse(getHandlers());
    }
}
