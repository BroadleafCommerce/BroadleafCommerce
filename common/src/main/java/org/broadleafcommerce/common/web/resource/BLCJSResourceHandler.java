/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Component("blBLCJSResourceHandler")
public class BLCJSResourceHandler extends AbstractGeneratedResourceHandler {
    
    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;

    @Override
    public boolean canHandle(String path) {
        return path.endsWith("BLC.js");
    }

    @Override
    public Resource getFileContents(String path, List<Resource> locations) {
        Resource resource = getRawResource(path, locations);
    	String contents;
        try {
            contents = getResourceContents(resource);
        } catch (IOException e) {
            throw new RuntimeException("Could not get raw resource contents", e);
        }
        
        String newContents = contents;
        if (StringUtils.isNotBlank(contents)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	        newContents = newContents.replace("//BLC-SERVLET-CONTEXT", request.getContextPath());

	        String siteBaseUrl = propMgr.getProperty("site.baseurl");
	        if (StringUtils.isNotBlank(siteBaseUrl)) {
	            newContents = newContents.replace("//BLC-SITE-BASEURL", siteBaseUrl);
	        }
        }
	    
        GeneratedResource gr = new GeneratedResource(newContents.getBytes(), path);
        gr.setHashRepresentation(String.valueOf(contents.hashCode()));
        return gr;
    }

    @Override
    public boolean isCachedResourceExpired(GeneratedResource cachedResource, String path, List<Resource> locations) {
        String contents;
        
        try {
            contents = getResourceContents(getRawResource(path, locations));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return !cachedResource.getHashRepresentation().equals(String.valueOf(contents.hashCode()));
    }

}
