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

package org.broadleafcommerce.common.security.channel;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.SecureChannelProcessor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;

/**
 * <p>Very similar to the {@link SecureChannelProcessor} except that instead of relying on the HttpServletRequest this allows
 * inspection of the X-Forwarded-Proto header to determine if the request is secure. This class is required when the
 * application is deployed to an environment where SSL termination happens at a layer above the servlet container
 * (like at a load balancer)</p>
 * 
 * <p>This is intended to be used in conjunction with the {@link ProtoChannelBeanPostProcessor}. See that class for
 * more information on how to configure.</p>
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link SecureChannelProcessor}
 * @see {@link ProtoChannelBeanPostProcessor}
 */
public class ProtoSecureChannelProcessor extends SecureChannelProcessor {

    @Override
    public void decide(FilterInvocation invocation, Collection<ConfigAttribute> config) throws IOException, ServletException {
        Assert.isTrue((invocation != null) && (config != null), "Nulls cannot be provided");

        for (ConfigAttribute attribute : config) {
            if (supports(attribute)) {
                if ("http".equals(invocation.getHttpRequest().getHeader("X-Forwarded-Proto"))) {
                    getEntryPoint().commence(invocation.getRequest(), invocation.getResponse());
                }
            }
        }
    }

}
