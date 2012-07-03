/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.pricing.service.fulfillment.processor;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;

/**
 * Processor used in conjunction with {@link FixedPriceFulfillmentOption}. Simply takes the
 * flat rate defined on the option and sets that to the total shipping price of the {@link FulfillmentGroup}
 * 
 * @author Phillip Verheyden
 * @see {@link FixedPriceFulfillmentOption}
 */
public class FixedPriceFulfillmentPricingProvider implements FulfillmentPricingProvider {

    @Override
    public boolean canCalculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        return fulfillmentGroup.getFulfillmentOption() instanceof FixedPriceFulfillmentOption;
    }

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        Money price = ((FixedPriceFulfillmentOption)fulfillmentGroup.getFulfillmentOption()).getPrice();
        fulfillmentGroup.setRetailShippingPrice(price);
        fulfillmentGroup.setSaleShippingPrice(price);        
        fulfillmentGroup.setShippingPrice(price);
        return fulfillmentGroup;
    }

    @Override
    public boolean canEstimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        return option instanceof FixedPriceFulfillmentOption;
    }

    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        //TODO: return the price that was set in the option
        return null;
    }

}
