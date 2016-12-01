/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Nathan Moore (nathanmoore).
 */
@Component("blPostLoaderDao")
public class DefaultPostLoaderDao implements PostLoaderDao, ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static PostLoaderDao postLoaderDao;

    public static PostLoaderDao getPostLoaderDao() {
        if (applicationContext == null) {
            return null;
        } else if (postLoaderDao == null) {
            postLoaderDao = (PostLoaderDao) applicationContext.getBean("blPostLoaderDao");
        }

        return postLoaderDao;
    }

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DefaultPostLoaderDao.applicationContext = applicationContext;
    }

    /**
     * Find and return the entity by primary key and class.
     *
     * Delegates find to {@link javax.persistence.EntityManager#find(Class, Object)}.
     *
     * @param clazz entity class
     * @param id primary key
     * @return managed entity or null if not found
     */
    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return em.find(clazz, id);
    }
}
