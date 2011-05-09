package org.broadleafcommerce.gwt.server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.changeset.dao.ChangeSetDao;
import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.DynamicEntityService;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;
import org.broadleafcommerce.presentation.SupportedFieldType;
import org.broadleafcommerce.util.money.Money;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService {
	
	private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);
	
	private static final Hashtable<String, Map<String, FieldMetadata>> mergedPropertyLibrary = new Hashtable<String, Map<String, FieldMetadata>>();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	private DecimalFormat decimalFormat = new DecimalFormat("0.########");

	@Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;
	
	@Resource(name="blChangeSetDao")
	protected ChangeSetDao changeSetDao;
	
	protected ClassMetadata getMergedClassMetadata(final Class<?>[] entities, Map<String, FieldMetadata> mergedProperties, String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException, ParserConfigurationException, DOMException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		Document doc = impl.createDocument(null,null,null);
		Element root = doc.createElement("inspection");
		doc.appendChild(root);
		Element e1 = doc.createElement("entity");
		e1.setAttribute("type",ceilingEntityFullyQualifiedClassname);
		root.appendChild(e1);
		
		ClassMetadata classMetadata = new ClassMetadata();
		PolymorphicEntity[] polyEntities = new PolymorphicEntity[entities.length];
		int j = 0;
		for (Class<?> type : entities) {
			polyEntities[j] = new PolymorphicEntity();
			polyEntities[j].setType(type.getName());
			polyEntities[j].setName(type.getSimpleName());
			j++;
		}
		classMetadata.setPolymorphicEntities(polyEntities);
		
		Property[] properties = new Property[mergedProperties.size()];
		j = 0;
		for (String property : mergedProperties.keySet()) {
			properties[j] = new Property();
			properties[j].setName(property);
			FieldMetadata metadata = mergedProperties.get(property);
			properties[j].setType(metadata.getFieldType().toString());
			if (metadata.getLength() != null) {
				 properties[j].setLength(Long.valueOf(metadata.getLength().toString()));
				 properties[j].setRequired(Boolean.valueOf(metadata.getRequired().toString()));
				 properties[j].setUnique(Boolean.valueOf(metadata.getUnique().toString()));
				 properties[j].setScale(Integer.valueOf(metadata.getScale().toString()));
				 properties[j].setPrecision(Integer.valueOf(metadata.getPrecision().toString()));
			}
			properties[j].setMutable(Boolean.valueOf(metadata.getMutable().toString()));
			properties[j].setInheritedFromType(metadata.getInheritedFromType().toString());
			properties[j].setAvailableToTypes(metadata.getAvailableToTypes().toString());
			properties[j].setForeignKeyClass(metadata.getProvidedForeignKeyClass());
			properties[j].setForeignKeyProperty(metadata.getComplexIdProperty());
			properties[j].setIsCollection(metadata.getCollection());
			if (metadata.getPresentationAttributes() != null) {
				properties[j].setFriendlyName(metadata.getPresentationAttributes().getFriendlyName());
				properties[j].setOrder(metadata.getPresentationAttributes().getOrder());
				properties[j].setHidden(metadata.getPresentationAttributes().isHidden());
				properties[j].setGroup(metadata.getPresentationAttributes().getGroup());
				properties[j].setLargeEntry(metadata.getPresentationAttributes().isLargeEntry());
				properties[j].setProminent(metadata.getPresentationAttributes().isProminent());
			}
			j++;
		}
		Arrays.sort(properties, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				/*
				 * First, compare properties based on order fields
				 * if order is equal, the inherited field wins
				 */
				if (o1.getOrder() != null && o2.getOrder() != null) {
					if (o1.getOrder() == o2.getOrder()) {
						Integer pos1;
						Integer pos2;
						try {
							pos1 = Arrays.binarySearch(entities, Class.forName(o1.getInheritedFromType()));
							pos2 = Arrays.binarySearch(entities, Class.forName(o2.getInheritedFromType()));
						} catch (ClassNotFoundException e) {
							throw new RuntimeException("Unable to sort properties", e);
						}
						return pos1.compareTo(pos2);
					} else if (o1.getOrder() < 0) {
						return 1;
					} else if (o2.getOrder() < 0) {
						return -1;
					} else {
						return o1.getOrder().compareTo(o2.getOrder());
					}
				} else if (o1.getOrder() != null && o2.getOrder() == null) {
					/*
					 * Always favor fields that have an order identified
					 */
					return -1;
				} else if (o1.getOrder() == null && o2.getOrder() != null) {
					/*
					 * Always favor fields that have an order identified
					 */
					return 1;
				} else if (o1.getFriendlyName() != null && o2.getFriendlyName() != null) {
					return o1.getFriendlyName().compareTo(o2.getFriendlyName());
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
		classMetadata.setProperties(properties);
		
		return classMetadata;
	}
	
	protected Entity[] getRecords(Map<String, FieldMetadata> mergedProperties, List<Serializable> records, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException {
		String idProperty = null;
		for (String property : mergedProperties.keySet()) {
			if (mergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
				idProperty = property;
				break;
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
			int x = 0;
	        for (String property : mergedProperties.keySet()) {
	        	String originalProperty = new String(property);
	        	if (PropertyUtils.getPropertyDescriptor(entity, property) == null) {
	        		//check to see if the member starts with "is"
	        		if (property.startsWith("is")) {
	        			property = property.substring(2, 3).toLowerCase() + property.substring(3, property.length());
	        		}
	        	}
	        	if (PropertyUtils.getPropertyDescriptor(entity, property) != null) {
	        		Property propertyItem = new Property();
		        	props.add(propertyItem);
		        	Object value = PropertyUtils.getProperty(entity, property);
		        	String strVal;
		        	if (value == null) {
		        		strVal = null;
		        	} else {
		        		if (mergedProperties.get(property).getCollection()) {
		        			propertyItem.setType(mergedProperties.get(property).getFieldType().toString());
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
			        		strVal = PropertyUtils.getProperty(value, mergedProperties.get(property).getComplexIdProperty()).toString();
			        	} else {
			        		strVal = value.toString();
			        	}
		        	}
		        	propertyItem.setName(originalProperty);
		        	propertyItem.setValue(strVal);
	        	} else {
	        		//try a direct property acquisition via reflection
	        		try {
						Method method = entity.getClass().getMethod(originalProperty, new Class[]{});
						Object value = method.invoke(entity, new Object[]{});
						Property propertyItem = new Property();
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
						propertyItem.setName(originalProperty);
						propertyItem.setValue(strVal);
					} catch (NoSuchMethodException e) {
						LOG.warn("Unable to find a specified property in the entity: " + originalProperty + "()");
						//do nothing - this property is simply not in the bean
					}
	        	}
	        	x++;
	        }
	        Property[] properties = new Property[props.size()];
	        properties = props.toArray(properties);
	        entityItem.setProperties(properties);
	        j++;
		}
        
		return entities;
	}

	protected Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, Class<?>[] entities, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ClassNotFoundException {
		//create a unique key for this inspection query
		StringBuffer sb = new StringBuffer();
		sb.append(ceilingEntityFullyQualifiedClassname);
		if (foreignFields != null) {
			for (ForeignKey foreignKey : foreignFields) {
				sb.append(foreignKey.getManyToField());
			}
		}
		if (additionalNonPersistentProperties != null) {
			for (String additionalNonPersistentProperty : additionalNonPersistentProperties) {
				sb.append(additionalNonPersistentProperty);
			}
		}
		//TODO re-establish library check for release
		//if (!mergedPropertyLibrary.containsKey(sb.toString())) {
			Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
			for (Class<?> clazz : entities) {
				Map<String, FieldMetadata> props = dynamicEntityDao.getPropertiesForEntityClass(clazz, foreignFields, additionalNonPersistentProperties);
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

	protected Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
		return entities;
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
					if (cto.get(propertyName).getFilterValues()[0] == null){
						//ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
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
	
	protected BaseCtoConverter getAlternateCtoConverter(CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties, JoinTable joinTable) {
		BaseCtoConverter ctoConverter = getCtoConverter(cto, joinTable.getJoinTableEntityClassname(), mergedProperties);
		String[] joinElements = joinTable.getAssociationPathToLinkedObject().split("\\.");
		AssociationPathElement[] pathElements = new AssociationPathElement[joinElements.length];
		for (int j=0;j<joinElements.length;j++) {
			pathElements[j] = new AssociationPathElement(joinElements[j]);
		}
		AssociationPath foreignCategory = new AssociationPath(pathElements);
		ctoConverter.addLongEQMapping(joinTable.getJoinTableEntityClassname(), joinTable.getManyToField(), foreignCategory, joinTable.getLinkedProperty());
		return ctoConverter;
	}

	protected int getTotalRecords(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
		PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), ceilingEntityFullyQualifiedClassname);
        int totalRecords = dynamicEntityDao.count(countCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
		return totalRecords;
	}
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException {
		ClassMetadata mergedMetadata;
		try {
			Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(ceilingEntityFullyQualifiedClassname, entities, foreignFields, additionalNonPersistentProperties);
			
			mergedMetadata = getMergedClassMetadata(entities, mergedProperties, ceilingEntityFullyQualifiedClassname);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
		
		return results;
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, CriteriaTransferObject cto, String[] additionalNonPersistentProperties) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		try {
			Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(ceilingEntityFullyQualifiedClassname, entities, foreignFields, additionalNonPersistentProperties);
			
			BaseCtoConverter ctoConverter = getCtoConverter(cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
			
			payload = getRecords(mergedProperties, records, null);
			totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, JoinTable joinTable, CriteriaTransferObject cto, String[] additionalNonPersistentProperties) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		try {
			Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedPropertiesTarget = getMergedProperties(ceilingEntityFullyQualifiedClassname, entities, new ForeignKey[]{}, additionalNonPersistentProperties);
			
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(joinTable.getJoinTableEntityClassname(), new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, new ForeignKey[]{}, new String[]{});
			BaseCtoConverter ctoConverter = getAlternateCtoConverter(cto, mergedProperties, joinTable);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinTable.getJoinTableEntityClassname());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(joinTable.getJoinTableEntityClassname()));
			payload = getRecords(mergedPropertiesTarget, records, joinTable.getAssociationPathToTargetObject());
			totalRecords = getTotalRecords(joinTable.getJoinTableEntityClassname(), cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + joinTable.getJoinTableEntityClassname(), e);
			throw new ServiceException("Unable to fetch results for " + joinTable.getJoinTableEntityClassname(), e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
	
	public DynamicResultSet create(String targetEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException {
		Entity[] payload;
		try {
			Class<?>[] entities = getPolymorphicEntities(targetEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(targetEntityFullyQualifiedClassname, entities, foreignFields, additionalNonPersistentProperties);
			List<Serializable> records = new ArrayList<Serializable>();
			//TODO The entity instance should actually come from some sort of factory to support specialized construction
			records.add((Serializable) Class.forName(targetEntityFullyQualifiedClassname).newInstance());
			payload = getRecords(mergedProperties, records, null);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + targetEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + targetEntityFullyQualifiedClassname, e);
		}
		DynamicResultSet results = new DynamicResultSet(null, payload, 1);
		
		return results;
	}

	public Entity add(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException {
		try {
			Class<?>[] entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, foreignFields, additionalNonPersistentProperties);
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
				instance = createPopulatedInstance(instance, entity, mergedProperties);
				instance = dynamicEntityDao.persist(instance);
				List<Serializable> entityList = new ArrayList<Serializable>();
				entityList.add(instance);
				
				return getRecords(mergedProperties, entityList, null)[0];
			} else {
				return update(entity, foreignFields, primaryKey, additionalNonPersistentProperties);
			}
		} catch (Exception e) {
			LOG.error("Problem adding new entity", e);
			throw new ServiceException("Problem adding new entity", e);
		} 
	}
	
	public Entity update(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException {
		return update(entity, foreignFields, null, additionalNonPersistentProperties);
	}
	
	public Entity update(Entity entity, ForeignKey[] foreignFields, Object primaryKey, String[] additionalNonPersistentProperties) throws ServiceException {
		try {
			Class<?>[] entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, foreignFields, additionalNonPersistentProperties);
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
			instance = createPopulatedInstance(instance, entity, mergedProperties);
			instance = dynamicEntityDao.merge(instance);
			//Object[] currentState = changeSetDao.getState(entity.getType(), instance);
			//changeSetDao.saveChangeSet(entity.getType(), instance, (Serializable) primaryKey, currentState, previousState);
			
			List<Serializable> entityList = new ArrayList<Serializable>();
			entityList.add(instance);
			
			return getRecords(mergedProperties, entityList, null)[0];
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem editing entity", e);
		}
	}

	public void remove(Entity entity, ForeignKey[] foreignFields, RemoveType removeType, String[] additionalNonPersistentProperties) throws ServiceException {
		try {
			Class<?>[] entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, foreignFields, additionalNonPersistentProperties);
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
			switch(removeType) {
			case COLLECTION:
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
						@SuppressWarnings("rawtypes")
						Collection collection = (Collection) PropertyUtils.getProperty(instance, property.getName());
						collection.remove(foreignInstance);
						break;
					}
				}
				break;
			case REGULAR:
				dynamicEntityDao.remove(instance);
				break;
			}
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity", e);
		}
	}

	@SuppressWarnings("unchecked")
	public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException {
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
			if (value != null) {
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
				case FOREIGN_KEY :
					Serializable foreignInstance = dynamicEntityDao.retrieve(Class.forName(entity.getType()), Long.valueOf(value));
					
					if (Collection.class.isAssignableFrom(returnType)) {
						@SuppressWarnings("rawtypes")
						Collection collection = (Collection) PropertyUtils.getProperty(instance, property.getName());
						if (!collection.contains(foreignInstance)){
							collection.add(foreignInstance);
						}
					} else if (Map.class.isAssignableFrom(returnType)) {
						
					} else {
						PropertyUtils.setProperty(instance, property.getName(), foreignInstance);
					}
					break;
				case ID :
					PropertyUtils.setProperty(instance, property.getName(), Long.valueOf(value));
					break;
				}
			}
		}
		
		return instance;
	}

}
