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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.service.ResourceMinificationService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link org.springframework.web.servlet.resource.ResourceTransformer} that minifies
 * the resource.    Only works with allowed extensions (".css" and ".js" by default).
 * 
 * {@link org.broadleafcommerce.common.resource.service.ResourceMinificationService} is used to
 * perform the minification. 
 *
 * @author Brian Polster
 * @since 4.0
 */
@Component("blResourceMinificationTranformer")
public class MinifyResourceTransformer implements ResourceTransformer {

    private static final Log logger = LogFactory.getLog(MinifyResourceTransformer.class);

    @javax.annotation.Resource(name = "blResourceMinificationService")
    protected ResourceMinificationService minifyService;

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
            throws IOException {

        Resource transformed = transformerChain.transform(request, resource);

        if (logger.isTraceEnabled()) {
            logger.trace("Putting transformed resource in cache");
        }
        return minifyService.minify(transformed);
    }

}
