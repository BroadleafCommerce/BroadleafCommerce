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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.access.channel.ChannelProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Fischer
 * @deprecated This class should not be brought into the Spring context as this will remove any channel processors and completely
 * replace them with ones that are only based on proto headers. As a result, this will only allow request.isSecure() to work
 * properly in a load-balanced environment. We need to instead make this work in both environments at the same time so that
 * the application can be deployed in a single-server staging environment and then in a load balanced environment and it
 * still work.
 */
@Deprecated
public class ProtoChannelBeanPostProcessor implements BeanPostProcessor, Ordered {

    Log LOG = LogFactory.getLog(ProtoChannelBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ChannelDecisionManagerImpl) {
            try {
                ChannelDecisionManagerImpl manager = (ChannelDecisionManagerImpl) bean;
                Field channelProcessors = manager.getClass().getDeclaredField("channelProcessors");
                channelProcessors.setAccessible(true);
                List<ChannelProcessor> list = (List<ChannelProcessor>) channelProcessors.get(manager);
                list.clear();
                List<ChannelProcessor> myProcessors = new ArrayList<ChannelProcessor>();
                myProcessors.add(new ProtoInsecureChannelProcessor());
                myProcessors.add(new ProtoSecureChannelProcessor());
                manager.setChannelProcessors(myProcessors);
                LOG.info("Replacing the standard Spring Security channel processors with custom processors that look for a 'X-Forwarded-Proto' request header. This allows Spring Security to sit behind a load balancer with SSL termination.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return 9999;
    }
}
