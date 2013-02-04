/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import java.io.Serializable;
import java.util.List;

public interface PromotableFulfillmentGroup extends Serializable {

    /**
     * Returns the list of all fulfillment group adjustments.
     * @return
     */
    List<PromotableFulfillmentGroupAdjustment> getCandidateFulfillmentGroupAdjustments();

    /**
     * Adds a fulfillmentGroupAdjustment
     * @return
     */
    //void addFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment adjustment);

    //    public void reset();
    //
    //    public FulfillmentGroup getDelegate();
    //
    //    public List<PromotableOrderItem> getDiscountableDiscreteOrderItems();
    //
    //    /*
    //     * Adds the adjustment to the order item's adjustment list and discounts the order item's adjustment
    //     * price by the value of the adjustment.
    //     */
    //    public void addFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment fulfillmentGroupAdjustment);
    //
    //    public void removeAllAdjustments();
    //
    //    public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
    //
    //    public Money getAdjustmentPrice();
    //
    //    public void setAdjustmentPrice(Money adjustmentPrice);
    //
    //    public Money getRetailShippingPrice();
    //
    //    public Money getSaleShippingPrice();
    //    
    //    public void removeAllCandidateOffers();
    //    
    //    public void setShippingPrice(Money shippingPrice);
    //    
    //    public Money getShippingPrice();
    //    
    //    public void addCandidateFulfillmentGroupOffer(PromotableCandidateFulfillmentGroupOffer candidateOffer);
    
}