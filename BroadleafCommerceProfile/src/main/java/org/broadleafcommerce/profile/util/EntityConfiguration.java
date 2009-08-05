/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.util;

import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("blEntityConfiguration")
public class EntityConfiguration implements ApplicationContextAware {

    @SuppressWarnings("unchecked")
    private final HashMap<String, Class> entityMap = new HashMap<String, Class>();

    private ApplicationContext applicationcontext;

    @SuppressWarnings("unchecked")
    public Class lookupEntityClass(String beanId) {
        Class clazz = null;
        if (entityMap.containsKey(beanId)) {
            clazz = entityMap.get(beanId);
        } else {
            Object object = applicationcontext.getBean(beanId);
            clazz = object.getClass();
            entityMap.put(beanId, clazz);
        }
        return clazz;
    }

    public Object createEntityInstance(String beanId) {
        return applicationcontext.getBean(beanId);
    }

    public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
        this.applicationcontext = applicationcontext;
    }
}
