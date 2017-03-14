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
