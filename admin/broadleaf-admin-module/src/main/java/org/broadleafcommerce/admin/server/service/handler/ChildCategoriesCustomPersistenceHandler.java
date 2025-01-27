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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blChildCategoriesCustomPersistenceHandler")
public class ChildCategoriesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    protected static final String ALL_CHILD_CATEGORY_XREFS = "allChildCategoryXrefs";
    protected static final String CATEGORY_ID = "category.id";
    protected static final String SUB_CATEGORY_ID = "subCategory.id";
    protected static final String CATEGORY_SEPARATOR = " -> ";
    private static final Log LOG = LogFactory.getLog(ChildCategoriesCustomPersistenceHandler.class);
    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return Objects.equals(ALL_CHILD_CATEGORY_XREFS, persistencePackage.getSectionEntityField());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        this.validateChildCategory(persistencePackage.getEntity());
        try {
            return helper.getCompatibleModule(OperationType.ADORNEDTARGETLIST).add(persistencePackage);
        } catch (Exception e) {
            LOG.error("Unable to add entity (execute persistence activity)");
            throw new ServiceException("Unable to add entity", e);
        }
    }

    protected void validateChildCategory(final Entity entity) throws ValidationException {
        this.validateSelfLink(entity);
        this.validateDuplicateChild(entity);
        this.validateRecursiveRelationship(entity);
    }

    protected void validateSelfLink(final Entity entity) throws ValidationException {
        final Property categoryIdProperty = entity.findProperty(CATEGORY_ID);
        final Property subCategoryIdProperty = entity.findProperty(SUB_CATEGORY_ID);
        if (categoryIdProperty != null && categoryIdProperty.getValue() != null
                && subCategoryIdProperty != null) {
            final String categoryId = categoryIdProperty.getValue();
            final String subCategoryId = subCategoryIdProperty.getValue();
            if (categoryId.equals(subCategoryId)) {
                entity.addGlobalValidationError("validateCategorySelfLink");
                throw new ValidationException(entity);
            }
        }
    }

    protected void validateDuplicateChild(final Entity entity) throws ValidationException {
        final Property categoryIdProperty = entity.findProperty(CATEGORY_ID);
        final Property subCategoryIdProperty = entity.findProperty(SUB_CATEGORY_ID);
        if (categoryIdProperty != null && categoryIdProperty.getValue() != null
                && subCategoryIdProperty != null && subCategoryIdProperty.getValue() != null) {
            final String categoryId = categoryIdProperty.getValue();
            final String subCategoryId = subCategoryIdProperty.getValue();
            final Category category = this.categoryDao.readCategoryById(Long.parseLong(categoryId));
            final Category subCategory = this.categoryDao.readCategoryById(Long.parseLong(subCategoryId));
            final List<Long> childCategoryIds = category.getChildCategoryXrefs().stream()
                    .map(categoryXref -> categoryXref.getSubCategory().getId())
                    .collect(Collectors.toList());
            if (childCategoryIds.contains(subCategory.getId())) {
                entity.addGlobalValidationError("validateCategoryDuplicateChild");
                throw new ValidationException(entity);
            }
        }
    }

    protected void validateRecursiveRelationship(final Entity entity) throws ValidationException {
        final Property categoryIdProperty = entity.findProperty(CATEGORY_ID);
        final Property subCategoryIdProperty = entity.findProperty(SUB_CATEGORY_ID);
        if (categoryIdProperty != null && categoryIdProperty.getValue() != null
                && subCategoryIdProperty != null && subCategoryIdProperty.getValue() != null) {
            final Long categoryId = Long.parseLong(categoryIdProperty.getValue());
            final Long subCategoryId = Long.parseLong(subCategoryIdProperty.getValue());
            final Category category = this.categoryDao.readCategoryById(categoryId);
            final Category subCategory = this.categoryDao.readCategoryById(subCategoryId);
            final StringBuilder categoryLinks = new StringBuilder();
            this.addCategoryLink(categoryLinks, category.getName());
            this.addCategoryLink(categoryLinks, subCategory.getName());
            this.validateChildCategories(entity, subCategory, categoryId, categoryLinks);
        }
    }

    protected void validateChildCategories(
            final Entity entity,
            final Category category,
            final Long id,
            final StringBuilder categoryLinks
    ) throws ValidationException {
        if (category != null) {
            for (CategoryXref categoryXref : category.getChildCategoryXrefs()) {
                final Category subCategory = categoryXref.getSubCategory();
                if (subCategory != null) {
                    final StringBuilder newCategoryLinks = new StringBuilder(categoryLinks);
                    this.addCategoryLink(newCategoryLinks, subCategory.getName());
                    Long originalId = this.sandBoxHelper.getOriginalId(subCategory);
                    if (id.equals(subCategory.getId()) || id.equals(originalId)) {
                        newCategoryLinks.delete(newCategoryLinks.lastIndexOf(CATEGORY_SEPARATOR), newCategoryLinks.length());
                        final String errorMessage = BLCMessageUtils.getMessage(
                                "validateCategoryRecursiveRelationship", newCategoryLinks
                        );
                        entity.addGlobalValidationError(errorMessage);
                        throw new ValidationException(entity);
                    }
                    this.validateChildCategories(entity, subCategory, id, newCategoryLinks);
                }
            }
        }
    }

    protected void addCategoryLink(final StringBuilder productLinks, final String categoryName) {
        productLinks.append(categoryName);
        productLinks.append(CATEGORY_SEPARATOR);
    }

}
