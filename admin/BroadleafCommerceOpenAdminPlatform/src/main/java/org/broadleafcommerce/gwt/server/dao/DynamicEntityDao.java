package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.persistence.EntityManager;

import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.hibernate.mapping.PersistentClass;


public interface DynamicEntityDao extends BaseCriteriaDao<Serializable> {

	public abstract Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);
	
	public abstract Map<String, FieldMetadata> getPropertiesForPrimitiveClass(String propertyName, String friendlyPropertyName, Class<?> targetClass, Class<?> parentClass, MergedPropertyType mergedPropertyType, Map<String, FieldMetadata> metadataOverrides) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
	
	public abstract Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, Class<?>[] entities, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, Map<String, FieldMetadata> metadataOverrides, String prefix) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
	
	public abstract Serializable persist(Serializable entity);
	
	public abstract Serializable merge(Serializable entity);

	public abstract Serializable retrieve(Class<?> entityClass, Object primaryKey);
	
	public abstract void remove(Serializable entity);
	
	public abstract void clear();
	
	public void flush();
	
	public void detach(Serializable entity);
	
	public void refresh(Serializable entity);
	
	public EntityManager getEntityManager();
	
	public PersistentClass getPersistentClass(String targetClassName);
	
}