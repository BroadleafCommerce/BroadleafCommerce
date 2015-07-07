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
import org.broadleafcommerce.core.offer.domain.Offer;

import java.io.Serializable;

/**
 * This class holds adjustment records during the discount calculation 
 * processing.  This and other disposable objects avoid churn on the database while the 
 * offer engine determines the best offer(s) for the order being priced.
 * 
 * @author bpolster
 */
public interface PromotableOrderItemPriceDetailAdjustment extends Serializable {

    /**
     * Returns the associated promotableOrderItemPriceDetail
     * @return
     */
    PromotableOrderItemPriceDetail getPromotableOrderItemPriceDetail();

    /**
     * Returns the associated promotableCandidateItemOffer
     * @return
     */
    Offer getOffer();

    /**
     * Returns the value of this adjustment if only retail prices
     * can be used.
     * @return
     */
    Money getRetailAdjustmentValue();

    /**
     * Returns the value of this adjustment if sale prices
     * can be used.
     * @return
     */
    Money getSaleAdjustmentValue();

    /**
     * Returns the value of this adjustment.
     * can be used.
     * @return
     */
    Money getAdjustmentValue();

    /**
     * Returns true if the value was applied to the sale price.
     * @return
     */
    boolean isAppliedToSalePrice();

    /**
     * Returns true if this adjustment represents a combinable offer.
     */
    boolean isCombinable();

    /**
     * Returns true if this adjustment represents a totalitarian offer.   
     */
    boolean isTotalitarian();

    /**
     * Returns the id of the contained offer.
     * @return
     */
    Long getOfferId();

    /**
     * Sets the adjustment price based on the passed in parameter.
     */
    void finalizeAdjustment(boolean useSalePrice);

    /**
     * Copy this adjustment.   Used when a detail that contains this adjustment needs to be split.
     * @param discountQty
     * @param copy
     * @return
     */
    public PromotableOrderItemPriceDetailAdjustment copy();

}
