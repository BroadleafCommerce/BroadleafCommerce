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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blEntityManagerService")
public class EntityManagerServiceImpl implements EntityManagerService, ApplicationContextAware {

    protected static final Log LOG = LogFactory.getLog(EntityManagerServiceImpl.class);

    @Resource(name = "blTargetEntityManagers")
    protected Map<String, String> targetEntityManagers = new HashMap<>();

    private final Object WHITELIST_LOCK = new Object();
    private final Map<String, String> ENTITY_WHITELIST = new ConcurrentHashMap<>();

    protected ApplicationContext applicationContext;

    @Override
    public void initializeEntityWhiteList() {
        synchronized(WHITELIST_LOCK) {
            if (ENTITY_WHITELIST.isEmpty()) {
                for (String targetMode : targetEntityManagers.keySet()) {
                    String entityManagerBeanName = targetEntityManagers.get(targetMode);
                    EntityManager em = retrieveEntityManager(entityManagerBeanName);

                    SessionFactory sessionFactory = em.unwrap(Session.class).getSessionFactory();
                    for (Object item : sessionFactory.getAllClassMetadata().values()) {
                        ClassMetadata metadata = (ClassMetadata) item;
                        Class<?> mappedClass = metadata.getMappedClass();

                        if (!ENTITY_WHITELIST.containsKey(mappedClass.getName())) {
                            ENTITY_WHITELIST.put(mappedClass.getName(), targetMode);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean validateEntityClassName(String entityClassName) {
        boolean isValid = ENTITY_WHITELIST.containsKey(entityClassName);
        if (!isValid) {
            isValid = ENTITY_WHITELIST.containsKey(entityClassName + "Impl");
        }

        if (!isValid) {
            LOG.warn("The system detected an entity class name submitted that is not present in the registered entities known to the system.");
        }

        return isValid;
    }

    @Override
    public EntityManager identifyEntityManagerForClass(String className) throws ServiceException {
        TargetModeType targetModeType = identifyTargetModeTypeForClass(className);
        String entityManagerBeanName = targetEntityManagers.get(targetModeType.getType());
        return retrieveEntityManager(entityManagerBeanName);
    }

    @Override
    public TargetModeType identifyTargetModeTypeForClass(String className) throws ServiceException {
        String targetMode = ENTITY_WHITELIST.get(className);
        if (targetMode == null) {
            targetMode = ENTITY_WHITELIST.get(className + "Impl");
        }
        TargetModeType targetModeType = TargetModeType.getInstance(targetMode);

        if (targetModeType == null) {
            throw new ServiceException("Unable to determine the EntityManager that maintains the following class: " + className);
        }

        return targetModeType;
    }

    @Override
    public EntityManager retrieveEntityManager(String entityManagerBeanName) {
        EntityManager bean = (EntityManager) applicationContext.getBean(entityManagerBeanName);
        return bean.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public EntityManager retrieveEntityManager(TargetModeType targetModeType) {
        String entityManagerBeanName = targetEntityManagers.get(targetModeType.getType());
        return retrieveEntityManager(entityManagerBeanName);
    }

    @Override
    public List<EntityManager> retrieveAllEntityManagers() {
        List<EntityManager> entityManagers = new ArrayList<>();

        Set<String> uniqueBeanNames = gatherUniqueEntityManagerBeanNames();
        for (String uniqueBeanName : uniqueBeanNames) {
            EntityManager entityManager = retrieveEntityManager(uniqueBeanName);
            entityManagers.add(entityManager);
        }

        return entityManagers;
    }

    protected Set<String> gatherUniqueEntityManagerBeanNames() {
        Collection<String> beanNames = targetEntityManagers.values();

        return new HashSet<String>(beanNames);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
