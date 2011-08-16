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
package org.broadleafcommerce.gwt.server.service.module;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKeyRestrictionType;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.*;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.service.remote.DynamicEntityRemoteService;
import org.broadleafcommerce.money.Money;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.DOMException;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author jfischer
 *
 */
public class BasicServerEntityModule implements RemoteServiceModule, RecordHelper, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(BasicServerEntityModule.class);
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	protected DecimalFormat decimalFormat = new DecimalFormat("0.########");
	
	@Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;
	
	protected ApplicationContext applicationContext;
	
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();
	protected DynamicEntityRemoteService dynamicEntityRemoteService;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public boolean isCompatible(OperationType operationType) {
		return OperationType.ENTITY.equals(operationType) || OperationType.FOREIGNKEY.equals(operationType);
	}
	
	public FieldManager getFieldManager() {
		return (FieldManager) applicationContext.getBean("blFieldManager");
	}
	
	@SuppressWarnings("unchecked")
	public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException {
		FieldManager fieldManager = getFieldManager();
		for (Property property : entity.getProperties()) {
			Field field = fieldManager.getField(instance.getClass(), property.getName());
			if (field == null) {
				LOG.debug("Unable to find a bean property for the reported property: " + property.getName() + ". Ignoring property.");
				continue;
			}
			Class<?> returnType = field.getType();
			String value = property.getValue();
			if (mergedProperties.get(property.getName()) != null) {
				if (value != null) {
					switch(mergedProperties.get(property.getName()).getFieldType()) {
					case BOOLEAN :
						if (Character.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), Boolean.valueOf(value)?'Y':'N');
						} else {
							fieldManager.setFieldValue(instance, property.getName(), Boolean.valueOf(value));
						}
						break;
					case DATE :
						fieldManager.setFieldValue(instance, property.getName(), dateFormat.parse(value));
						break;
					case DECIMAL :
						if (BigDecimal.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
						} else {
							fieldManager.setFieldValue(instance, property.getName(), new Double(value));
						}
						break;
					case MONEY :
						if (BigDecimal.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
						} else if (Double.class.isAssignableFrom(returnType)){
							fieldManager.setFieldValue(instance, property.getName(), new Double(value));
						} else {
							fieldManager.setFieldValue(instance, property.getName(), new Money(new Double(value)));
						}
						break;
					case INTEGER :
						if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), Integer.valueOf(value));
						} else if (byte.class.isAssignableFrom(returnType) || Byte.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), Byte.valueOf(value));
						} else if (short.class.isAssignableFrom(returnType) || Short.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), Short.valueOf(value));
						} else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
							fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
						}
						break;
					default :
						fieldManager.setFieldValue(instance, property.getName(), value);
						break;
					case EMAIL :
						fieldManager.setFieldValue(instance, property.getName(), value);
						break;
					case FOREIGN_KEY :{
						Serializable foreignInstance;
						if (SupportedFieldType.INTEGER.toString().equals(mergedProperties.get(property.getName()).getSecondaryType().toString())) {
							foreignInstance = dynamicEntityDao.retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), Long.valueOf(value));
						} else {
							foreignInstance = dynamicEntityDao.retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), value);
						}
						
						if (Collection.class.isAssignableFrom(returnType)) {
							@SuppressWarnings("rawtypes")
							Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
							if (!collection.contains(foreignInstance)){
								collection.add(foreignInstance);
							}
						} else if (Map.class.isAssignableFrom(returnType)) {
							throw new RuntimeException("Map structures are not supported for foreign key fields.");
						} else {
							fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
						}
						break;
					}
					case ADDITIONAL_FOREIGN_KEY :{
						Serializable foreignInstance;
						if (SupportedFieldType.INTEGER.toString().equals(mergedProperties.get(property.getName()).getSecondaryType().toString())) {
							foreignInstance = dynamicEntityDao.retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), Long.valueOf(value));
						} else {
							foreignInstance = dynamicEntityDao.retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), value);
						}
						
						if (Collection.class.isAssignableFrom(returnType)) {
							@SuppressWarnings("rawtypes")
							Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
							if (!collection.contains(foreignInstance)){
								collection.add(foreignInstance);
							}
						} else if (Map.class.isAssignableFrom(returnType)) {
							throw new RuntimeException("Map structures are not supported for foreign key fields.");
						} else {
							fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
						}
						break;
					}
					case ID :
						if (setId) {
							switch(mergedProperties.get(property.getName()).getSecondaryType()) {
							case INTEGER:
								fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
								break;
							case STRING:
								fieldManager.setFieldValue(instance, property.getName(), value);
								break;
							}
						}
						break;
					}
				} else {
					if (fieldManager.getFieldValue(instance, property.getName()) != null && (!mergedProperties.get(property.getName()).getFieldType().equals(SupportedFieldType.ID) || setId)) {
						fieldManager.setFieldValue(instance, property.getName(), null);
					}
				}
			}
		}
		fieldManager.persistMiddleEntities();
		return instance;
	}
	
	public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
		List<Serializable> records = new ArrayList<Serializable>();
		records.add(record);
		Entity[] productEntities = getRecords(primaryMergedProperties, records, alternateMergedProperties, pathToTargetObject);
		return productEntities[0];
	}
	
	public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityClass);
		Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
		Entity entity = getRecord(mergedProperties, record, null, null);
		
		return entity;
	}
	
	public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityClass);
		Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
		Entity[] entities = getRecords(mergedProperties, records, null, null);
		
		return entities;
	}
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return dynamicEntityDao.getSimpleMergedProperties(entityName, persistencePerspective, dynamicEntityDao, entityClasses);
	}

	public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
		String idProperty = null;
		getIdProperty:{
			if (alternateMergedProperties != null) {
				for (String property : alternateMergedProperties.keySet()) {
					if (alternateMergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
						idProperty = property;
						break getIdProperty;
					}
				}
			}
			for (String property : primaryMergedProperties.keySet()) {
				if (primaryMergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
					idProperty = property;
					break getIdProperty;
				}
			}
		}
       
		Entity[] entities = new Entity[records.size()];
		int j = 0;
		for (Serializable recordEntity : records) {
			Serializable entity;
			if (pathToTargetObject != null) {
				entity = (Serializable) getFieldManager().getFieldValue(recordEntity, pathToTargetObject);
			} else {
				entity = recordEntity;
			}
			Entity entityItem = new Entity();
			entityItem.setType(new String[]{entity.getClass().getName()});
			entities[j] = entityItem;
			
			List<Property> props = new ArrayList<Property>();
	        extractPropertiesFromPersistentEntity(primaryMergedProperties, idProperty, entity, props);
	        if (alternateMergedProperties != null) {
	        	extractPropertiesFromPersistentEntity(alternateMergedProperties, null, recordEntity, props);
	        }
	        Property[] properties = new Property[props.size()];
	        properties = props.toArray(properties);
	        entityItem.setProperties(properties);
	        j++;
		}
        
		return entities;
	}
	
	protected void extractPropertiesFromPersistentEntity(Map<String, FieldMetadata> mergedProperties, String idProperty, Serializable entity, List<Property> props) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException {
		FieldManager fieldManager = getFieldManager();
		for (String property : mergedProperties.keySet()) {
			FieldMetadata metadata = mergedProperties.get(property);
			String originalProperty = new String(property);
			if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(entity.getClass())) {
				boolean proceed = true;
				if (property.indexOf(".") >= 0) {
					StringTokenizer tokens = new StringTokenizer(property, ".");
					Object testObject = entity;
					while(tokens.hasMoreTokens()) {
						String token = tokens.nextToken();
						if (tokens.hasMoreTokens()) {
							testObject = fieldManager.getFieldValue(testObject, token);
							if (testObject == null) {
								Property propertyItem = new Property();
								propertyItem.setName(originalProperty);
								if (props.contains(propertyItem)) {
									proceed = false;
									break;
								}
								propertyItem.setValue(null);
								props.add(propertyItem);
								proceed = false;
								break;
							}
						}
					}
				}
				if (!proceed) {
					continue;
				}

				boolean isFieldAccessible = true;
				Object value = null;
				try {
					value = fieldManager.getFieldValue(entity, property);
				} catch (Exception e1) {
					isFieldAccessible = false;
				}
				if (isFieldAccessible) {
					Property propertyItem = new Property();
					propertyItem.setName(originalProperty);
					if (props.contains(propertyItem)) {
						continue;
					}
			    	props.add(propertyItem);
			    	String strVal;
			    	String displayVal = null;
			    	if (value == null) {
			    		strVal = null;
			    	} else {
			    		if (metadata.getCollection()) {
			    			propertyItem.getMetadata().setFieldType(metadata.getFieldType());
			    			strVal = null;
			    		} else if (Date.class.isAssignableFrom(value.getClass())) {
			        		strVal = dateFormat.format((Date) value);
			        	} else if (Timestamp.class.isAssignableFrom(value.getClass())) {
			        		strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
			        	} else if (Calendar.class.isAssignableFrom(value.getClass())) {
			        		strVal = dateFormat.format(((Calendar) value).getTime());
			        	} else if (Double.class.isAssignableFrom(value.getClass())) {
			        		strVal = decimalFormat.format((Double) value);
			        	} else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
			        		strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
			        	//} else if (entity.getClass().equals(value.getClass()) && idProperty != null){
			        		//strVal = fieldManager.getFieldValue(value, idProperty).toString();
			        	} else if (metadata.getForeignKeyClass() != null) {
			        		strVal = fieldManager.getFieldValue(value, metadata.getForeignKeyProperty()).toString();
			        		//see if there's a name property and use it for the display value
			        		Object temp = fieldManager.getFieldValue(value, metadata.getForeignKeyDisplayValueProperty());
			        		if (temp != null) {
			        			displayVal = temp.toString();
			        		}
			        	} else {
			        		strVal = value.toString();
			        	}
			    	}
			    	propertyItem.setValue(strVal);
			    	propertyItem.setDisplayValue(displayVal);
				} else {
					//try a direct property acquisition via reflection
					try {
						Method method;
						try {
							//try a 'get' prefixed mutator first
							String temp = new String(originalProperty);
							temp = "get" + originalProperty.substring(0, 1).toUpperCase() + originalProperty.substring(1, originalProperty.length());
							method = entity.getClass().getMethod(temp, new Class[]{});
						} catch (NoSuchMethodException e) {
							method = entity.getClass().getMethod(originalProperty, new Class[]{});
						}
						value = method.invoke(entity, new Object[]{});
						Property propertyItem = new Property();
						propertyItem.setName(originalProperty);
						if (props.contains(propertyItem)) {
							continue;
						}
						props.add(propertyItem);
						String strVal;
						if (value == null) {
							strVal = null;
						} else {
							if (Date.class.isAssignableFrom(value.getClass())) {
								strVal = dateFormat.format((Date) value);
							} else if (Timestamp.class.isAssignableFrom(value.getClass())) {
								strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
							} else if (Calendar.class.isAssignableFrom(value.getClass())) {
								strVal = dateFormat.format(((Calendar) value).getTime());
							} else if (Double.class.isAssignableFrom(value.getClass())) {
								strVal = decimalFormat.format((Double) value);
							} else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
								strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
							} else {
								strVal = value.toString();
							}
						}
						propertyItem.setValue(strVal);
					} catch (NoSuchMethodException e) {
						LOG.debug("Unable to find a specified property in the entity: " + originalProperty);
						//do nothing - this property is simply not in the bean
					}
				}
			}
		}
	}
	
	protected Entity update(Entity entity, Object primaryKey, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		try {
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleUpdate(entity.getType()[0], customCriteria)) {
					return handler.update(entity, persistencePerspective, customCriteria, dynamicEntityDao, this);
				}
			}
			
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				entity.getType()[0], 
				entities, 
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
			if (primaryKey == null) {
				primaryKey = getPrimaryKey(entity, mergedProperties);
			}
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			//dynamicEntityDao.clear();
			//Object[] previousState = changeSetDao.getState(entity.getType(), instance);
			instance = createPopulatedInstance(instance, entity, mergedProperties, false);
			instance = dynamicEntityDao.merge(instance);
			//Object[] currentState = changeSetDao.getState(entity.getType(), instance);
			//changeSetDao.saveChangeSet(entity.getType(), instance, (Serializable) primaryKey, currentState, previousState);
			
			List<Serializable> entityList = new ArrayList<Serializable>();
			entityList.add(instance);
			
			return getRecords(mergedProperties, entityList, null, null)[0];
		} catch (ServiceException e) {
			LOG.error("Problem editing entity", e);
			throw e;
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
		}
	}

	public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedProperties) throws RuntimeException, NumberFormatException {
		Object primaryKey = null;
		String idProperty = null;
		for (String property : mergedProperties.keySet()) {
			if (mergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID) && property.indexOf(".") < 0) {
				idProperty = property;
				break;
			}
		}
		if (idProperty == null) {
			throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType());
		}
		for (Property property : entity.getProperties()) {
			if (property.getName().equals(idProperty)) {
				switch(property.getMetadata().getSecondaryType()) {
				case INTEGER:
					primaryKey = Long.valueOf(property.getValue());
					break;
				case STRING:
					primaryKey = property.getValue();
					break;
				}
				break;
			}
		}
		if (primaryKey == null) {
			throw new RuntimeException("Could not find the primary key property (" + idProperty + ") in the passed entity with type: " + entity.getType());
		}
		return primaryKey;
	}
	
	public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties) {
		BaseCtoConverter ctoConverter = new BaseCtoConverter();
		for (String propertyName : mergedProperties.keySet()) {
			AssociationPath associationPath;
			int dotIndex = propertyName.lastIndexOf(".");
			String property;
			if (dotIndex >= 0) {
				property = propertyName.substring(dotIndex + 1, propertyName.length());
				String prefix = propertyName.substring(0, dotIndex);
				StringTokenizer tokens = new StringTokenizer(prefix, ".");
				AssociationPathElement[] elements = new AssociationPathElement[tokens.countTokens()];
				int j = 0;
				while (tokens.hasMoreElements()) {
					elements[j] = new AssociationPathElement(tokens.nextToken());
					j++;
				}
				associationPath = new AssociationPath(elements);
			} else {
				property = propertyName;
				associationPath = AssociationPath.ROOT;
			}
			switch(mergedProperties.get(propertyName).getFieldType()) {
			case BOOLEAN :
				ctoConverter.addBooleanMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case DATE :
				ctoConverter.addDateMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case DECIMAL :
				ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case MONEY :
				ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case INTEGER :
				ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			default :
				ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case EMAIL :
				ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
				break;
			case FOREIGN_KEY :
				if (cto.get(propertyName).getFilterValues().length > 0) {
					ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
					if (mergedProperties.get(propertyName).getCollection()) {
						if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
							ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
						} else {
							AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
							ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
						}
					} else if (cto.get(propertyName).getFilterValues()[0] == null || cto.get(propertyName).getFilterValues()[0].equals("null")){
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
					} else {
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
					}
				}
				break;
			case ADDITIONAL_FOREIGN_KEY :
				if (cto.get(propertyName).getFilterValues().length > 0) {
					int additionalForeignKeyIndexPosition = -1;
					additionalForeignKeyIndexPosition = Arrays.binarySearch(persistencePerspective.getAdditionalForeignKeys(), new ForeignKey(propertyName, null, null), new Comparator<ForeignKey>() {
						public int compare(ForeignKey o1, ForeignKey o2) {
							return o1.getManyToField().compareTo(o2.getManyToField());
						}
					});
					ForeignKey foreignKey = persistencePerspective.getAdditionalForeignKeys()[additionalForeignKeyIndexPosition];
					if (mergedProperties.get(propertyName).getCollection()) {
						if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
							ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
						} else {
							AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
							ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
						}
					} else if (cto.get(propertyName).getFilterValues()[0] == null || cto.get(propertyName).getFilterValues()[0].equals("null")){
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
					} else {
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getForeignKeyProperty());
					}
				}
				break;
			case ID :
				switch(mergedProperties.get(propertyName).getSecondaryType()) {
				case INTEGER:
					ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
					break;
				case STRING:
					ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, property);
					break;
				}
				break;
			}
		}
		return ctoConverter;
	}
	
	public int getTotalRecords(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
		PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), ceilingEntityFullyQualifiedClassname);
        int totalRecords = dynamicEntityDao.count(countCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
		return totalRecords;
	}
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
		extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.PRIMARY), properties, false);
	}
	
	protected void extractPropertiesFromMetadata(Map<String, FieldMetadata> mergedProperties, List<Property> properties, Boolean isHiddenOverride) throws NumberFormatException {
		for (String property : mergedProperties.keySet()) {
			Property prop = new Property();
			FieldMetadata metadata = mergedProperties.get(property);
			prop.setName(property);
			if (properties.contains(prop)) {
				continue;
			}
			properties.add(prop);
			prop.setMetadata(metadata);
			if (isHiddenOverride) {
				prop.getMetadata().getPresentationAttributes().setHidden(true);
			}
		}
	}
	
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties, Map<String, FieldMetadata> metadataOverrides) throws ServiceException {
		try{
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
				metadataOverrides,
				""
			);
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		return update(entity, null, persistencePerspective, customCriteria);
	}

	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		try {
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleAdd(entity.getType()[0], customCriteria)) {
					Entity response = handler.add(entity, persistencePerspective, customCriteria, dynamicEntityDao, this);
					return response;
				}
			}
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				entity.getType()[0], 
				entities, 
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
			
			String idProperty = null;
			for (String property : mergedProperties.keySet()) {
				if (mergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
					idProperty = property;
					break;
				}
			}
			if (idProperty == null) {
				throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType());
			}
			Object primaryKey = null;
			try {
				primaryKey = getPrimaryKey(entity, mergedProperties);
			} catch (Exception e) {
				//do nothing
			}
			if (primaryKey == null) {
				Serializable instance = (Serializable) Class.forName(entity.getType()[0]).newInstance();
				instance = createPopulatedInstance(instance, entity, mergedProperties, false);
				instance = dynamicEntityDao.persist(instance);
				List<Serializable> entityList = new ArrayList<Serializable>();
				entityList.add(instance);
				
				return getRecords(mergedProperties, entityList, null, null)[0];
			} else {
				return update(entity, primaryKey, persistencePerspective, customCriteria);
			}
		} catch (ServiceException e) {
			LOG.error("Problem adding new entity", e);
			throw e;
		} catch (Exception e) {
			LOG.error("Problem adding new entity", e);
			throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
		} 
	}
	
	@SuppressWarnings("rawtypes")
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		try {
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleRemove(entity.getType()[0], customCriteria)) {
					handler.remove(entity, persistencePerspective, customCriteria, dynamicEntityDao, this);
					return;
				}
			}
			
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				entity.getType()[0], 
				entities, 
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
			Object primaryKey = getPrimaryKey(entity, mergedProperties);
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			
			switch(persistencePerspective.getOperationTypes().getRemoveType()) {
			case FOREIGNKEY:
				for (Property property : entity.getProperties()) {
					String originalPropertyName = new String(property.getName());
					FieldManager fieldManager = getFieldManager();
					if (fieldManager.getField(instance.getClass(), property.getName()) == null) {
						LOG.debug("Unable to find a bean property for the reported property: " + originalPropertyName + ". Ignoring property.");
						continue;
					}
					if (SupportedFieldType.FOREIGN_KEY.equals(mergedProperties.get(originalPropertyName).getFieldType())) {
						String value = property.getValue();
						ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
						Serializable foreignInstance = dynamicEntityDao.retrieve(Class.forName(foreignKey.getForeignKeyClass()), Long.valueOf(value));
						Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
						collection.remove(foreignInstance);
						break;
					}
				}
				break;
			case ENTITY:
				dynamicEntityDao.remove(instance);
				break;
			}
		} catch (ServiceException e) {
			LOG.error("Problem removing entity", e);
			throw e;
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		try {
			Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
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
			
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleFetch(ceilingEntityFullyQualifiedClassname, customCriteria)) {
					DynamicResultSet results = handler.fetch(ceilingEntityFullyQualifiedClassname, persistencePerspective, cto, customCriteria, dynamicEntityDao, this);
					return results;
				}
			}
			
			BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
			
			payload = getRecords(mergedProperties, records, null, null);
			totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
		} catch (ServiceException e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw e;
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
	
	public List<CustomPersistenceHandler> getCustomPersistenceHandlers() {
		return customPersistenceHandlers;
	}

	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers) {
		this.customPersistenceHandlers = customPersistenceHandlers;
	}

	public void setDynamicEntityRemoteService(DynamicEntityRemoteService dynamicEntityRemoteService) {
		this.dynamicEntityRemoteService = dynamicEntityRemoteService;
	}
	
}
