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
package org.broadleafcommerce.admin.server.service.persistence.module.provider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.admin.server.service.persistence.module.provider.extension.CategoryParentCategoryFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

/**
 * This field persistence provider manages the default CategoryXref reference for a Category instance through
 * the "defaultParentCategory" pseudo field.
 *
 * @author Jeff Fischer
 */
@Component("blCategoryParentCategoryFieldPersistenceProvider")
@Scope("prototype")
public class CategoryParentCategoryFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Resource(name="blCategoryParentCategoryFieldPersistenceProviderExtensionManager")
    protected CategoryParentCategoryFieldPersistenceProviderExtensionManager extensionManager;

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        boolean handled = false;
        if (extensionManager != null) {
            ExtensionResultStatusType result = extensionManager.getProxy().manageParentCategory(populateValueRequest.getProperty(), (Category) instance);
            handled = ExtensionResultStatusType.NOT_HANDLED != result;
        }
        if (!handled || BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            Long requestedValue = null;
            if (!StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                requestedValue = Long.parseLong(populateValueRequest.getRequestedValue());
            }
            boolean dirty = checkDirtyState((Category) instance, requestedValue);
            if (dirty) {
                populateValueRequest.getProperty().setIsDirty(true);
                if (requestedValue != null) {
                    ((Category) instance).setParentCategory((Category) populateValueRequest.getPersistenceManager()
                            .getDynamicEntityDao().find(CategoryImpl.class, requestedValue));
                } else {
                    ((Category) instance).setParentCategory(null);
                }
            }
        }
        return MetadataProviderResponse.HANDLED_BREAK;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        Category category = getDefaultCategory((Category) extractValueRequest.getEntity());
        if (category != null) {
            property.setValue(String.valueOf(category.getId()));
            property.setDisplayValue(category.getName());
        }
        return MetadataProviderResponse.HANDLED_BREAK;
    }

    protected boolean checkDirtyState(Category instance, Long checkValue) {
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

    protected Category getDefaultCategory(Category category) {
        Category response = null;
        List<CategoryXref> xrefs = category.getAllParentCategoryXrefs();
        if (!CollectionUtils.isEmpty(xrefs)) {
            for (CategoryXref xref : xrefs) {
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
        return instance instanceof Category && property.getName().equals("defaultParentCategory") &&
                !ParentCategoryLegacyModeServiceImpl.getLegacyModeService().isLegacyMode();
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getEntity() instanceof Category && property.getName().equals("defaultParentCategory") &&
                !ParentCategoryLegacyModeServiceImpl.getLegacyModeService().isLegacyMode();
    }

    @Override
    public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE + 100;
        }
}
