/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.resource;

import org.apache.commons.io.IOUtils;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by brandon on 8/27/15.
 */
@Component("blMessagesResourceResolver")
public class MessagesResourceResolver implements ResourceResolver {

    public org.springframework.core.io.Resource resolveResource(HttpServletRequest request, String path, List<? extends Resource> locations, ResourceResolverChain chain) {
        if(!path.equalsIgnoreCase("admin/ui/messages.js")) {
            return chain.resolveResource(request, path, locations);
        } else {
            org.springframework.core.io.Resource resource = chain.resolveResource(request, path, locations);
            return this.updateMessagesVariables(resource, path);
        }
    }

    protected org.springframework.core.io.Resource updateMessagesVariables(org.springframework.core.io.Resource resource, String path) {
        if(resource != null) {
            String contents;
            try {
                contents = this.getResourceContents(resource);
                contents = replaceResourceContents(contents);
            } catch (IOException e) {
                throw new RuntimeException("Could not get resource (offerTemplate JS) contents", e);
            }

            return new GeneratedResource(contents.getBytes(), path);
        } else {
            return resource;
        }
    }

    protected String getResourceContents(org.springframework.core.io.Resource resource) throws IOException {
        StringWriter writer = null;

        String contents;
        try {
            writer = new StringWriter();
            IOUtils.copy(resource.getInputStream(), writer, "UTF-8");
            contents = writer.toString();
        } finally {
            if(writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return contents;
    }

    protected String replaceResourceContents(String contents) throws IOException {
        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            String propFileName = "messages/OpenAdminJavascriptMessages.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            StringWriter propertiesObject = new StringWriter();
            propertiesObject.append("{");

            // get the property value and print it out
            for(Map.Entry<Object,Object> property : prop.entrySet()) {
                propertiesObject.append(property.getKey() + ": \"" + property.getValue() + "\", ");
            }

            contents = contents.replace("//BLC-ADMIN-JS-MESSAGES", propertiesObject.getBuffer().toString().substring(0, propertiesObject.getBuffer().toString().length() - 2) + "}");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return contents;
    }

    public String resolveUrlPath(String resourcePath, List<? extends org.springframework.core.io.Resource> locations, ResourceResolverChain chain) {
        return !"admin/ui/messages.js".equals(resourcePath)
                ? chain.resolveUrlPath(resourcePath, locations)
                : "admin/ui/messages.js";
    }
}
