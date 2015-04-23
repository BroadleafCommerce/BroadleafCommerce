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

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link org.springframework.web.servlet.resource.ResourceTransformer} that replaces the
 * //BLC-SERVLET-CONTEXT and //BLC-SITE-BASEURL" tokens before serving the BLC.js file.
 * 
 * @since 4.0
 */
@Component("blBLCJsTranformer")
public class BLCJSResourceTransformer implements ResourceTransformer {

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
            throws IOException {

        // TODO: Only resolve BLC.js

        return null;
    }

}
