/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.EJB3ConfigurationDao;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
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

    SessionFactory getSessionFactory();

    boolean useCache();

    EJB3ConfigurationDao getEjb3ConfigurationDao();

    DynamicDaoHelper getDynamicDaoHelper();

}
