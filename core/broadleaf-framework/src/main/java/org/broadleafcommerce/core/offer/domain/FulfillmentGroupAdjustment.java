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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;

public interface FulfillmentGroupAdjustment extends Adjustment {

    FulfillmentGroup getFulfillmentGroup();

    void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    void init(FulfillmentGroup fulfillmentGroup, Offer offer, String reason);

    void setValue(Money value);

    /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     * <p>
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @return
     */
    Boolean isFutureCredit();

    /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     * <p>
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @param futureCredit
     */
    void setFutureCredit(Boolean futureCredit);

}
