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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component("blForeignSkuFieldPersistenceProvider")
public class ForeignSkuFieldPersistenceProvider extends FieldPersistenceProviderAdapter {
    
    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        try {
            String val = extractValueRequest.getFieldManager().getFieldValue(extractValueRequest.getRequestedValue(), 
                    extractValueRequest.getMetadata().getForeignKeyProperty()).toString();
            String displayVal = null;
            
            if (!StringUtils.isEmpty(extractValueRequest.getMetadata().getForeignKeyDisplayValueProperty())) {
                String nameProperty = extractValueRequest.getMetadata().getForeignKeyDisplayValueProperty();
                Sku sku = (Sku) extractValueRequest.getRequestedValue();
                displayVal = extractValueRequest.getRecordHelper().getStringValueFromGetter(sku, nameProperty);
            }
            
            extractValueRequest.setDisplayVal(displayVal);
            
            property.setValue(val);
            property.setDisplayValue(displayVal);
        } catch (Exception e) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        return MetadataProviderResponse.HANDLED_BREAK;
    }
    
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        String fkc = extractValueRequest.getMetadata().getForeignKeyClass();
        String rvc = null;
        if (extractValueRequest.getRequestedValue() != null) {
            rvc = extractValueRequest.getRequestedValue().getClass().getName();
        }
        
        return (SkuImpl.class.getName().equals(fkc) || Sku.class.getName().equals(fkc)) &&
                (SkuImpl.class.getName().equals(rvc) || Sku.class.getName().equals(rvc)) &&
                extractValueRequest.getMetadata().getFieldType().equals(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
    }

}
