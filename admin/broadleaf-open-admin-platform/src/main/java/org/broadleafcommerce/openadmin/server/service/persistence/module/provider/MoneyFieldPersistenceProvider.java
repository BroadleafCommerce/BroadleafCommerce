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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Persistence provider capable of extracting friendly display values for Money fields
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Scope("prototype")
@Component("blMoneyFieldPersistenceProvider")
public class MoneyFieldPersistenceProvider extends PersistenceProviderAdapter {
    
    
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.MONEY;
    }

    @Override
    public boolean extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return false;
        }
        
        if (extractValueRequest.getRequestedValue() == null) {
            return false;
        }
        
        // Set the raw value
        BigDecimal bd = (BigDecimal) extractValueRequest.getRequestedValue();
        NumberFormat valFormat = NumberFormat.getInstance();
        valFormat.setMaximumFractionDigits(2);
        valFormat.setMinimumFractionDigits(2);
        String val = valFormat.format(bd);
        property.setValue(val);
        
        // Set the friendly, currency-formatted price
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        NumberFormat format = NumberFormat.getCurrencyInstance(brc.getJavaLocale());
        format.setCurrency(Money.defaultCurrency());
        BigDecimal value = new BigDecimal(val);
        property.setDisplayValue(format.format(value));
        
        return true;
    }
    
    @Override
    public int getOrder() {
        return PersistenceProvider.MONEY;
    }
}
