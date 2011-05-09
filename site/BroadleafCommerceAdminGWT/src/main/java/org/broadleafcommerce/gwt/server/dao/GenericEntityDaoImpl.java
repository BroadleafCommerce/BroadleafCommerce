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
package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.spi.PersistenceUnitInfo;

import org.broadleafcommerce.gwt.client.datasource.results.SupportedFieldType;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

@Repository("blAdminGenericDao")
public class GenericEntityDaoImpl extends BaseHibernateCriteriaDao<Serializable> implements GenericEntityDao {
    
	private Ejb3Configuration configuration = null;
	
	@PersistenceContext(unitName = "blPU")
    protected EntityManager em;
	
	@Resource (name = "sessionFactory")
	protected SessionFactory sessionFactory;
	
	@Resource (name = "persistenceUnitInfo")
	protected PersistenceUnitInfo persistenceUnitInfo;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public Class<? extends Serializable> getEntityClass() {
		throw new RuntimeException("Must supply the entity class to query and count method calls! Default entity not supported!");
	}
	
	public Serializable persist(Serializable entity) {
		em.persist(entity);
		return entity;
	}
	
	public Serializable merge(Serializable entity) {
		return em.merge(entity);
	}
	
	public Serializable retrieve(Class<?> entityClass, Object primaryKey) {
		return (Serializable) em.find(entityClass, primaryKey);
	}
	
	public void remove(Serializable entity) {
		em.remove(entity);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.server.dao.GenericEntityDao#getAllPolymorphicEntitiesFromCeiling(java.lang.Class)
	 */
	public List<Class<?>> getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
		List<Class<?>> entities = new ArrayList<Class<?>>();
		for (Object item : sessionFactory.getAllClassMetadata().values()) {
			ClassMetadata metadata = (ClassMetadata) item;
			Class<?> mappedClass = metadata.getMappedClass(EntityMode.POJO);
			if (mappedClass != null && ceilingClass.isAssignableFrom(mappedClass)) {
				entities.add(mappedClass);
			}
		}
		
		return entities;
	}
	
	protected FieldMetadata getFieldMetadata(String propertyName, PersistentClass persistentClass, SupportedFieldType type, Type entityType, Class<?> targetClass) {
		FieldMetadata fieldMetadata = new FieldMetadata();
		fieldMetadata.setFieldType(type);
		Column column = (Column) persistentClass.getProperty(propertyName).getColumnIterator().next();
		fieldMetadata.setLength(column.getLength());
		fieldMetadata.setScale(column.getScale());
		fieldMetadata.setPrecision(column.getPrecision());
		fieldMetadata.setRequired(!column.isNullable());
		fieldMetadata.setUnique(column.isUnique());
		fieldMetadata.setMutable(true);
		fieldMetadata.setInheritedFromType(targetClass.getName());
		fieldMetadata.setAvailableToTypes(targetClass.getName());
		
		return fieldMetadata;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, FieldMetadata> getPropertiesForEntityClass(Class<?> targetClass, String[] optionalFields) {
		synchronized(this) {
			if (configuration == null) {
				Ejb3Configuration temp = new Ejb3Configuration();
				configuration = temp.configure(persistenceUnitInfo, new HashMap());
			}
		}
		PersistentClass persistentClass = configuration.getClassMapping(targetClass.getName());
		ClassMetadata metadata = sessionFactory.getClassMetadata(targetClass);
		Map<String, FieldMetadata> fields = new HashMap<String, FieldMetadata>();
		List<String> propertyNames = new ArrayList<String>();
		String idProperty = metadata.getIdentifierPropertyName();
		for (String propertyName : metadata.getPropertyNames()) {
			propertyNames.add(propertyName);
		}
		propertyNames.add(idProperty);
		for (String propertyName : propertyNames) {
			Type type = metadata.getPropertyType(propertyName);
			if (
					(!type.isAnyType() && !type.isEntityType() && !type.isAssociationType() && !type.isCollectionType() && !type.isComponentType()) || 
					type.getReturnedClass().equals(targetClass) ||
					(optionalFields != null && Arrays.binarySearch(optionalFields, propertyName) >= 0)
			) {
				Class<?> returnedClass = type.getReturnedClass();
				if (returnedClass.equals(Boolean.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.BOOLEAN, type, targetClass));
					continue;
				}
				if (returnedClass.equals(Byte.class) || returnedClass.equals(Short.class) || returnedClass.equals(Integer.class) || returnedClass.equals(Long.class)) {
					if (propertyName.equals(idProperty)) {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.ID, type, targetClass));
					} else {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.INTEGER, type, targetClass));
					}
					continue;
				}
				if (returnedClass.equals(Calendar.class) || returnedClass.equals(Date.class) || returnedClass.equals(Timestamp.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.DATE, type, targetClass));
					continue;
				}
				if (returnedClass.equals(Character.class) || returnedClass.equals(String.class)) {
					if (propertyName.equals(idProperty)) {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.ID, type, targetClass));
					} else {
						//try to determine if this is an email field by the field name
						if (propertyName.toLowerCase().contains("email")) {
							fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.EMAIL, type, targetClass));
						} else {
							fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.STRING, type, targetClass));
						}
					}
					continue;
				}
				if (returnedClass.equals(Double.class) || returnedClass.equals(BigDecimal.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.DECIMAL, type, targetClass));
					continue;
				}
				if (returnedClass.equals(Money.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.MONEY, type, targetClass));
					continue;
				}
				if (returnedClass.equals(targetClass)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.HIERARCHY_KEY, type, targetClass));
					ClassMetadata foreignMetadata = sessionFactory.getClassMetadata(returnedClass);
					fields.get(propertyName).setComplexIdProperty(foreignMetadata.getIdentifierPropertyName());
					fields.get(propertyName).setComplexType(returnedClass.getName());
					continue;
				}
				if (optionalFields != null && Arrays.binarySearch(optionalFields, propertyName) >= 0) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.FOREIGN_KEY, type, targetClass));
					ClassMetadata foreignMetadata = sessionFactory.getClassMetadata(returnedClass);
					fields.get(propertyName).setComplexIdProperty(foreignMetadata.getIdentifierPropertyName());
					fields.get(propertyName).setComplexType(returnedClass.getName());
					continue;
				}
				//return type not supported - just skip this property
			}
		}
		
		return fields;
	}
    
}
