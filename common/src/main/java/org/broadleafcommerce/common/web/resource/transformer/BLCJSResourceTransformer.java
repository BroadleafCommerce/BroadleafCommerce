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

import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link org.springframework.web.servlet.resource.ResourceTransformer} that replaces the
 * //BLC-SERVLET-CONTEXT and //BLC-SITE-BASEURL" tokens before serving the BLC.js file.
 * @see org.broadleafcommerce.common.web.resource.transformer.BLCAbstractResourceTransformer
 * @since 4.0
 */
@Component("blBLCJsTranformer")
public class BLCJSResourceTransformer extends BLCAbstractResourceTransformer {

    private static final String BLC_JS_NAME="BLC.js";

    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;

    @Override
    public String getResourceFileName() {
        return BLC_JS_NAME;
    }

    @Override
    protected String generateNewContent(String content) {
        String newContent = content;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(content)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            newContent = newContent.replace("//BLC-SERVLET-CONTEXT", request.getContextPath());

            String siteBaseUrl = propMgr.getProperty("site.baseurl");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(siteBaseUrl)) {
                newContent = newContent.replace("//BLC-SITE-BASEURL", siteBaseUrl);
            }
        }
        return newContent;
    }
}
