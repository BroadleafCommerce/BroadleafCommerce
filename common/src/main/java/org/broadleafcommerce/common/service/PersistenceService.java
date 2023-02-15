/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.persistence.TargetModeType;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface PersistenceService {

    /**
     * Determine if a test class name represents a known entity class registered with Hibernate
     *
     * @param entityClassName
     * @return
     */
    boolean validateEntityClassName(String entityClassName);

    /**
     * Identifies the {@link EntityManager} for the given entityClass, using the default targetModeType of {@link TargetModeType#SANDBOX}
     *
     * @param entityClass
     * @return the {@link EntityManager}
     */
    EntityManager identifyEntityManager(Class entityClass);

    /**
     * Identifies the {@link EntityManager} for the given entityClass and targetModeType
     *
     * @param entityClass
     * @param targetModeType
     * @return the {@link EntityManager}
     */
    EntityManager identifyEntityManager(Class entityClass, TargetModeType targetModeType);

    /**
     * Identifies the {@link PlatformTransactionManager} for the given className and targetModeType
     *
     * @param className
     * @param targetModeType
     * @return the {@link PlatformTransactionManager}
     */
    PlatformTransactionManager identifyTransactionManager(String className, TargetModeType targetModeType);

    /**
     * Identifies the default {@link EntityManager} for the given targetModeType
     *
     * NOTE: This assumes that the {@link EntityManager} is based on blPU
     *
     * @param targetModeType
     * @return the {@link EntityManager}
     */
    EntityManager identifyDefaultEntityManager(TargetModeType targetModeType);

    /**
     * Identifies the default {@link PlatformTransactionManager} for the given targetModeType
     *
     * NOTE: This assumes that the {@link PlatformTransactionManager} is based on blPU
     *
     * @param targetModeType
     * @return the {@link PlatformTransactionManager}
     */
    PlatformTransactionManager identifyDefaultTransactionManager(TargetModeType targetModeType);

    /**
     * Gathers the {@link EntityManager} from the provided managerMap
     *
     * @param managerMap
     * @return the {@link EntityManager}
     */
    EntityManager getEntityManager(Map<String, Object> managerMap);

    /**
     * Gathers the {@link PlatformTransactionManager} from the provided managerMap
     *
     * @param managerMap
     * @return the {@link PlatformTransactionManager}
     */
    PlatformTransactionManager getTransactionManager(Map<String, Object> managerMap);

    /**
     * Retrieves the topmost implementation for the given className by checking each registered {@link EntityManager}. If
     * this is not found in the entity managers, this returns null
     *
     * @param className
     */
    @Nullable
    Class<?> getCeilingImplClassFromEntityManagers(String className);
}
