package org.broadleafcommerce.gwt.server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.datasource.results.SupportedFieldType;
import org.broadleafcommerce.gwt.client.service.DynamicEntityService;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;
import org.broadleafcommerce.gwt.server.dao.GenericEntityDao;
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

	@Resource(name="blAdminGenericDao")
	protected GenericEntityDao genericDao;
	
	protected ClassMetadata getMergedClassMetadata(List<Class<?>> entities, Map<String, FieldMetadata> mergedProperties, String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException, ParserConfigurationException, DOMException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException {
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
		PolymorphicEntity[] polyEntities = new PolymorphicEntity[entities.size()];
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
			properties[j].setLength(Long.valueOf(metadata.getLength().toString()));
			properties[j].setRequired(Boolean.valueOf(metadata.getRequired().toString()));
			properties[j].setUnique(Boolean.valueOf(metadata.getUnique().toString()));
			properties[j].setScale(Integer.valueOf(metadata.getScale().toString()));
			properties[j].setPrecision(Integer.valueOf(metadata.getPrecision().toString()));
			properties[j].setMutable(Boolean.valueOf(metadata.getMutable().toString()));
			properties[j].setInheritedFromType(metadata.getInheritedFromType().toString());
			properties[j].setAvailableToTypes(metadata.getAvailableToTypes().toString());
			j++;
		}
		classMetadata.setProperties(properties);
		
		return classMetadata;
	}
	
	protected Entity[] getRecords(Map<String, FieldMetadata> mergedProperties, List<Serializable> records) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException {
		String idProperty = null;
		for (String property : mergedProperties.keySet()) {
			if (mergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
				idProperty = property;
				break;
			}
		}
       
		Entity[] entities = new Entity[records.size()];
		int j = 0;
		for (Serializable entity : records) {
			Entity entityItem = new Entity();
			entityItem.setType(entity.getClass().getName());
			entities[j] = entityItem;
			Property[] props = new Property[mergedProperties.size()];
			entityItem.setProperties(props);
			int x = 0;
	        for (String property : mergedProperties.keySet()) {
	        	Property propertyItem = new Property();
	        	props[x] = propertyItem;
	        	String originalProperty = new String(property);
	        	if (PropertyUtils.getPropertyDescriptor(entity, property) == null) {
	        		//check to see if the member starts with "is"
	        		if (property.startsWith("is")) {
	        			property = property.substring(2, 3).toLowerCase() + property.substring(3, property.length());
	        		}
	        	}
	        	if (PropertyUtils.getPropertyDescriptor(entity, property) != null) {
		        	Object value = PropertyUtils.getProperty(entity, property);
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
			        	} else if (entity.getClass().equals(value.getClass()) && idProperty != null){
			        		strVal = PropertyUtils.getProperty(value, idProperty).toString();
			        	} else if (mergedProperties.get(originalProperty).getComplexType() != null) {
			        		strVal = PropertyUtils.getProperty(value, mergedProperties.get(property).getComplexIdProperty()).toString();
			        	} else {
			        		strVal = value.toString();
			        	}
		        	}
		        	propertyItem.setName(originalProperty);
		        	propertyItem.setValue(strVal);
	        	}
	        	x++;
	        }
	        j++;
		}
        
		return entities;
	}

	protected Map<String, FieldMetadata> getMergedProperties(String ceilingEntityFullyQualifiedClassname, List<Class<?>> entities, String[] optionalFields) throws ClassNotFoundException {
		if (!mergedPropertyLibrary.containsKey(ceilingEntityFullyQualifiedClassname)) {
			Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
			for (Class<?> clazz : entities) {
				Map<String, FieldMetadata> props = genericDao.getPropertiesForEntityClass(clazz, optionalFields);
				//first check all the properties currently in there to see if my entity inherits from them
				for (String key: props.keySet()) {
					FieldMetadata metadata = props.get(key);
					if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz) && !metadata.getAvailableToTypes().contains(clazz.getName())) {
						metadata.setAvailableToTypes(metadata.getAvailableToTypes() + ";" + clazz.getName());
					}
				}
				mergedProperties.putAll(props);
			}
			mergedPropertyLibrary.put(ceilingEntityFullyQualifiedClassname, mergedProperties);
		}
		return mergedPropertyLibrary.get(ceilingEntityFullyQualifiedClassname);
	}

	protected List<Class<?>> getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
		List<Class<?>> entities = genericDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
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
				AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
				ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, mergedProperties.get(propertyName).getComplexIdProperty());
				break;
			case ID :
				ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				break;
			case HIERARCHY_KEY :
				if (cto.get(propertyName).getFilterValues()[0] == null){
					ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
				} else {
					AssociationPath parentCategory = new AssociationPath(new AssociationPathElement(propertyName));
					ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, parentCategory, mergedProperties.get(propertyName).getComplexIdProperty());
				}
				break;
			}
		}
		return ctoConverter;
	}

	protected int getTotalRecords(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
		PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), ceilingEntityFullyQualifiedClassname);
        int totalRecords = genericDao.count(countCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
		return totalRecords;
	}
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, String[] optionalFields) throws ServiceException {
		ClassMetadata mergedMetadata;
		try {
			List<Class<?>> entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(ceilingEntityFullyQualifiedClassname, entities, optionalFields);
			
			mergedMetadata = getMergedClassMetadata(entities, mergedProperties, ceilingEntityFullyQualifiedClassname);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
		
		return results;
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, String[] optionalFields) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		try {
			List<Class<?>> entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(ceilingEntityFullyQualifiedClassname, entities, optionalFields);
			
			BaseCtoConverter ctoConverter = getCtoConverter(cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
			List<Serializable> records = genericDao.query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
			
			payload = getRecords(mergedProperties, records);
			totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
	
	public DynamicResultSet create(String targetEntityFullyQualifiedClassname, String[] optionalFields) throws ServiceException {
		Entity[] payload;
		try {
			List<Class<?>> entities = getPolymorphicEntities(targetEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(targetEntityFullyQualifiedClassname, entities, optionalFields);
			List<Serializable> records = new ArrayList<Serializable>();
			//TODO The entity instance should actually come from some sort of factory to support specialized construction
			records.add((Serializable) Class.forName(targetEntityFullyQualifiedClassname).newInstance());
			payload = getRecords(mergedProperties, records);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + targetEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + targetEntityFullyQualifiedClassname, e);
		}
		DynamicResultSet results = new DynamicResultSet(null, payload, 1);
		
		return results;
	}

	public Entity add(Entity entity, String[] optionalFields) throws ServiceException {
		try {
			List<Class<?>> entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, optionalFields);
			Serializable instance = (Serializable) Class.forName(entity.getType()).newInstance();
			instance = createPopulatedInstance(instance, entity, mergedProperties);
			instance = genericDao.persist(instance);
			List<Serializable> entityList = new ArrayList<Serializable>();
			entityList.add(instance);
			
			return getRecords(mergedProperties, entityList)[0];
		} catch (Exception e) {
			LOG.error("Problem adding new entity", e);
			throw new ServiceException("Problem adding new entity", e);
		} 
	}
	
	public Entity update(Entity entity, String[] optionalFields) throws ServiceException {
		try {
			List<Class<?>> entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, optionalFields);
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
			Serializable instance = genericDao.retrieve(Class.forName(entity.getType()), primaryKey);
			instance = createPopulatedInstance(instance, entity, mergedProperties);
			instance = genericDao.merge(instance);
			List<Serializable> entityList = new ArrayList<Serializable>();
			entityList.add(instance);
			
			return getRecords(mergedProperties, entityList)[0];
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem editing entity", e);
		}
	}

	public void remove(Entity entity) throws ServiceException {
		try {
			List<Class<?>> entities = getPolymorphicEntities(entity.getType());
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(entity.getType(), entities, null);
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
			Serializable instance = genericDao.retrieve(Class.forName(entity.getType()), primaryKey);
			genericDao.remove(instance);
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity", e);
		}
	}

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
					Serializable foreignInstance = (Serializable) Class.forName(mergedProperties.get(originalPropertyName).getComplexType()).newInstance();
					PropertyUtils.setProperty(foreignInstance, mergedProperties.get(originalPropertyName).getComplexIdProperty(), Long.valueOf(value));
					PropertyUtils.setProperty(instance, property.getName(), foreignInstance);
					break;
				case ID :
					PropertyUtils.setProperty(instance, property.getName(), Long.valueOf(value));
					break;
				case HIERARCHY_KEY :
					Serializable hierarchyInstance = (Serializable) Class.forName(mergedProperties.get(originalPropertyName).getComplexType()).newInstance();
					PropertyUtils.setProperty(hierarchyInstance, mergedProperties.get(originalPropertyName).getComplexIdProperty(), Long.valueOf(value));
					PropertyUtils.setProperty(instance, property.getName(), hierarchyInstance);
					break;
				}
			}
		}
		
		return instance;
	}

}
