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

package org.broadleafcommerce.admin.server.service.persistence.module.provider;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.AbstractMoneyFieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    
    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MONEY - 1000;
    }

    /**
     * We understand how to handle two different types of records:
     *  - SkuImpl fields retailPrice and salePrice
     *  - ProductImpl fields defaultSku.retailPrice and defaultSku.salePrice
     *  
     * @param extractValueRequest
     * @param property
     * @return whether or not we can handle extraction
     */
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return isDefaultSkuPrice(extractValueRequest, property) || isAdditionalSkuPrice(extractValueRequest, property);
    }
    
    protected boolean isDefaultSkuPrice(ExtractValueRequest extractValueRequest, Property property) {
        return (extractValueRequest.getMetadata().getTargetClass().equals(SkuImpl.class.getName()) 
            && (property.getName().equals("defaultSku.retailPrice") || property.getName().equals("defaultSku.salePrice")));
    }
    
    protected boolean isAdditionalSkuPrice(ExtractValueRequest extractValueRequest, Property property) {
        return (extractValueRequest.getMetadata().getTargetClass().equals(SkuImpl.class.getName()) 
            && (property.getName().equals("retailPrice") || property.getName().equals("salePrice")));
    }
    
    @Override
    protected Locale getLocale(ExtractValueRequest extractValueRequest, Property property) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        return brc.getJavaLocale();
    }

    @Override
    protected Currency getCurrency(ExtractValueRequest extractValueRequest, Property property) {
        BroadleafCurrency bc;
        if (isDefaultSkuPrice(extractValueRequest, property)) {
            Product p = (Product) extractValueRequest.getEntity();
            bc = p.getDefaultSku().getCurrency();
        } else {
            Sku s = (Sku) extractValueRequest.getEntity();
            bc = s.getCurrency();
        }
        
        if (bc == null) {
            return Money.defaultCurrency();
        } else {
            return Currency.getInstance(bc.getCurrencyCode());
        }
    }
}
