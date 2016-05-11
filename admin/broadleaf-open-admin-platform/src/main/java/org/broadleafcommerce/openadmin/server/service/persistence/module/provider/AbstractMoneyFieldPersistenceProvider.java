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

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;

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
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        if (extractValueRequest.getRequestedValue() == null) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        if (BigDecimal.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
            property.setValue(formatValue((BigDecimal)extractValueRequest.getRequestedValue(), extractValueRequest, property));
            property.setDisplayValue(formatDisplayValue((BigDecimal) extractValueRequest.getRequestedValue(), extractValueRequest, property));
        } else {
            property.setValue(extractValueRequest.getRequestedValue().toString());
            property.setDisplayValue(extractValueRequest.getDisplayVal());
        }
        
        return MetadataProviderResponse.HANDLED_BREAK;
    }
    
    protected String formatValue(BigDecimal value, ExtractValueRequest extractValueRequest, Property property) {
        NumberFormat format = NumberFormat.getInstance(getLocale(extractValueRequest, property));
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
