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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.changeset.dao.ChangeSetDao;
import org.broadleafcommerce.changeset.dao.EJB3ConfigurationDao;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.SupportedFieldType;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

@Repository("blDynamicEntityDao")
public class DynamicEntityDaoImpl extends BaseHibernateCriteriaDao<Serializable> implements DynamicEntityDao {
    	
	@PersistenceContext(unitName = "blPU")
    protected EntityManager em;
	
	@Resource (name = "sessionFactory")
	protected SessionFactory sessionFactory;
    
    @Resource(name = "blEJB3ConfigurationDao")
    protected EJB3ConfigurationDao ejb3ConfigurationDao;
    
    @Resource(name = "blChangeSetDao")
    protected ChangeSetDao changeSetDao;
    
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
	
	public void flush() {
		em.flush();
	}
 	
	public Serializable retrieve(Class<?> entityClass, Object primaryKey) {
		return (Serializable) em.find(entityClass, primaryKey);
	}
	
	public void remove(Serializable entity) {
		em.remove(entity);
	}
	
	public void clear() {
		em.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.server.dao.DynamicEntityDao#getAllPolymorphicEntitiesFromCeiling(java.lang.Class)
	 */
	public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
		List<Class<?>> entities = new ArrayList<Class<?>>();
		for (Object item : sessionFactory.getAllClassMetadata().values()) {
			ClassMetadata metadata = (ClassMetadata) item;
			Class<?> mappedClass = metadata.getMappedClass(EntityMode.POJO);
			if (mappedClass != null && ceilingClass.isAssignableFrom(mappedClass)) {
				entities.add(mappedClass);
			}
		}
		/*
		 * Sort entities in descending order of inheritance
		 */
		Class<?>[] sortedEntities = new Class<?>[entities.size()];
		sortedEntities = entities.toArray(sortedEntities);
		Arrays.sort(sortedEntities, new Comparator<Class<?>>() {

			public int compare(Class<?> o1, Class<?> o2) {
				if (o1.equals(o2)) {
					return 0;
				} else if (o1.isAssignableFrom(o2)) {
					return 1;
				}
				return -1;
			}
			
		});
		
		return sortedEntities;
	}
	
	protected FieldMetadata getFieldMetadata(String propertyName, PersistentClass persistentClass, SupportedFieldType type, Type entityType, Class<?> targetClass, FieldPresentationAttributes presentationAttribute) {
		FieldMetadata fieldMetadata = new FieldMetadata();
		fieldMetadata.setFieldType(type);
		if (entityType != null && !entityType.isCollectionType()) {
			Column column = (Column) persistentClass.getProperty(propertyName).getColumnIterator().next();
			fieldMetadata.setLength(column.getLength());
			fieldMetadata.setScale(column.getScale());
			fieldMetadata.setPrecision(column.getPrecision());
			fieldMetadata.setRequired(!column.isNullable());
			fieldMetadata.setUnique(column.isUnique());
			fieldMetadata.setCollection(false);
		} else {
			fieldMetadata.setCollection(true);
		}
		fieldMetadata.setMutable(true);
		fieldMetadata.setInheritedFromType(targetClass.getName());
		fieldMetadata.setAvailableToTypes(targetClass.getName());
		fieldMetadata.setPresentationAttributes(presentationAttribute);
		
		return fieldMetadata;
	}
	
	protected Map<String, FieldPresentationAttributes> getFieldPresentationAttributes(Class<?> targetClass) {
		Map<String, FieldPresentationAttributes> attributes = new HashMap<String, FieldPresentationAttributes>();
		Field[] fields = targetClass.getDeclaredFields();
		for (Field field : fields) {
			AdminPresentation annot = field.getAnnotation(AdminPresentation.class);
			if (annot != null) {
				FieldPresentationAttributes attr = new FieldPresentationAttributes();
				attr.setName(field.getName());
				attr.setFriendlyName(annot.friendlyName());
				attr.setHidden(annot.hidden());
				attr.setOrder(annot.order());
				attr.setFieldType(annot.fieldType());
				attr.setGroup(annot.group());
				attr.setLargeEntry(annot.largeEntry());
				attr.setProminent(annot.prominent());
				attributes.put(field.getName(), attr);
			}
		}
		return attributes;
	}
	
	public Map<String, FieldMetadata> getPropertiesForEntityClass(Class<?> targetClass, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ClassNotFoundException {
		Map<String, FieldPresentationAttributes> presentationAttributes = getFieldPresentationAttributes(targetClass);
		PersistentClass persistentClass = ejb3ConfigurationDao.getConfiguration().getClassMapping(targetClass.getName());
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
			int foreignKeyIndexPosition = -1;
			if (foreignFields != null) {
				foreignKeyIndexPosition = Arrays.binarySearch(foreignFields, new ForeignKey(propertyName, null, null), new Comparator<ForeignKey>() {
					public int compare(ForeignKey o1, ForeignKey o2) {
						return o1.getManyToField().compareTo(o2.getManyToField());
					}
				});
			}
			if (
					(!type.isAnyType() && !type.isEntityType() && !type.isAssociationType() && !type.isCollectionType() && !type.isComponentType()) || 
					foreignKeyIndexPosition >= 0 ||
					presentationAttributes.containsKey(propertyName)
			) {
				FieldPresentationAttributes presentationAttribute = presentationAttributes.get(propertyName);
				SupportedFieldType explicitType = null;
				if (presentationAttribute != null) {
					explicitType = presentationAttribute.getFieldType();
				}
				Class<?> returnedClass = type.getReturnedClass();
				if ((explicitType != null && explicitType.equals(SupportedFieldType.BOOLEAN)) || returnedClass.equals(Boolean.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.BOOLEAN, type, targetClass, presentationAttribute));
					continue;
				}
				if (
						(explicitType != null && explicitType.equals(SupportedFieldType.INTEGER)) || 
						returnedClass.equals(Byte.class) || returnedClass.equals(Short.class) || returnedClass.equals(Integer.class) || returnedClass.equals(Long.class)
				) {
					if (propertyName.equals(idProperty)) {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.ID, type, targetClass, presentationAttribute));
					} else {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute));
					}
					continue;
				}
				if (
						(explicitType != null && explicitType.equals(SupportedFieldType.DATE)) || 
						returnedClass.equals(Calendar.class) || returnedClass.equals(Date.class) || returnedClass.equals(Timestamp.class)
				) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.DATE, type, targetClass, presentationAttribute));
					continue;
				}
				if (
						(explicitType != null && explicitType.equals(SupportedFieldType.STRING)) || 
						returnedClass.equals(Character.class) || returnedClass.equals(String.class)
				) {
					if (propertyName.equals(idProperty)) {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.ID, type, targetClass, presentationAttribute));
					} else {
						fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.STRING, type, targetClass, presentationAttribute));
					}
					continue;
				}
				if (
						(explicitType != null && explicitType.equals(SupportedFieldType.DECIMAL)) || 
						returnedClass.equals(Double.class) || returnedClass.equals(BigDecimal.class)
				) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.DECIMAL, type, targetClass, presentationAttribute));
					continue;
				}
				if ((explicitType != null && explicitType.equals(SupportedFieldType.DECIMAL)) || returnedClass.equals(Money.class)) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.MONEY, type, targetClass, presentationAttribute));
					continue;
				}
				if ((explicitType != null && explicitType.equals(SupportedFieldType.FOREIGN_KEY)) || foreignFields != null && foreignKeyIndexPosition >= 0) {
					fields.put(propertyName, getFieldMetadata(propertyName, persistentClass, SupportedFieldType.FOREIGN_KEY, type, targetClass, presentationAttribute));
					ClassMetadata foreignMetadata;
					foreignMetadata = sessionFactory.getClassMetadata(Class.forName(foreignFields[foreignKeyIndexPosition].getForeignKeyClass()));
					fields.get(propertyName).setComplexIdProperty(foreignMetadata.getIdentifierPropertyName());
					//fields.get(propertyName).setComplexType(returnedClass.getName());
					fields.get(propertyName).setProvidedForeignKeyClass(foreignFields[foreignKeyIndexPosition].getForeignKeyClass());
					continue;
				}
				//return type not supported - just skip this property
			}
		}
		FieldPresentationAttributes presentationAttribute = new FieldPresentationAttributes();
		presentationAttribute.setFieldType(SupportedFieldType.STRING);
		presentationAttribute.setHidden(true);
		for (String additionalNonPersistentProperty : additionalNonPersistentProperties) {
			fields.put(additionalNonPersistentProperty, getFieldMetadata(additionalNonPersistentProperty, persistentClass, SupportedFieldType.STRING, null, targetClass, presentationAttribute));
		}
		
		return fields;
	}
    
}
