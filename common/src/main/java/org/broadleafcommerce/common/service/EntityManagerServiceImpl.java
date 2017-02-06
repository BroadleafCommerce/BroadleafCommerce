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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blEntityManagerService")
public class EntityManagerServiceImpl implements EntityManagerService {

    protected static final Log LOG = LogFactory.getLog(EntityManagerServiceImpl.class);

    @Resource(name = "blTargetEntityManagers")
    protected Map<String, EntityManager> targetEntityManagers = new HashMap<>();

    @Resource(name = "blTargetTransactionManagers")
    protected Map<String, PlatformTransactionManager> targetTransactionManagers = new HashMap<>();

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    private final Object ENTITY_MANAGER_CACHE_LOCK = new Object();
    private final Map<String, String> ENTITY_MANAGER_CACHE = new ConcurrentHashMap<>();

    @EventListener
    public void init(ContextRefreshedEvent event) {
        synchronized(ENTITY_MANAGER_CACHE_LOCK) {
            if (ENTITY_MANAGER_CACHE.isEmpty()) {
                initializeEntityManagerCache();
            }
        }
    }

    protected void initializeEntityManagerCache() {
        for (final String targetMode : targetTransactionManagers.keySet()) {
            PlatformTransactionManager txManager = targetTransactionManagers.get(targetMode);
            transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void execute() throws Throwable {
                    populateEntityManagerCache(targetMode);
                }
            }, RuntimeException.class, txManager);
        }
    }

    protected void populateEntityManagerCache(String targetMode) {
        EntityManager em = targetEntityManagers.get(targetMode);

        SessionFactory sessionFactory = em.unwrap(Session.class).getSessionFactory();
        for (Object item : sessionFactory.getAllClassMetadata().values()) {
            ClassMetadata metadata = (ClassMetadata) item;
            Class<?> mappedClass = metadata.getMappedClass();

            if (!ENTITY_MANAGER_CACHE.containsKey(mappedClass.getName())) {
                ENTITY_MANAGER_CACHE.put(mappedClass.getName(), targetMode);
            }
        }
    }

    @Override
    public boolean validateEntityClassName(String entityClassName) {
        boolean isValid = ENTITY_MANAGER_CACHE.containsKey(entityClassName);
        if (!isValid) {
            isValid = ENTITY_MANAGER_CACHE.containsKey(entityClassName + "Impl");
        }

        if (!isValid) {
            LOG.warn("The system detected an entity class name submitted that is not present in the registered entities known to the system.");
        }

        return isValid;
    }

    @Override
    public EntityManager identifyEntityManagerForClass(String className) throws ServiceException {
        TargetModeType targetModeType = identifyTargetModeTypeForClass(className);
        return targetEntityManagers.get(targetModeType.getType());
    }

    @Override
    public PlatformTransactionManager identifyTransactionManagerForClass(String className) throws ServiceException {
        TargetModeType targetModeType = identifyTargetModeTypeForClass(className);
        return targetTransactionManagers.get(targetModeType.getType());
    }

    @Override
    public TargetModeType identifyTargetModeTypeForClass(String className) throws ServiceException {
        String targetMode = ENTITY_MANAGER_CACHE.get(className);
        if (targetMode == null) {
            targetMode = ENTITY_MANAGER_CACHE.get(className + "Impl");
        }
        TargetModeType targetModeType = TargetModeType.getInstance(targetMode);

        if (targetModeType == null) {
            throw new ServiceException("Unable to determine the EntityManager that maintains the following class: " + className);
        }

        return targetModeType;
    }
}
