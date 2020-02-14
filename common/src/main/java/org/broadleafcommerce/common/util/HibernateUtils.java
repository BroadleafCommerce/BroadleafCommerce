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
package org.broadleafcommerce.common.util;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Hibernate convenience methods
 *
 * @author Philip Baggett (pbaggett)
 */
public class HibernateUtils {
    
    public static final String DEFAULT_ENTITY_MANAGER_NAME = "blPU";
    
    /**
     * <p>Ensure a domain object is an actual persisted object and not a Hibernate proxy object by getting its real implementation.
     *
     * <p>This is primarily useful when retrieving a lazy loaded object that has been subclassed and you have the intention of casting it.
     *
     * @param t the domain object to deproxy
     * @return the actual persisted object or the passed in object if it is not a Hibernate proxy
     */
    public static <T> T deproxy(T t) {
        if (t instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)t;
            LazyInitializer lazyInitializer = proxy.getHibernateLazyInitializer();
            return (T)lazyInitializer.getImplementation();
        }
        return t;
    }
    
    /**
     * During bulk operations such as reindexing we don't always want things to be cached.  This allows us to surgically turn off caching where appropriate.
     * If the {@link EntityManager} is null, the operation is executed without affecting the cache settings.
     * 
     * @param operation
     * @param em
     * @return
     * @throws G
     */
    public static <T> T executeWithoutCache(GenericOperation<T> operation, EntityManager em) throws Exception {
        if (em == null) {
            return operation.execute();
        }
        final Session session = em.unwrap(Session.class);
        final CacheMode initialCacheMode = session.getCacheMode();
        session.setCacheMode(CacheMode.IGNORE);
        try {
            return operation.execute();
        } finally {
            session.setCacheMode(initialCacheMode);
        }
    }
    
    /**
     * Attempts to execute the operation without L2 or query cache engaged.  This affects the {@link PersistenceUnit} with the provided name.  
     * If no {@link EntityManager} is bound to the thread for the provided {@link PersistenceUnit}, 
     * then the operation is performed without modifying the cache mode.  Note that this does not create an {@link EntityManager} if one is not already initialized.
     * 
     * @param operation
     * @param persistenceUnitName
     * @return
     * @throws Exception
     */
    public static <T> T executeWithoutCache(GenericOperation<T> operation, String persistenceUnitName) throws Exception {
        return executeWithoutCache(operation, getCurrentEntityManager(persistenceUnitName));
    }
    
    /**
     * Attempts to execute the operation without L2 or query cache engaged.  This method uses the {@link PersistenceUnit} name "blPU".  If no {@link EntityManager} is bound to the thread, 
     * then the operation is performed without modifying the cache mode.  Note that this does not create an {@link EntityManager} if one is not already initialized.
     * 
     * @param operation
     * @return
     * @throws Exception
     */
    public static <T> T executeWithoutCache(GenericOperation<T> operation) throws Exception {
        return executeWithoutCache(operation, DEFAULT_ENTITY_MANAGER_NAME);
    }
    
    /**
     * Retrieves the current, default {@link EntityManager}, with the {@link PersistenceUnit} name provided, or null if it has not been initialized and bound to the Thread.
     * 
     * @param persistenceUnitName
     * @return
     */
    public static EntityManager getCurrentEntityManager(String persistenceUnitName) {
        ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
        if (ctx != null) {
            EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(ctx, persistenceUnitName);
            if (emf != null && TransactionSynchronizationManager.hasResource(emf)) {
                return ((EntityManagerHolder)TransactionSynchronizationManager.getResource(emf)).getEntityManager();
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves the current, default {@link EntityManager}, with the {@link PersistenceUnit} name "blPU", or null if it has not been initialized and bound to the Thread.
     * 
     * @return
     */
    public static EntityManager getCurrentDefaultEntityManager() {
        return getCurrentEntityManager(DEFAULT_ENTITY_MANAGER_NAME);
    }
}
