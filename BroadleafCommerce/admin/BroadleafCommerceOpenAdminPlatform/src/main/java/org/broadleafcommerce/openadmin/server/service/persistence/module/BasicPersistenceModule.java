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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.DOMException;

import javax.persistence.Embedded;
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
public class BasicPersistenceModule implements PersistenceModule, RecordHelper, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(BasicPersistenceModule.class);
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	protected DecimalFormat decimalFormat = new DecimalFormat("0.########");
	protected ApplicationContext applicationContext;
	protected PersistenceManager persistenceManager;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public boolean isCompatible(OperationType operationType) {
		return OperationType.ENTITY.equals(operationType) || OperationType.FOREIGNKEY.equals(operationType);
	}
	
	public FieldManager getFieldManager() {
		return persistenceManager.getDynamicEntityDao().getFieldManager();
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
                Boolean mutable = mergedProperties.get(property.getName()).getMutable();
                Boolean readOnly = mergedProperties.get(property.getName()).getPresentationAttributes().getReadOnly();
                if ((mutable==null || mutable) && (readOnly==null || !readOnly)) {
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
                            if (StringUtils.isEmpty(value)) {
                                foreignInstance = null;
                            } else {
                                if (SupportedFieldType.INTEGER.toString().equals(mergedProperties.get(property.getName()).getSecondaryType().toString())) {
                                    foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), Long.valueOf(value));
                                } else {
                                    foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), value);
                                }
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
                            if (StringUtils.isEmpty(value)) {
                                foreignInstance = null;
                            } else {
                                if (SupportedFieldType.INTEGER.toString().equals(mergedProperties.get(property.getName()).getSecondaryType().toString())) {
                                    foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), Long.valueOf(value));
                                } else {
                                    foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(mergedProperties.get(property.getName()).getForeignKeyClass()), value);
                                }
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
		Class<?>[] entityClasses = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(ceilingEntityClass);
		Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective, entityClasses);
		Entity entity = getRecord(mergedProperties, record, null, null);
		
		return entity;
	}
	
	public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Class<?>[] entityClasses = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(ceilingEntityClass);
		Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective, entityClasses);
		Entity[] entities = getRecords(mergedProperties, records, null, null);
		
		return entities;
	}
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return persistenceManager.getDynamicEntityDao().getSimpleMergedProperties(entityName, persistencePerspective, entityClasses);
	}

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<Serializable> records) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        return getRecords(primaryMergedProperties, records, null, null);
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
                String strVal;
                checkField: {
                    if (isFieldAccessible) {
                        Property propertyItem = new Property();
                        propertyItem.setName(originalProperty);
                        if (props.contains(propertyItem)) {
                            continue;
                        }
                        props.add(propertyItem);
                        String displayVal = null;
                        if (value != null) {
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
                            propertyItem.setValue(strVal);
                            propertyItem.setDisplayValue(displayVal);
                            break checkField;
                        }
                    }
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
	
	protected Entity update(PersistencePackage persistencePackage, Object primaryKey) throws ServiceException {
		try {
			Entity entity  = persistencePackage.getEntity();
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entities = persistenceManager.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				entity.getType()[0], 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
				""
			);
			if (primaryKey == null) {
				primaryKey = getPrimaryKey(entity, mergedProperties);
			}
			Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);
            SandBoxContext context = SandBoxContext.getSandBoxContext();
            if (context != null && context.getSandBoxMode() != SandBoxMode.IMMEDIATE_COMMIT) {
                //clone the instance to disconnect it from its session
                instance = (Serializable) SerializationUtils.clone(instance);
            }
			instance = createPopulatedInstance(instance, entity, mergedProperties, false);
			instance = persistenceManager.getDynamicEntityDao().merge(instance);
			
			List<Serializable> entityList = new ArrayList<Serializable>();
			entityList.add(instance);
			
			return getRecords(mergedProperties, entityList, null, null)[0];
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
	
	public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties) throws ClassNotFoundException {
		BaseCtoConverter ctoConverter = new BaseCtoConverter();
		for (String propertyName : mergedProperties.keySet()) {
			AssociationPath associationPath;
			int dotIndex = propertyName.lastIndexOf(".");
			String property;
			if (dotIndex >= 0) {
				property = propertyName.substring(dotIndex + 1, propertyName.length());
				String prefix = propertyName.substring(0, dotIndex);
				StringTokenizer tokens = new StringTokenizer(prefix, ".");
                List<AssociationPathElement> elementList = new ArrayList<AssociationPathElement>();
                Class clazz = Class.forName(mergedProperties.get(propertyName).getInheritedFromType());
                StringBuffer sb = new StringBuffer();
                StringBuffer pathBuilder = new StringBuffer();
                while(tokens.hasMoreElements()) {
                    String token = tokens.nextToken();
                    sb.append(token);
                    pathBuilder.append(token);
                    Field field = getFieldManager().getField(clazz, pathBuilder.toString());
                    Embedded embedded = field.getAnnotation(Embedded.class);
                    if (embedded != null) {
                        sb.append(".");
                    } else {
                        elementList.add(new AssociationPathElement(sb.toString()));
                        sb = new StringBuffer();
                    }
                    pathBuilder.append(".");
                }
                if (elementList.size() > 0) {
                    AssociationPathElement[] elements = elementList.toArray(new AssociationPathElement[]{});
                    associationPath = new AssociationPath(elements);
                } else {
                    property = sb.toString() + property;
				    associationPath = AssociationPath.ROOT;
                }
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
						ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
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
						ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
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
        int totalRecords = persistenceManager.getDynamicEntityDao().count(countCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
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
	
	public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try{
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
				""
			);
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}
	
	public Entity update(PersistencePackage persistencePackage) throws ServiceException {
		return update(persistencePackage, null);
	}

	public Entity add(PersistencePackage persistencePackage) throws ServiceException {
		try {
			Entity entity = persistencePackage.getEntity();
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entities = persistenceManager.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				persistencePackage.getCeilingEntityFullyQualifiedClassname(),
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
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
				instance = persistenceManager.getDynamicEntityDao().persist(instance);
				List<Serializable> entityList = new ArrayList<Serializable>();
				entityList.add(instance);
				
				return getRecords(mergedProperties, entityList, null, null)[0];
			} else {
				return update(persistencePackage, primaryKey);
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
	public void remove(PersistencePackage persistencePackage) throws ServiceException {
		try {
			Entity entity = persistencePackage.getEntity();
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entities = persistenceManager.getPolymorphicEntities(entity.getType()[0]);
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				entity.getType()[0], 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
				""
			);
			Object primaryKey = getPrimaryKey(entity, mergedProperties);
			Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);
			
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
						Serializable foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(foreignKey.getForeignKeyClass()), Long.valueOf(value));
						Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
						collection.remove(foreignInstance);
						break;
					}
				}
				break;
			case ENTITY:
				persistenceManager.getDynamicEntityDao().remove(instance);
				break;
			}
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}
	
	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		try {
			Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
				""
			);
			
			BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
			List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
			
			payload = getRecords(mergedProperties, records, null, null);
			totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}

	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}
	
}
