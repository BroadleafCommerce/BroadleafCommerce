package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.ForeignKey;


public interface DynamicEntityDao extends BaseCriteriaDao<Serializable> {

	public abstract Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);
	
	public abstract Map<String, FieldMetadata> getPropertiesForEntityClass(Class<?> targetClass, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ClassNotFoundException;
	
	public abstract Serializable persist(Serializable entity);
	
	public abstract Serializable merge(Serializable entity);

	public abstract Serializable retrieve(Class<?> entityClass, Object primaryKey);
	
	public abstract void remove(Serializable entity);
	
	public abstract void clear();
	
	public void flush();
	
}