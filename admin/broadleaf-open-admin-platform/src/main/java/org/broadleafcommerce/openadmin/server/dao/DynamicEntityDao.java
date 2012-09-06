/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.dao;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import org.broadleafcommerce.openadmin.client.dto.override.AdornedTargetCollectionMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.override.BasicCollectionMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.override.BasicFieldMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.override.MapMetadataOverride;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.Criteria;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.Type;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public interface DynamicEntityDao extends BaseCriteriaDao<Serializable> {

	public abstract Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);

    public ClassTree getClassTreeFromCeiling(Class<?> ceilingClass);

    public ClassTree getClassTree(Class<?>[] polymorphicClasses);
	
	public abstract Map<String, FieldMetadata> getPropertiesForPrimitiveClass(String propertyName, String friendlyPropertyName, Class<?> targetClass, Class<?> parentClass, MergedPropertyType mergedPropertyType) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
	
	public abstract Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, Class<?>[] entities, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey, String prefix) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException;
	
	public abstract Serializable persist(Serializable entity);
	
	public abstract Serializable merge(Serializable entity);

	public abstract Serializable retrieve(Class<?> entityClass, Object primaryKey);
	
	public abstract void remove(Serializable entity);
	
	public abstract void clear();
	
	public void flush();
	
	public void detach(Serializable entity);
	
	public void refresh(Serializable entity);
	
	public EntityManager getStandardEntityManager();
	
	public void setStandardEntityManager(EntityManager entityManager);

    /**
     * Get the Hibernate PersistentClass instance associated with the fully-qualified
     * class name. Will return null if no persistent class is associated with this name.
     *
     * @param targetClassName
     * @return The PersistentClass instance
     */
	public PersistentClass getPersistentClass(String targetClassName);
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException;

    public FieldManager getFieldManager();

    public EntityConfiguration getEntityConfiguration();

    public void setEntityConfiguration(EntityConfiguration entityConfiguration);

    public Map<String, Class<?>> getIdMetadata(Class<?> entityClass);

    public List<Type> getPropertyTypes(Class<?> entityClass);

    public List<String> getPropertyNames(Class<?> entityClass);

    public abstract Criteria getCriteria(PersistentEntityCriteria entityCriteria, Class<?> entityClass);

    public Criteria createCriteria(Class<?> entityClass);

    public Map<String, Map<String, Map<String, BasicFieldMetadataOverride>>> getFieldMetadataOverrides();

    public void setFieldMetadataOverrides(Map<String, Map<String, Map<String, BasicFieldMetadataOverride>>> metadataOverrides);

    public Map<String, Map<String, Map<String, MapMetadataOverride>>> getMapMetadataOverrides();

    public void setMapMetadataOverrides(Map<String, Map<String, Map<String, MapMetadataOverride>>> mapMetadataOverrides);

    public Map<String, Map<String, Map<String, AdornedTargetCollectionMetadataOverride>>> getAdornedTargetCollectionMetadataOverrides();

    public void setAdornedTargetCollectionMetadataOverrides(Map<String, Map<String, Map<String, AdornedTargetCollectionMetadataOverride>>> adornedTargetCollectionMetadataOverrides);

    public Map<String, Map<String, Map<String, BasicCollectionMetadataOverride>>> getCollectionMetadataOverrides();

    public void setCollectionMetadataOverrides(Map<String, Map<String, Map<String, BasicCollectionMetadataOverride>>> collectionMetadataOverrides);

}