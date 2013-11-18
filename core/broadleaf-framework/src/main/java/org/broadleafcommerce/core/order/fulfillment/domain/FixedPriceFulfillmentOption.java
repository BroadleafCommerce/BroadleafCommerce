/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FixedPriceFulfillmentPricingProvider;

import java.io.Serializable;

/**
 * Used in conjunction with the {@link FixedPriceFulfillmentPricingProvider} to allow for a single price
 * for fulfilling an order (e.g. $5 shipping)
 * 
 * @author Phillip Verheyden
 * @see {@link FixedPriceFulfillmentPricingProvider}
 */
public interface FixedPriceFulfillmentOption extends FulfillmentOption, Serializable {
    
    public Money getPrice();
    
    public void setPrice(Money price);
    public BroadleafCurrency getCurrency();

    public void setCurrency(BroadleafCurrency currency);
    
}
