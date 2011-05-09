package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.EntityManager;

import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.hibernate.mapping.PersistentClass;


public interface DynamicEntityDao extends BaseCriteriaDao<Serializable> {

	public abstract Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);
	
	public abstract Map<String, FieldMetadata> getPropertiesForEntityClass(Class<?> targetClass, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType) throws ClassNotFoundException;
	
	public abstract Map<String, FieldMetadata> getPropertiesForPrimitiveClass(String propertyName, String friendlyPropertyName, Class<?> targetClass, Class<?> parentClass, MergedPropertyType mergedPropertyType) throws ClassNotFoundException;
	
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