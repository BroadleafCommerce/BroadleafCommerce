/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.util.TypedPredicate;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }
    
    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }
    
    public Boolean canHandle(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            Class testClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            return Category.class.isAssignableFrom(testClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Category adminInstance = (Category) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Category.class.getName(), persistencePerspective);
            adminInstance = (Category) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            CategoryXref categoryXref = new CategoryXrefImpl();
            categoryXref.setCategory(adminInstance.getDefaultParentCategory());
            categoryXref.setSubCategory(adminInstance);
            if (adminInstance.getDefaultParentCategory() != null && !adminInstance.getAllParentCategoryXrefs().contains(categoryXref)) {
                adminInstance.getAllParentCategoryXrefs().add(categoryXref);
            }

            adminInstance = (Category) dynamicEntityDao.merge(adminInstance);

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }
    
    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Category.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Category adminInstance = (Category) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            
            final Long oldDefaultParentCategoryId = getDefaultCategoryId(adminInstance);

            adminInstance = (Category) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            adminInstance = (Category) dynamicEntityDao.merge(adminInstance);
            
            Long newDefaultParentCategoryId = getDefaultCategoryId(adminInstance);
            
            boolean categoryIdChanged = ObjectUtils.notEqual(oldDefaultParentCategoryId, newDefaultParentCategoryId);
            if (categoryIdChanged) {
                CategoryXref categoryXref = new CategoryXrefImpl();
                categoryXref.setCategory(adminInstance.getDefaultParentCategory());
                categoryXref.setSubCategory(adminInstance);
                if (adminInstance.getDefaultParentCategory() != null && !adminInstance.getAllParentCategoryXrefs().contains(categoryXref)) {
                    adminInstance.getAllParentCategoryXrefs().add(categoryXref);
                }
                
                //remove the old relationship
                if (oldDefaultParentCategoryId != null) {
                    CategoryXref oldXref = (CategoryXref)CollectionUtils.find(adminInstance.getAllParentCategoryXrefs(), new TypedPredicate<CategoryXref>() {
                        
                        @Override
                        public boolean eval(CategoryXref xref) {
                            return oldDefaultParentCategoryId.equals(xref.getCategory().getId());
                        }
                    });

                    if (oldXref != null) {
                        oldXref.getSubCategory().getAllParentCategoryXrefs().remove(oldXref);
                        dynamicEntityDao.remove(oldXref);
                    }
                }
            }
            
            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }
    
    /**
     * Returns the default parent category ID for the given category or null if there is not one set
     */
    public Long getDefaultCategoryId(Category category) {
        if (category.getDefaultParentCategory() != null) {
            return category.getDefaultParentCategory().getId();
        }
        return null;
    }
    

}
