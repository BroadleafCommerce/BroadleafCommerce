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

import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentProcessor;

import java.util.List;

public class FulfillmentServiceImpl implements FulfillmentService {

    protected List<FulfillmentProcessor> fulfillmentProcessors;

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {
        if (fulfillmentGroup.getFulfillmentOption() == null) {
            throw new ShippingPriceException("FulfillmentGroups must have a FulfillmentOption associated with them in order to price the FulfillmentGroup");
        }
        
        for (FulfillmentProcessor processor : fulfillmentProcessors) {
            if (processor.canCalculateCostForFulfillmentGroup(fulfillmentGroup)) {
                return processor.calculateCostForFulfillmentGroup(fulfillmentGroup);
            }
        }
        
        //no processor was found, throw that up the stack
        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
        		"FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
        				" and name: " + fulfillmentGroup.getFulfillmentOption().getName());
    }
    
    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) throws ShippingPriceException {
        for (FulfillmentProcessor processor : fulfillmentProcessors) {
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
    public List<FulfillmentProcessor> getFulfillmentProcessors() {
        return fulfillmentProcessors;
    }

    @Override
    public void setFulfillmentProcessors(List<FulfillmentProcessor> fulfillmentProcessors) {
        this.fulfillmentProcessors = fulfillmentProcessors;
    }

}
