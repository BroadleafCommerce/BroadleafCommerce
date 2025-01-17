/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;

import java.io.Serializable;

public interface PromotableOrderAdjustment extends Serializable {

    /**
     * Returns the associated promotableOrder
     *
     * @return
     */
    PromotableOrder getPromotableOrder();

    /**
     * Returns the associated promotableCandidateOrderOffer
     *
     * @return
     */
    Offer getOffer();

    /**
     * Returns the value of this adjustment
     *
     * @return
     */
    Money getAdjustmentValue();

    /**
     * Returns true if this adjustment represents a combinable offer.
     */
    boolean isCombinable();

    /**
     * Returns true if this adjustment represents a totalitarian offer.
     */
    boolean isTotalitarian();

    /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     * <p>
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @return
     */
    boolean isFutureCredit();

    /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     * <p>
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @param futureCredit
     */
    void setFutureCredit(boolean futureCredit);

}
