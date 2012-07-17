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

package org.broadleafcommerce.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * This class extends the default ThymeleafViewResolver to facilitate rendering
 * template fragments (such as those used by AJAX modals or iFrames) within a 
 * full page container should the request for that template have occurred in a 
 * stand-alone context.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafThymeleafViewResolver extends ThymeleafViewResolver {
    private static final Log LOG = LogFactory.getLog(BroadleafThymeleafViewResolver.class);
    
    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP redirect with AJAX support. That is, if you want a redirect to be followed
     *   by the browser as the result of an AJAX call or within an iFrame at the parent 
     *   window, you can utilize this prefix.
     *   
     *   If the request was not performed in an AJAX / iFrame context, this method will
     *   delegate to the normal "redirect:" prefix.
     * </p>
     * <p>
     *   Value: <tt>ajaxredirect:</tt>
     * </p>
     */
    public static final String AJAX_REDIRECT_URL_PREFIX = "ajaxredirect:";
    
    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   a template that can be resolved by itself (for use in a modal for example)
     *   when requested via AJAX or placed inside of a container when requested in
     *   a non-AJAX manner.
     * </p>
     * <p>
     *   Value: <tt>ajax:</tt>
     * </p>
     */
    public static final String AJAX_URL_PREFIX = "ajax:";
    
    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   a template that can be resolved by itself (for use in a modal for example)
     *   when requested via an iFrame or placed inside of a container when requested in
     *   a non-iFrame manner.
     * </p>
     * <p>
     *   Value: <tt>iframe:</tt>
     * </p>
     */
    public static final String IFRAME_URL_PREFIX = "iframe:";
    
    protected String fullPageLayout = "layout/fullPageLayout";
    
    private boolean canHandle(final String viewName) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
    }

    /**
     * Determines which internal method to call for creating the appropriate view. If no
     * Broadleaf specific methods match the viewName, it delegates to the parent 
     * ThymeleafViewResolver createView method
     */
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {
        if (!canHandle(viewName)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain");
            return null;
        }
        if (viewName.startsWith(AJAX_REDIRECT_URL_PREFIX)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} is an ajax redirect, and will be handled directly by BroadleafThymeleafViewResolver");
            String redirectUrl = viewName.substring(AJAX_REDIRECT_URL_PREFIX.length());
        	return loadAjaxRedirectView(redirectUrl, locale);
        }
        if (viewName.startsWith(AJAX_URL_PREFIX)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} is an ajax modal, and will be handled directly by BroadleafThymeleafViewResolver");
            String requestedViewName = viewName.substring(AJAX_URL_PREFIX.length());
        	return loadAjaxView(requestedViewName, locale);
        }
        if (viewName.startsWith(IFRAME_URL_PREFIX)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} is an iFrame modal, and will be handled directly by BroadleafThymeleafViewResolver");
            String requestedViewName = viewName.substring(IFRAME_URL_PREFIX.length());
        	return loadIFrameView(requestedViewName, locale);
        }
        return super.createView(viewName, locale);
    }
    
    /**
     * Performs a Broadleaf AJAX redirect. This is used in conjunction with BLC.js to support
     * doing a browser page change as as result of an AJAX call.
     * 
     * @param redirectUrl
     * @param locale
     * @return
     * @throws Exception
     */
    protected View loadAjaxRedirectView(String redirectUrl, final Locale locale) throws Exception {
    	if (isAjaxRequest()) {
    		String viewName = "utility/blcRedirect";
    		addStaticVariable(BroadleafControllerUtility.BLC_REDIRECT_ATTRIBUTE, redirectUrl);
    		return super.loadView(viewName, locale);
    	} else {
            return new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
    	}
    }
    
    protected View loadIFrameView(final String requestedViewName, final Locale locale) throws Exception {
    	String viewName = requestedViewName;
    	if (!isIFrameRequest()) {
	        viewName = "layout/iFrameContainerLayout";
	        addStaticVariable("templateName", requestedViewName);
    	}
	    return super.loadView(viewName, locale);
    }
    
    /**
     * If the current request is an AJAX request, this method will render the requested viewName.
     * However, if the current request is NOT and AJAX request, this method will render the 
     * fullPageLayout with the requested viewName set as the templateName variable for use by 
     * the fullPageLayout.
     * 
     * @param requestedViewName
     * @param locale
     * @return
     * @throws Exception
     */
    protected View loadAjaxView(final String requestedViewName, final Locale locale) throws Exception {
    	String viewName = requestedViewName;
    	if (!isAjaxRequest()) {
	        viewName = getFullPageLayout();
	        addStaticVariable("templateName", requestedViewName);
    	}
	    return super.loadView(viewName, locale);
    }
    
    protected boolean isIFrameRequest() {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
    	String iFrameParameter = request.getParameter("blcIFrame");
    	return  (iFrameParameter != null && "true".equals(iFrameParameter));
    }
    
    protected boolean isAjaxRequest() {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        return BroadleafControllerUtility.isAjaxRequest(request);
    }

	public String getFullPageLayout() {
		return fullPageLayout;
	}

	public void setFullPageLayout(String fullPageLayout) {
		this.fullPageLayout = fullPageLayout;
	}
    
}
