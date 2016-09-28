/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.bootstrap;

import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor;
import org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor;
import org.broadleafcommerce.common.extensibility.context.merge.MergeBeanStatusProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.type.MethodMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component
public class MergeAnnotationAwareBeanDefinitionRegistryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) bf;

        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                MethodMetadata metadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
                if (metadata != null) {
                    Map<String, Object> attributes = metadata.getAnnotationAttributes(Merge.class.getName());
                    if (!MapUtils.isEmpty(attributes)) {
                        boolean isEarly = MapUtils.getBooleanValue(attributes,"early");
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                            .genericBeanDefinition(isEarly?EarlyStageMergeBeanPostProcessor.class:LateStageMergeBeanPostProcessor.class)
                            .setScope(BeanDefinition.SCOPE_SINGLETON)
                            .addPropertyValue("sourceRef", name)
                            .addPropertyValue("targetRef", attributes.get("targetRef"))
                            .addPropertyValue("placement", attributes.get("placement"))
                            .addPropertyValue("position", attributes.get("position"));
                        Class<MergeBeanStatusProvider> clazz = (Class<MergeBeanStatusProvider>) attributes.get("statusProvider");
                        if (MergeBeanStatusProvider.class != clazz) {
                            try {
                                builder.addPropertyValue("statusProvider", clazz.newInstance());
                            } catch (InstantiationException e) {
                                throw ExceptionHelper.refineException(e);
                            } catch (IllegalAccessException e) {
                                throw ExceptionHelper.refineException(e);
                            }
                        }
                        BeanDefinition definition = builder.getBeanDefinition();
                        beanFactory.registerBeanDefinition(
                                name +
                                "_" +
                                attributes.get("targetRef") +
                                (isEarly?"Early":"Late") +
                                "AnnotatedMergePostProcessor",
                        definition);
                    }
                }
            }
        }
    }
}
