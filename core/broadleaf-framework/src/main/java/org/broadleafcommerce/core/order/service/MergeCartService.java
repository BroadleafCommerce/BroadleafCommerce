/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface MergeCartService {

    /**
     * Merges the anonymous cart with the customer's current cart, taking into consideration the active
     * status of the SKUs to merge. For example, if the customer had a SKU in their anonymous cart that is no longer
     * active, it will not be merged into the new cart.
     * 
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @param priceOrder whether or not to price the order
     * @return the response containing the cart, any items added to the cart, and any items removed from the cart
     * @throws PricingException
     * @throws RemoveFromCartException 
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart, boolean priceOrder) throws PricingException, RemoveFromCartException;
    
    /**
     * Delegates to mergeCart(Customer, Order, boolean) with priceOrder set to true
     * 
     * Merges the anonymous cart with the customer's current cart, taking into consideration the active
     * status of the SKUs to merge. For example, if the customer had a SKU in their anonymous cart that is no longer
     * active, it will not be merged into the new cart.
     * 
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @return the response containing the cart, any items added to the cart, and any items removed from the cart
     * @throws PricingException
     * @throws RemoveFromCartException 
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException, RemoveFromCartException;
    
    /**
     * Reconstruct the cart using previous stored state taking into
     * consideration sku activation
     * 
     * @param customer the customer whose cart is to be reconstructed
     * @return the response containing the cart and any items removed from the cart
     * @throws RemoveFromCartException
     */
    public ReconstructCartResponse reconstructCart(Customer customer, boolean priceOrder) throws PricingException, RemoveFromCartException;
    
    /**
     * 
     * Delegates to reconstructCart(Customer, boolean) with priceOrder set to true
     * 
     * Reconstruct the cart using previous stored state taking into
     * consideration sku activation
     * 
     * @param customer the customer whose cart is to be reconstructed
     * @return the response containing the cart and any items removed from the cart
     * @throws PricingException
     * @throws RemoveFromCartException 
     */
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException, RemoveFromCartException;

}
