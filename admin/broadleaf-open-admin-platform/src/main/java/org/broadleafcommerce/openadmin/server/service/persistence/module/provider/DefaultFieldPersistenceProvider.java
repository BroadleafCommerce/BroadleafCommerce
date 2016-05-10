/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import java.io.Serializable;

import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultFieldPersistenceProvider")
@Scope("prototype")
public class DefaultFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        boolean dirty;
        try {
            Property p = populateValueRequest.getProperty();
            Object value = populateValueRequest.getFieldManager().getFieldValue(instance, p.getName());

            if (value != null) {
                p.setOriginalValue(String.valueOf(value));
                p.setOriginalDisplayValue(p.getOriginalValue());
            }

            dirty = checkDirtyState(populateValueRequest, instance, populateValueRequest.getRequestedValue());
            populateValueRequest.getFieldManager().setFieldValue(instance,
                    populateValueRequest.getProperty().getName(), populateValueRequest.getRequestedValue());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        populateValueRequest.getProperty().setIsDirty(dirty);
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (extractValueRequest.getRequestedValue() != null) {
            String val = extractValueRequest.getRequestedValue().toString();
            property.setValue(val);
            property.setDisplayValue(extractValueRequest.getDisplayVal());
        }
        return MetadataProviderResponse.HANDLED;
    }

}
