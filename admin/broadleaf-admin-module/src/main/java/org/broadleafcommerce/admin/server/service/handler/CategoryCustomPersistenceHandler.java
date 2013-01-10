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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);

    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "OrphanedCategoryListDataSource".equals(customCriteria[0]);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "addNewCategory".equals(customCriteria[0]) && Category.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Category adminInstance = (Category) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Category.class.getName(), persistencePerspective);
            adminInstance = (Category) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            if (adminInstance.getDefaultParentCategory() != null && !adminInstance.getAllParentCategories().contains(adminInstance.getDefaultParentCategory())) {
                adminInstance.getAllParentCategories().add(adminInstance.getDefaultParentCategory());
            }

            adminInstance = (Category) dynamicEntityDao.merge(adminInstance);

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            LOG.error("Unable to add entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> categoryProperties = getMergedProperties(Category.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey());
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
                    StringBuilder sb = new StringBuilder(500);
                    sb.append("Cannot delete category (");
                    sb.append(category.getId());
                    sb.append(',');
                    sb.append(category.getName());
                    sb.append("). There are Categories that reference it as the default parent category. These categories must first be updated to a different default parent category before this category can be deleted. ");
                    sb.append("\nThe categories in question are: ");
                    for (Category myCategory : categories) {
                        sb.append('\n');
                        sb.append('(');
                        sb.append(myCategory.getId());
                        sb.append(',');
                        sb.append(myCategory.getName());
                        sb.append(')');
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
                    StringBuilder sb = new StringBuilder(500);
                    sb.append("Cannot delete category (");
                    sb.append(category.getId());
                    sb.append(',');
                    sb.append(category.getName());
                    sb.append("). There are Products that reference it as the default category. These products must first be updated to a different default category before this category can be deleted. ");
                    sb.append("\nThe products in question are: ");
                    for (Product product : products) {
                        sb.append('\n');
                        sb.append('(');
                        sb.append(product.getId());
                        sb.append(',');
                        sb.append(product.getName());
                        sb.append(')');
                    }
                    throw new ServiceException(sb.toString());
                }
            }
            
            dynamicEntityDao.remove(category);
        } catch (ServiceException e) {
            LOG.error("Unable to remove entity", e);
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
        
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
        return dynamicEntityDao.getMergedProperties(
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
    }
}
