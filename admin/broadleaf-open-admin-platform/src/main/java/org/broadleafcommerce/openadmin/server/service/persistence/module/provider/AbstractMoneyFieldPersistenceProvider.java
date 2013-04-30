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

import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Abstract persistence provider that provides a method to actually handle formatting moneys.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class AbstractMoneyFieldPersistenceProvider extends FieldPersistenceProviderAdapter {
    
    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        if (extractValueRequest.getRequestedValue() == null) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        
        BigDecimal value = (BigDecimal) extractValueRequest.getRequestedValue();
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        property.setValue(format.format(value));
        
        Locale locale = getLocale(extractValueRequest, property);
        Currency currency = getCurrency(extractValueRequest, property);
        format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        property.setDisplayValue(format.format(value));
        
        return FieldProviderResponse.HANDLED_BREAK;
    }
    
    protected abstract boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property);
    
    protected abstract Locale getLocale(ExtractValueRequest extractValueRequest, Property property);
    
    protected abstract Currency getCurrency(ExtractValueRequest extractValueRequest, Property property);
    
}
