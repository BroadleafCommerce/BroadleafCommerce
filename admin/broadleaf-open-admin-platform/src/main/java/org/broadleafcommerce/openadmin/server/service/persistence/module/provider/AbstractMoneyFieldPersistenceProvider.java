/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
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
        
        property.setValue(formatValue((BigDecimal)extractValueRequest.getRequestedValue(), extractValueRequest, property));
        property.setDisplayValue(formatDisplayValue((BigDecimal)extractValueRequest.getRequestedValue(), extractValueRequest, property));
        
        return FieldProviderResponse.HANDLED_BREAK;
    }
    
    protected String formatValue(BigDecimal value, ExtractValueRequest extractValueRequest, Property property) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);
        return format.format(value);
    }
    
    protected String formatDisplayValue(BigDecimal value, ExtractValueRequest extractValueRequest, Property property) {
        Locale locale = getLocale(extractValueRequest, property);
        Currency currency = getCurrency(extractValueRequest, property);
        return BroadleafCurrencyUtils.getNumberFormatFromCache(locale, currency).format(value);
    }
    
    protected abstract boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property);
    
    protected abstract Locale getLocale(ExtractValueRequest extractValueRequest, Property property);
    
    protected abstract Currency getCurrency(ExtractValueRequest extractValueRequest, Property property);
    
}
