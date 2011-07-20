package org.broadleafcommerce.openadmin.server.service.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PolymorphicEntity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.SandBoxInfo;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule;
import org.springframework.stereotype.Service;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

@Service("blPersistenceManager")
public class PersistenceManagerImpl implements InspectHelper, PersistenceManager {

	private static final Log LOG = LogFactory.getLog(PersistenceManagerImpl.class);
	
	@Resource(name="blSandBoxService")
	protected SandBoxService sandBoxService;
	
	@Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;
	
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();
	protected PersistenceModule[] modules;
	protected Map<TargetModeType, EntityManager> targetEntityManagers = new HashMap<TargetModeType, EntityManager>();
	protected TargetModeType targetMode;
	
	public PersistenceManagerImpl(PersistenceModule[] modules) {
		this.modules = modules;
		for (PersistenceModule module : modules) {
			module.setPersistenceManager(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getAllPolymorphicEntitiesFromCeiling(java.lang.Class)
	 */
	@Override
	public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
		return dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingClass);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getPolymorphicEntities(java.lang.String)
	 */
	@Override
	public Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
		Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
		return entities;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getSimpleMergedProperties(java.lang.String, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao, java.lang.Class)
	 */
	@Override
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return dynamicEntityDao.getSimpleMergedProperties(entityName, persistencePerspective, dynamicEntityDao, entityClasses);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getMergedClassMetadata(java.lang.Class, java.util.Map)
	 */
	@Override
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
		for (PersistenceModule module : modules) {
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
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#inspect(java.lang.String, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, java.lang.String[], java.util.Map)
	 */
	@Override
	public DynamicResultSet inspect(
			String ceilingEntityFullyQualifiedClassname,
			PersistencePerspective persistencePerspective,
			String[] customCriteria,
			Map<String, FieldMetadata> metadataOverrides)
			throws ServiceException, ClassNotFoundException {
		//check to see if there is a custom handler registered
		for (CustomPersistenceHandler handler : customPersistenceHandlers) {
			if (handler.canHandleInspect(ceilingEntityFullyQualifiedClassname, customCriteria)) {
				DynamicResultSet results = handler.inspect(ceilingEntityFullyQualifiedClassname, persistencePerspective, customCriteria, metadataOverrides, dynamicEntityDao, this);
				
				return results;
			}
		}
		
		Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
		Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
		for(PersistenceModule module : modules) {
			module.updateMergedProperties(ceilingEntityFullyQualifiedClassname, persistencePerspective, allMergedProperties, metadataOverrides);
		}
		ClassMetadata mergedMetadata = getMergedClassMetadata(entities, allMergedProperties);
		
		DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#fetch(java.lang.String, com.anasoft.os.daofusion.cto.client.CriteriaTransferObject, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, java.lang.String[])
	 */
	@Override
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname,
			CriteriaTransferObject cto,
			PersistencePerspective persistencePerspective,
			String[] customCriteria) throws ServiceException {
		PersistenceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
		return myModule.fetch(ceilingEntityFullyQualifiedClassname, cto, persistencePerspective, customCriteria);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#add(java.lang.String, org.broadleafcommerce.openadmin.client.dto.Entity, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, java.lang.String[])
	 */
	@Override
	public Entity add(String ceilingEntityFullyQualifiedClassname,
			Entity entity, PersistencePerspective persistencePerspective,
			String[] customCriteria) throws ServiceException {
		PersistenceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getAddType());
		return myModule.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#update(org.broadleafcommerce.openadmin.client.dto.Entity, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, org.broadleafcommerce.openadmin.client.dto.SandBoxInfo, java.lang.String[])
	 */
	@Override
	public Entity update(Entity entity,
			PersistencePerspective persistencePerspective,
			SandBoxInfo sandBoxInfo, String[] customCriteria)
			throws ServiceException {
		
		sandBoxService.saveSandBox(entity, persistencePerspective, sandBoxInfo);
		
		PersistenceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getUpdateType());
		return myModule.update(entity, persistencePerspective, customCriteria);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#remove(org.broadleafcommerce.openadmin.client.dto.Entity, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, java.lang.String[])
	 */
	@Override
	public void remove(Entity entity,
			PersistencePerspective persistencePerspective,
			String[] customCriteria) throws ServiceException {
		PersistenceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getRemoveType());
		myModule.remove(entity, persistencePerspective, customCriteria);
	}
	
	protected PersistenceModule getCompatibleModule(OperationType operationType) {
		PersistenceModule myModule = null;
		for(PersistenceModule module : modules) {
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

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getSandBoxService()
	 */
	@Override
	public SandBoxService getSandBoxService() {
		return sandBoxService;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#setSandBoxService(org.broadleafcommerce.openadmin.server.service.SandBoxService)
	 */
	@Override
	public void setSandBoxService(SandBoxService sandBoxService) {
		this.sandBoxService = sandBoxService;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getDynamicEntityDao()
	 */
	@Override
	public DynamicEntityDao getDynamicEntityDao() {
		return dynamicEntityDao;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#setDynamicEntityDao(org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao)
	 */
	@Override
	public void setDynamicEntityDao(DynamicEntityDao dynamicEntityDao) {
		this.dynamicEntityDao = dynamicEntityDao;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getTargetEntityManagers()
	 */
	@Override
	public Map<TargetModeType, EntityManager> getTargetEntityManagers() {
		return targetEntityManagers;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#setTargetEntityManagers(java.util.Map)
	 */
	@Override
	public void setTargetEntityManagers(
			Map<TargetModeType, EntityManager> targetEntityManagers) {
		this.targetEntityManagers = targetEntityManagers;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getTargetMode()
	 */
	@Override
	public TargetModeType getTargetMode() {
		return targetMode;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#setTargetMode(java.lang.String)
	 */
	@Override
	public void setTargetMode(TargetModeType targetMode) {
		EntityManager targetManager = targetEntityManagers.get(targetMode);
		if (targetManager == null) {
			throw new RuntimeException("Unable to find a target entity manager registered with the key: " + targetMode + ". Did you add an entity manager with this key to the targetEntityManagers property?");
		}
		dynamicEntityDao.setStandardEntityManager(targetManager);
		this.targetMode = targetMode;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#getCustomPersistenceHandlers()
	 */
	@Override
	public List<CustomPersistenceHandler> getCustomPersistenceHandlers() {
		return customPersistenceHandlers;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager#setCustomPersistenceHandlers(java.util.List)
	 */
	@Override
	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers) {
		this.customPersistenceHandlers = customPersistenceHandlers;
	}
}
