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
package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.MethodMetadata;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This processor is responsible for registering Early/LateStageMergeBeanPostProcessors for {@link Merge} annotated
 * beans and ensuring the correct prioritization of these post processors. The current ordering of the post processors
 * will be Framework XML, Framework Merge, Client XML, and the Client Merge.
 *
 * @author Jeff Fischer
 * @author Nick Crum (ncrum)
 */
@Component
public class MergeAnnotationAwareBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final String ANNOTATED_POST_PROCESSOR_SUFFIX = "AnnotatedMergePostProcessor";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<String, BeanDefinition> clientAnnotatedBeanPostProcessors = new LinkedHashMap<>();
        Map<String, BeanDefinition> clientBeanPostProcessors = new LinkedHashMap<>();

        for (String name : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(name);
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
                        String beanName = name +
                                "_" +
                                attributes.get("targetRef") +
                                (isEarly?"Early":"Late") +
                                ANNOTATED_POST_PROCESSOR_SUFFIX;
                        if (isBroadleafAnnotationBean(metadata)) {
                            registry.registerBeanDefinition(beanName, definition);
                        } else {
                            clientAnnotatedBeanPostProcessors.put(beanName, definition);
                        }
                    }
                }
            }

            /*
                If this is a client bean post processor, then remove it and store it away until we register all
                framework post processors.
             */
            if (beanDefinition.getBeanClassName() != null
                    && (beanDefinition.getBeanClassName().equals(EarlyStageMergeBeanPostProcessor.class.getName())
                    || beanDefinition.getBeanClassName().equals(LateStageMergeBeanPostProcessor.class.getName()))) {
                if (!isBroadleafBean(beanDefinition)) {
                    registry.removeBeanDefinition(name);
                    clientBeanPostProcessors.put(name, beanDefinition);
                }
            }
        }

        if (org.apache.commons.collections4.MapUtils.isNotEmpty(clientBeanPostProcessors)) {
            for (Map.Entry<String, BeanDefinition> entry : clientBeanPostProcessors.entrySet()) {
                registry.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        }

        if (org.apache.commons.collections4.MapUtils.isNotEmpty(clientAnnotatedBeanPostProcessors)) {
            for (Map.Entry<String, BeanDefinition> entry : clientAnnotatedBeanPostProcessors.entrySet()) {
                registry.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        }
    }

    protected boolean isBroadleafAnnotationBean(MethodMetadata metadata) {
        return metadata.getDeclaringClassName().contains("org.broadleafcommerce")
                || metadata.getDeclaringClassName().contains("com.broadleafcommerce");
    }

    protected boolean isBroadleafBean(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AnnotatedBeanDefinition){
            return isBroadleafAnnotationBean(((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata());
        } else if (beanDefinition instanceof GenericBeanDefinition && ((GenericBeanDefinition) beanDefinition).getResource() != null) {
            return ((GenericBeanDefinition) beanDefinition).getResource().getFilename().startsWith("bl-");
        }

        return false;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        // intentionally unimplemented
    }

}
