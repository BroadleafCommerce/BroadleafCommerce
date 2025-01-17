/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.service.PersistenceService;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;

/**
 * @author Nathan Moore (nathanmoore).
 */
@Component("blPostLoaderDao")
public class DefaultPostLoaderDao implements PostLoaderDao, ApplicationContextAware {

    protected static final Log LOG = LogFactory.getLog(DefaultPostLoaderDao.class);

    private static ApplicationContext applicationContext;
    private static PostLoaderDao postLoaderDao;

    @Resource(name = "blPersistenceService")
    protected PersistenceService persistenceService;

    public static PostLoaderDao getPostLoaderDao() {
        if (applicationContext == null) {
            return null;
        } else if (postLoaderDao == null) {
            postLoaderDao = (PostLoaderDao) applicationContext.getBean("blPostLoaderDao");
        }

        return postLoaderDao;
    }

    /**
     * see org.broadleafcommerce.test.TestNGSiteIntegrationSetup#reSetApplicationContext()
     *
     * @param applicationContext
     */
    public static void resetApplicationContext(ApplicationContext applicationContext) {
        DefaultPostLoaderDao.applicationContext = applicationContext;
        postLoaderDao = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DefaultPostLoaderDao.applicationContext = applicationContext;
    }

    /**
     * Find and return the entity by primary key and class.
     * <p>
     * Delegates find to {@link jakarta.persistence.EntityManager#find(Class, Object)}.
     *
     * @param clazz entity class
     * @param id    primary key
     * @return managed entity or null if not found
     */
    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityManager em = getEntityManager(clazz);
        if (em == null) {
            LOG.warn("EntityManager is null in DefaultPostLoaderDao returning NULL instead of doing em.find");
            return null;
        }
        return em.find(clazz, id);
    }

    @Override
    public <T> T findSandboxEntity(Class<T> clazz, Object id) {
        return find(clazz, id);
    }

    @Override
    public void evict(Class<?> clazz, Object id) {
        EntityManager em = getEntityManager(clazz);
        if (em != null) {
            Session session = em.unwrap(Session.class);
            session.evict(session.getReference(clazz, id));
        }
    }

    @Override
    public void evict(Object entity) {
        EntityManager em;
        if (entity instanceof HibernateProxy) {
            Class<?> persistentClass = ((HibernateProxy) entity).getHibernateLazyInitializer().getPersistentClass();
            em = getEntityManager(persistentClass);
        } else {
            em = getEntityManager(entity.getClass());
        }

        if (em != null) {
            Session session = em.unwrap(Session.class);
            session.evict(entity);
        }

    }

    protected EntityManager getEntityManager(Class clazz) {
        return persistenceService.identifyEntityManager(clazz);
    }

}
