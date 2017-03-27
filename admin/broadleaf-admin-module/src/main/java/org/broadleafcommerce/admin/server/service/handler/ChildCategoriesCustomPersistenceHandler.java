/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
import org.apache.commons.collections4.ListUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.util.Tuple;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jeff Fischer
 */
@Component("blChildCategoriesCustomPersistenceHandler")
public class ChildCategoriesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    protected static final String ALL_CHILD_CATEGORY_XREFS = "allChildCategoryXrefs";
    protected static final String ADMIN_CANT_ADD_DUPLICATE_CHILD = "admin.cantAddDuplicateChild";
    protected static final String ADMIN_CANT_ADD_CATEGORY_AS_OWN_PARENT = "admin.cantAddCategoryAsOwnParent";
    protected static final String ADMIN_CANT_ADD_ANCESTOR_AS_CHILD = "admin.cantAddAncestorAsChild";

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return Objects.equals(ALL_CHILD_CATEGORY_XREFS, persistencePackage.getSectionEntityField());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        final Tuple<Category, Category> parentAndChild = getChildAndParentCategories(persistencePackage, dynamicEntityDao);
        final Category parent = parentAndChild.getFirst();
        final Category child = parentAndChild.getSecond();
        final CategoryXref categoryXref = createXref(parentAndChild);

        if (parent.getAllChildCategoryXrefs().contains(categoryXref)) {
            throw new ServiceException(BLCMessageUtils.getMessage(ADMIN_CANT_ADD_DUPLICATE_CHILD));
        } else if (Objects.equals(child.getId(), parent.getId())) {
            throw new ServiceException(BLCMessageUtils.getMessage(ADMIN_CANT_ADD_CATEGORY_AS_OWN_PARENT));
        } else if (isChildAlreadyAnAncestor(child, parent)) {
            throw new ServiceException(BLCMessageUtils.getMessage(ADMIN_CANT_ADD_ANCESTOR_AS_CHILD));
        }
        
        return helper.getCompatibleModule(OperationType.ADORNEDTARGETLIST).add(persistencePackage);
    }
    
    protected Tuple<Category, Category> getChildAndParentCategories(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao) {
        AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePackage.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
        final String targetPath = adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty();
        final String linkedPath = adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty();

        Long parentId = Long.parseLong(persistencePackage.getEntity().findProperty(linkedPath).getValue());
        Long childId = Long.parseLong(persistencePackage.getEntity().findProperty(targetPath).getValue());

        Category parent = (Category) dynamicEntityDao.retrieve(CategoryImpl.class, parentId);
        Category child = (Category) dynamicEntityDao.retrieve(CategoryImpl.class, childId);
        
        return new Tuple<>(parent, child);
    }
    
    protected CategoryXref createXref(Tuple<Category, Category> parentAndChild) {
        CategoryXref xref = new CategoryXrefImpl();
        xref.setCategory(parentAndChild.getFirst());
        xref.setSubCategory(parentAndChild.getSecond());
        
        return xref;
    }
    
    protected boolean isChildAlreadyAnAncestor(Category child, Category parent) {
        Set<Category> knownAncestors = new HashSet<>();
        checkCategoryAncestry(parent, knownAncestors);
        
        return knownAncestors.contains(child);
    }
    
    protected void checkCategoryAncestry(Category category, Set<Category> knownAncestors) {
        List<CategoryXref> parentXrefs = ListUtils.emptyIfNull(category.getAllParentCategoryXrefs());

        knownAncestors.add(category);

        for (CategoryXref parentXref : parentXrefs) {
            final Category parentCategory = parentXref.getCategory();

            if (!knownAncestors.contains(parentCategory)) {
                checkCategoryAncestry(parentCategory, knownAncestors);
            }
        }
    }
    
 }
