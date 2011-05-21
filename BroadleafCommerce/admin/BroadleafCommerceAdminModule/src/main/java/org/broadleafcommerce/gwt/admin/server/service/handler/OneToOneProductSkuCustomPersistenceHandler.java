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
package org.broadleafcommerce.gwt.admin.server.service.handler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuCustomPersistenceHandler implements CustomPersistenceHandler {

	public static final String IDENTITYCRITERIA = "OneToOneProductSku";
	private static final Log LOG = LogFactory.getLog(OneToOneProductSkuCustomPersistenceHandler.class);
	
	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		try {
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
				null,
				""
			);
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			
			Map<String, FieldMetadata> skuMergedProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
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
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
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
				null,
				""
			);
			BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, Product.class.getName(), productProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, Product.class.getName());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Product.class);
			Entity[] entities = helper.getRecords(productProperties, records, null, null);
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
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

	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Product productInstance = (Product) Class.forName(entity.getType()[0]).newInstance();
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
			productInstance = (Product) helper.createPopulatedInstance(productInstance, entity, productProperties, false);
			
			Sku skuInstance;
			Class<?> configuredEntity = helper.getFieldManager().getEntityConfiguration().lookupEntityClass(Sku.class.getName());
			if (configuredEntity != null) {
				skuInstance = (Sku) helper.getFieldManager().getEntityConfiguration().createEntityInstance(Sku.class.getName());
			} else {
				Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Sku.class);
				skuInstance = (Sku) entities[0].newInstance();
			}
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
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

	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
			Object primaryKey = helper.getPrimaryKey(entity, productProperties);
			Product productInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			Sku skuInstance = productInstance.getAllSkus().get(0);
			dynamicEntityDao.remove(skuInstance);
			dynamicEntityDao.remove(productInstance);
		} catch (Exception e) {
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
	}

	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Map<String, FieldMetadata> productProperties = getMergedProperties(Product.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
			Object primaryKey = helper.getPrimaryKey(entity, productProperties);
			Product productInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			productInstance = (Product) helper.createPopulatedInstance(productInstance, entity, productProperties, false);
			
			Sku skuInstance = productInstance.getAllSkus().get(0);
			Map<String, FieldMetadata> skuProperties = getMergedProperties(Sku.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
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
	
	protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
			null,
			""
		);
		
		return mergedProperties;
	}

}
