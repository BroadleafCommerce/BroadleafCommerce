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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.SimpleValueMapStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.hibernate.mapping.PersistentClass;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class MapStructureServerModule extends BasicServerEntityModule {

	private static final Log LOG = LogFactory.getLog(MapStructureServerModule.class);
	
	public boolean isCompatible(OperationType operationType) {
		return OperationType.MAPSTRUCTURE.equals(operationType);
	}
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
		if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY) != null) {
			extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY), properties, false);
		}
		if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE) != null) {
			extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE), properties, false);
		}
	}

	@SuppressWarnings("rawtypes")
	protected Entity[] getMapRecords(Serializable record, MapStructure mapStructure, Map<String, FieldMetadata> valueMergedProperties, Property symbolicIdProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException {
		String idProperty = null;
		for (String property : valueMergedProperties.keySet()) {
			if (valueMergedProperties.get(property).getFieldType().equals(SupportedFieldType.ID)) {
				idProperty = property;
				break;
			}
		}
		FieldManager fieldManager = getFieldManager();
		Map map = (Map) fieldManager.getFieldValue(record, mapStructure.getMapProperty());
		Entity[] entities = new Entity[map.size()];
		int j=0;
		for (Object key : map.keySet()) {
			Entity entityItem = new Entity();
			entityItem.setType(new String[]{record.getClass().getName()});
			entities[j] = entityItem;
			List<Property> props = new ArrayList<Property>();
			
			Property propertyItem = new Property();
			propertyItem.setName(mapStructure.getKeyPropertyName());
	    	props.add(propertyItem);
	    	String strVal;
    		if (Date.class.isAssignableFrom(key.getClass())) {
        		strVal = dateFormat.format((Date) key);
        	} else if (Timestamp.class.isAssignableFrom(key.getClass())) {
        		strVal = dateFormat.format(new Date(((Timestamp) key).getTime()));
        	} else if (Calendar.class.isAssignableFrom(key.getClass())) {
        		strVal = dateFormat.format(((Calendar) key).getTime());
        	} else if (Double.class.isAssignableFrom(key.getClass())) {
        		strVal = decimalFormat.format((Double) key);
        	} else if (BigDecimal.class.isAssignableFrom(key.getClass())) {
        		strVal = decimalFormat.format(((BigDecimal) key).doubleValue());
        	} else {
        		strVal = key.toString();
        	}
	    	propertyItem.setValue(strVal);
	    	
	    	PersistentClass persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getValueClassName());
	    	if (persistentClass == null) {
	    		Property temp = new Property();
	    		temp.setName(((SimpleValueMapStructure) mapStructure).getValuePropertyName());
	    		temp.setValue(String.valueOf(map.get(key)));
	    		props.add(temp);
	    	} else {
	    		extractPropertiesFromPersistentEntity(valueMergedProperties, idProperty, (Serializable) map.get(key), props);
	    	}
	    	if (symbolicIdProperty != null) {
	    		props.add(symbolicIdProperty);
	    	}
	    	
	    	Property[] properties = new Property[props.size()];
	        properties = props.toArray(properties);
	        entityItem.setProperties(properties);
	    	j++;
		}
		
		return entities;
	}
	
	@Override
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties, Map<String, FieldMetadata> metadataOverrides) throws ServiceException {
		try {	
			MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
			if (mapStructure != null) {
				PersistentClass persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getKeyClassName());
				Map<String, FieldMetadata> keyMergedProperties;
				if (persistentClass == null) {
					keyMergedProperties = dynamicEntityDao.getPropertiesForPrimitiveClass(
						mapStructure.getKeyPropertyName(), 
						mapStructure.getKeyPropertyFriendlyName(),
						Class.forName(mapStructure.getKeyClassName()), 
						Class.forName(ceilingEntityFullyQualifiedClassname), 
						MergedPropertyType.MAPSTRUCTUREKEY,
						metadataOverrides
					);
				} else {
					keyMergedProperties = dynamicEntityDao.getMergedProperties(
						mapStructure.getKeyClassName(), 
						new Class[]{Class.forName(mapStructure.getKeyClassName())}, 
						null, 
						new String[]{}, 
						new ForeignKey[]{}, 
						MergedPropertyType.MAPSTRUCTUREKEY,
						persistencePerspective.getPopulateToOneFields(), 
						persistencePerspective.getIncludeFields(), 
						persistencePerspective.getExcludeFields(),
						metadataOverrides,
						""
					);
				}
				allMergedProperties.put(MergedPropertyType.MAPSTRUCTUREKEY, keyMergedProperties);
				
				persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getValueClassName());
				Map<String, FieldMetadata> valueMergedProperties;
				if (persistentClass == null) {
					valueMergedProperties = dynamicEntityDao.getPropertiesForPrimitiveClass(
						((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
						((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
						Class.forName(mapStructure.getValueClassName()), 
						Class.forName(ceilingEntityFullyQualifiedClassname), 
						MergedPropertyType.MAPSTRUCTUREVALUE,
						metadataOverrides
					);
				} else {
					valueMergedProperties = dynamicEntityDao.getMergedProperties(
						mapStructure.getValueClassName(), 
						new Class[]{Class.forName(mapStructure.getValueClassName())}, 
						null, 
						new String[]{}, 
						new ForeignKey[]{}, 
						MergedPropertyType.MAPSTRUCTUREVALUE,
						persistencePerspective.getPopulateToOneFields(), 
						persistencePerspective.getIncludeFields(), 
						persistencePerspective.getExcludeFields(),
						metadataOverrides,
						""
					);
				}
				allMergedProperties.put(MergedPropertyType.MAPSTRUCTUREVALUE, valueMergedProperties);
			}
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for add types other than ENTITY");
		}
		try {
			MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
			
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
			FieldManager fieldManager = getFieldManager();
			Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
			
			PersistentClass persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getValueClassName());
			Map<String, FieldMetadata> valueMergedProperties;
			if (persistentClass == null) {
				valueMergedProperties = dynamicEntityDao.getPropertiesForPrimitiveClass(
					((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
					((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
					Class.forName(mapStructure.getValueClassName()), 
					Class.forName(entity.getType()[0]), 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					null
				);
			} else {
				valueMergedProperties = dynamicEntityDao.getMergedProperties(
					mapStructure.getValueClassName(), 
					new Class[]{Class.forName(mapStructure.getValueClassName())}, 
					null, 
					new String[]{}, 
					new ForeignKey[]{}, 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					persistencePerspective.getPopulateToOneFields(), 
					persistencePerspective.getIncludeFields(), 
					persistencePerspective.getExcludeFields(),
					null,
					""
				);
			}
			
			if (persistentClass != null) {
				Serializable valueInstance = (Serializable) Class.forName(mapStructure.getValueClassName()).newInstance();
				valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);
				valueInstance = dynamicEntityDao.persist(valueInstance);
				/*
				 * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
				 * additional types of primitive objects.
				 */
				map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), valueInstance); 
			} else {
				map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), entity.findProperty(((SimpleValueMapStructure) mapStructure).getValuePropertyName()).getValue());
			}
			
			instance = dynamicEntityDao.merge(instance);
			
			return getMapRecords(instance, mapStructure, valueMergedProperties, entity.findProperty("symbolicId"))[0];
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for update types other than ENTITY");
		}
		try {
			MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
			
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
			FieldManager fieldManager = getFieldManager();
			Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
			
			PersistentClass persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getValueClassName());
			Map<String, FieldMetadata> valueMergedProperties;
			if (persistentClass == null) {
				valueMergedProperties = dynamicEntityDao.getPropertiesForPrimitiveClass(
					((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
					((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
					Class.forName(mapStructure.getValueClassName()), 
					Class.forName(entity.getType()[0]), 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					null
				);
			} else {
				valueMergedProperties = dynamicEntityDao.getMergedProperties(
					mapStructure.getValueClassName(), 
					new Class[]{Class.forName(mapStructure.getValueClassName())}, 
					null, 
					new String[]{}, 
					new ForeignKey[]{}, 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					persistencePerspective.getPopulateToOneFields(), 
					persistencePerspective.getIncludeFields(), 
					persistencePerspective.getExcludeFields(),
					null,
					""
				);
			}
			
			if (!entity.findProperty("priorKey").getValue().equals(entity.findProperty(mapStructure.getKeyPropertyName()).getValue())) {
				map.remove(entity.findProperty("priorKey").getValue());
			}
			if (persistentClass != null) {
				Serializable valueInstance = (Serializable) map.get(entity.findProperty("priorKey").getValue());
				valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);
				/*
				 * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
				 * additional types of primitive objects.
				 */
				map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), valueInstance);
			} else {
				map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), entity.findProperty(((SimpleValueMapStructure) mapStructure).getValuePropertyName()).getValue());
			}
			
			
			instance = dynamicEntityDao.merge(instance);
			
			return getMapRecords(instance, mapStructure, valueMergedProperties, entity.findProperty("symbolicId"))[0];
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than ENTITY");
		}
		try {
			MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
			
			Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
			FieldManager fieldManager = getFieldManager();
			Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
			
			Object value = map.remove(entity.findProperty("priorKey").getValue());
			if (mapStructure.getDeleteValueEntity()) {
				dynamicEntityDao.remove((Serializable) value);
			}
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}
	
	@Override
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		Entity[] payload;
		int totalRecords;
		try {
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
				null,
				""
			);
			MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
			
			PersistentClass persistentClass = dynamicEntityDao.getPersistentClass(mapStructure.getValueClassName());
			Map<String, FieldMetadata> valueMergedProperties;
			if (persistentClass == null) {
				valueMergedProperties = dynamicEntityDao.getPropertiesForPrimitiveClass(
					((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
					((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
					Class.forName(mapStructure.getValueClassName()), 
					Class.forName(ceilingEntityFullyQualifiedClassname), 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					null
				);
			} else {
				valueMergedProperties = dynamicEntityDao.getMergedProperties(
					mapStructure.getValueClassName(), 
					new Class[]{Class.forName(mapStructure.getValueClassName())}, 
					null, 
					new String[]{}, 
					new ForeignKey[]{}, 
					MergedPropertyType.MAPSTRUCTUREVALUE,
					false,
					new String[]{},
					new String[]{},
					null,
					""
				);
			}
			
			BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
			totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
			if (totalRecords > 1) {
				throw new ServiceException("Queries to retrieve an entity containing a MapStructure must return only 1 entity. Your query returned ("+totalRecords+") values.");
			}
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
			payload = getMapRecords(records.get(0), mapStructure, valueMergedProperties, null);
		} catch (ServiceException e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw e;
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, payload.length);
		
		return results;
	}
}
