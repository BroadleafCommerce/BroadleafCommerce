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
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentPricingProcessor;

import java.util.List;

public class FulfillmentPricingServiceImpl implements FulfillmentPricingService {

    protected List<FulfillmentPricingProcessor> processors;

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {
        //TODO: throw exception if a FulfillmentOption is not associated to the fulfillmentGroup
        /*
        if (fulfillmentGroup.getFulfillmentOption() == null) {
            throw new ShippingPriceException("FulfillmentGroups must have a FulfillmentOption associated with them in order to price the FulfillmentGroup");
        }
        */
        for (FulfillmentPricingProcessor processor : processors) {
            if (processor.canCalculateCostForFulfillmentGroup(fulfillmentGroup)) {
                return processor.calculateCostForFulfillmentGroup(fulfillmentGroup);
            }
        }
        //TODO: remove this section after FulfillmentPricingProcessor implementations
        fulfillmentGroup.setShippingPrice(new Money(0));
        fulfillmentGroup.setSaleShippingPrice(new Money(0));
        fulfillmentGroup.setRetailShippingPrice(new Money(0));
        return fulfillmentGroup;
        //TODO: throw exception here since there wasn't a valid Processor found
        /*
        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
        		"FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
        				" and name: " + fulfillmentGroup.getFulfillmentOption().getName());
        */
    }
    
    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) throws ShippingPriceException {
        for (FulfillmentPricingProcessor processor : processors) {
            if (processor.canEstimateCostForFulfillmentGroup(fulfillmentGroup, option)) {
                return processor.estimateCostForFulfillmentGroup(fulfillmentGroup, option);
            }
        }
        
        //no processor was found, throw that up the stack
        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
                "FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
                        " and name: " + fulfillmentGroup.getFulfillmentOption().getName());
    }

    @Override
    public List<FulfillmentPricingProcessor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<FulfillmentPricingProcessor> processors) {
        this.processors = processors;
    }

}
