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

import org.apache.commons.beanutils.PropertyUtils;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.AbstractMoneyFieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Persistence provider capable of extracting friendly display values for Sku prices, taking currency into consideration.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Scope("prototype")
@Component("blSkuPricingPersistenceProvider")
public class SkuPricingPersistenceProvider extends AbstractMoneyFieldPersistenceProvider {
    
    public static int ORDER = FieldPersistenceProvider.MONEY - 1000;
    
    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        
        Object displayValue = extractValueRequest.getRequestedValue();
        if (displayValue == null) {
            try {
                displayValue = PropertyUtils.getProperty(extractValueRequest.getEntity(), property.getName());
                ((BasicFieldMetadata)property.getMetadata()).setDerived(true);
            } catch (Exception e) {
                //swallow all exceptions because null is fine for the display value
            }
        }
        Object actualValue = extractValueRequest.getRequestedValue();
        
        property.setValue(formatValue(actualValue, extractValueRequest, property));
        property.setDisplayValue(formatDisplayValue(displayValue, extractValueRequest, property));

        return MetadataProviderResponse.HANDLED_BREAK;
    }
    
    protected String formatValue(Object value, ExtractValueRequest extractValueRequest, Property property) {
        if (value == null) {
            return null;
        }
        BigDecimal decimalValue = (value instanceof Money) ? ((Money)value).getAmount() : (BigDecimal) value;
        return super.formatValue(decimalValue, extractValueRequest, property);
    }
    
    protected String formatDisplayValue(Object value, ExtractValueRequest extractValueRequest, Property property) {
        if (value == null) {
            return null;
        }
        BigDecimal decimalValue = (value instanceof Money) ? ((Money)value).getAmount() : (BigDecimal) value;
        return super.formatDisplayValue(decimalValue, extractValueRequest, property);
    }
    
    /**
     * Handle all fields that have declared themselves to be apart of a Sku and have a field type of Money
     *  
     * @param extractValueRequest
     * @param property
     * @return whether or not we can handle extraction
     */
    @Override
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return (
                extractValueRequest.getMetadata().getTargetClass().equals(SkuImpl.class.getName()) ||
                extractValueRequest.getMetadata().getTargetClass().equals(Sku.class.getName())
               ) 
                && !property.getName().contains(FieldManager.MAPFIELDSEPARATOR)
                && SupportedFieldType.MONEY.equals(extractValueRequest.getMetadata().getFieldType());
    }
    
    protected boolean isDefaultSkuProperty(ExtractValueRequest extractValueRequest, Property property) {
        return property.getName().startsWith("defaultSku");
    }
    
    @Override
    protected Locale getLocale(ExtractValueRequest extractValueRequest, Property property) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        return brc.getJavaLocale();
    }

    @Override
    protected Currency getCurrency(ExtractValueRequest extractValueRequest, Property property) {
        BroadleafCurrency bc = null;
        if (extractValueRequest.getEntity() instanceof Product && isDefaultSkuProperty(extractValueRequest, property)) {
            Product p = (Product) extractValueRequest.getEntity();
            bc = p.getDefaultSku().getCurrency();
        } else if (extractValueRequest.getEntity() instanceof Sku) {
            Sku s = (Sku) extractValueRequest.getEntity();
            bc = s.getCurrency();
        }
        
        if (bc == null) {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            return brc.getJavaCurrency();
        } else {
            return Currency.getInstance(bc.getCurrencyCode());
        }
    }
}
