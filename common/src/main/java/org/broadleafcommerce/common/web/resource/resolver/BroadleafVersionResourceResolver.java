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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Wraps Spring's {@link VersionResourceResolver} but adds in support to disable with 
 * environment properties.
 * 
 * Before delegating to {@link VersionResourceResolver}, first checks to see if the request is for 
 * a Broadleaf bundle.   If so, skips versioning since bundles are already versioned.
 * 
 *  {@code }
 * 
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
public class BroadleafVersionResourceResolver extends VersionResourceResolver {

    protected static final Log LOG = LogFactory.getLog(BroadleafVersionResourceResolver.class);

    @Value("${resource.versioning.enabled:true}")
    protected boolean resourceVersioningEnabled;

    @javax.annotation.Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        if (resourceVersioningEnabled && !bundlingService.checkForRegisteredBundleFile(requestPath)) {
            return super.resolveResourceInternal(request, requestPath, locations, chain);
        } else {
            return chain.resolveResource(request, requestPath, locations);
        }
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        if (resourceVersioningEnabled && !bundlingService.checkForRegisteredBundleFile(resourceUrlPath)) {
            String result = super.resolveUrlPathInternal(resourceUrlPath, locations, chain);

            // Spring's default version handler will return null if it doesn't have a strategy
            // for that resource - that seems incorrect.   Overriding here.
            if (result == null) {
                return chain.resolveUrlPath(resourceUrlPath, locations);
            } else {
                return result;
            }
        } else {
            return chain.resolveUrlPath(resourceUrlPath, locations);
        }
    }


}
