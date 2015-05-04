/*
 * #%L
 * broadleaf-theme
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
