/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

/**
 * This class holds adjustment records during the discount calculation 
 * processing.  This and other disposable objects avoid churn on the database while the 
 * offer engine determines the best offer(s) for the order being priced.
 * 
 * @author bpolster
 */
public interface PromotableFulfillmentGroupAdjustment extends Serializable {

    /**
     * Returns the associated promotableFulfillmentGroup
     * @return
     */
    public PromotableFulfillmentGroup getPromotableFulfillmentGroup();

    /**
     * Returns the associated promotableCandidateOrderOffer
     * @return
     */
    public PromotableCandidateFulfillmentGroupOffer getPromotableCandidateFulfillmentGroupOffer();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getSaleAdjustmentValue();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getRetailAdjustmentValue();

    /**
     * Returns the value of this adjustment 
     * @return
     */
    public Money getAdjustmentValue();

    /**
     * Returns true if this adjustment represents a combinable offer.
     */
    boolean isCombinable();

    /**
     * Returns true if this adjustment represents a totalitarian offer.   
     */
    boolean isTotalitarian();
    
    /**
     * Updates the adjustmentValue to the sales or retail value based on the passed in param
     */
    void finalizeAdjustment(boolean useSaleAdjustments);

    boolean isAppliedToSalePrice();
}