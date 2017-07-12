/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.order.service;

import org.broadleafcommerce.core.web.order.CartState;

/**
 * Convenience methods for determining the state of the active cart
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface CartStateService {

    /**
     * A helper method used to determine the validity of the {@link CartState#getCart()}'s order info
     *
     * @return boolean indicating whether or not the order has valid info
     */
    boolean hasPopulatedOrderInfo();

    /**
     * A helper method used to determine the validity of the payments on the {@link CartState#getCart()}
     *
     * @return boolean indicating whether or not the CREDIT_CARD order payment on the order has an address
     */
    boolean hasPopulatedBillingAddress();

    /**
     * A helper method used to determine the validity of the {@link CartState#getCart()}'s fulfillment groups
     *
     * @return boolean indicating whether or not the fulfillment groups on the cart have addresses.
     */
    boolean hasPopulatedShippingAddress();

    /**
     * A helper method used to determine whether or not the {@link CartState#getCart()} is using a third party payment
     *
     * @return boolean
     */
    boolean orderContainsThirdPartyPayment();

    /**
     * A helper method used to determine whether or not the {@link CartState#getCart()} contains an unconfirmed credit card
     *
     * @return boolean
     */
    boolean orderContainsUnconfirmedCreditCard();
}
