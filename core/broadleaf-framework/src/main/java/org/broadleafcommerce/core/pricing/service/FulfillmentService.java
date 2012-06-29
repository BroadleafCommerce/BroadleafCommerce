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
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentGroupTotalActivity;

import java.util.List;

/**
 * This service can be used in a couple of different ways. First, this is used in the pricing workflow and specifically
 * {@link FulfillmentGroupTotalActivity} to calculate costs for {@link FulfillmentGroup}s in an {@link Order}. This can
 * also be injected in a controller to provide estimations for various {@link FulfillmentOption}s to display to the user
 * before an option is actually selected.
 * 
 * @author Phillip Verheyden
 */
public interface FulfillmentService {

    /**
     * Called during the Pricing workflow to determine the cost for the {@link FulfillmentGroup}. This will loop through
     * {@link #getFulfillmentProcessors()} and call {@link FulfillmentProcessor#calculateCostForFulfillmentGroup(FulfillmentGroup)}
     * on the first processor that returns true from {@link FulfillmentProcessor#canCalculateCostForFulfillmentGroup(FulfillmentGroup)}
     * 
     * @param fulfillmentGroup
     * @return the updated </b>fulfillmentGroup</b> with its shippingPrice set
     * @throws ShippingPriceException if <b>fulfillmentGroup</b> does not have a FulfillmentOption associated to it or
     * if there was no processor found to calculate costs for <b>fulfillmentGroup</b>
     * @see {@link FulfillmentProcessor}
     */
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException;

    /**
     * This provides an estimation for a {@link FulfillmentGroup} with a {@link FulfillmentOption}. The main use case for this method
     * is in a view cart controller that wants to provide estimations for different {@link FulfillmentOption}s before the user
     * actually selects one. This uses {@link #getFulfillmentProcessors()} to allow third-party integrations to respond to
     * estimations, and returns the first processor that returns true from {@link FulfillmentProcessor#canEstimateCostForFulfillmentGroup(FulfillmentGroup, FulfillmentOption)}.
     * 
     * @param fulfillmentGroup
     * @param option
     * @return the price estimation for a particular {@link FulfillmentGroup} with a candidate {@link FulfillmentOption}
     * @throws ShippingPriceException if no processor was found to estimate costs for <b>fulfillmentGroup</b> with the given <b>option</b>
     * @see {@link FulfillmentProcessor}
     */
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) throws ShippingPriceException;
    
    public List<FulfillmentProcessor> getFulfillmentProcessors();

}
