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

package org.broadleafcommerce.common.web.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * An abstract controller that provides convenience methods and resource declarations for its  children
 * 
 * Operations that are shared between all controllers belong here.   To use composition rather than
 * extension, implementors can utilize BroadleafControllerUtility.
 * 
 * @see BroadleafControllerUtility
 * 
 * @author apazzolini
 * @author bpolster
 */
public abstract class BroadleafAbstractController {
	
	/**
	 * A helper method that returns whether or not the given request was invoked via an AJAX call
	 * 
	 * @param request
	 * @return - whether or not it was an AJAX request
	 */
    protected boolean isAjaxRequest(HttpServletRequest request) {
    	return BroadleafControllerUtility.isAjaxRequest(request);    	
    }

}
