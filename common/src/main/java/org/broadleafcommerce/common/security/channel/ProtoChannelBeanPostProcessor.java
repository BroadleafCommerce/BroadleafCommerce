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
 */
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
