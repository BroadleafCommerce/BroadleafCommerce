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
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentGroupPricingActivity;

/**
 * Main extension interface to allow third-party integrations to respond to fulfillment pricing
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentPricingService}
 */
public interface FulfillmentPricingProvider {

    /**
     * Whether or not this processor can calculate the fulfillment cost for the given {@link FulfillmentGroup}. This is
     * called during the PricingWorkflow and specifically invoked via FulfillmentPricingService which is invoked
     * via {@link FulfillmentGroupPricingActivity}. A common check here is to see if {@link FulfFulfillmentPriceActivityentOption()}
     * is the correct type for this Processor.
     * 
     * @param fulfillmentGroup - the {@link FulfillmentGroup} to calculate costs for. The {@link FulfillmentOption} on this
     * FulfillmentGroup should already be set when this is called
     * @return true if this processor can calculate fulfillment costs for the given FulfillmentGroup
     * @see {@link FulfillmentPricingService}, {@link FulfillmentGroupPricingActivity}
     */
    public boolean canCalculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    /**
     * Calculates the total cost for this FulfillmentGroup. Specific configurations for calculating
     * this cost can come from {@link FulfillmentGroup#getFulfillmentOption()}. This method is invoked
     * during the pricing workflow and will only be called if {@link #canCalculateCostForFulfillmentGroup(FulfillmentGroup)}
     * returns true. This should call {@link FulfillmentGroup#setShippingPrice(org.broadleafcommerce.common.money.Money)} to
     * set the shipping price on <b>fulfillmentGroup</b>
     * 
     * @param fulfillmentGroup - the {@link FulfillmentGroup} to calculate costs for
     * @return the modified {@link FulfillmentGroup} with correct pricing. This is typically <b>fulfillmentGroup</b> after it
     * has been modified
     */
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    /**
     * Whether or not this processor can provide a cost estimate for the given FulfillmentGroup and the given
     * FulfillmentOption. This is not invoked directly by any workflow, but could instead be invoked via a controller
     * that wants to display pricing to a user before the user actually picks a FulfillmentOption. The controller would
     * inject an instance of FulfillmentPricingService  and thus indirectly invoke this method for a particular option.
     * 
     * @param fulfillmentGroup
     * @param option - the candidate option a user might select based on the estimate
     * @return <b>true</b> if this processor can estimate the costs, <b>false</b> otherwise
     * @see {@link FulfillmentPricingService}, {@link FulfillmentOption}
     */
    public boolean canEstimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option);

    /**
     * Estimates the cost for the fulfilling the given fulfillment gr
     * Estimates the cost for the fulfilling the given fulfillment group with the given option. This is used in conjunction with
     * {@link #canEstimateCostForFulfillmentGroup(FulfillmentGroup, FulfillmentOption)} and is not invoked unless it returns true.
     * The common use case for this would be in a controller to display to the user how much each fulfillment option would cost.
     * 
     * @param fulfillmentGroup - the group to estimate fulfillment costs for
     * @param option - the candidate option that a user might select
     * @return a DTO that represents pricing information that might be added to the fulfillment cost of <b>fulfillmentGroup</b> when
     * {@link #calculateCostForFulfillmentGroup(FulfillmentGroup)} is invoked during the pricing workflow
     * @see {@link FulfillmentPricingService}, {@link FulfillmentOption}
     */
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option);
    
}
