/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring3.view.AbstractThymeleafView;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
     *   window, you can utilize this prefix. Note that this requires a JavaScript component,
     *   which is provided as part of BLC.js
     *   
     *   If the request was not performed in an AJAX / iFrame context, this method will
     *   delegate to the normal "redirect:" prefix.
     * </p>
     * <p>
     *   Value: <tt>ajaxredirect:</tt>
     * </p>
     */
    public static final String AJAX_REDIRECT_URL_PREFIX = "ajaxredirect:";
    
    protected Map<String, String> layoutMap = new HashMap<String, String>();
    protected String fullPageLayout = "layout/fullPageLayout";
    protected String iframeLayout = "layout/iframeLayout";
    
    /*
     * This method is a copy of the same method in ThymeleafViewResolver, but since it is marked private,
     * we are unable to call it from the BroadleafThymeleafViewResolver
     */
    protected boolean canHandle(final String viewName) {
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
    
    @Override
    protected View loadView(final String originalViewName, final Locale locale) throws Exception {
        String viewName = originalViewName;
        
        if (!isAjaxRequest()) {
            String longestPrefix = "";
            
            for (Entry<String, String> entry : layoutMap.entrySet()) {
                String viewPrefix = entry.getKey();
                String viewLayout = entry.getValue();
                
                if (viewPrefix.length() > longestPrefix.length()) {
                    if (originalViewName.startsWith(viewPrefix)) {
                        longestPrefix = viewPrefix;
                        
                        if (!"NONE".equals(viewLayout)) {
                            viewName = viewLayout;
                        }
                    }
                }
            }  
            
            if (longestPrefix.equals("")) {
                viewName = getFullPageLayout();
            }
        }
        
        AbstractThymeleafView view = (AbstractThymeleafView) super.loadView(viewName, locale);
        
        if (!isAjaxRequest()) {
            view.addStaticVariable("templateName", originalViewName);
        }
        
        return view;
    }
    
    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + "_" + locale + "_" + isAjaxRequest();
    }
    
    protected boolean isIFrameRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String iFrameParameter = request.getParameter("blcIFrame");
        return (iFrameParameter != null && "true".equals(iFrameParameter));
    }
    
    protected boolean isAjaxRequest() {
        // First, let's try to get it from the BroadleafRequestContext
        HttpServletRequest request = null;
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            HttpServletRequest brcRequest = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
            if (brcRequest != null) {
                request = brcRequest;
            }
        }
        
        // If we didn't find it there, we might be outside of a security-configured uri. Let's see if the filter got it
        if (request == null) {
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); 
            } catch (ClassCastException e) {
                // In portlet environments, we won't be able to cast to a ServletRequestAttributes. We don't want to 
                // blow up in these scenarios.
                LOG.warn("Unable to cast to ServletRequestAttributes and the request in BroadleafRequestContext " + 
                         "was not set. This may introduce incorrect AJAX behavior.");
            }
        }
        
        // If we still don't have a request object, we'll default to non-ajax
        if (request == null) {
            return false;
        }
                
        return BroadleafControllerUtility.isAjaxRequest(request);
    }

    /**
     * Gets the map of prefix : layout for use in determining which layout
     * to dispatch the request to in non-AJAX calls
     * 
     * @return the layout map
     */
    public Map<String, String> getLayoutMap() {
        return layoutMap;
    }

    /**
     * @see #getLayoutMap()
     * @param layoutMap
     */
    public void setLayoutMap(Map<String, String> layoutMap) {
        this.layoutMap = layoutMap;
    }

    /**
     * The default layout to use if there is no specifc entry in the layout map
     * 
     * @return the full page layout
     */
    public String getFullPageLayout() {
        return fullPageLayout;
    }

    /**
     * @see #getFullPageLayout()
     * @param fullPageLayout
     */
    public void setFullPageLayout(String fullPageLayout) {
        this.fullPageLayout = fullPageLayout;
    }

    /**
     * The layout to use for iframe requests
     * 
     * @return the iframe layout
     */
    public String getIframeLayout() {
        return iframeLayout;
    }

    /**
     * @see #getIframeLayout()
     * @param iframeLayout
     */
    public void setIframeLayout(String iframeLayout) {
        this.iframeLayout = iframeLayout;
    }
    
}
