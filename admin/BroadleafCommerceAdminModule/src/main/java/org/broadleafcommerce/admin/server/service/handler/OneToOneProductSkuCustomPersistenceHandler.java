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
package org.broadleafcommerce.admin.server.service.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

	public static final String IDENTITYCRITERIA = "OneToOneProductSku";
	private static final Log LOG = LogFactory.getLog(OneToOneProductSkuCustomPersistenceHandler.class);
	
	public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        boolean canHandle = false;
        if (customCriteria != null) {
            for (String criteria : customCriteria) {
                if (criteria != null && criteria.equals(IDENTITYCRITERIA)) {
                    canHandle = true;
                    break;
                }
            }
        }
		return canHandle;
	}

	public Boolean canHandleAdd(PersistencePackage persistencePackage) {
		return canHandleFetch(persistencePackage);
	}

	public Boolean canHandleRemove(PersistencePackage persistencePackage) {
		return canHandleFetch(persistencePackage);
	}

	public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
		return canHandleFetch(persistencePackage);
	}

	public Boolean canHandleInspect(PersistencePackage persistencePackage) {
		return canHandleFetch(persistencePackage);
	}

	public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Product.class);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				Product.class.getName(), 
				entityClasses, 
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
			
			Map<String, FieldMetadata> skuMergedProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			String[] keys = skuMergedProperties.keySet().toArray(new String[]{});
			int order = 29;
			for (String key : keys) {
				String newKey = new String("allSkus." + key);
				FieldMetadata metadata = skuMergedProperties.remove(key);
				if (
					key.equals("activeStartDate") || 
					key.equals("activeEndDate") || 
					key.equals("name") || 
					key.equals("description") ||
					key.equals("longDescription")
				) {
					metadata.getPresentationAttributes().setHidden(true);
				}
				if (metadata.getPresentationAttributes() != null) {
					metadata.getPresentationAttributes().setProminent(false);
					if (metadata.getPresentationAttributes().getOrder() != null) {
						metadata.getPresentationAttributes().setOrder(metadata.getPresentationAttributes().getOrder() + order);
					}
				}
				skuMergedProperties.put(newKey, metadata);
			}
			mergedProperties.putAll(skuMergedProperties);
			
			Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Product.class);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entities, allMergedProperties);
			
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
			
			return results;
		} catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
		}
	}

	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Product.class);
			Map<String, FieldMetadata> productProperties = dynamicEntityDao.getMergedProperties(
				Product.class.getName(), 
				entityClasses, 
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
			BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, Product.class.getName(), productProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, Product.class.getName());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Product.class);
			Entity[] entities = helper.getRecords(productProperties, records, null, null);
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			List<Serializable> skus = new ArrayList<Serializable>();
			for (Serializable record : records) {
				Sku sku = ((Product) record).getAllSkus().get(0);
				skus.add(sku);
			}
			Entity[] skuEntities = helper.getRecords(skuProperties, skus, null, null);
			for (int j=0;j<entities.length;j++) {
				entities[j].mergeProperties("allSkus", skuEntities[j]);
				String[] both = (String[]) ArrayUtils.addAll(entities[j].getType(), skuEntities[j].getType());
				entities[j].setType(both);
			}
		
			int totalRecords = helper.getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
			
			DynamicResultSet response = new DynamicResultSet(null, entities, totalRecords);
			
			return response;
		} catch (Exception e) {
			throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Product productInstance = (Product) Class.forName(entity.getType()[0]).newInstance();
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			productInstance = (Product) helper.createPopulatedInstance(productInstance, entity, productProperties, false);
			
			Sku skuInstance;
			Class<?> configuredEntity = helper.getFieldManager().getEntityConfiguration().lookupEntityClass(Sku.class.getName());
			if (configuredEntity != null) {
				skuInstance = (Sku) helper.getFieldManager().getEntityConfiguration().createEntityInstance(Sku.class.getName());
			} else {
				Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Sku.class);
				skuInstance = (Sku) entities[0].newInstance();
			}
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			for (Property property : entity.getProperties()) {
				if (property.getName().startsWith("allSkus.")) {
					property.setName(property.getName().substring("allSkus.".length(), property.getName().length()));
				}
			}
			skuInstance = (Sku) helper.createPopulatedInstance(skuInstance, entity, skuProperties, false);
			
			dynamicEntityDao.persist(skuInstance);
			productInstance.getAllSkus().add(skuInstance);
			dynamicEntityDao.persist(productInstance);
			
			Entity productEntity = helper.getRecord(productProperties, productInstance, null, null);
			Entity skuEntity = helper.getRecord(skuProperties, skuInstance, null, null);
			productEntity.mergeProperties("allSkus", skuEntity);
			
			return productEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
	}

	public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			Object primaryKey = helper.getPrimaryKey(entity, productProperties);
			Product productInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			Sku skuInstance = productInstance.getAllSkus().get(0);
			dynamicEntityDao.remove(skuInstance);
			dynamicEntityDao.remove(productInstance);
		} catch (Exception e) {
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
	}

	public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			Object primaryKey = helper.getPrimaryKey(entity, productProperties);
			Product productInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			productInstance = (Product) helper.createPopulatedInstance(productInstance, entity, productProperties, false);
			
			Sku skuInstance = productInstance.getAllSkus().get(0);
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
			for (Property property : entity.getProperties()) {
				if (property.getName().startsWith("allSkus.")) {
					property.setName(property.getName().substring("allSkus.".length(), property.getName().length()));
				}
			}
			skuInstance = (Sku) helper.createPopulatedInstance(skuInstance, entity, skuProperties, false);
			
			dynamicEntityDao.merge(productInstance);
			
			Entity productEntity = helper.getRecord(productProperties, productInstance, null, null);
			Entity skuEntity = helper.getRecord(skuProperties, skuInstance, null, null);
			productEntity.mergeProperties("allSkus", skuEntity);
			
			return productEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
	}
	
	protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(), 
			entities, 
			null, 
			new String[]{}, 
			new ForeignKey[]{},
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields, 
			excludeManyToOneFields,
            configurationKey,
			""
		);
		
		return mergedProperties;
	}

}
