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
package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.hibernate.Criteria;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 *
 * @author jfischer
 *
 */
public interface DynamicEntityDao {

    Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);

    Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass, boolean includeUnqualifiedPolymorphicEntities);

    Class<?>[] getUpDownInheritance(Class<?> testClass);

    Class<?> getImplClass(String className);

    Class<?> getCeilingImplClass(String className);

    ClassTree getClassTreeFromCeiling(Class<?> ceilingClass);

    ClassTree getClassTree(Class<?>[] polymorphicClasses);

    Map<String, FieldMetadata> getPropertiesForPrimitiveClass(String propertyName, String friendlyPropertyName, Class<?> targetClass, Class<?> parentClass, MergedPropertyType mergedPropertyType);

    Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, Class<?>[] entities, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey, String prefix);

    /**
     * Convenience method that obtains all of the {@link MergedPropertyType#PRIMARY} properties for a given class. Delegates to
     * {@link #getMergedProperties(String, Class[], ForeignKey, String[], ForeignKey[], MergedPropertyType, Boolean, String[], String[], String, String)}
     * @param cls
     * @return
     */
    Map<String, FieldMetadata> getMergedProperties(@Nonnull Class<?> cls);

    <T> T persist(T entity);

    <T> T merge(T entity);

    Serializable retrieve(Class<?> entityClass, Object primaryKey);

    void remove(Serializable entity);

    void clear();

    void flush();

    void detach(Serializable entity);

    void refresh(Serializable entity);

    Object find(Class<?> entityClass, Object key);

    EntityManager getStandardEntityManager();

    void setStandardEntityManager(EntityManager entityManager);

    /**
     * Get the Hibernate PersistentClass instance associated with the fully-qualified
     * class name. Will return null if no persistent class is associated with this name.
     *
     * @param targetClassName
     * @return The PersistentClass instance
     */
    PersistentClass getPersistentClass(String targetClassName);

    Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective);

    FieldManager getFieldManager();

    FieldManager getFieldManager(boolean cleanFieldManger);

    EntityConfiguration getEntityConfiguration();

    void setEntityConfiguration(EntityConfiguration entityConfiguration);

    Map<String, Object> getIdMetadata(Class<?> entityClass);

    List<Type> getPropertyTypes(Class<?> entityClass);

    List<String> getPropertyNames(Class<?> entityClass);

    Criteria createCriteria(Class<?> entityClass);

    Field[] getAllFields(Class<?> targetClass);

    Metadata getMetadata();

    void setMetadata(Metadata metadata);

    FieldMetadataProvider getDefaultFieldMetadataProvider();

    boolean useCache();

    DynamicDaoHelper getDynamicDaoHelper();

    Map<String, TabMetadata> getTabAndGroupMetadata(Class<?>[] clazz, ClassMetadata cmd);

    /**
     * Returns a list of ids for entities that share the property value of the entity passed in
     *
     * @param instance
     * @param propertyName
     * @param value
     * @return
     */
    List<Long> readOtherEntitiesWithPropertyValue(Serializable instance, String propertyName, String value);

    /**
     * Retrieve the identifier from the Hibernate entity (the entity must reside in the current session)
     *
     * @param entity
     * @return
     */
    Serializable getIdentifier(Object entity);
}
