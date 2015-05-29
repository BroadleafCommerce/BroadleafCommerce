/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
