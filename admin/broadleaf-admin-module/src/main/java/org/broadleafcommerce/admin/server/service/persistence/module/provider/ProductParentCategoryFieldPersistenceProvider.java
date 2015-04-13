/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.persistence.module.provider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.admin.server.service.persistence.module.provider.extension
        .ProductParentCategoryFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

/**
 * This field persistence provider manages the default CategoryProductXref reference for a Product instance through
 * the "defaultCategory" pseudo field.
 *
 * @author Jeff Fischer
 */
@Component("blProductParentCategoryFieldPersistenceProvider")
@Scope("prototype")
public class ProductParentCategoryFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Resource(name="blProductParentCategoryFieldPersistenceProviderExtensionManager")
    protected ProductParentCategoryFieldPersistenceProviderExtensionManager extensionManager;

    @Override
    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        boolean handled = false;
        if (extensionManager != null) {
            ExtensionResultStatusType result = extensionManager.getProxy().manageParentCategory(populateValueRequest.getProperty(), (Product) instance);
            handled = ExtensionResultStatusType.NOT_HANDLED != result;
        }
        if (!handled) {
            Long requestedValue = null;
            if (!StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                requestedValue = Long.parseLong(populateValueRequest.getRequestedValue());
            }
            boolean dirty = checkDirtyState((Product) instance, requestedValue);
            if (dirty) {
                populateValueRequest.getProperty().setIsDirty(true);
                if (requestedValue != null) {
                    ((Product) instance).setCategory((Category) populateValueRequest.getPersistenceManager()
                            .getDynamicEntityDao().find(CategoryImpl.class, requestedValue));
                } else {
                    ((Product) instance).setCategory(null);
                }
            }
        }
        return FieldProviderResponse.HANDLED_BREAK;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        Category category = getDefaultCategory((Product) extractValueRequest.getEntity());
        if (category != null) {
            property.setValue(String.valueOf(category.getId()));
            property.setDisplayValue(category.getName());
        }
        return FieldProviderResponse.HANDLED_BREAK;
    }

    protected boolean checkDirtyState(Product instance, Long checkValue) {
        boolean dirty = !(instance == null && checkValue == null) && (instance == null || checkValue == null);
        if (!dirty) {
            Long value = null;
            Category category = getDefaultCategory(instance);
            if (category != null) {
                value = category.getId();
            }
            dirty = value == null || !value.equals(checkValue);
        }
        return dirty;
    }

    protected Category getDefaultCategory(Product product) {
        Category response = null;
        List<CategoryProductXref> xrefs = product.getAllParentCategoryXrefs();
        if (!CollectionUtils.isEmpty(xrefs)) {
            for (CategoryProductXref xref : xrefs) {
                if (xref.getCategory().isActive() && xref.getDefaultReference() != null && xref.getDefaultReference()) {
                    response = xref.getCategory();
                    break;
                }
            }
        }
        return response;
    }

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        Property property = populateValueRequest.getProperty();
        return instance instanceof Product && property.getName().equals("defaultCategory") &&
                !ParentCategoryLegacyModeServiceImpl.getLegacyModeService().isLegacyMode();
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getEntity() instanceof Product && property.getName().equals("defaultCategory") &&
                !ParentCategoryLegacyModeServiceImpl.getLegacyModeService().isLegacyMode();
    }

    @Override
    public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE + 100;
        }
}
