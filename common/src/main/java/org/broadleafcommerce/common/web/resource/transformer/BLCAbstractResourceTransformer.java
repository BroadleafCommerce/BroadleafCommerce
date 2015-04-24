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
package org.broadleafcommerce.common.web.resource.transformer;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Abstract class that contains the Common variables and method implementations for {@link ResourceTransformer}
 * inside BroadleafCommerce framework.
 * @since 4.0
 */
public abstract class BLCAbstractResourceTransformer  implements ResourceTransformer{

    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    @Override
    public Resource transform(HttpServletRequest httpServletRequest, Resource resource, ResourceTransformerChain resourceTransformerChain) throws IOException {
        resource = resourceTransformerChain.transform(httpServletRequest,resource);

        String filename = resource.getFilename();
        if (!getResourceFileName().equalsIgnoreCase(filename)) {
            return resource;
        }

        // Resolve properties
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        String newContents = generateNewContent(content);


        // Generate the new Resource
        resource = new GeneratedResource(newContents.getBytes(), getResourceFileName());

        return resource;
    }

    /**
     * The file name that the {@link ResourceTransformer} should handle
     * @return
     */
     protected abstract String getResourceFileName() ;

    /**
     * Method that modify the content of resolved {@link Resource}.
     * @param content
     * @return
     */
     protected abstract String generateNewContent(String content);



}
