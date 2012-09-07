/*
 * Copyright 2012 the original author or authors.
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

import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is used to control the cache seconds for all things that are not resources. The default Spring
 * WebContentInterceptor will apply the cacheSeconds and associated variables to everything that goes through Spring
 * MVC. We really only want to apply that to Controllers, not our static resources.
 * 
 * This interceptor facilitates that functionality.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class NonResourceWebContentInterceptor extends WebContentInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws ServletException {
		
		if (ResourceHttpRequestHandler.class.isAssignableFrom(handler.getClass())) {
		    return true;
		} else {
			return super.preHandle(request, response, handler);
		}
	}

}
