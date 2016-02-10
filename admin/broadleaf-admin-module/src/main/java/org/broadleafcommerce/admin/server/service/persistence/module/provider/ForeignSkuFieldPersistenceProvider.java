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
