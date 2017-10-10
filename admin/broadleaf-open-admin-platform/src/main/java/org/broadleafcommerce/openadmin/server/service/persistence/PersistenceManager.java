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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PersistenceManager {

    Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);

    Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException;

    Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException;

    ClassMetadata buildClassMetadata(Class<?>[] entities, PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) throws IllegalArgumentException;

    PersistenceResponse inspect(PersistencePackage persistencePackage) throws ServiceException, ClassNotFoundException;

    PersistenceResponse fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    PersistenceResponse add(PersistencePackage persistencePackage) throws ServiceException;

    PersistenceResponse update(PersistencePackage persistencePackage) throws ServiceException;

    PersistenceResponse remove(PersistencePackage persistencePackage) throws ServiceException;

    void configureDynamicEntityDao(Class entityClass, TargetModeType targetMode);

    /**
     * This method produces a {@link DynamicEntityDao} with a blPU-based standardEntityManager
     *  using the passed in {@link TargetModeType}
     */
    void configureDefaultDynamicEntityDao(TargetModeType targetModeType);

    DynamicEntityDao getDynamicEntityDao();

    void setDynamicEntityDao(DynamicEntityDao dynamicEntityDao);

    TargetModeType getTargetMode();

    void setTargetMode(TargetModeType targetMode);

    List<CustomPersistenceHandler> getCustomPersistenceHandlers();

    void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers);

    Class<?>[] getUpDownInheritance(Class<?> testClass);

    Class<?>[] getUpDownInheritance(String testClassname) throws ClassNotFoundException;

    String getIdPropertyName(String entityClass);

}
