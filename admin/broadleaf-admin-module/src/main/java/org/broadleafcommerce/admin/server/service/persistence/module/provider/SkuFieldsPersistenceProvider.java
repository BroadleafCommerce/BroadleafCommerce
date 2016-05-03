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

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Persistence provider for populating the display value of all Sku fields to invoke the getter if the entity property
 * itself is null. This is designed to immediately come after the {@link SkuPricingPersistenceProvider} and should not have
 * to handle any of the Money field types that occur on a Sku
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link SkuPricingPersistenceProvider}
 */
@Scope("prototype")
@Component("blSkuFieldsPersistenceProvider")
public class SkuFieldsPersistenceProvider extends FieldPersistenceProviderAdapter {

    
    @Override
    public int getOrder() {
        return SkuPricingPersistenceProvider.ORDER + 1;
    }
    
    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        Object actualValue = extractValueRequest.getRequestedValue();
        
        String value = extractValueRequest.getRecordHelper().formatValue(actualValue);
        String displayValue = value;
        if (displayValue == null) {
            try {
                displayValue = extractValueRequest.getRecordHelper().getStringValueFromGetter(extractValueRequest.getEntity(), property.getName());
                ((BasicFieldMetadata)property.getMetadata()).setDerived(true);
            } catch (Exception e) {
                //swallow all exceptions because null is fine for the display value
            }
        }
        
        property.setValue(value);
        property.setDisplayValue(displayValue);
        
        return MetadataProviderResponse.HANDLED;
    }
    
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return (
                extractValueRequest.getMetadata().getTargetClass().equals(SkuImpl.class.getName()) ||
                extractValueRequest.getMetadata().getTargetClass().equals(Sku.class.getName())
               ) 
                && !property.getName().contains(FieldManager.MAPFIELDSEPARATOR);
    }
    
}
