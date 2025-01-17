/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.server.service.extension.CategoryCustomPersistenceHandlerExtensionManager;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

/**
 * @author jfischer
 */
@Component("blCategoryCustomPersistenceHandler")
public class CategoryCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    protected static final String DEFAULT_PARENT_CATEGORY = "defaultParentCategory";
    protected static final String ID_PROPERTY = "id";
    protected static final String CATEGORY_SEPARATOR = " -> ";
    private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);
    @Value("${allow.category.delete.with.children:false}")
    protected boolean allowCategoryDeleteWithChildren;

    @Resource(name = "blCategoryCustomPersistenceHandlerExtensionManager")
    protected CategoryCustomPersistenceHandlerExtensionManager extensionManager;

    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "categoryDirectEdit".equals(customCriteria[0])
                && Category.class.getName().equals(ceilingEntityFullyQualifiedClassname);
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
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return Category.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public DynamicResultSet inspect(
            PersistencePackage persistencePackage,
            DynamicEntityDao dynamicEntityDao,
            InspectHelper helper
    ) throws ServiceException {
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
        Entity entity = persistencePackage.getEntity();
        this.validateCategory(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Category adminInstance = (Category) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(
                    Category.class.getName(), persistencePerspective
            );
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
        } catch (Exception e) {
            LOG.error("Unable to add entity (execute persistence activity)");
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        this.validateCategory(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(
                    Category.class.getName(), persistencePerspective
            );
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Category adminInstance = (Category) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            CategoryXref oldDefault = this.getCurrentDefaultXref(adminInstance);
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
        } catch (Exception e) {
            LOG.error("Unable to update entity (execute persistence activity)");
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String id = persistencePackage.getEntity().getPMap().get("id").getValue();
        checkIfHasSubCategories(persistencePackage, id);
        List<CategoryProductXref> resultList = categoryDao.findXrefByCategoryWithDefaultReference(Long.valueOf(id));
        if (resultList.isEmpty()) {
            OperationType removeType = persistencePackage.getPersistencePerspective().getOperationTypes().getRemoveType();
            helper.getCompatibleModule(removeType).remove(persistencePackage);
        } else {
            throw new ValidationException(
                    persistencePackage.getEntity(),
                    "Unable to delete category - found that this category is primary category for some product(s)"
            );
        }
    }

    protected void validateCategory(final Entity entity) throws ValidationException {
        this.validateSelfLink(entity);
        this.validateRecursiveRelationship(entity);
    }

    protected void validateSelfLink(final Entity entity) throws ValidationException {
        final Property categoryIdProperty = entity.findProperty(ID_PROPERTY);
        final Property parentCategoryIdProperty = entity.findProperty(DEFAULT_PARENT_CATEGORY);
        if (categoryIdProperty != null && categoryIdProperty.getValue() != null
                && parentCategoryIdProperty != null) {
            final String categoryId = categoryIdProperty.getValue();
            final String parentCategoryId = parentCategoryIdProperty.getValue();
            if (categoryId.equals(parentCategoryId)) {
                entity.addValidationError(DEFAULT_PARENT_CATEGORY, "validateCategorySelfLink");
                throw new ValidationException(entity);
            }
        }
    }

    protected void validateRecursiveRelationship(final Entity entity) throws ValidationException {
        final Property categoryIdProperty = entity.findProperty(ID_PROPERTY);
        final Property parentCategoryIdProperty = entity.findProperty(DEFAULT_PARENT_CATEGORY);
        if (parentCategoryIdProperty != null && parentCategoryIdProperty.getValue() != null
                && categoryIdProperty != null && categoryIdProperty.getValue() != null) {
            final String parentCategoryId = parentCategoryIdProperty.getValue();
            final String categoryId = categoryIdProperty.getValue();
            final Category parentCategory = this.categoryDao.readCategoryById(Long.parseLong(parentCategoryId));
            final Category category = this.categoryDao.readCategoryById(Long.parseLong(categoryId));
            final StringBuilder categoryLinks = new StringBuilder();
            this.addCategoryLink(categoryLinks, category.getName());
            this.addCategoryLink(categoryLinks, parentCategory.getName());
            this.validateCategories(entity, parentCategory, Long.parseLong(categoryId), categoryLinks);
        }
    }

    protected void validateCategories(
            final Entity entity,
            final Category category,
            final Long id,
            final StringBuilder categoryLinks
    ) throws ValidationException {
        if (category != null) {
            Category parentCategory = category.getParentCategory();
            if (parentCategory != null) {
                this.addCategoryLink(categoryLinks, parentCategory.getName());
                Long originalId = this.sandBoxHelper.getOriginalId(parentCategory);
                if (id.equals(parentCategory.getId()) || id.equals(originalId)) {
                    categoryLinks.delete(categoryLinks.lastIndexOf(CATEGORY_SEPARATOR), categoryLinks.length());
                    final String errorMessage = BLCMessageUtils.getMessage(
                            "validateCategoryRecursiveRelationship", categoryLinks
                    );
                    entity.addValidationError(DEFAULT_PARENT_CATEGORY, errorMessage);
                    throw new ValidationException(entity);
                }
                this.validateCategories(entity, parentCategory, id, categoryLinks);
            }
        }
    }

    protected void addCategoryLink(final StringBuilder productLinks, final String categoryName) {
        productLinks.append(categoryName);
        productLinks.append(CATEGORY_SEPARATOR);
    }

    protected void checkIfHasSubCategories(PersistencePackage persistencePackage, String id) throws ValidationException {
        if (!allowCategoryDeleteWithChildren) {
            final List<Category> subCategories = categoryDao.readAllSubCategories(Long.valueOf(id));
            if (!subCategories.isEmpty()) {
                throw new ValidationException(
                        persistencePackage.getEntity(),
                        "Unable to delete category - found that this category is parent category for some other category(s)"
                );
            }
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        }
        return parentCategory;
    }

    protected void removeOldDefault(Category adminInstance, CategoryXref oldDefault, Entity entity) {
        if (!isDefaultCategoryLegacyMode()) {
            if (entity.findProperty(DEFAULT_PARENT_CATEGORY) != null
                    && StringUtils.isEmpty(entity.findProperty(DEFAULT_PARENT_CATEGORY).getValue())) {
                adminInstance.setParentCategory(null);
            }
            CategoryXref newDefault = this.getCurrentDefaultXref(adminInstance);
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
