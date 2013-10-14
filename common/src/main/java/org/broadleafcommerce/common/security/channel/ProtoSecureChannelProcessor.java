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
 * <p>This can be added to Spring Security by configuring an <b>access-decision-manager-ref</b> property in the <b>sec:http</b>
 * xml element. Below is an example configuration for SSL requests being terminated both by the servlet container OR relying
 * on a header being set by a load balancer:</p>
 *
 * <pre>
 * {@code
 * <sec:http auto-config="false" access-decision-manager-ref="loadBalancerAwareAccessDecisionManager">
 *      ...
 * </sec:http>
 * 
 * <bean id="loadBalancerAwareAccessDecisionManager" class="org.springframework.security.web.access.channel.ChannelDecisionManagerImpl">
 *    <property name="channelProcessors">
 *       <list>
 *          <bean class="org.springframework.security.web.access.channel.InsecureChannelProcessor" />
 *          <bean class="org.broadleafcommerce.common.security.channel.ProtoInsecureChannelProcessor" />
 *          <bean class="org.springframework.security.web.access.channel.SecureChannelProcessor" />
 *          <bean class="org.broadleafcommerce.common.security.channel.ProtoSecureChannelProcessor" />
 *        </list>
 *    </property>
 *  </bean>
 *  }
 * </pre>
 * 
 * <p>Note the inclusion of both the Spring secure/insecure channel processors alongside the Broadleaf proto secure/insecure
 * channel processors</p>
 * 
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link SecureChannelProcessor}
 * @see {@link ProtoInSecureChannelProcessor}
 */
public class ProtoSecureChannelProcessor extends SecureChannelProcessor {

    @Override
    public void decide(FilterInvocation invocation, Collection<ConfigAttribute> config) throws IOException, ServletException {
        Assert.isTrue((invocation != null) && (config != null), "Nulls cannot be provided");

        for (ConfigAttribute attribute : config) {
            if (supports(attribute)) {
                if (invocation.getHttpRequest().getHeader("X-Forwarded-Proto").equals("http")) {
                    getEntryPoint().commence(invocation.getRequest(), invocation.getResponse());
                }
            }
        }
    }

}
