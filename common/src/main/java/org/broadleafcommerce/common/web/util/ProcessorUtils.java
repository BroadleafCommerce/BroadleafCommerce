/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.util;

import org.broadleafcommerce.common.web.resource.BroadleafResourceHttpRequestHandler;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.spring3.context.SpringWebContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author apazzolini
 *
 * Utility class for Thymeleaf Processors
 */
public class ProcessorUtils {
    
    protected static Map<String, Object> cachedBeans = new HashMap<String, Object>();
    
    public static BroadleafResourceHttpRequestHandler getJsRequestHandler(Arguments arguments) {
        String key = "blJsResources";
        BroadleafResourceHttpRequestHandler reqHandler = (BroadleafResourceHttpRequestHandler) cachedBeans.get(key);
        if (reqHandler == null) {
            final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext();
            reqHandler = (BroadleafResourceHttpRequestHandler) appCtx.getBean(key);
            cachedBeans.put(key, reqHandler);
        }
        return reqHandler;
    }
    
    public static BroadleafResourceHttpRequestHandler getCssRequestHandler(Arguments arguments) {
        String key = "blCssResources";
        BroadleafResourceHttpRequestHandler reqHandler = (BroadleafResourceHttpRequestHandler) cachedBeans.get(key);
        if (reqHandler == null) {
            final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext();
            reqHandler = (BroadleafResourceHttpRequestHandler) appCtx.getBean(key);
            cachedBeans.put(key, reqHandler);
        }
        return reqHandler;
    }

}
