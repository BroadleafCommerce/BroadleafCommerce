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

    @Override
    public <T> T findSandboxEntity(Class<T> clazz, Object id) {
        return em.find(clazz, id);
    }

}
