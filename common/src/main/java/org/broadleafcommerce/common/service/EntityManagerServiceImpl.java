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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.TargetModeType;
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

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blEntityManagerService")
public class EntityManagerServiceImpl implements EntityManagerService, ApplicationContextAware {

    @Resource(name = "blTargetEntityManagers")
    protected Map<String, String> targetEntityManagers = new HashMap<>();

    protected Map<String, TargetModeType> identifiedTargetModeTypeCache = new HashMap<>();

    protected ApplicationContext applicationContext;

    @Override
    public EntityManager identifyEntityManagerForClass(String className) throws ServiceException {
        TargetModeType targetModeType = identifyTargetModeTypeForClass(className);
        String entityManagerBeanName = targetEntityManagers.get(targetModeType.getType());
        return retrieveEntityManager(entityManagerBeanName);
    }

    @Override
    public TargetModeType identifyTargetModeTypeForClass(String className) throws ServiceException {
        TargetModeType targetModeType = identifiedTargetModeTypeCache.get(className);

        if (targetModeType == null) {
            targetModeType = searchCandidateEntityManagersForTargetModeType(className);
            identifiedTargetModeTypeCache.put(className, targetModeType);
        }

        return targetModeType;
    }

    protected TargetModeType searchCandidateEntityManagersForTargetModeType(String className) throws ServiceException {
        Class<?> targetClass = getClassForName(className);

        if (targetClass != null) {
            for (String targetMode : targetEntityManagers.keySet()) {
                String entityManagerBeanName = targetEntityManagers.get(targetMode);
                EntityManager em = retrieveEntityManager(entityManagerBeanName);

                Set<EntityType<?>> maintainedEntityTypes = em.getMetamodel().getEntities();
                for (EntityType type : maintainedEntityTypes) {
                    Class maintainedTypeClass = type.getJavaType();

                    if (targetClass.isAssignableFrom(maintainedTypeClass)) {
                        return TargetModeType.getInstance(targetMode);
                    }
                }
            }
        }

        throw new ServiceException("Unable to determine the EntityManager that maintains the following class: " + className);
    }

    protected Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public EntityManager retrieveEntityManager(String entityManagerBeanName) {
        return (EntityManager) applicationContext.getBean(entityManagerBeanName);
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

        return new HashSet<>(beanNames);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
