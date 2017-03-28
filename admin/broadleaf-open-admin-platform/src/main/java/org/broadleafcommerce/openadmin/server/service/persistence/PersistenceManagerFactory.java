/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.common.service.PersistenceService;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
@Service("blPersistenceManagerFactory")
public class PersistenceManagerFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static final Map<Integer, PersistenceManager> persistenceManagers = new HashMap<Integer, PersistenceManager>();
    public static final String DEFAULTPERSISTENCEMANAGERREF = "blPersistenceManager";
    protected static String persistenceManagerRef = DEFAULTPERSISTENCEMANAGERREF;

    protected static PersistenceService persistenceService;
    protected static EntityConfiguration entityConfiguration;

    @Autowired
    public PersistenceManagerFactory(PersistenceService persistenceService, EntityConfiguration entityConfiguration) {
        PersistenceManagerFactory.persistenceService = persistenceService;
        PersistenceManagerFactory.entityConfiguration = entityConfiguration;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PersistenceManagerFactory.applicationContext = applicationContext;
    }

    /**
     * This method should only be used within the context of a thread with an established {@link PersistenceManagerContext}
     *  and the operation to be performed is on an entity that is managed by the {@link EntityManager} identified
     *  by {@link #startPersistenceManager(TargetModeType)}.
     *
     * See {@link PersistenceThreadManager#operation(TargetModeType, Persistable)} and {@link #startPersistenceManager(TargetModeType)}
     *  for an example of how the context is established.
     */
    public static PersistenceManager getPersistenceManager() {
        if (PersistenceManagerContext.getPersistenceManagerContext() != null) {
            return PersistenceManagerContext.getPersistenceManagerContext().getPersistenceManager();
        }
        throw new IllegalStateException("PersistenceManagerContext is not set on ThreadLocal. If you want to use the " +
                "non-cached version, try getPersistenceManager(Class, TargetModeType)");
    }

    public static PersistenceManager getPersistenceManager(String className) {
        return getPersistenceManager(className, TargetModeType.SANDBOX);
    }

    public static PersistenceManager getPersistenceManager(String className, TargetModeType targetModeType) {
        Class entityClass = getClassForName(className);
        return getPersistenceManager(entityClass, targetModeType);
    }

    public static PersistenceManager getPersistenceManager(Class entityClass) {
        return getPersistenceManager(entityClass, TargetModeType.SANDBOX);
    }

    /**
     * This method produces a {@link PersistenceManager} with a blPU-based standardEntityManager and EJB3ConfigurationDao.
     *  It also uses a {@link TargetModeType} of {@link TargetModeType#SANDBOX}
     */
    public static PersistenceManager getDefaultPersistenceManager() {
        return getDefaultPersistenceManager(TargetModeType.SANDBOX);
    }

    /**
     * This method produces a {@link PersistenceManager} with a blPU-based standardEntityManager and EJB3ConfigurationDao
     *  using the passed in {@link TargetModeType}
     */
    public static PersistenceManager getDefaultPersistenceManager(TargetModeType targetModeType) {
        synchronized (DynamicDaoHelperImpl.LOCK_OBJECT) {
            Integer cacheKey = persistenceService.identifyDefaultEntityManager(targetModeType).hashCode();
            if (!persistenceManagers.containsKey(cacheKey)) {
                PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
                persistenceManager.setTargetMode(targetModeType);
                persistenceManager.configureDefaultDynamicEntityDao(targetModeType);

                persistenceManagers.put(cacheKey, persistenceManager);
            }
            return persistenceManagers.get(cacheKey);
        }
    }

    public static PersistenceManager getPersistenceManager(Class entityClass, TargetModeType targetModeType) {
        synchronized (DynamicDaoHelperImpl.LOCK_OBJECT) {
            Integer cacheKey = persistenceService.identifyEntityManager(entityClass, targetModeType).hashCode();
            if (!persistenceManagers.containsKey(cacheKey)) {
                PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
                persistenceManager.setTargetMode(targetModeType);
                persistenceManager.configureDynamicEntityDao(entityClass, targetModeType);
                persistenceManagers.put(cacheKey, persistenceManager);
            }
            return persistenceManagers.get(cacheKey);
        }
    }

    public static boolean isPersistenceManagerActive() {
        return applicationContext.containsBean(getPersistenceManagerRef());
    }

    public static void startPersistenceManager(TargetModeType targetModeType) {
        PersistenceManagerContext context = PersistenceManagerContext.getPersistenceManagerContext();
        if (context == null) {
            context = new PersistenceManagerContext();
            PersistenceManagerContext.addPersistenceManagerContext(context);
        }
        context.addPersistenceManager(getDefaultPersistenceManager(targetModeType));
    }

    public static void startPersistenceManager(String entityClassName, TargetModeType targetModeType) {
        PersistenceManagerContext context = PersistenceManagerContext.getPersistenceManagerContext();
        if (context == null) {
            context = new PersistenceManagerContext();
            PersistenceManagerContext.addPersistenceManagerContext(context);
        }
        context.addPersistenceManager(getPersistenceManager(entityClassName, targetModeType));
    }

    public static void endPersistenceManager() {
        PersistenceManagerContext context = PersistenceManagerContext.getPersistenceManagerContext();
        if (context != null) {
            context.remove();
        }
    }

    public static String getPersistenceManagerRef() {
        return persistenceManagerRef;
    }

    public static void setPersistenceManagerRef(String persistenceManagerRef) {
        PersistenceManagerFactory.persistenceManagerRef = persistenceManagerRef;
    }

    protected static Class getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
