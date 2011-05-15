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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.FieldPresentationAttributes;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.ConfigurationItem;
import org.broadleafcommerce.presentation.ValidationConfiguration;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityDaoImpl extends BaseHibernateCriteriaDao<Serializable> implements DynamicEntityDao {
    	
	private static final Hashtable<String, Map<String, FieldMetadata>> mergedPropertyLibrary = new Hashtable<String, Map<String, FieldMetadata>>();
	
    protected EntityManager entityManager;
	
	protected SessionFactory sessionFactory;
    
    protected EJB3ConfigurationDao ejb3ConfigurationDao;
    
    //@Resource(name = "blChangeSetDao")
    //protected ChangeSetDao changeSetDao;

	@Override
	public Class<? extends Serializable> getEntityClass() {
		throw new RuntimeException("Must supply the entity class to query and count method calls! Default entity not supported!");
	}
	
	public Serializable persist(Serializable entity) {
		entityManager.persist(entity);
		return entity;
	}
	
	public Serializable merge(Serializable entity) {
		return entityManager.merge(entity);
	}
	
	public void flush() {
		entityManager.flush();
	}
	
	public void detach(Serializable entity) {
		entityManager.detach(entity);
	}
	
	public void refresh(Serializable entity) {
		entityManager.refresh(entity);
	}
 	
	public Serializable retrieve(Class<?> entityClass, Object primaryKey) {
		return (Serializable) entityManager.find(entityClass, primaryKey);
	}
	
	public void remove(Serializable entity) {
		entityManager.remove(entity);
	}
	
	public void clear() {
		entityManager.clear();
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
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			entityName, 
			entityClasses, 
			(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
			persistencePerspective.getAdditionalNonPersistentProperties(), 
			persistencePerspective.getAdditionalForeignKeys(),
			MergedPropertyType.PRIMARY,
			persistencePerspective.getPopulateToOneFields(), 
			persistencePerspective.getIncludeFields(), 
			persistencePerspective.getExcludeFields(),
			null,
			""
		);
		return mergedProperties;
	}
	
	public Map<String, FieldMetadata> getMergedProperties(
		String ceilingEntityFullyQualifiedClassname, 
		Class<?>[] entities, 
		ForeignKey foreignField, 
		String[] additionalNonPersistentProperties, 
		ForeignKey[] additionalForeignFields, 
		MergedPropertyType mergedPropertyType, 
		Boolean populateManyToOneFields,
		String[] includeFields, 
		String[] excludeFields,
		Map<String, FieldMetadata> metadataOverrides,
		String prefix
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		//create a unique key for this inspection query
		StringBuffer sb = new StringBuffer();
		sb.append(ceilingEntityFullyQualifiedClassname);
		if (foreignField != null) {
			sb.append(foreignField.getManyToField());
		}
		if (additionalNonPersistentProperties != null) {
			for (String additionalNonPersistentProperty : additionalNonPersistentProperties) {
				sb.append(additionalNonPersistentProperty);
			}
		}
		if (additionalForeignFields != null) {
			for (ForeignKey foreignKey : additionalForeignFields) {
				sb.append(foreignKey.getManyToField());
			}
		}
		//TODO re-establish library check for release
		//if (!mergedPropertyLibrary.containsKey(sb.toString())) {
			Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
			buildPropertiesFromPolymorphicEntities(entities, foreignField, additionalNonPersistentProperties, additionalForeignFields, mergedPropertyType, populateManyToOneFields, includeFields, excludeFields, metadataOverrides, mergedProperties, prefix);
			mergedPropertyLibrary.put(sb.toString(), mergedProperties);
		//}
		return mergedPropertyLibrary.get(sb.toString());
	}

	protected void buildPropertiesFromPolymorphicEntities(
		Class<?>[] entities, 
		ForeignKey foreignField, 
		String[] additionalNonPersistentProperties, 
		ForeignKey[] additionalForeignFields, 
		MergedPropertyType mergedPropertyType, 
		Boolean populateManyToOneFields, 
		String[] includeFields, 
		String[] excludeFields, 
		Map<String, FieldMetadata> metadataOverrides, 
		Map<String, FieldMetadata> mergedProperties, 
		String prefix
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		for (Class<?> clazz : entities) {
			Map<String, FieldMetadata> props = getPropertiesForEntityClass(clazz, foreignField, additionalNonPersistentProperties, additionalForeignFields, mergedPropertyType, populateManyToOneFields, includeFields, excludeFields, prefix, metadataOverrides);
			//first check all the properties currently in there to see if my entity inherits from them
			for (Class<?> clazz2 : entities) {
				if (!clazz2.getName().equals(clazz.getName())) {
					for (String key: props.keySet()) {
						FieldMetadata metadata = props.get(key);
						if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz2)) {
							String[] both = (String[]) ArrayUtils.addAll(metadata.getAvailableToTypes(), new String[]{clazz2.getName()});
							metadata.setAvailableToTypes(both);
						}
					}
				}
			}
			mergedProperties.putAll(props);
		}
	}
	
	protected FieldMetadata getFieldMetadata(
		String prefix, 
		String propertyName, 
		Iterator<Property> componentProperties, 
		SupportedFieldType type, 
		Type entityType, 
		Class<?> targetClass, 
		FieldPresentationAttributes presentationAttribute, 
		MergedPropertyType mergedPropertyType, 
		Map<String, FieldMetadata> metadataOverrides
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return getFieldMetadata(prefix, propertyName, componentProperties, type, null, entityType, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides);
	}
	
	protected void overrideMetadata(Map<String, FieldMetadata> metadataOverrides, FieldMetadata serverMetadata, String propertyName) {
    	if (metadataOverrides != null && metadataOverrides.containsKey(propertyName)) {
    		FieldMetadata localMetadata = metadataOverrides.get(propertyName);
    		if (localMetadata.getCollection() != null) {
    			serverMetadata.setCollection(localMetadata.getCollection());
    		}
    		if (localMetadata.getMutable() != null) {
    			serverMetadata.setMutable(localMetadata.getMutable());
    		}
    		if (localMetadata.getRequired() != null) {
    			serverMetadata.setRequired(localMetadata.getRequired());
    		}
    		if (localMetadata.getUnique() != null) {
    			serverMetadata.setUnique(localMetadata.getUnique());
    		}
    		if (localMetadata.getAvailableToTypes() != null) {
    			serverMetadata.setAvailableToTypes(localMetadata.getAvailableToTypes());
    		}
    		if (localMetadata.getEnumerationValues() != null) {
    			serverMetadata.setEnumerationValues(localMetadata.getEnumerationValues());
    		}
    		if (localMetadata.getEnumerationClass() != null) {
    			serverMetadata.setEnumerationClass(localMetadata.getEnumerationClass());
    		}
    		if (localMetadata.getFieldType() != null) {
    			serverMetadata.setFieldType(localMetadata.getFieldType());
    		}
    		if (localMetadata.getForeignKeyClass() != null) {
    			serverMetadata.setForeignKeyClass(localMetadata.getForeignKeyClass());
    		}
    		if (localMetadata.getForeignKeyProperty() != null) {
    			serverMetadata.setForeignKeyProperty(localMetadata.getForeignKeyProperty());
    		}
    		if (localMetadata.getInheritedFromType() != null) {
    			serverMetadata.setInheritedFromType(localMetadata.getInheritedFromType());
    		}
    		if (localMetadata.getLength() != null) {
    			serverMetadata.setLength(localMetadata.getLength());
    		}
    		if (localMetadata.getMergedPropertyType() != null) {
    			serverMetadata.setMergedPropertyType(localMetadata.getMergedPropertyType());
    		}
    		if (localMetadata.getPrecision() != null) {
    			serverMetadata.setPrecision(localMetadata.getPrecision());
    		}
    		if (localMetadata.getScale() != null) {
    			serverMetadata.setScale(localMetadata.getScale());
    		}
    		if (localMetadata.getPresentationAttributes().isHidden() != null) {
    			serverMetadata.getPresentationAttributes().setHidden(localMetadata.getPresentationAttributes().isHidden());
    		}
    		if (localMetadata.getPresentationAttributes().isLargeEntry() != null) {
    			serverMetadata.getPresentationAttributes().setLargeEntry(localMetadata.getPresentationAttributes().isLargeEntry());
    		}
    		if (localMetadata.getPresentationAttributes().isProminent() != null) {
    			serverMetadata.getPresentationAttributes().setProminent(localMetadata.getPresentationAttributes().isProminent());
    		}
    		if (localMetadata.getPresentationAttributes().getBroadleafEnumeration() != null) {
    			serverMetadata.getPresentationAttributes().setBroadleafEnumeration(localMetadata.getPresentationAttributes().getBroadleafEnumeration());
    		}
    		if (localMetadata.getPresentationAttributes().getColumnWidth() != null) {
    			serverMetadata.getPresentationAttributes().setColumnWidth(localMetadata.getPresentationAttributes().getColumnWidth());
    		}
    		if (localMetadata.getPresentationAttributes().getExplicitFieldType() != null) {
    			serverMetadata.getPresentationAttributes().setExplicitFieldType(localMetadata.getPresentationAttributes().getExplicitFieldType());
    		}
    		if (localMetadata.getPresentationAttributes().getFriendlyName() != null) {
    			serverMetadata.getPresentationAttributes().setFriendlyName(localMetadata.getPresentationAttributes().getFriendlyName());
    		}
    		if (localMetadata.getPresentationAttributes().getGroup() != null) {
    			serverMetadata.getPresentationAttributes().setGroup(localMetadata.getPresentationAttributes().getGroup());
    		}
    		if (localMetadata.getPresentationAttributes().getName() != null) {
    			serverMetadata.getPresentationAttributes().setName(localMetadata.getPresentationAttributes().getName());
    		}
    		if (localMetadata.getPresentationAttributes().getOrder() != null) {
    			serverMetadata.getPresentationAttributes().setOrder(localMetadata.getPresentationAttributes().getOrder());
    		}
    		if (localMetadata.getPresentationAttributes().getGroupOrder() != null) {
    			serverMetadata.getPresentationAttributes().setGroupOrder(localMetadata.getPresentationAttributes().getGroupOrder());
    		}
    	}
    }
	
	protected FieldMetadata getFieldMetadata(
		String prefix, 
		String propertyName, 
		Iterator<Property> componentProperties, 
		SupportedFieldType type, 
		SupportedFieldType secondaryType, 
		Type entityType, 
		Class<?> targetClass, 
		FieldPresentationAttributes presentationAttribute, 
		MergedPropertyType mergedPropertyType, 
		Map<String, FieldMetadata> metadataOverrides
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		FieldMetadata fieldMetadata = new FieldMetadata();
		fieldMetadata.setFieldType(type);
		fieldMetadata.setSecondaryType(secondaryType);
		if (entityType != null && !entityType.isCollectionType()) {
			Column column = null;
			while (componentProperties.hasNext()) {
				Property property = (Property) componentProperties.next();
				if (property.getName().equals(propertyName)) {
					column = (Column) property.getColumnIterator().next();
					break;
				}
			}
			if (column != null) {
				fieldMetadata.setLength(column.getLength());
				fieldMetadata.setScale(column.getScale());
				fieldMetadata.setPrecision(column.getPrecision());
				fieldMetadata.setRequired(!column.isNullable());
				fieldMetadata.setUnique(column.isUnique());
			}
			fieldMetadata.setCollection(false);
		} else {
			fieldMetadata.setCollection(true);
		}
		fieldMetadata.setMutable(true);
		fieldMetadata.setInheritedFromType(targetClass.getName());
		fieldMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
		if (presentationAttribute != null) {
			fieldMetadata.setPresentationAttributes(presentationAttribute);
		}
		fieldMetadata.setMergedPropertyType(mergedPropertyType);
		if (SupportedFieldType.BROADLEAF_ENUMERATION.equals(type)) {
			Map<String, String> enumVals = new HashMap<String, String>();
			Class<?> broadleafEnumeration = Class.forName(presentationAttribute.getBroadleafEnumeration());
			Method typeMethod = broadleafEnumeration.getMethod("getType", new Class<?>[]{});
			Method friendlyTypeMethod = broadleafEnumeration.getMethod("getFriendlyType", new Class<?>[]{});
			Field[] fields = broadleafEnumeration.getFields();
			for (Field field : fields) {
				boolean isStatic = Modifier.isStatic(field.getModifiers());
				boolean isNameEqual = field.getType().getName().equals(broadleafEnumeration.getName());
				if (isStatic && isNameEqual){
					enumVals.put((String) typeMethod.invoke(field.get(null), new Object[]{}), (String) friendlyTypeMethod.invoke(field.get(null), new Object[]{}));
				}
			}
			String[][] enumerationValues = new String[enumVals.size()][2];
			int j = 0;
			for (String key : enumVals.keySet()) {
				enumerationValues[j][0] = key;
				enumerationValues[j][1] = enumVals.get(key);
				j++;
			}
			fieldMetadata.setEnumerationValues(enumerationValues);
			fieldMetadata.setEnumerationClass(presentationAttribute.getBroadleafEnumeration());
		}
		
		overrideMetadata(metadataOverrides, fieldMetadata, prefix + propertyName);
		
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
				attr.setSecurityLevel(annot.securityLevel());
				attr.setHidden(annot.hidden());
				attr.setOrder(annot.order());
				attr.setExplicitFieldType(annot.fieldType());
				attr.setGroup(annot.group());
				attr.setGroupOrder(annot.groupOrder());
				attr.setLargeEntry(annot.largeEntry());
				attr.setProminent(annot.prominent());
				attr.setColumnWidth(annot.columnWidth());
				attr.setBroadleafEnumeration(annot.broadleafEnumeration());
				attr.setReadOnly(annot.readOnly());
				if (annot.validationConfigurations().length != 0) {
					ValidationConfiguration[] configurations = annot.validationConfigurations();
					for (ValidationConfiguration configuration : configurations) {
						ConfigurationItem[] items = configuration.configurationItems();
						Map<String, String> itemMap = new HashMap<String, String>();
						for (ConfigurationItem item : items) {
							itemMap.put(item.itemName(), item.itemValue());
						}
						attr.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
					}
				}
				attributes.put(field.getName(), attr);
			}
		}
		return attributes;
	}
	
	public PersistentClass getPersistentClass(String targetClassName) {
		return ejb3ConfigurationDao.getConfiguration().getClassMapping(targetClassName);
	}
	
	public Map<String, FieldMetadata> getPropertiesForPrimitiveClass(
		String propertyName, 
		String friendlyPropertyName, 
		Class<?> targetClass, 
		Class<?> parentClass, 
		MergedPropertyType mergedPropertyType, 
		Map<String, FieldMetadata> metadataOverrides
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<String, FieldMetadata> fields = new HashMap<String, FieldMetadata>();
		FieldPresentationAttributes presentationAttribute = new FieldPresentationAttributes();
		presentationAttribute.setFriendlyName(friendlyPropertyName);
		if (String.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.STRING);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.STRING, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (Boolean.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.BOOLEAN);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.BOOLEAN, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (Date.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.DATE);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.DATE, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (Money.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.MONEY);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.MONEY, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (
				Byte.class.isAssignableFrom(targetClass) ||
				Integer.class.isAssignableFrom(targetClass) ||
				Long.class.isAssignableFrom(targetClass) ||
				Short.class.isAssignableFrom(targetClass)
			) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.INTEGER);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.INTEGER, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (
				Double.class.isAssignableFrom(targetClass) ||
				BigDecimal.class.isAssignableFrom(targetClass)
			) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.DECIMAL);
			presentationAttribute.setHidden(false);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.DECIMAL, null, parentClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		}
		fields.get(propertyName).setLength(255);
		fields.get(propertyName).setCollection(false);
		fields.get(propertyName).setRequired(true);
		fields.get(propertyName).setUnique(true);
		fields.get(propertyName).setScale(100);
		fields.get(propertyName).setPrecision(100);
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, FieldMetadata> getPropertiesForEntityClass(
		Class<?> targetClass, 
		ForeignKey foreignField, 
		String[] additionalNonPersistentProperties, 
		ForeignKey[] additionalForeignFields, 
		MergedPropertyType mergedPropertyType,
		Boolean populateManyToOneFields,
		String[] includeFields, 
		String[] excludeFields,
		String prefix,
		Map<String, FieldMetadata> metadataOverrides
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<String, FieldPresentationAttributes> presentationAttributes = getFieldPresentationAttributes(targetClass);
		ClassMetadata metadata = sessionFactory.getClassMetadata(targetClass);
		Map<String, FieldMetadata> fields = new HashMap<String, FieldMetadata>();
		List<String> propertyNames = new ArrayList<String>();
		String idProperty = metadata.getIdentifierPropertyName();
		for (String propertyName : metadata.getPropertyNames()) {
			propertyNames.add(propertyName);
		}
		propertyNames.add(idProperty);
		List<Type> propertyTypes = new ArrayList<Type>();
		Type idType = metadata.getIdentifierType();
		for (Type propertyType : metadata.getPropertyTypes()) {
			propertyTypes.add(propertyType);
		}
		propertyTypes.add(idType);
		
		PersistentClass persistentClass = getPersistentClass(targetClass.getName());
		Iterator<Property> iter = persistentClass.getPropertyIterator();
		
		//check the properties for problems
		while(iter.hasNext()) {
			Property property = iter.next();
			if (property.getName().indexOf(".") >= 0) {
				throw new IllegalArgumentException("Properties from entities that utilize a period character ('.') in their name are incompatible with this system. The property name in question is: (" + property.getName() + ") from the class: (" + targetClass.getName() + ")");
			}
		}
		
		iter = persistentClass.getPropertyIterator();
		
		buildProperties(
			targetClass, 
			foreignField, 
			additionalForeignFields, 
			additionalNonPersistentProperties,
			mergedPropertyType, 
			presentationAttributes, 
			iter, 
			metadata, 
			fields, 
			propertyNames, 
			propertyTypes, 
			idProperty, 
			populateManyToOneFields,
			includeFields, 
			excludeFields,
			prefix,
			metadataOverrides
		);
		FieldPresentationAttributes presentationAttribute = new FieldPresentationAttributes();
		presentationAttribute.setExplicitFieldType(SupportedFieldType.STRING);
		presentationAttribute.setHidden(true);
		if (additionalNonPersistentProperties != null) {
			for (String additionalNonPersistentProperty : additionalNonPersistentProperties) {
				fields.put(additionalNonPersistentProperty, getFieldMetadata(prefix, additionalNonPersistentProperty, iter, SupportedFieldType.STRING, null, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			}
		}
		
		return fields;
	}

	protected void buildProperties(
		Class<?> targetClass, 
		ForeignKey foreignField, 
		ForeignKey[] additionalForeignFields, 
		String[] additionalNonPersistentProperties,
		MergedPropertyType mergedPropertyType, 
		Map<String, FieldPresentationAttributes> presentationAttributes, 
		Iterator<Property> propertyIterator, 
		ClassMetadata metadata, 
		Map<String, FieldMetadata> fields, 
		List<String> propertyNames, 
		List<Type> propertyTypes, 
		String idProperty, 
		Boolean populateManyToOneFields, 
		String[] includeFields, 
		String[] excludeFields,
		String prefix,
		Map<String, FieldMetadata> metadataOverrides
	) throws HibernateException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		int j = 0;
		for (String propertyName : propertyNames) {
			Type type = propertyTypes.get(j);
			boolean isPropertyForeignKey = checkForeignProperty(foreignField, prefix, propertyName);
			int additionalForeignKeyIndexPosition = findAdditionalForeignKeyIndex(additionalForeignFields, prefix, propertyName);
			//Boolean isLazy = !propertyName.equals(idProperty) && metadata.getPropertyLaziness()[j];
			j++;
			if (
					(!type.isAnyType() && !type.isCollectionType()) || 
					isPropertyForeignKey ||
					additionalForeignKeyIndexPosition >= 0 ||
					presentationAttributes.containsKey(propertyName)
					//(type.isEntityType() && !isLazy)
			) {
				Boolean includeField = testFieldInclusion(includeFields, excludeFields, prefix, propertyName, isPropertyForeignKey, additionalForeignKeyIndexPosition);
				
				FieldPresentationAttributes presentationAttribute = presentationAttributes.get(propertyName);
				SupportedFieldType explicitType = null;
				if (presentationAttribute != null) {
					explicitType = presentationAttribute.getExplicitFieldType();
				}
				Class<?> returnedClass = type.getReturnedClass();
				if (type.isComponentType() && includeField) {
					buildComponentProperties(targetClass, foreignField, additionalForeignFields, additionalNonPersistentProperties, mergedPropertyType, metadata, fields, idProperty, populateManyToOneFields, includeFields, excludeFields, propertyName, type, returnedClass, metadataOverrides);
					continue;
				}
				/*
				 * Currently we do not support ManyToOne fields whose class type is the same
				 * as the target type, since this forms an infinite loop and will cause a stack overflow.
				 */
				if (
					type.isEntityType() && 
					!returnedClass.equals(targetClass) && 
					populateManyToOneFields &&
					includeField
				) {
					buildEntityProperties(fields, foreignField, additionalForeignFields, additionalNonPersistentProperties, populateManyToOneFields, includeFields, excludeFields, propertyName, returnedClass, targetClass, prefix, metadataOverrides);
					continue;
				}
				//Don't include this property if it failed manyToOne inclusion and is not a specified foreign key
				if (includeField || isPropertyForeignKey || additionalForeignKeyIndexPosition >= 0) {
					buildProperty(targetClass, foreignField, additionalForeignFields, mergedPropertyType, propertyIterator, fields, idProperty, prefix, metadataOverrides, propertyName, type, isPropertyForeignKey, additionalForeignKeyIndexPosition, presentationAttribute, explicitType, returnedClass);
				}
			}
		}
	}

	protected void buildProperty(
		Class<?> targetClass, 
		ForeignKey foreignField, 
		ForeignKey[] additionalForeignFields, 
		MergedPropertyType mergedPropertyType, 
		Iterator<Property> propertyIterator, 
		Map<String, FieldMetadata> fields, 
		String idProperty, 
		String prefix, 
		Map<String, 
		FieldMetadata> metadataOverrides, 
		String propertyName, 
		Type type, 
		boolean isPropertyForeignKey, 
		int additionalForeignKeyIndexPosition, 
		FieldPresentationAttributes presentationAttribute, 
		SupportedFieldType explicitType, 
		Class<?> returnedClass
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (explicitType != null && explicitType.equals(SupportedFieldType.BROADLEAF_ENUMERATION)) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.BROADLEAF_ENUMERATION, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if ((explicitType != null && explicitType.equals(SupportedFieldType.BOOLEAN)) || returnedClass.equals(Boolean.class) || returnedClass.equals(Character.class)) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.BOOLEAN, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (
				(explicitType != null && explicitType.equals(SupportedFieldType.INTEGER)) || 
				returnedClass.equals(Byte.class) || returnedClass.equals(Short.class) || returnedClass.equals(Integer.class) || returnedClass.equals(Long.class)
		) {
			if (propertyName.equals(idProperty)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.ID, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			}
		} else if (
				(explicitType != null && explicitType.equals(SupportedFieldType.DATE)) || 
				returnedClass.equals(Calendar.class) || returnedClass.equals(Date.class) || returnedClass.equals(Timestamp.class)
		) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.DATE, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (explicitType != null && explicitType.equals(SupportedFieldType.PASSWORD)) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.PASSWORD, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (
				(explicitType != null && explicitType.equals(SupportedFieldType.STRING)) || returnedClass.equals(String.class)
		) {
			if (propertyName.equals(idProperty)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.ID, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			}
		} else if ((explicitType != null && explicitType.equals(SupportedFieldType.MONEY)) || returnedClass.equals(Money.class)) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.MONEY, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if (
				(explicitType != null && explicitType.equals(SupportedFieldType.DECIMAL)) || 
				returnedClass.equals(Double.class) || returnedClass.equals(BigDecimal.class)
		) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.DECIMAL, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
		} else if ((explicitType != null && explicitType.equals(SupportedFieldType.FOREIGN_KEY)) || foreignField != null && isPropertyForeignKey) {
			ClassMetadata foreignMetadata;
			foreignMetadata = sessionFactory.getClassMetadata(Class.forName(foreignField.getForeignKeyClass()));
			Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
			if (foreignResponseType.equals(String.class)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.FOREIGN_KEY, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.FOREIGN_KEY, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			}
			fields.get(propertyName).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
			//fields.get(propertyName).setComplexType(returnedClass.getName());
			fields.get(propertyName).setForeignKeyClass(foreignField.getForeignKeyClass());
			fields.get(propertyName).setForeignKeyDisplayValueProperty(foreignField.getDisplayValueProperty());
		} else if ((explicitType != null && explicitType.equals(SupportedFieldType.ADDITIONAL_FOREIGN_KEY)) || additionalForeignFields != null && additionalForeignKeyIndexPosition >= 0) {
			ClassMetadata foreignMetadata;
			foreignMetadata = sessionFactory.getClassMetadata(Class.forName(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass()));
			Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
			if (foreignResponseType.equals(String.class)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, propertyIterator, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType, metadataOverrides));
			}
			fields.get(propertyName).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
			fields.get(propertyName).setForeignKeyClass(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass());
			fields.get(propertyName).setForeignKeyDisplayValueProperty(additionalForeignFields[additionalForeignKeyIndexPosition].getDisplayValueProperty());
		}
		//return type not supported - just skip this property
	}

	protected Boolean testFieldInclusion(String[] includeFields, String[] excludeFields, String prefix, String propertyName, boolean isPropertyForeignKey, int additionalForeignKeyIndexPosition) {
		//Test if this property is on the excluded or included list
		Boolean includeField = !isPropertyForeignKey && additionalForeignKeyIndexPosition < 0 && checkPropertyForInclusion(includeFields, excludeFields, prefix + propertyName);
		if (includeField) {
			//check to make sure we're not locked into an infinite recursion
			if (!StringUtils.isEmpty(prefix) && prefix.contains(propertyName)) {
				int start = 0;
				int count = 0;
				boolean eof = false;
				while (!eof) {
					int pos = prefix.indexOf(propertyName, start);
					if (pos >= 0) {
						start += pos + propertyName.length();
						count++;
						if (count > 6) {
							includeField = false;
							eof = true;
						}
					} else {
						eof = true;
					}
				}
			}
		}
		return includeField;
	}

	protected int findAdditionalForeignKeyIndex(ForeignKey[] additionalForeignFields, String prefix, String propertyName) {
		int additionalForeignKeyIndexPosition = -1;
		if (additionalForeignFields != null) {
			additionalForeignKeyIndexPosition = Arrays.binarySearch(additionalForeignFields, new ForeignKey(prefix + propertyName, null, null), new Comparator<ForeignKey>() {
				public int compare(ForeignKey o1, ForeignKey o2) {
					return o1.getManyToField().compareTo(o2.getManyToField());
				}
			});
		}
		return additionalForeignKeyIndexPosition;
	}

	protected boolean checkForeignProperty(ForeignKey foreignField, String prefix, String propertyName) {
		boolean isPropertyForeignKey = false;
		if (foreignField != null) {
			isPropertyForeignKey = foreignField.getManyToField().equals(prefix + propertyName);
		}
		return isPropertyForeignKey;
	}

	protected void buildEntityProperties(
		Map<String, FieldMetadata> fields, 
		ForeignKey foreignField, 
		ForeignKey[] additionalForeignFields, 
		String[] additionalNonPersistentProperties, 
		Boolean populateManyToOneFields, 
		String[] includeFields, 
		String[] excludeFields, 
		String propertyName, 
		Class<?> returnedClass, 
		Class<?> targetClass, 
		String prefix, 
		Map<String, FieldMetadata> metadataOverrides
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] polymorphicEntities = getAllPolymorphicEntitiesFromCeiling(returnedClass);
		Map<String, FieldMetadata> newFields = getMergedProperties(returnedClass.getName(), polymorphicEntities, foreignField, additionalNonPersistentProperties, additionalForeignFields, MergedPropertyType.PRIMARY, populateManyToOneFields, includeFields, excludeFields, metadataOverrides, prefix + propertyName + ".");
		for (FieldMetadata newMetadata : newFields.values()) {
			newMetadata.setInheritedFromType(targetClass.getName());
			newMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
		}
		Map<String, FieldMetadata> convertedFields = new HashMap<String, FieldMetadata>();
		for (String key : newFields.keySet()) {
			convertedFields.put(propertyName + "." + key, newFields.get(key));
		}
		fields.putAll(convertedFields);
	}

	protected void buildComponentProperties(
		Class<?> targetClass, 
		ForeignKey foreignField, 
		ForeignKey[] additionalForeignFields, 
		String[] additionalNonPersistentProperties, 
		MergedPropertyType mergedPropertyType, 
		ClassMetadata metadata, 
		Map<String, FieldMetadata> fields, 
		String idProperty, 
		Boolean populateManyToOneFields, 
		String[] includeFields, 
		String[] excludeFields, 
		String propertyName, 
		Type type, 
		Class<?> returnedClass, 
		Map<String, FieldMetadata> metadataOverrides
	) throws MappingException, HibernateException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String[] componentProperties = ((ComponentType) type).getPropertyNames();
		List<String> componentPropertyNames = new ArrayList<String>();
		for (String componentProperty : componentProperties) {
			componentPropertyNames.add(componentProperty);
		}
		Type[] componentTypes = ((ComponentType) type).getSubtypes();
		List<Type> componentPropertyTypes = new ArrayList<Type>();
		for (Type componentType : componentTypes) {
			componentPropertyTypes.add(componentType);
		}
		Map<String, FieldPresentationAttributes> componentPresentationAttributes = getFieldPresentationAttributes(returnedClass);
		PersistentClass persistentClass = getPersistentClass(targetClass.getName());
		@SuppressWarnings("unchecked")
		Iterator<Property> componentPropertyIterator = ((Component) persistentClass.getProperty(propertyName).getValue()).getPropertyIterator();
		Map<String, FieldMetadata> newFields = new HashMap<String, FieldMetadata>();
		buildProperties(
			targetClass, 
			foreignField, 
			additionalForeignFields, 
			additionalNonPersistentProperties,
			mergedPropertyType,  
			componentPresentationAttributes, 
			componentPropertyIterator, 
			metadata, 
			newFields, 
			componentPropertyNames, 
			componentPropertyTypes, 
			idProperty, 
			populateManyToOneFields,
			includeFields,
			excludeFields,
			propertyName + ".",
			metadataOverrides
		);
		Map<String, FieldMetadata> convertedFields = new HashMap<String, FieldMetadata>();
		for (String key : newFields.keySet()) {
			convertedFields.put(propertyName + "." + key, newFields.get(key));
		}
		fields.putAll(convertedFields);
	}

	public Boolean checkPropertyForInclusion(String[] includeFields, String[] excludeFields, String propertyName) {
		if (!ArrayUtils.isEmpty(includeFields)) {
			Boolean includeManyToOneField = Arrays.binarySearch(includeFields, propertyName) >= 0;
			/*
			 * check to see if a parent prefix for this property has already been designated
			 * as included
			 */
			if (!includeManyToOneField) {
				StringTokenizer tokens = new StringTokenizer(propertyName, ".");
				while (tokens.hasMoreElements()) {
					includeManyToOneField = Arrays.binarySearch(includeFields, tokens.nextToken()) >= 0;
					if (includeManyToOneField) {
						break;
					}
				}
				if (!includeManyToOneField) {
					for(String field : includeFields) {
						includeManyToOneField = field.contains(propertyName);
						if (includeManyToOneField) {
							break;
						}
					}
				}
			}
			return includeManyToOneField;
		}
		if (!ArrayUtils.isEmpty(excludeFields)) {
			return !(Arrays.binarySearch(excludeFields, propertyName) >= 0);
		}
		return true;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public EJB3ConfigurationDao getEjb3ConfigurationDao() {
		return ejb3ConfigurationDao;
	}

	public void setEjb3ConfigurationDao(EJB3ConfigurationDao ejb3ConfigurationDao) {
		this.ejb3ConfigurationDao = ejb3ConfigurationDao;
	}
    
}
