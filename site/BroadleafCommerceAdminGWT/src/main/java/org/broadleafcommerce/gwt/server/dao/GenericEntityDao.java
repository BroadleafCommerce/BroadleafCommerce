package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface GenericEntityDao extends BaseCriteriaDao<Serializable> {

	public abstract List<Class<?>> getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass);
	
	public abstract Map<String, FieldMetadata> getPropertiesForEntityClass(Class<?> targetClass, String[] optionalFields);
	
	public abstract Serializable persist(Serializable entity);
	
	public abstract Serializable merge(Serializable entity);

	public abstract Serializable retrieve(Class<?> entityClass, Object primaryKey);
	
	public abstract void remove(Serializable entity);
	
}