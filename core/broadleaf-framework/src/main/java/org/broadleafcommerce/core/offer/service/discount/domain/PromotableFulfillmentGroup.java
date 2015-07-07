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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface PromotableFulfillmentGroup extends Serializable {

    /**
     * Adds a fulfillmentGroupAdjustment
     * @return
     */
    void addCandidateFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment adjustment);

    /**
     * Adds a fulfillmentGroupAdjustment
     * @return
     */
    List<PromotableFulfillmentGroupAdjustment> getCandidateFulfillmentGroupAdjustments();

    /**
     * Removes all candidate adjustments
     * @return
     */
    void removeAllCandidateAdjustments();

    /**
     * This method will check to see if the saleAdjustments or retail only adjustments are better
     * and finalize the set that achieves the best result for the customer.
     */
    void chooseSaleOrRetailAdjustments();

    /**
     * Returns the decorated FulfillmentGroup
     */
    public FulfillmentGroup getFulfillmentGroup();

    /**
     * Adds the underlying fulfillmentGroup to the rule variable map.
     */
    void updateRuleVariables(Map<String, Object> ruleVars);

    /**
     * Calculates the price with adjustments.   Uses the sale or retail adjustments
     * based on the passed in parameter.     
     * @param useSalePrice
     */
    public Money calculatePriceWithAdjustments(boolean useSalePrice);

    /**
     * Calculates the price with all adjustments.   May error in the case where adjustments have
     * not been finalized with a call to chooseSaleOrRetailAdjustments.
     * @param useSalePrice
     */
    public Money getFinalizedPriceWithAdjustments();

    /**
     * Return list of discountable discrete order items contained in this fulfillmentGroup.
     * @return
     */
    List<PromotableOrderItem> getDiscountableOrderItems();

    /**
     * Checks to see if the offer can be added to this fulfillmentGroup based on whether or not 
     * it is combinable or if this fulfillmentGroup already has a non-combinable offer applied.
     * @param fulfillmentGroupOffer
     * @return
     */
    boolean canApplyOffer(PromotableCandidateFulfillmentGroupOffer fulfillmentGroupOffer);

    /**
     * Returns the price of this fulfillment group if no adjustments were applied.
     * @return
     */
    Money calculatePriceWithoutAdjustments();
    
    /**
     * Returns true if totalitarian offer was applied to this promotable fulfillment group.
     * @return
     */
    boolean isTotalitarianOfferApplied();

}
