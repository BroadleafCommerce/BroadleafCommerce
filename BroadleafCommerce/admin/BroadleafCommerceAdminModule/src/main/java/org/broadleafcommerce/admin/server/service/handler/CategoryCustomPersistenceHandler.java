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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryCustomPersistenceHandler implements CustomPersistenceHandler {
	
	private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);

	public Boolean canHandleFetch(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleAdd(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleRemove(PersistencePackage persistencePackage) {
		String[] customCriteria = persistencePackage.getCustomCriteria();
		return customCriteria != null && customCriteria.length > 0 && customCriteria[0].equals("OrphanedCategoryListDataSource");
	}

	public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleInspect(PersistencePackage persistencePackage) {
		return false;
	}

	public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom add not supported");
	}

	public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<String, FieldMetadata> categoryProperties = getMergedProperties(Category.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
			Object primaryKey = helper.getPrimaryKey(entity, categoryProperties);
			Category category = (Category) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			
			{
				//Use JPA 2.0 criteria support to load all Categories whose defaultParentCategory equals this one
				CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
				CriteriaQuery<Category> query = criteriaBuilder.createQuery(Category.class);
				Root<CategoryImpl> root= query.from(CategoryImpl.class);
				query.where(criteriaBuilder.equal(root.get("defaultParentCategory"), category)); 
				query.select(root); 
				TypedQuery<Category> categoryQuery = dynamicEntityDao.getStandardEntityManager().createQuery(query); 
				List<Category> categories = categoryQuery.getResultList();
				if (!categories.isEmpty()) {
					StringBuffer sb = new StringBuffer();
					sb.append("Cannot delete category (");
					sb.append(category.getId());
					sb.append(",");
					sb.append(category.getName());
					sb.append("). There are Categories that reference it as the default parent category. These categories must first be updated to a different default parent category before this category can be deleted. ");
					sb.append("\nThe categories in question are: ");
					for (Category myCategory : categories) {
						sb.append("\n");
						sb.append("(");
						sb.append(myCategory.getId());
						sb.append(",");
						sb.append(myCategory.getName());
						sb.append(")");
					}
					throw new ServiceException(sb.toString());
				}
			}
			
			{
				//Use JPA 2.0 criteria support to load all Products whose defaultCategory equals this one
				CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
				CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
				Root<ProductImpl> root= query.from(ProductImpl.class);
				query.where(criteriaBuilder.equal(root.get("defaultCategory"), category)); 
				query.select(root); 
				TypedQuery<Product> productQuery = dynamicEntityDao.getStandardEntityManager().createQuery(query); 
				List<Product> products = productQuery.getResultList();
				if (!products.isEmpty()) {
					StringBuffer sb = new StringBuffer();
					sb.append("Cannot delete category (");
					sb.append(category.getId());
					sb.append(",");
					sb.append(category.getName());
					sb.append("). There are Products that reference it as the default category. These products must first be updated to a different default category before this category can be deleted. ");
					sb.append("\nThe products in question are: ");
					for (Product product : products) {
						sb.append("\n");
						sb.append("(");
						sb.append(product.getId());
						sb.append(",");
						sb.append(product.getName());
						sb.append(")");
					}
					throw new ServiceException(sb.toString());
				}
			}
			
			dynamicEntityDao.remove(category);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
		
	}

	public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom update not supported");
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
