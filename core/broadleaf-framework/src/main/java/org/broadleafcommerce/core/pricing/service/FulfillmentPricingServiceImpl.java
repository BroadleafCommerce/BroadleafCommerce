/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.pricing.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentPricingProvider;

import java.util.List;

public class FulfillmentPricingServiceImpl implements FulfillmentPricingService {

    protected List<FulfillmentPricingProvider> providers;

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {

        if (fulfillmentGroup.getFulfillmentOption() == null) {
            //There is no shipping option yet. We'll simply set the shipping price to zero for now, and continue.
            fulfillmentGroup.setRetailShippingPrice(new Money(0D));
            fulfillmentGroup.setShippingPrice(new Money(0D));
            fulfillmentGroup.setSaleShippingPrice(new Money(0D));
            return fulfillmentGroup;
        }

        for (FulfillmentPricingProvider processor : providers) {
            if (processor.canCalculateCostForFulfillmentGroup(fulfillmentGroup, fulfillmentGroup.getFulfillmentOption())) {
                return processor.calculateCostForFulfillmentGroup(fulfillmentGroup);
            }
        }

        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
        		"FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
        				" and name: " + fulfillmentGroup.getFulfillmentOption().getName());
    }
    
    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) throws ShippingPriceException {
        for (FulfillmentPricingProvider processor : providers) {
            if (processor.canCalculateCostForFulfillmentGroup(fulfillmentGroup, option)) {
                return processor.estimateCostForFulfillmentGroup(fulfillmentGroup, option);
            }
        }
        
        //no processor was found, throw that up the stack
        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
                "FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
                        " and name: " + fulfillmentGroup.getFulfillmentOption().getName());
    }

    @Override
    public List<FulfillmentPricingProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<FulfillmentPricingProvider> providers) {
        this.providers = providers;
    }

}
