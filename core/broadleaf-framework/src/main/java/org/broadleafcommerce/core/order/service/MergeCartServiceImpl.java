/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.core.order.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * The 2.0 implementation of merge cart service. Instead of merging items directly from one cart to another, we will
 * convert the previous cart to a named order that the customer is able to interact with as they see fit.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blMergeCartService")
public class MergeCartServiceImpl implements MergeCartService {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blMergeCartServiceExtensionManager")
    protected MergeCartServiceExtensionManager extensionManager;

    @Override
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart)
            throws PricingException, RemoveFromCartException {
        return mergeCart(customer, anonymousCart, true);
    }

    @Override
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException, RemoveFromCartException {
        return reconstructCart(customer, true);
    }

    @Override
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart, boolean priceOrder)
            throws PricingException, RemoveFromCartException {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        mergeCartResponse.setMerged(false); // We no longer merge items, only transition cart states

        // We need to make sure that the old, saved customer cart is reconstructed with availability concerns in mind
        ReconstructCartResponse reconstructCartResponse = reconstructCart(customer, false);
        mergeCartResponse.setRemovedItems(reconstructCartResponse.getRemovedItems());
        Order customerCart = reconstructCartResponse.getOrder();
        
        if (anonymousCart != null && customerCart != null && anonymousCart.equals(customerCart)) {
            // The carts are the same, use either ensuring it's owned by the current customer
            setNewCartOwnership(anonymousCart, customer);
            mergeCartResponse.setOrder(anonymousCart);
        } else if (anonymousCart == null || anonymousCart.getOrderItems().size() == 0) {
            // The anonymous cart is of no use, use the customer cart
            mergeCartResponse.setOrder(customerCart);
            
            // The anonymous cart is owned by a different customer, so there is no chance for a single customer to have
            // multiple IN_PROCESS carts. We can go ahead and clean up this empty cart anyway since it's empty
            if (anonymousCart != null) {
                orderService.cancelOrder(anonymousCart);
            }
            
        } else if (customerCart == null || customerCart.getOrderItems().size() == 0) {
            // Delete the saved customer order since it is completely empty anyway. We do not want 2 IN_PROCESS orders
            // hanging around
            if (customerCart != null) {
                orderService.cancelOrder(customerCart);
            }
            
            // The customer cart is of no use, use the anonymous cart
            setNewCartOwnership(anonymousCart, customer);
            mergeCartResponse.setOrder(anonymousCart);
        } else {
            // Both carts have some items. The anonymous cart will always be the more recent one by definition
            // Save off the old customer cart and use the anonymous cart
            setSavedCartAttributes(customerCart);
            orderService.save(customerCart, false);

            setNewCartOwnership(anonymousCart, customer);
            mergeCartResponse.setOrder(anonymousCart);
        }
        
        if (mergeCartResponse.getOrder() != null) {
            Order savedCart = orderService.save(mergeCartResponse.getOrder(), priceOrder);
            mergeCartResponse.setOrder(savedCart);
        }
        
        return mergeCartResponse;
    }
    
    @Override
    public ReconstructCartResponse reconstructCart(Customer customer, boolean priceOrder)
            throws PricingException, RemoveFromCartException {
        ReconstructCartResponse reconstructCartResponse = new ReconstructCartResponse();
        Order customerCart = orderService.findCartForCustomer(customer);

        if (customerCart != null) {
            List<OrderItem> itemsToRemove = new ArrayList<OrderItem>();

            for (OrderItem orderItem : customerCart.getOrderItems()) {
                if (orderItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem doi = (DiscreteOrderItem) orderItem;
                    if (!checkActive(doi) || !checkInventory(doi) || !checkOtherValidity(orderItem)) {
                        itemsToRemove.add(orderItem);
                    }
                } else if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    for (DiscreteOrderItem doi : bundleOrderItem.getDiscreteOrderItems()) {
                        if (!checkActive(doi) || !checkInventory(doi) || !checkOtherValidity(orderItem)) {
                            itemsToRemove.add(doi.getBundleOrderItem());
                        }
                    }
                }
            }

            //Remove any giftwrap items who have one or more wrapped item members that have been removed
            for (OrderItem orderItem : customerCart.getOrderItems()) {
                if (orderItem instanceof GiftWrapOrderItem) {
                    for (OrderItem wrappedItem : ((GiftWrapOrderItem) orderItem).getWrappedItems()) {
                        if (itemsToRemove.contains(wrappedItem)) {
                            itemsToRemove.add(orderItem);
                            break;
                        }
                    }
                }
            }

            for (OrderItem item : itemsToRemove) {
                orderService.removeItem(customerCart.getId(), item.getId(), false);
            }

            reconstructCartResponse.setRemovedItems(itemsToRemove);
            customerCart = orderService.save(customerCart, priceOrder);
        }

        reconstructCartResponse.setOrder(customerCart);
        return reconstructCartResponse;
    }
    
    protected void setSavedCartAttributes(Order cart) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, ''yy");
        Date cartLastUpdated = cart.getAuditable().getDateUpdated();
        
        cart.setName("Previously saved cart - " + sdf.format(cartLastUpdated));
        cart.setStatus(OrderStatus.NAMED);
    }

    protected void setNewCartOwnership(Order cart, Customer customer) {
        cart.setCustomer(customer);

        // copy the customer's email to this order, overriding any previously set email
        if (cart != null && StringUtils.isNotBlank(customer.getEmailAddress())) {
            cart.setEmailAddress(customer.getEmailAddress());
        }
        
        extensionManager.getProxy().setNewCartOwnership(cart, customer);
    }

    /**
     * @param orderItem
     * @return whether or not the discrete order item's sku is active
     */
    protected boolean checkActive(DiscreteOrderItem orderItem) {
        return orderItem.getSku().isActive(orderItem.getProduct(), orderItem.getCategory());
    }

    /**
     * By default, Broadleaf does not provide an inventory check. This is set up as an extension point if your
     * application needs it.
     * 
     * @param orderItem
     * @return whether or not the item is in stock
     */
    protected boolean checkInventory(DiscreteOrderItem orderItem) {
        return true;
    }

    /**
     * By default, Broadleaf does not provide additional validity checks. This is set up as an extension point if your
     * application needs it.
     * 
     * @param orderItem
     * @return whether or not the orderItem is valid
     */
    protected boolean checkOtherValidity(OrderItem orderItem) {
        return true;
    }

}
