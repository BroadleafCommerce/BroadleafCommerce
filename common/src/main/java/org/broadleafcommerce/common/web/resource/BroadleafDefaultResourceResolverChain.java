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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a straight copy of Spring's DefaultResourceResolverChain  (as of 4.1.6).
 * 
 * This had to be copied as Spring set the class scope as  "package" scope thus not 
 * allowing it to be used or extended.
 *  
 * @author bpolster
 *
 */
public class BroadleafDefaultResourceResolverChain implements ResourceResolverChain {

    private final List<ResourceResolver> resolvers = new ArrayList<ResourceResolver>();

    protected static final Log LOG = LogFactory.getLog(BroadleafDefaultResourceResolverChain.class);

    private int index = -1;

    public BroadleafDefaultResourceResolverChain(List<? extends ResourceResolver> resolvers) {
        if (resolvers != null) {
            this.resolvers.addAll(resolvers);
        }
    }

    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations) {
        ResourceResolver resolver = getNext();
        if (resolver == null) {
            return null;
        }
        try {
            return resolver.resolveResource(request, requestPath, locations, this);
        } finally {
            this.index--;
        }
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations) {
        ResourceResolver resolver = getNext();
        if (resolver == null) {          
            return null;
        }
        try {
            String returnPath = resolver.resolveUrlPath(resourcePath, locations, this);
            if (LOG.isTraceEnabled()) {
                LOG.trace("The return path for " + resourcePath + " from resolver " + resolver + " is " + returnPath);
            }            
            return returnPath;
        } finally {
            this.index--;
        }
    }

    private ResourceResolver getNext() {

        Assert.state(this.index <= this.resolvers.size(),
                "Current index exceeds the number of configured ResourceResolver's");

        if (this.index == (this.resolvers.size() - 1)) {
            return null;
        }

        this.index++;
        return this.resolvers.get(this.index);
    }

}

