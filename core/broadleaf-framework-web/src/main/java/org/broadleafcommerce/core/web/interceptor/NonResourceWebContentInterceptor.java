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

package org.broadleafcommerce.core.web.interceptor;

import org.broadleafcommerce.common.web.resource.BroadleafResourceHttpRequestHandler;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is used to control the cache seconds. The default Spring WebContentInterceptor will apply the cacheSeconds 
 * and associated variables to everything that goes through Spring MVC. We only want to apply the configured cache settings
 * to requests that go through controllers. For static resources, we will let Spring use its defaults.
 * 
 * Additionally, for requests for files that are known bundles, we will cache for a full 10 years, as we are generating
 * unique filenames that will make this acceptable.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class NonResourceWebContentInterceptor extends WebContentInterceptor {
    
    protected static final int TEN_YEARS_SECONDS = 60 * 60 * 24 * 365 * 10;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws ServletException {
        if (BroadleafResourceHttpRequestHandler.class.isAssignableFrom(handler.getClass())) {
            // Bundle resources are cached for ten years
            BroadleafResourceHttpRequestHandler h = (BroadleafResourceHttpRequestHandler) handler;
            if (h.isBundleRequest(request)) {
                applyCacheSeconds(response, TEN_YEARS_SECONDS);
            }
            return true;
        } else if (ResourceHttpRequestHandler.class.isAssignableFrom(handler.getClass())) {
            // Non-bundle resources will not specify cache parameters - we will rely on the server responding
            // with a 304 if the resource hasn't been modified.
            return true;
        } else {
            // Non-resources (meaning requests that go to controllers) will apply the configured caching parameters.
            return super.preHandle(request, response, handler);
        }
    }

}
