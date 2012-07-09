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

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

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
    
    /**
     * <p>A helper method that will correctly handle either returning the partial content requested (to be displayed
     * in a modal or only update a certain part of a page for example) or render it in its full view. It will 
     * determine which rendering method to use based on whether or not it was an AJAX request.</p>
     * 
     * <p>This method will assume that given the String identifier of a nonModalPath (such as "cart"), the partial
     * view that would be returned lives in "ajax/cart".</p>
     * 
     * 
     * @param nonModalPath - the path to the full content view
     * @param request
     * @param model
     * @return the String that should be returned by the method responsible for returning a view. Typically this
     * will be the method with the @RequestMapping annotation
     */
    protected String ajaxRender(String nonModalPath, HttpServletRequest request, Model model) {
    	return BroadleafControllerUtility.ajaxRender(nonModalPath, request, model);    	
    }
    
	/**
	 * A helper method that utilizes a Broadleaf Commerce specific mechanism to allow redirects from 
	 * ajax calls.
	 * 
	 * This method will return the user
	 * 
	 * @param request
	 * @param redirectPath
	 * @param model model to add the 
	 * @throws IOException 
	 */
	protected static String buildAjaxRedirect(HttpServletRequest request, String redirectPath, Model model) {
		return BroadleafControllerUtility.buildAjaxRedirect(request, redirectPath, model);	
	}

}
