/*
 * #%L
 * broadleaf-theme
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.web.resource.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@code ResourceResolver} that is used solely to modify URL names of resources that are served from 
 * Theme locations or those that may be modified by ThemeConfiguration variables.
 * 
 * <p>
 * It works with {@link ThemeFileResourceResolver} which actually resolves the resource from the DB or file system.
 * 
 * <p>
 * These two components were not combined into a single resolver in order to allow for the introduction of a 
 * {@link CachingResourceResolver} at either step (typically between the two with URL resolution occurring prior to
 * caching and resource resolution occurring after). 
 * 
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
public class BundleResourceResolver extends AbstractResourceResolver {

    protected static final Log LOG = LogFactory.getLog(BundleResourceResolver.class);

    @javax.annotation.Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource bundle = bundlingService.resolveBundleResource(requestPath);
        if (bundle != null) {
            return bundle;
        } else {
            return chain.resolveResource(request, requestPath, locations);
        }
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
            ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }
}
