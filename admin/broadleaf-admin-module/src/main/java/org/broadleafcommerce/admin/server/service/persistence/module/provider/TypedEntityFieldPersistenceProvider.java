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

import org.broadleafcommerce.common.admin.domain.TypedEntity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.DefaultFieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

/**
 * This field persistence provider manages the type field for {@link TypedEntity}s and ensures that they are always
 * marked as dirty when they change, regardless of PreAdd state.
 *
 * @author Jon Fleschler (jfleschler)
 */
@Component("blTypedEntityFieldPersistenceProvider")
@Scope("prototype")
public class TypedEntityFieldPersistenceProvider extends DefaultFieldPersistenceProvider {

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        super.populateValue(populateValueRequest, instance);
        return MetadataProviderResponse.HANDLED_BREAK;
    }

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        Property property = populateValueRequest.getProperty();
        if (TypedEntity.class.isAssignableFrom(instance.getClass())) {
            String typeFieldName = ((TypedEntity) instance).getTypeFieldName();
            return Objects.equals(property.getName(), typeFieldName);
        }

        return false;
    }

    @Override
    protected boolean checkDirtyState(PopulateValueRequest request, Object instance, Object checkValue) throws Exception {
        return isFieldDirty(request, instance, checkValue);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }
}
