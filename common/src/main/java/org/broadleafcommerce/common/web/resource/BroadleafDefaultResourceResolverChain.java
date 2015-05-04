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

