package org.broadleafcommerce.common.web.resource;

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
            return resolver.resolveUrlPath(resourcePath, locations, this);
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

