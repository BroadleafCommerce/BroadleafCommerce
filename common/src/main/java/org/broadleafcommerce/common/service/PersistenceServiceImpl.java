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
package org.broadleafcommerce.common.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.common.util.dao.MappingProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;

/**
 * Service to help gather the correct {@link EntityManager} or {@link PlatformTransactionManager},
 *  based on a class and {@link TargetModeType}. This functionality is
 *  especially useful when multiple {@link javax.persistence.PersistenceUnit}s are in use.
 *
 * Note: All "default" items reference blPU, which is used to manage most Broadleaf entities in the Admin.
 *
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPersistenceService")
public class PersistenceServiceImpl implements PersistenceService, SmartLifecycle {

    protected static final Log LOG = LogFactory.getLog(PersistenceServiceImpl.class);

    protected static final String ENTITY_MANAGER_KEY = "entityManager";
    protected static final String TRANSACTION_MANAGER_KEY = "transactionManager";

    @Resource (name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blTargetModeMaps")
    protected List<Map<String, Map<String, Object>>> targetModeMaps;

    @Resource(name = "blDefaultTargetModeMap")
    protected Map<String, Map<String, Object>> defaultTargetModeMap;

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @Autowired
    protected List<EntityManager> entityManagers;

    private final Map<String, EntityManager> ENTITY_MANAGER_CACHE = new ConcurrentHashMap<>();
    private final Map<String, PlatformTransactionManager> TRANSACTION_MANAGER_CACHE = new ConcurrentHashMap<>();

    private DynamicDaoHelperImpl daoHelper = new DynamicDaoHelperImpl();

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public void start() {
        initializeEntityManagerCache();
    }

    @Override
    public void stop() {
        //do nothing
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    protected void initializeEntityManagerCache() {
        for (Map<String, Map<String, Object>> targetModeMap : targetModeMaps) {
            for (final String targetMode : targetModeMap.keySet()) {
                final Map<String, Object> managerMap = targetModeMap.get(targetMode);

                transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                    @Override
                    public void execute() throws Throwable {
                        populateCaches(targetMode, managerMap);
                    }
                }, RuntimeException.class, getTransactionManager(managerMap));
            }
        }
    }

    protected void populateCaches(String targetMode, Map<String, Object> managerMap) {
        final EntityManager em = getEntityManager(managerMap);
        final PlatformTransactionManager txManager = getTransactionManager(managerMap);

        SessionFactory sessionFactory = em.unwrap(Session.class).getSessionFactory();
        for (EntityType<?> item : sessionFactory.getMetamodel().getEntities()) {
            Class<?> mappedClass = item.getJavaType();

            String managerCacheKey = buildManagerCacheKey(targetMode, mappedClass);
            ENTITY_MANAGER_CACHE.put(managerCacheKey, em);
            TRANSACTION_MANAGER_CACHE.put(managerCacheKey, txManager);
        }
    }

    @Override
    public boolean validateEntityClassName(String entityClassName) {
        String cacheKey = buildManagerCacheKey(TargetModeType.SANDBOX.getType(), entityClassName);
        boolean isValid = ENTITY_MANAGER_CACHE.containsKey(cacheKey);

        if (!isValid) {
            LOG.warn("The system detected an entity class name submitted that is not present in the registered entities known to the system.");
        }

        return isValid;
    }

    @Override
    public EntityManager identifyEntityManager(Class entityClass) {
        return identifyEntityManager(entityClass, TargetModeType.SANDBOX);
    }

    @Override
    public EntityManager identifyEntityManager(Class entityClass, TargetModeType targetModeType) {
        String cacheKey = buildManagerCacheKey(targetModeType.getType(), entityClass);
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get(cacheKey);

        if (entityManager == null) {
            throw new RuntimeException("Unable to determine the EntityManager for the following " +
                    "targetModeType and class pair: " + cacheKey);
        }

        return entityManager;
    }

    @Override
    public PlatformTransactionManager identifyTransactionManager(String className, TargetModeType targetModeType) {
        String cacheKey = buildManagerCacheKey(targetModeType.getType(), className);
        PlatformTransactionManager txManager = TRANSACTION_MANAGER_CACHE.get(cacheKey);

        if (txManager == null) {
            throw new RuntimeException("Unable to determine the PlatformTransactionManager for the following " +
                    "targetModeType and class pair: " + cacheKey);
        }

        return txManager;
    }

    @Override
    public EntityManager identifyDefaultEntityManager(TargetModeType targetModeType) {
        Map<String, Object> managerMap = defaultTargetModeMap.get(targetModeType.getType());
        return getEntityManager(managerMap);
    }

    @Override
    public EntityManager getEntityManager(Map<String, Object> managerMap) {
        return (EntityManager) managerMap.get(ENTITY_MANAGER_KEY);
    }

    @Override
    public PlatformTransactionManager identifyDefaultTransactionManager(TargetModeType targetModeType) {
        Map<String, Object> managerMap = defaultTargetModeMap.get(targetModeType.getType());
        return getTransactionManager(managerMap);
    }

    @Override
    public PlatformTransactionManager getTransactionManager(Map<String, Object> managerMap) {
        return (PlatformTransactionManager) managerMap.get(TRANSACTION_MANAGER_KEY);
    }

    @Override
    public Class<?> getCeilingImplClassFromEntityManagers(String className) {
        Class<?> beanIdClass = getClassForName(className);

        for (EntityManager em : entityManagers) {
            Class<?>[] entitiesFromCeiling = daoHelper.getAllPolymorphicEntitiesFromCeiling(beanIdClass, em.unwrap(Session.class).getSessionFactory(), true, true);

            if (ArrayUtils.isNotEmpty(entitiesFromCeiling)) {
                return entitiesFromCeiling[entitiesFromCeiling.length - 1];
            }
        }
        return null;
    }

    protected String buildManagerCacheKey(String targetMode, Class<?> clazz) {
        return buildManagerCacheKey(targetMode, clazz.getName());
    }

    protected String buildManagerCacheKey(String targetMode, String className) {
        String managedClassName = getManagedClassName(className);

        return targetMode + "|" + managedClassName;
    }

    protected String buildEJB3ConfigDaoCacheKey(Class<?> clazz) {
        return getManagedClassName(clazz.getName());
    }

    protected String getManagedClassName(String className) {
        try {
            return entityConfiguration.lookupEntityClass(className).getName();
        } catch (NoSuchBeanDefinitionException e) {
            return getCeilingImplClassFromEntityManagers(className).getName();
        }
    }

    protected Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
