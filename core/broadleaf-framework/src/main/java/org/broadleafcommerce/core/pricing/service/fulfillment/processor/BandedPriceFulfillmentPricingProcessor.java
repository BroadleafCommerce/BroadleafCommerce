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

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBand;

/**
 * Used in conjunction with {@link BandedPriceFulfillmentOption}.
 * 
 * @author Phillip Verheyden
 * @see {@link BandedPriceFulfillmentOption}, {@link FulfillmentPriceBand}
 */
public class BandedPriceFulfillmentPricingProcessor implements FulfillmentPricingProcessor {

    @Override
    public boolean canCalculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        return fulfillmentGroup.getFulfillmentOption() instanceof BandedPriceFulfillmentOption;
    }

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        
        return null;
    }

    @Override
    public boolean canEstimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        return option instanceof BandedPriceFulfillmentOption;
    }

    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        // TODO Auto-generated method stub
        return null;
    }

}
