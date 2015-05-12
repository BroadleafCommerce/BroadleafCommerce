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
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link ResourceResolver} that replaces the //BLC-SERVLET-CONTEXT and //BLC-SITE-BASEURL" 
 * tokens before serving the BLC.js file.
 * 
 * Works in conjunction with {@link BLCJSUrlPathResolver}
 * 
 * @since 4.0
 * 
 * @author Reggie Cole
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
@Component("blBLCJSResolver")
public class BLCJSResourceResolver extends AbstractResourceResolver implements Ordered {

    protected static final Log LOG = LogFactory.getLog(BLCJSResourceResolver.class);

    private static final String BLC_JS_NAME = "BLC.js";
    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @javax.annotation.Resource(name = "blBaseUrlResolver")
    BaseUrlResolver urlResolver;

    private int order = BroadleafResourceResolverOrder.BLC_JS_RESOURCE_RESOLVER;

    protected static final Pattern pattern = Pattern.compile("(\\S*)BLC((\\S{0})|([-]{1,2}[0-9]+)|([-]{1,2}[0-9]+(-[0-9]+)+)).js");


    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
            ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }
    
    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        if (requestPath != null && requestPath.contains("BLC")) {
            Matcher matcher = pattern.matcher(requestPath);
            if (matcher.find()) {
                requestPath = matcher.group(1) + "BLC.js";
                Resource resource = chain.resolveResource(request, "BLC.js", locations);
                if (resource == null) {
                    requestPath = matcher.group(1) + "BLC.js";
                    resource = chain.resolveResource(request, requestPath, locations);
                }

                try {
                    resource = convertResource(resource, requestPath);
                } catch (IOException ioe) {
                    LOG.error("Exception modifying " + BLC_JS_NAME, ioe);
                }
                return resource;
            }
        }
        return chain.resolveResource(request, requestPath, locations);
    }

    protected Resource convertResource(Resource origResource, String resourceFileName) throws IOException {
        byte[] bytes = FileCopyUtils.copyToByteArray(origResource.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        
        String newContent = content;
        if (! StringUtils.isEmpty(content)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            newContent = newContent.replace("//BLC-SERVLET-CONTEXT", request.getContextPath());

            String siteBaseUrl = urlResolver.getSiteBaseUrl();
            if (! StringUtils.isEmpty(siteBaseUrl)) {
                newContent = newContent.replace("//BLC-SITE-BASEURL", siteBaseUrl);
            }
        }
        
        return new GeneratedResource(newContent.getBytes(), resourceFileName);
    }
    
    protected String addVersion(String requestPath, String version) {
        String baseFilename = StringUtils.stripFilenameExtension(requestPath);
        String extension = StringUtils.getFilenameExtension(requestPath);
        return baseFilename + version + "." + extension;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
