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

import java.util.List;

import javax.persistence.EntityManager;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface EntityManagerService {

    /**
     * Identifies the {@link EntityManager} for the given className
     *
     * @param className
     * @return the {@link EntityManager}
     */
    EntityManager identifyEntityManagerForClass(String className) throws ServiceException;

    /**
     * Identifies the {@link TargetModeType} for the given className
     *
     * @param className
     * @return the {@link TargetModeType}
     */
    TargetModeType identifyTargetModeTypeForClass(String className) throws ServiceException;

    /**
     * Gathers the EntityManager associated to the given beanName, according to the
     *  `blTargetEntityManagers` bean list
     *
     * @param entityManagerBeanName
     * @return the {@link EntityManager}
     */
    EntityManager retrieveEntityManager(String entityManagerBeanName);

    /**
     * Gathers the EntityManager associated to the given {@link TargetModeType}, according to the
     *  `blTargetEntityManagers` bean list
     *
     * @param targetModeType
     * @return the {@link EntityManager}
     */
    EntityManager retrieveEntityManager(TargetModeType targetModeType);

    /**
     * Gathers all EntityManagers registered with the `blTargetEntityManagers` bean list
     *
     * @return a list of {@link EntityManager}s
     */
    List<EntityManager> retrieveAllEntityManagers();

}
