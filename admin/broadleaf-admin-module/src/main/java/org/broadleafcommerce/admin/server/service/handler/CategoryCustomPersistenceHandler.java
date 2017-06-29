/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.server.service.extension.CategoryCustomPersistenceHandlerExtensionManager;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Component("blCategoryCustomPersistenceHandler")
public class CategoryCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);
    
    protected static final String DEFAULT_PARENT_CATEGORY = "defaultParentCategory";

    @Resource(name = "blCategoryCustomPersistenceHandlerExtensionManager")
    protected CategoryCustomPersistenceHandlerExtensionManager extensionManager;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "categoryDirectEdit".equals(customCriteria[0]) && Category.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        Map<String, FieldMetadata> md = getMetadata(persistencePackage, helper);

        if (!isDefaultCategoryLegacyMode()) {
            md.remove("allParentCategoryXrefs");

            BasicFieldMetadata defaultCategory = ((BasicFieldMetadata) md.get(DEFAULT_PARENT_CATEGORY));
            defaultCategory.setFriendlyName("CategoryImpl_ParentCategory");
        }

        return getResultSet(persistencePackage, helper, md);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            entity = validateParentCategory(entity, true);

            if (entity.isValidationFailure()) {
                return entity;
            } else {
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
                Category adminInstance = (Category) Class.forName(entity.getType()[0]).newInstance();
                Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Category.class.getName(), persistencePerspective);
                adminInstance = (Category) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
                adminInstance = dynamicEntityDao.merge(adminInstance);
                boolean handled = false;
                if (extensionManager != null) {
                    ExtensionResultStatusType result = extensionManager.getProxy()
                            .manageParentCategoryForAdd(persistencePackage, adminInstance);
                    handled = ExtensionResultStatusType.NOT_HANDLED != result;
                }
                if (!handled) {
                    setupXref(adminInstance);
                }
                adminInstance = dynamicEntityDao.merge(adminInstance);

                return helper.getRecord(adminProperties, adminInstance, null, null);
            }
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    protected Entity validateParentCategory(Entity entity, boolean isAdd) {
        String defaultParentCategoryId = (entity.findProperty(DEFAULT_PARENT_CATEGORY) != null)
                ? entity.findProperty(DEFAULT_PARENT_CATEGORY).getValue() : null;
        String categoryId = entity.findProperty("id").getValue();

        if (!isAdd && Objects.equals(defaultParentCategoryId, categoryId)) {
            entity.addValidationError(DEFAULT_PARENT_CATEGORY, "admin.cantAddCategoryAsOwnParent");
        }

        return entity;
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        
        try {
            entity = validateParentCategory(entity, false);
            
            if (entity.isValidationFailure()) {
                return entity;
            } else {
                PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
                Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Category.class.getName(), persistencePerspective);
                Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
                Category adminInstance = (Category) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
                CategoryXref oldDefault = getCurrentDefaultXref(adminInstance);
                adminInstance = (Category) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
                adminInstance = dynamicEntityDao.merge(adminInstance);
                boolean handled = false;
                if (extensionManager != null) {
                    ExtensionResultStatusType result = extensionManager.getProxy()
                            .manageParentCategoryForUpdate(persistencePackage, adminInstance);
                    handled = ExtensionResultStatusType.NOT_HANDLED != result;
                }
                if (!handled) {
                    setupXref(adminInstance);
                    removeOldDefault(adminInstance, oldDefault, entity);
                }

                return helper.getRecord(adminProperties, adminInstance, null, null);
            }
        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    protected Boolean isDefaultCategoryLegacyMode() {
        ParentCategoryLegacyModeService legacyModeService = ParentCategoryLegacyModeServiceImpl.getLegacyModeService();
        if (legacyModeService != null) {
            return legacyModeService.isLegacyMode();
        }
        return false;
    }

    protected Category getExistingDefaultCategory(Category category) {
        //Make sure we get the actual field value - not something manipulated in the getter
        Category parentCategory;
        try {
            Field defaultCategory = CategoryImpl.class.getDeclaredField(DEFAULT_PARENT_CATEGORY);
            defaultCategory.setAccessible(true);
            parentCategory = (Category) defaultCategory.get(category);
        } catch (NoSuchFieldException e) {
            throw ExceptionHelper.refineException(e);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        }
        return parentCategory;
    }

    protected void removeOldDefault(Category adminInstance, CategoryXref oldDefault, Entity entity) {
        if (!isDefaultCategoryLegacyMode()) {
            if (entity.findProperty(DEFAULT_PARENT_CATEGORY) != null && StringUtils.isEmpty(entity.findProperty(DEFAULT_PARENT_CATEGORY).getValue())) {
                adminInstance.setParentCategory(null);
            }
            CategoryXref newDefault = getCurrentDefaultXref(adminInstance);
            if (oldDefault != null && !oldDefault.equals(newDefault)) {
                adminInstance.getAllParentCategoryXrefs().remove(oldDefault);
            }
        }
    }

    protected void setupXref(Category adminInstance) {
        if (isDefaultCategoryLegacyMode()) {
            CategoryXref categoryXref = new CategoryXrefImpl();
            categoryXref.setCategory(getExistingDefaultCategory(adminInstance));
            categoryXref.setSubCategory(adminInstance);
            if (!adminInstance.getAllParentCategoryXrefs().contains(categoryXref) && categoryXref.getCategory() != null) {
                adminInstance.getAllParentCategoryXrefs().add(categoryXref);
            }
        }
    }

    protected CategoryXref getCurrentDefaultXref(Category category) {
        CategoryXref currentDefault = null;
        List<CategoryXref> xrefs = category.getAllParentCategoryXrefs();
        if (!CollectionUtils.isEmpty(xrefs)) {
            for (CategoryXref xref : xrefs) {
                if (xref.getCategory().isActive() && xref.getDefaultReference() != null && xref.getDefaultReference()) {
                    currentDefault = xref;
                    break;
                }
            }
        }
        return currentDefault;
    }
}
