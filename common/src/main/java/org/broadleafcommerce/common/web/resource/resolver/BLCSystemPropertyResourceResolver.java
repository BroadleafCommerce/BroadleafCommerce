/*
 * #%L
 * broadleaf-theme
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
package org.broadleafcommerce.common.web.resource.resolver;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
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
 * A {@link ResourceResolver} that replaces system properties in BLC-system-property.js 
 * 
 * @since 4.0
 * 
 * @author Reggie Cole
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
@Component("blSystemPropertyJSResolver")
public class BLCSystemPropertyResourceResolver extends AbstractResourceResolver implements Ordered {

    protected static final Log LOG = LogFactory.getLog(BLCSystemPropertyResourceResolver.class);

    protected static final String BLC_SYSTEM_PROPERTY_FILE = "BLC-system-property.js";
    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private int order = BroadleafResourceResolverOrder.BLC_SYSTEM_PROPERTY_RESOURCE_RESOLVER;

    @javax.annotation.Resource(name = "blBaseUrlResolver")
    BaseUrlResolver urlResolver;

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
            ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }
    
    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {

        Resource resource = chain.resolveResource(request, requestPath, locations);

        if (requestPath.equalsIgnoreCase(BLC_SYSTEM_PROPERTY_FILE)) {
            try {
                resource = convertResource(resource, requestPath);
            } catch (IOException ioe) {
                LOG.error("Exception modifying " + BLC_SYSTEM_PROPERTY_FILE, ioe);
            }
        }

        return resource;
    }

    protected Resource convertResource(Resource origResource, String resourceFileName) throws IOException {
        byte[] bytes = FileCopyUtils.copyToByteArray(origResource.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        
        String newContent = content;
        if (! StringUtils.isEmpty(content)) {
            String regexKey = "\\\"BLC_PROP:(.*)\\\"";

            Pattern p = Pattern.compile(regexKey);
            Matcher m = p.matcher(content);
            while (m.find()) {
                String matchedPlaceholder = m.group(0);
                String propertyName = m.group(1);

                String propVal = BLCSystemProperty.resolveSystemProperty(propertyName);
                if (propVal == null) {
                    propVal = "";
                }

                newContent = newContent.replaceAll(matchedPlaceholder, '"' + propVal + '"');
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
