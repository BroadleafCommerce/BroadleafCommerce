package org.broadleafcommerce.gwt.server.service.remote;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.DynamicEntityService;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RemoteServiceModule;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public class DynamicEntityRemoteService implements DynamicEntityService, InspectHelper {
	
	private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);

	protected DynamicEntityDao dynamicEntityDao;
	
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();
	protected RemoteServiceModule[] modules;
	protected Map<String, FieldMetadata> metadataOverrides;
	
	public DynamicEntityRemoteService(RemoteServiceModule[] modules) {
		this.modules = modules;
		for (RemoteServiceModule module : modules) {
			module.setDynamicEntityRemoteService(this);
		}
	}
	
	public Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
		return entities;
	}
	
	public ClassMetadata getMergedClassMetadata(final Class<?>[] entities, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) throws ClassNotFoundException, IllegalArgumentException {		
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
		
		List<Property> propertiesList = new ArrayList<Property>();
		for (RemoteServiceModule module : modules) {
			module.extractProperties(mergedProperties, propertiesList);
		}
		/*
		 * Insert inherited fields whose order has been specified
		 */
		for (int i=0;i<entities.length-1;i++) {
			for (Property myProperty : propertiesList) {
				if (myProperty.getMetadata().getInheritedFromType().equals(entities[i].getName()) && myProperty.getMetadata().getPresentationAttributes().getOrder() != null) {
					for (Property property : propertiesList) {
						if (!property.getMetadata().getInheritedFromType().equals(entities[i].getName()) && property.getMetadata().getPresentationAttributes().getOrder() != null && property.getMetadata().getPresentationAttributes().getOrder() >= myProperty.getMetadata().getPresentationAttributes().getOrder()) {
							property.getMetadata().getPresentationAttributes().setOrder(property.getMetadata().getPresentationAttributes().getOrder() + 1);
						}
					}
				}
			}
		}
		Property[] properties = new Property[propertiesList.size()];
		properties = propertiesList.toArray(properties);
		Arrays.sort(properties, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				/*
				 * First, compare properties based on order fields
				 */
				if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
					return o1.getMetadata().getPresentationAttributes().getOrder().compareTo(o2.getMetadata().getPresentationAttributes().getOrder());
				} else if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() == null) {
					/*
					 * Always favor fields that have an order identified
					 */
					return -1;
				} else if (o1.getMetadata().getPresentationAttributes().getOrder() == null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
					/*
					 * Always favor fields that have an order identified
					 */
					return 1;
				} else if (o1.getMetadata().getPresentationAttributes().getFriendlyName() != null && o2.getMetadata().getPresentationAttributes().getFriendlyName() != null) {
					return o1.getMetadata().getPresentationAttributes().getFriendlyName().compareTo(o2.getMetadata().getPresentationAttributes().getFriendlyName());
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
		classMetadata.setProperties(properties);
		
		return classMetadata;
	}
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, String[] metadataOverrideKeys, FieldMetadata[] metadataOverrideValues) throws ServiceException {
		try {
			//use any override provided by the presentation layer
			Map<String, FieldMetadata> metadataOverrides = null;
			if (metadataOverrideKeys != null) {
				metadataOverrides = new HashMap<String, FieldMetadata>();
				for (int j=0; j<metadataOverrideKeys.length; j++) {
					metadataOverrides.put(metadataOverrideKeys[j], metadataOverrideValues[j]);
				}
			}
			//if no presentation layer override are defined, use any defined via configuration on the server side
			if (metadataOverrides == null && this.metadataOverrides != null) {
				metadataOverrides = this.metadataOverrides;
			}
			//check to see if there is a custom handler registered
			for (CustomPersistenceHandler handler : customPersistenceHandlers) {
				if (handler.canHandleInspect(ceilingEntityFullyQualifiedClassname, customCriteria)) {
					DynamicResultSet results = handler.inspect(ceilingEntityFullyQualifiedClassname, persistencePerspective, customCriteria, metadataOverrides, dynamicEntityDao, this);
					
					return results;
				}
			}
			
			Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			for(RemoteServiceModule module : modules) {
				module.updateMergedProperties(ceilingEntityFullyQualifiedClassname, persistencePerspective, allMergedProperties, metadataOverrides);
			}
			ClassMetadata mergedMetadata = getMergedClassMetadata(entities, allMergedProperties);
			
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
			
			return results;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return dynamicEntityDao.getSimpleMergedProperties(entityName, persistencePerspective, dynamicEntityDao, entityClasses);
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		RemoteServiceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
		return myModule.fetch(ceilingEntityFullyQualifiedClassname, cto, persistencePerspective, customCriteria);
	}

	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		RemoteServiceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getAddType());
		return myModule.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria);
	}
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		RemoteServiceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getUpdateType());
		return myModule.update(entity, persistencePerspective, customCriteria);
	}
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		RemoteServiceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getRemoveType());
		myModule.remove(entity, persistencePerspective, customCriteria);
	}
	
	protected RemoteServiceModule getCompatibleModule(OperationType operationType) {
		RemoteServiceModule myModule = null;
		for(RemoteServiceModule module : modules) {
			if (module.isCompatible(operationType)) {
				myModule = module;
				break;
			}
		}
        if (myModule == null) {
        	LOG.error("Unable to find a compatible remote service module for the operation type: " + operationType);
			throw new RuntimeException("Unable to find a compatible remote service module for the operation type: " + operationType);
		}
        
        return myModule;
	}

	public List<CustomPersistenceHandler> getCustomPersistenceHandlers() {
		return customPersistenceHandlers;
	}

	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers) {
		this.customPersistenceHandlers = customPersistenceHandlers;
		for (RemoteServiceModule module : modules) {
			module.setCustomPersistenceHandlers(customPersistenceHandlers);
		}
	}

	public Map<String, FieldMetadata> getMetadataOverrides() {
		return metadataOverrides;
	}

	public void setMetadataOverrides(Map<String, FieldMetadata> metadataOverrides) {
		this.metadataOverrides = metadataOverrides;
	}

	public DynamicEntityDao getDynamicEntityDao() {
		return dynamicEntityDao;
	}

	public void setDynamicEntityDao(DynamicEntityDao dynamicEntityDao) {
		this.dynamicEntityDao = dynamicEntityDao;
	}

}
