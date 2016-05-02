/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;

import java.lang.reflect.Type;

/**
 * Provides an implementation of a {@link TypeModifier} that looks up types in the application context for wrapper overrides.
 * This allows for correct instantiation to occur when wrappers have been overridden and a client is actually sending JSON
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blWrapperOverrideTypeModifier")
public class WrapperOverrideTypeModifier extends TypeModifier implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(WrapperOverrideTypeModifier.class);
    
    protected ApplicationContext applicationContext;
    
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {
        try {
            if (type.getClass().isAssignableFrom(SimpleType.class)) {
                Object overriddenBean = applicationContext.getBean(type.getRawClass().getName());
                return SimpleType.construct(overriddenBean.getClass());
            }
        } catch (NoSuchBeanDefinitionException e) {
            LOG.debug("No configured bean for " + type.getClass().getName() + " returning original type");
        }
        return type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
