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
import org.springframework.security.web.access.channel.InsecureChannelProcessor;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;

/**
 * <p>Very similar to the {@link InsecureChannelProcessor} except that instead of relying on only the HttpServletRequest
 * this also allows
 * inspection of the X-Forwarded-Proto header to determine if the request is secure. This class is required when the
 * application is deployed to an environment where SSL termination happens at a layer above the servlet container
 * (like at a load balancer)</p>
 * 
 * <p>This is intended to be used in conjunction with the {@link ProtoChannelBeanPostProcessor}. See that class for
 * more information on how to configure.</p>
 * 
 * <p>This class encapsulates functionality given in {@link InsecureChannelProcessor} so it is unnecessary to configure
 * both</p>
 * 
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link InsecureChannelProcessor}
 * @see {@link ProtoSecureChannelProcessor}
 */
public class ProtoInsecureChannelProcessor extends InsecureChannelProcessor {

    @Override
    public void decide(FilterInvocation invocation, Collection<ConfigAttribute> config) throws IOException, ServletException {
        if ((invocation == null) || (config == null)) {
            throw new IllegalArgumentException("Nulls cannot be provided");
        }

        for (ConfigAttribute attribute : config) {
            if (supports(attribute)) {
                if (supports(attribute)) {
                    if (invocation.getHttpRequest().getHeader("X-Forwarded-Proto") != null
                            && "http".equalsIgnoreCase(invocation.getHttpRequest().getHeader("X-Forwarded-Proto"))) {
                        return;
                    } else if (!invocation.getHttpRequest().isSecure()) {
                        return;
                    } else {
                        getEntryPoint().commence(invocation.getRequest(), invocation.getResponse());
                    }
                }
            }
        }
    }
}
