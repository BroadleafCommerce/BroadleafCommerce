/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Persistence provider capable of extracting friendly display values for Money fields
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Scope("prototype")
@Component("blMoneyFieldPersistenceProvider")
public class MoneyFieldPersistenceProvider extends AbstractMoneyFieldPersistenceProvider {
    
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.MONEY;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        if (extractValueRequest.getRequestedValue() == null) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        BigDecimal value = (BigDecimal) extractValueRequest.getRequestedValue();
        property.setValue(getFormattedValue(value));
        
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        property.setDisplayValue(getFormattedDisplayValue(value, brc.getJavaLocale(), Money.defaultCurrency()));
        
        return FieldProviderResponse.HANDLED;
    }
    
    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MONEY;
    }
}
