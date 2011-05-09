package org.broadleafcommerce.gwt.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.DynamicEntityService;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;
import org.broadleafcommerce.gwt.server.module.RemoteServiceModule;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService {
	
	private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);

	@Resource(name="blDynamicEntityDao")
	protected DynamicEntityDao dynamicEntityDao;
	
	protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();
	protected RemoteServiceModule[] modules;
	
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
	
	protected ClassMetadata getMergedClassMetadata(final Class<?>[] entities, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException, ParserConfigurationException, DOMException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException {
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
		
		List<Property> propertiesList = new ArrayList<Property>();
		for (RemoteServiceModule module : modules) {
			module.extractProperties(mergedProperties, propertiesList);
		}
		Property[] properties = new Property[propertiesList.size()];
		properties = propertiesList.toArray(properties);
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
							pos1 = Arrays.binarySearch(entities, Class.forName(o1.getInheritedFromType()), new Comparator<Class<?>>() {
								public int compare(Class<?> o1, Class<?> o2) {
									return o1.getName().compareTo(o2.getName());
								}
							});
							pos2 = Arrays.binarySearch(entities, Class.forName(o2.getInheritedFromType()), new Comparator<Class<?>>() {
								public int compare(Class<?> o1, Class<?> o2) {
									return o1.getName().compareTo(o2.getName());
								}
							});
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
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective) throws ServiceException {
		try {
			Class<?>[] entities = getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			for(RemoteServiceModule module : modules) {
				module.updateMergedProperties(ceilingEntityFullyQualifiedClassname, persistencePerspective, allMergedProperties);
			}
			ClassMetadata mergedMetadata = getMergedClassMetadata(entities, allMergedProperties, ceilingEntityFullyQualifiedClassname);
			
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
			
			return results;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
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

}
