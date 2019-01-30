/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.web.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a PostConstruct method that sorts the {@link ResourceResolver}, {@link ResourceTransformer}, 
 * or location ({@link Resource}) collections based on the {@link Ordered} interface.
 * 
 *  
 * @author bpolster
 *
 */
public class BroadleafResourceHttpRequestHandler extends ResourceHttpRequestHandler {
    
    @Resource(name = "blBroadleafContextUtil")
    protected BroadleafContextUtil blcContextUtil;

    @Value("${staticResourceBrowserCacheSeconds}")
    protected long cacheSeconds = 0;

    @PostConstruct
    protected void sortCollections() {
        OrderedComparator oc = new OrderedComparator();

        if (getLocations() != null) {
            Collections.sort(getLocations(), oc);
        }

        if (getResourceResolvers() != null) {
            Collections.sort(getResourceResolvers(), oc);
        }

        if (getResourceTransformers() != null) {
            Collections.sort(getResourceTransformers(), oc);
        }
    }
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            blcContextUtil.establishThinRequestContext();
            super.handleRequest(request, response);
        } finally {
            blcContextUtil.clearThinRequestContext();
        }
    }

    @Override
    protected void setHeaders(HttpServletResponse response, org.springframework.core.io.Resource resource, MediaType
            mediaType) throws IOException {
        super.setHeaders(response, resource, mediaType);
        //Add public to cache control for universal CDN recognition
        if (isUseCacheControlHeader() && cacheSeconds > 0) {
            String header = response.getHeader(HEADER_CACHE_CONTROL);
            if (!header.contains("public")) {
                header += ",public";
                response.setHeader(HEADER_CACHE_CONTROL, header);
            }
        }
    }

    /**
     * Items that implement Ordered will sort by the value of {@link Ordered#getOrder()}.
     * 
     * <p>
     * Nulls are considered greater except that a getOrder with a value of Integer.MAX_VALUE 
     * will always sort at the end (even after nulls). 
     *      
     */
    protected class OrderedComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Ordered && o2 instanceof Ordered) {
                return ((Ordered) o1).getOrder() - ((Ordered) o2).getOrder();
            }

            if (o1 instanceof Ordered) {
                if (((Ordered) o1).getOrder() == Integer.MAX_VALUE) {
                    // Put MAX_VALUE items at the end of the list (even behind nulls)
                    return 1;
                } else {
                    return -1;
                }
            }

            if (o2 instanceof Ordered) {
                if (((Ordered) o2).getOrder() == Integer.MAX_VALUE) {
                    // Put MAX_VALUE items at the end of the list (even behind nulls)
                    return -1;
                } else {
                    return 1;
                }
            }

            // Neither is ordered (respect the natural order)
            return 0;
        }

    }
}
