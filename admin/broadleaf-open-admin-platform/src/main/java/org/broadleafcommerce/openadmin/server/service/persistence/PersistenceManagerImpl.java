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

package org.broadleafcommerce.openadmin.server.service.persistence;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.remote.AdminSecurityServiceRemote;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerFilter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component("blPersistenceManager")
@Scope("prototype")
public class PersistenceManagerImpl implements InspectHelper, PersistenceManager, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(PersistenceManagerImpl.class);

    @Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;

    @Resource(name="blCustomPersistenceHandlers")
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();

    @Resource(name="blCustomPersistenceHandlerFilters")
    protected List<CustomPersistenceHandlerFilter> customPersistenceHandlerFilters = new ArrayList<CustomPersistenceHandlerFilter>();

    @Resource(name="blTargetEntityManagers")
	protected Map<String, String> targetEntityManagers = new HashMap<String, String>();

    @Resource(name="blAdminSecurityRemoteService")
    protected AdminSecurityServiceRemote adminRemoteSecurityService;

    @Resource(name="blPersistenceModules")
    protected PersistenceModule[] modules;

    protected TargetModeType targetMode;
    protected ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        for (PersistenceModule module : modules) {
            module.setPersistenceManager(this);
        }
    }

	public void close() throws Exception {
        //do nothing
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
		return dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingClass);
	}

	@Override
	public Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
		Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
		return entities;
	}

	@Override
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		return dynamicEntityDao.getSimpleMergedProperties(entityName, persistencePerspective);
	}

	@Override
	public ClassMetadata getMergedClassMetadata(final Class<?>[] entities, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) throws ClassNotFoundException, IllegalArgumentException {
		ClassMetadata classMetadata = new ClassMetadata();
		classMetadata.setPolymorphicEntities(dynamicEntityDao.getClassTree(entities));

		List<Property> propertiesList = new ArrayList<Property>();
		for (PersistenceModule module : modules) {
			module.extractProperties(mergedProperties, propertiesList);
		}
		/*
		 * Insert inherited fields whose order has been specified
		 */
		for (int i = 0; i < entities.length - 1; i++) {
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
        classMetadata.setCurrencyCode(Money.defaultCurrency().getCurrencyCode());

		return classMetadata;
	}

	@Override
	public DynamicResultSet inspect(PersistencePackage persistencePackage) throws ServiceException, ClassNotFoundException {
		// check to see if there is a custom handler registered
		for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
			if (handler.canHandleInspect(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.INSPECT);
                }
				DynamicResultSet results = handler.inspect(persistencePackage, dynamicEntityDao, this);

				return results;
			}
		}

        adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.INSPECT);
		Class<?>[] entities = getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
		Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
		for (PersistenceModule module : modules) {
			module.updateMergedProperties(persistencePackage, allMergedProperties);
		}
		ClassMetadata mergedMetadata = getMergedClassMetadata(entities, allMergedProperties);

		DynamicResultSet results = new DynamicResultSet(mergedMetadata);

		return results;
	}

	@Override
	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleFetch(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.FETCH);
                }
                DynamicResultSet results = handler.fetch(persistencePackage, cto, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.ENTITY));
                return postFetch(results, persistencePackage);
            }
        }
        adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.FETCH);
		PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getFetchType());
		return postFetch(myModule.fetch(persistencePackage, cto), persistencePackage);
	}

    protected DynamicResultSet postFetch(DynamicResultSet resultSet, PersistencePackage persistencePackage) throws ServiceException {
        //do nothing
        return resultSet;
    }

	@Override
	public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleAdd(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.ADD);
                }
                Entity response = handler.add(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.ENTITY));
                return postAdd(response, persistencePackage);
            }
        }
        adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.ADD);
		PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getAddType());
		Entity response = myModule.add(persistencePackage);
        return postAdd(response, persistencePackage);
	}

    protected Entity postAdd(Entity entity, PersistencePackage persistencePackage) throws ServiceException {
        //do nothing
        return entity;
    }

	@Override
	public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleUpdate(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    Entity entity = persistencePackage.getEntity();
                    for (Property p : entity.getProperties()) {
                        if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                            adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.UPDATE);
                            break;
                        }
                    }
                }
                Entity response = handler.update(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.ENTITY));
                return postUpdate(response, persistencePackage);
            }
        }
        Entity entity = persistencePackage.getEntity();
        for (Property p : entity.getProperties()) {
            if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.UPDATE);
                break;
            }
        }
        PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getUpdateType());
        Entity response = myModule.update(persistencePackage);
        return postUpdate(response, persistencePackage);
    }

    protected Entity postUpdate(Entity entity, PersistencePackage persistencePackage) throws ServiceException {
        //do nothing
        return entity;
    }

	@Override
	public void remove(PersistencePackage persistencePackage) throws ServiceException {
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleRemove(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    Entity entity = persistencePackage.getEntity();
                    for (Property p : entity.getProperties()) {
                        if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                            adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.REMOVE);
                            break;
                        }
                    }
                }
                handler.remove(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.ENTITY));
                return;
            }
        }
        Entity entity = persistencePackage.getEntity();
        for (Property p : entity.getProperties()) {
            if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.REMOVE);
                break;
            }
        }
		PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getRemoveType());
		myModule.remove(persistencePackage);
	}

	public PersistenceModule getCompatibleModule(OperationType operationType) {
		PersistenceModule myModule = null;
		for (PersistenceModule module : modules) {
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

	@Override
	public DynamicEntityDao getDynamicEntityDao() {
		return dynamicEntityDao;
	}

	@Override
	public void setDynamicEntityDao(DynamicEntityDao dynamicEntityDao) {
		this.dynamicEntityDao = dynamicEntityDao;
	}

	@Override
	public Map<String, String> getTargetEntityManagers() {
		return targetEntityManagers;
	}

	@Override
	public void setTargetEntityManagers(Map<String, String> targetEntityManagers) {
		this.targetEntityManagers = targetEntityManagers;
	}

	@Override
	public TargetModeType getTargetMode() {
		return targetMode;
	}

	@Override
	public void setTargetMode(TargetModeType targetMode) {
		String targetManagerRef = targetEntityManagers.get(targetMode.getType());
		EntityManager targetManager = (EntityManager) applicationContext.getBean(targetManagerRef);
		if (targetManager == null) {
			throw new RuntimeException("Unable to find a target entity manager registered with the key: " + targetMode + ". Did you add an entity manager with this key to the targetEntityManagers property?");
		}
		dynamicEntityDao.setStandardEntityManager(targetManager);
		this.targetMode = targetMode;
	}

	@Override
	public List<CustomPersistenceHandler> getCustomPersistenceHandlers() {
        List<CustomPersistenceHandler> cloned = new ArrayList<CustomPersistenceHandler>();
        cloned.addAll(customPersistenceHandlers);
        if (getCustomPersistenceHandlerFilters() != null) {
            for (CustomPersistenceHandlerFilter filter : getCustomPersistenceHandlerFilters()) {
                Iterator<CustomPersistenceHandler> itr = cloned.iterator();
                while (itr.hasNext()) {
                    CustomPersistenceHandler handler = itr.next();
                    if (!filter.shouldUseHandler(handler.getClass().getName())) {
                        itr.remove();
                    }
                }
            }
        }
		return cloned;
	}

	@Override
	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers) {
		this.customPersistenceHandlers = customPersistenceHandlers;
	}

    public AdminSecurityServiceRemote getAdminRemoteSecurityService() {
        return adminRemoteSecurityService;
    }

    public void setAdminRemoteSecurityService(AdminSecurityServiceRemote adminRemoteSecurityService) {
        this.adminRemoteSecurityService = adminRemoteSecurityService;
    }

    public List<CustomPersistenceHandlerFilter> getCustomPersistenceHandlerFilters() {
        return customPersistenceHandlerFilters;
    }

    public void setCustomPersistenceHandlerFilters(List<CustomPersistenceHandlerFilter> customPersistenceHandlerFilters) {
        this.customPersistenceHandlerFilters = customPersistenceHandlerFilters;
    }

    public PersistenceModule[] getModules() {
        return modules;
    }

    public void setModules(PersistenceModule[] modules) {
        this.modules = modules;
    }
}
