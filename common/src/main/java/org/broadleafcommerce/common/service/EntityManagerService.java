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
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface EntityManagerService {

    /**
     * Determine if a test class name represents a known entity class registered with Hibernate
     *
     * @param entityClassName
     * @return
     */
    boolean validateEntityClassName(String entityClassName);

    /**
     * Identifies the {@link EntityManager} for the given className
     *
     * @param className
     * @return the {@link EntityManager}
     */
    EntityManager identifyEntityManagerForClass(String className) throws ServiceException;

    /**
     * Identifies the {@link PlatformTransactionManager} for the given className
     *
     * @param className
     * @return the {@link PlatformTransactionManager}
     */
    PlatformTransactionManager identifyTransactionManagerForClass(String className) throws ServiceException;

    /**
     * Identifies the {@link TargetModeType} for the given className
     *
     * @param className
     * @return the {@link TargetModeType}
     */
    TargetModeType identifyTargetModeTypeForClass(String className) throws ServiceException;
}
