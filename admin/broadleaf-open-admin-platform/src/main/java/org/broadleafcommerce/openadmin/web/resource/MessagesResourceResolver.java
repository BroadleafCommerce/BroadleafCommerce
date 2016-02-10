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
 * Generated resource resolver for Admin Javascript Messages
 *
 * @author Brandon Smith
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blMessagesResourceResolver")
public class MessagesResourceResolver implements ResourceResolver {

    protected static final String MESSAGES_JS_PATH="admin/ui/messages.js";
    protected static final String OPEN_ADMIN_MESSAGES_PROPERTIES="messages/OpenAdminJavascriptMessages.properties";

    public Resource resolveResource(HttpServletRequest request, String path, List<? extends Resource> locations, ResourceResolverChain chain) {
        if (!path.equalsIgnoreCase(getMessagesJsPath())) {
            return chain.resolveResource(request, path, locations);
        } else {
            Resource resource = chain.resolveResource(request, path, locations);
            return this.updateMessagesVariables(resource, path);
        }
    }

    protected Resource updateMessagesVariables(Resource resource, String path) {
        if (resource != null) {
            String contents;
            try {
                contents = this.getResourceContents(resource);
                contents = replaceResourceContents(contents);
            } catch (IOException e) {
                throw new RuntimeException("Could not get resource (Messages JS) contents", e);
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
            String propFileName = getOpenAdminMessagesProperties();

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
            throw new RuntimeException(e);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return contents;
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        if(!MESSAGES_JS_PATH.equals(resourcePath)){
            return chain.resolveUrlPath(resourcePath,locations);
        }
        return MESSAGES_JS_PATH;
    }

    public String getMessagesJsPath() {
        return MESSAGES_JS_PATH;
    }

    public String getOpenAdminMessagesProperties() {
        return OPEN_ADMIN_MESSAGES_PROPERTIES;
    }
}
