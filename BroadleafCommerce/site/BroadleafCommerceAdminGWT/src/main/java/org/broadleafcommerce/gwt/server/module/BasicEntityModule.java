package org.broadleafcommerce.gwt.server.module;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.CustomFetchResponse;
import org.broadleafcommerce.gwt.server.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.DynamicEntityRemoteService;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;
import org.broadleafcommerce.presentation.SupportedFieldType;
import org.broadleafcommerce.util.money.Money;
import org.w3c.dom.DOMException;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

public class BasicEntityModule implements RemoteServiceModule {

	private static final Log LOG = LogFactory.getLog(BasicEntityModule.class);
	private static final Hashtable<String, Map<String, FieldMetadata>> mergedPropertyLibrary = new Hashtable<String, Map<String, FieldMetadata>>();
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	protected DecimalFormat decimalFormat = new DecimalFormat("0.########");
	
	@Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;
	
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();
	protected DynamicEntityRemoteService dynamicEntityRemoteService;

	public boolean isCompatible(OperationType operationType) {
		return OperationType.ENTITY.equals(operationType) || OperationType.FOREIGNKEY.equals(operationType);
	}
	
	protected Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, Class<?>[] entities, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType) throws ClassNotFoundException {
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
			for (Class<?> clazz : entities) {
				Map<String, FieldMetadata> props = dynamicEntityDao.getPropertiesForEntityClass(clazz, foreignField, additionalNonPersistentProperties, additionalForeignFields, mergedPropertyType);
				//first check all the properties currently in there to see if my entity inherits from them
				for (Class<?> clazz2 : entities) {
					if (!clazz2.getName().equals(clazz.getName())) {
						for (String key: props.keySet()) {
							FieldMetadata metadata = props.get(key);
							if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz2)) {
								metadata.setAvailableToTypes(metadata.getAvailableToTypes() + ";" + clazz2.getName());
							}
						}
					}
				}
				mergedProperties.putAll(props);
			}
			mergedPropertyLibrary.put(sb.toString(), mergedProperties);
		//}
		return mergedPropertyLibrary.get(sb.toString());
	}
	
	@SuppressWarnings("unchecked")
	protected Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException {
		for (Property property : entity.getProperties()) {
			String originalPropertyName = new String(property.getName());
			if (PropertyUtils.getPropertyDescriptor(instance, property.getName()) == null) {
        		//check to see if the member starts with "is"
        		if (property.getName().startsWith("is")) {
        			property.setName(property.getName().substring(2, 3).toLowerCase() + property.getName().substring(3, property.getName().length()));
        		}
        	}
			if (PropertyUtils.getPropertyDescriptor(instance, property.getName()) == null) {
				LOG.warn("Unable to find a bean property for the reported property: " + originalPropertyName + ". Ignoring property.");
				continue;
			}
			Class<?> returnType = PropertyUtils.getPropertyDescriptor(instance, property.getName()).getPropertyType();
			String value = property.getValue();
			if (value != null && mergedProperties.get(originalPropertyName) != null) {
				switch(mergedProperties.get(originalPropertyName).getFieldType()) {
				case BOOLEAN :
					PropertyUtils.setProperty(instance, property.getName(), Boolean.valueOf(value));
					break;
				case DATE :
					PropertyUtils.setProperty(instance, property.getName(), dateFormat.parse(value));
					break;
				case DECIMAL :
					if (BigDecimal.class.isAssignableFrom(returnType)) {
						PropertyUtils.setProperty(instance, property.getName(), new BigDecimal((Double) decimalFormat.parse(value)));
					} else {
						PropertyUtils.setProperty(instance, property.getName(), (Double) decimalFormat.parse(value));
					}
					break;
				case MONEY :
					PropertyUtils.setProperty(instance, property.getName(), new Money((Double) decimalFormat.parse(value)));
					break;
				case INTEGER :
					if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
						PropertyUtils.setProperty(instance, property.getName(), Integer.valueOf(value));
					} else if (byte.class.isAssignableFrom(returnType) || Byte.class.isAssignableFrom(returnType)) {
						PropertyUtils.setProperty(instance, property.getName(), Byte.valueOf(value));
					} else if (short.class.isAssignableFrom(returnType) || Short.class.isAssignableFrom(returnType)) {
						PropertyUtils.setProperty(instance, property.getName(), Short.valueOf(value));
					} else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
						PropertyUtils.setProperty(instance, property.getName(), Long.valueOf(value));
					}
					break;
				case STRING :
					PropertyUtils.setProperty(instance, property.getName(), value);
					break;
				case EMAIL :
					PropertyUtils.setProperty(instance, property.getName(), value);
					break;
				case FOREIGN_KEY :{
					Serializable foreignInstance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), Long.valueOf(value));
					
					if (Collection.class.isAssignableFrom(returnType)) {
						@SuppressWarnings("rawtypes")
						Collection collection = (Collection) PropertyUtils.getProperty(instance, property.getName());
						if (!collection.contains(foreignInstance)){
							collection.add(foreignInstance);
						}
					} else if (Map.class.isAssignableFrom(returnType)) {
						throw new RuntimeException("Map structures are not supported for foreign key fields.");
					} else {
						PropertyUtils.setProperty(instance, property.getName(), foreignInstance);
					}
					break;
				}
				case ADDITIONAL_FOREIGN_KEY :{
					Serializable foreignInstance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), Long.valueOf(value));
					
					if (Collection.class.isAssignableFrom(returnType)) {
						@SuppressWarnings("rawtypes")
						Collection collection = (Collection) PropertyUtils.getProperty(instance, property.getName());
						if (!collection.contains(foreignInstance)){
							collection.add(foreignInstance);
						}
					} else if (Map.class.isAssignableFrom(returnType)) {
						throw new RuntimeException("Map structures are not supported for foreign key fields.");
					} else {
						PropertyUtils.setProperty(instance, property.getName(), foreignInstance);
					}
					break;
				}
				case ID :
					if (setId) {
						PropertyUtils.setProperty(instance, property.getName(), Long.valueOf(value));
					}
					break;
				}
			}
		}
		
		return instance;
	}
	
	protected Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
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
				entity = (Serializable) PropertyUtils.getProperty(recordEntity, pathToTargetObject);
			} else {
				entity = recordEntity;
			}
			Entity entityItem = new Entity();
			entityItem.setType(entity.getClass().getName());
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
		for (String property : mergedProperties.keySet()) {
			String originalProperty = new String(property);
			if (Class.forName(mergedProperties.get(originalProperty).getInheritedFromType()).isAssignableFrom(entity.getClass())) {
				if (PropertyUtils.getPropertyDescriptor(entity, property) == null) {
					//check to see if the member starts with "is"
					if (property.startsWith("is")) {
						property = property.substring(2, 3).toLowerCase() + property.substring(3, property.length());
					}
				}
				if (PropertyUtils.getPropertyDescriptor(entity, property) != null) {
					Property propertyItem = new Property();
					propertyItem.setName(originalProperty);
					if (props.contains(propertyItem)) {
						continue;
					}
			    	props.add(propertyItem);
			    	Object value = PropertyUtils.getProperty(entity, property);
			    	String strVal;
			    	if (value == null) {
			    		strVal = null;
			    	} else {
			    		if (mergedProperties.get(originalProperty).getCollection()) {
			    			propertyItem.setType(mergedProperties.get(originalProperty).getFieldType().toString());
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
			        	} else if (entity.getClass().equals(value.getClass()) && idProperty != null){
			        		strVal = PropertyUtils.getProperty(value, idProperty).toString();
			        	} else if (mergedProperties.get(originalProperty).getProvidedForeignKeyClass() != null) {
			        		strVal = PropertyUtils.getProperty(value, mergedProperties.get(originalProperty).getComplexIdProperty()).toString();
			        	} else {
			        		strVal = value.toString();
			        	}
			    	}
			    	propertyItem.setValue(strVal);
				} else {
					//try a direct property acquisition via reflection
					try {
						Method method = entity.getClass().getMethod(originalProperty, new Class[]{});
						Object value = method.invoke(entity, new Object[]{});
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
						LOG.warn("Unable to find a specified property in the entity: " + originalProperty + "()");
						//do nothing - this property is simply not in the bean
					}
				}
			}
		}
	}
	
	protected Entity update(Entity entity, Object primaryKey, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				entity.getType(), 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
			);
			if (primaryKey == null) {
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
				for (Property property : entity.getProperties()) {
					if (property.getName().equals(idProperty)) {
						primaryKey = Long.valueOf(property.getValue());
						break;
					}
				}
				if (primaryKey == null) {
					throw new RuntimeException("Could not find the primary key property (" + idProperty + ") in the passed entity with type: " + entity.getType());
				}
			}
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), primaryKey);
			//dynamicEntityDao.clear();
			//Object[] previousState = changeSetDao.getState(entity.getType(), instance);
			instance = createPopulatedInstance(instance, entity, mergedProperties, false);
			
			persistInstance: {
				//check to see if there is a custom handler registered
				for (CustomPersistenceHandler handler : customPersistenceHandlers) {
					if (handler.canHandleUpdate(entity.getType())) {
						instance = handler.update(instance, customCriteria, dynamicEntityDao);
						break persistInstance;
					}
				}
				
				instance = dynamicEntityDao.merge(instance);
			}
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
	
	protected BaseCtoConverter getCtoConverter(CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties) {
		BaseCtoConverter ctoConverter = new BaseCtoConverter();
		for (String propertyName : mergedProperties.keySet()) {
			switch(mergedProperties.get(propertyName).getFieldType()) {
			case BOOLEAN :
				ctoConverter.addBooleanMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case DATE :
				ctoConverter.addDateMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case DECIMAL :
				ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case MONEY :
				ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case INTEGER :
				ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case STRING :
				ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case EMAIL :
				ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case FOREIGN_KEY :
				if (cto.get(propertyName).getFilterValues().length > 0) {
					if (mergedProperties.get(propertyName).getCollection()) {
						ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
					} else if (cto.get(propertyName).getFilterValues()[0] == null || cto.get(propertyName).getFilterValues()[0].equals("null")){
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getComplexIdProperty());
					} else {
						AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
						ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getComplexIdProperty());
					}
				}
				break;
			case ID :
				ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			}
		}
		return ctoConverter;
	}
	
	protected int getTotalRecords(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
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
			prop.setName(property);
			if (properties.contains(prop)) {
				continue;
			}
			properties.add(prop);
			FieldMetadata metadata = mergedProperties.get(property);
			prop.setType(metadata.getFieldType().toString());
			if (metadata.getLength() != null) {
				prop.setLength(Long.valueOf(metadata.getLength().toString()));
				prop.setRequired(Boolean.valueOf(metadata.getRequired().toString()));
				prop.setUnique(Boolean.valueOf(metadata.getUnique().toString()));
				prop.setScale(Integer.valueOf(metadata.getScale().toString()));
				prop.setPrecision(Integer.valueOf(metadata.getPrecision().toString()));
			}
			prop.setMutable(Boolean.valueOf(metadata.getMutable().toString()));
			prop.setInheritedFromType(metadata.getInheritedFromType().toString());
			prop.setAvailableToTypes(metadata.getAvailableToTypes().toString());
			prop.setForeignKeyClass(metadata.getProvidedForeignKeyClass());
			prop.setForeignKeyProperty(metadata.getComplexIdProperty());
			prop.setIsCollection(metadata.getCollection());
			if (metadata.getPresentationAttributes() != null) {
				prop.setFriendlyName(metadata.getPresentationAttributes().getFriendlyName());
				prop.setOrder(metadata.getPresentationAttributes().getOrder());
				prop.setHidden(metadata.getPresentationAttributes().isHidden());
				prop.setGroup(metadata.getPresentationAttributes().getGroup());
				prop.setLargeEntry(metadata.getPresentationAttributes().isLargeEntry());
				prop.setProminent(metadata.getPresentationAttributes().isProminent());
			}
			if (isHiddenOverride) {
				prop.setHidden(true);
			}
			prop.setMergedPropertyType(metadata.getMergedPropertyType().toString());
		}
	}
	
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
		try{
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
			);
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		return update(entity, persistencePerspective, null, customCriteria);
	}

	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				entity.getType(), 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
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
			for (Property property : entity.getProperties()) {
				if (property.getName().equals(idProperty)) {
					primaryKey = Long.valueOf(property.getValue());
					break;
				}
			}
			if (primaryKey == null) {
				Serializable instance = (Serializable) Class.forName(entity.getType()).newInstance();
				instance = createPopulatedInstance(instance, entity, mergedProperties, true);
				
				persistInstance: {
					//check to see if there is a custom handler registered
					for (CustomPersistenceHandler handler : customPersistenceHandlers) {
						if (handler.canHandleAdd(entity.getType())) {
							instance = handler.add(instance, customCriteria, dynamicEntityDao);
							break persistInstance;
						}
					}
					
					instance = dynamicEntityDao.persist(instance);
				}
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
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				entity.getType(), 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
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
			for (Property property : entity.getProperties()) {
				if (property.getName().equals(idProperty)) {
					primaryKey = Long.valueOf(property.getValue());
				}
			}
			if (primaryKey == null) {
				throw new RuntimeException("Could not find the primary key property (" + idProperty + ") in the passed entity with type: " + entity.getType());
			}
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), primaryKey);
			
			switch(persistencePerspective.getOperationTypes().getRemoveType()) {
			case FOREIGNKEY:
				for (Property property : entity.getProperties()) {
					String originalPropertyName = new String(property.getName());
					if (PropertyUtils.getPropertyDescriptor(instance, property.getName()) == null) {
		        		//check to see if the member starts with "is"
		        		if (property.getName().startsWith("is")) {
		        			property.setName(property.getName().substring(2, 3).toLowerCase() + property.getName().substring(3, property.getName().length()));
		        		}
					}
					if (PropertyUtils.getPropertyDescriptor(instance, property.getName()) == null) {
						LOG.warn("Unable to find a bean property for the reported property: " + originalPropertyName + ". Ignoring property.");
						continue;
					}
					if (SupportedFieldType.FOREIGN_KEY.equals(mergedProperties.get(originalPropertyName).getFieldType())) {
						String value = property.getValue();
						Serializable foreignInstance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), Long.valueOf(value));
						Collection collection = (Collection) PropertyUtils.getProperty(instance, property.getName());
						collection.remove(foreignInstance);
						break;
					}
				}
				break;
			case ENTITY:
				persistInstance: {
					//check to see if there is a custom handler registered
					for (CustomPersistenceHandler handler : customPersistenceHandlers) {
						if (handler.canHandleRemove(entity.getType())) {
							handler.remove(instance, customCriteria, dynamicEntityDao);
							break persistInstance;
						}
					}
					dynamicEntityDao.remove(instance);
				}
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
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
			);
			
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleFetch(ceilingEntityFullyQualifiedClassname)) {
					CustomFetchResponse response = handler.fetch(ceilingEntityFullyQualifiedClassname, cto, customCriteria, dynamicEntityDao);
					payload = getRecords(mergedProperties, response.getRecords(), null, null);
					totalRecords = response.getTotalRecords();
					DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
					
					return results;
				}
			}
			
			BaseCtoConverter ctoConverter = getCtoConverter(cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
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
