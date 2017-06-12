/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;


/**
 * <p>
 * Prefixes a default Spring-generated bean name with 'bl', and also uppercases the first character of the default bean name.
 * If the bean name is already prefixed with {@code bl}|, this does nothing.
 * 
 * <p>
 * Example: {@code catalogEndpoint -> blCatalogEndpoint}, {@code blCatalogService -> blCatalogService}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafBeanNameGenerator extends AnnotationBeanNameGenerator {

    public static final String BROADLEAF_BEAN_PREFIX = "bl";
    
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanName = super.generateBeanName(definition, registry);
        if (!beanName.startsWith(BROADLEAF_BEAN_PREFIX)) {
            beanName = BROADLEAF_BEAN_PREFIX + StringUtils.capitalize(beanName);
        }
        
        return beanName;
    }
}
