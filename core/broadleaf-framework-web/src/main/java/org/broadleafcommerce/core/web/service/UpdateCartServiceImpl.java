/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderLockManager;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.UpdateCartResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.security.exception.OrderLockAcquisitionFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Author: jerryocanas
 * Date: 9/26/12
 */
@Service("blUpdateCartService")
public class UpdateCartServiceImpl implements UpdateCartService {
    protected static final Log LOG = LogFactory.getLog(UpdateCartServiceImpl.class);

    protected static BroadleafCurrency savedCurrency;

    @Resource(name="blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blUpdateCartServiceExtensionManager")
    protected UpdateCartServiceExtensionManager extensionManager;

    @Autowired
    @Qualifier("blOrderLockManager")
    protected OrderLockManager orderLockManager;


    @Override
    public boolean currencyHasChanged() {
        BroadleafCurrency currency = findActiveCurrency();
        if (getSavedCurrency() == null) {
            setSavedCurrency(currency);
        } else if (getSavedCurrency() != currency){
            return true;
        }
        return false;
    }

    @Override
    public UpdateCartResponse copyCartToCurrentContext(Order currentCart) {
        if(currentCart.getOrderItems() == null){
            return null;
        }
        BroadleafCurrency currency = findActiveCurrency();
        if(currency == null){
            return null;
        }

        //Reprice order logic
        List<OrderItemRequestDTO> itemsToReprice = new ArrayList<>();
        List<OrderItem> itemsToRemove = new ArrayList<>();
        List<OrderItem> itemsToReset = new ArrayList<>();
        boolean repriceOrder = true;

        for(OrderItem orderItem: currentCart.getOrderItems()){
            //Lookup price in price list, if null, then add to itemsToRemove
            if (orderItem instanceof DiscreteOrderItem){
                DiscreteOrderItem doi = (DiscreteOrderItem) orderItem;
                if(checkAvailabilityInLocale(doi, currency)){
                    OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
                    itemRequest.setProductId(doi.getProduct().getId());
                    itemRequest.setQuantity(doi.getQuantity());
                    itemsToReprice.add(itemRequest);
                    itemsToReset.add(orderItem);
                } else {
                    itemsToRemove.add(orderItem);
                }
            } else if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem boi = (BundleOrderItem) orderItem;
                for (DiscreteOrderItem doi : boi.getDiscreteOrderItems()) {
                    if(checkAvailabilityInLocale(doi, currency)){
                        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
                        itemRequest.setProductId(doi.getProduct().getId());
                        itemRequest.setQuantity(doi.getQuantity());
                        itemsToReprice.add(itemRequest);
                        itemsToReset.add(orderItem);
                    } else {
                        itemsToRemove.add(orderItem);
                    }
                }
            }
        }

        for(OrderItem orderItem: itemsToReset){
            try {
                currentCart = orderService.removeItem(currentCart.getId(), orderItem.getId(), false);
            } catch (RemoveFromCartException e) {
                LOG.error("Could not remove from cart.", e);
            }
        }

        for(OrderItemRequestDTO itemRequest: itemsToReprice){
            try {
                currentCart = orderService.addItem(currentCart.getId(), itemRequest, false);
            } catch (AddToCartException e) {
                LOG.error("Could not add to cart.", e);
            }
        }

        // Reprice and save the cart
        try {
         currentCart = orderService.save(currentCart, repriceOrder);
        } catch (PricingException e) {
            LOG.error("Could not save cart.", e);
        }
        setSavedCurrency(currency);

        UpdateCartResponse updateCartResponse = new UpdateCartResponse();
        updateCartResponse.setRemovedItems(itemsToRemove);
        updateCartResponse.setOrder(currentCart);

        return updateCartResponse;
    }

    @Override
    public void validateAddToCartRequest(OrderItemRequestDTO itemRequest, Order cart) {
        if (extensionManager != null) {
            extensionManager.getProxy().validateAddToCartItem(itemRequest, cart);
        }
    }

    @Override
    public void updateAndValidateCart(Order cart) {
        if (extensionManager != null) {
            ExtensionResultHolder erh = new ExtensionResultHolder();
            extensionManager.getProxy().updateAndValidateCart(cart, erh);
            Boolean clearCart = (Boolean) erh.getContextMap().get("clearCart");
            Boolean repriceCart = (Boolean) erh.getContextMap().get("repriceCart");
            Boolean saveCart = (Boolean) erh.getContextMap().get("saveCart");
            if (clearCart != null && clearCart.booleanValue()) {
                Object lockObject = null;
                try {
                    lockObject = lockOrder(cart, lockObject);

                    orderService.cancelOrder(cart);
                    cart = orderService.createNewCartForCustomer(cart.getCustomer());
                }finally {
                    if (lockObject != null) {
                        orderLockManager.releaseLock(lockObject);
                    }

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Thread[" + Thread.currentThread().getId() + "] released lock for order[" + cart.getId() +"]");
                    }
                }
            } else {
                try {
                    if (repriceCart != null && repriceCart.booleanValue()) {
                        Object lockObject = null;
                        try {
                            lockObject = lockOrder(cart, lockObject);

                            orderService.save(cart, true, true);
                        }finally {
                            if (lockObject != null) {
                                orderLockManager.releaseLock(lockObject);
                            }

                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Thread[" + Thread.currentThread().getId() + "] released lock for order[" + cart.getId() +"]");
                            }
                        }
                    } else if (saveCart != null && saveCart.booleanValue()) {
                        Object lockObject = null;
                        try {
                            lockObject = lockOrder(cart, lockObject);

                            orderService.save(cart, false);
                        }finally {
                            if (lockObject != null) {
                                orderLockManager.releaseLock(lockObject);
                            }

                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Thread[" + Thread.currentThread().getId() + "] released lock for order[" + cart.getId() +"]");
                            }
                        }
                    }
                } catch (PricingException pe) {
                    LOG.error("Pricing Exception while validating cart.   Clearing cart.", pe);
                    orderService.cancelOrder(cart);
                    cart = orderService.createNewCartForCustomer(cart.getCustomer());
                }
            }
        }
    }


    protected Object lockOrder(Order order, Object lockObject){
        if (lockObject == null) {
            if (getErrorInsteadOfQueue()) {
                lockObject = orderLockManager.acquireLockIfAvailable(order);
                if (lockObject == null) {
                    // We weren't able to acquire the lock immediately because some other thread has it. Because the
                    // order.lock.errorInsteadOfQueue property was set to true, we're going to throw an exception now.
                    throw new OrderLockAcquisitionFailureException("Thread[" + Thread.currentThread().getId() +
                            "] could not acquire lock for order[" + order.getId() + "]");
                }
            } else {
                lockObject = orderLockManager.acquireLock(order);
            }
        }
        return lockObject;
    }

    protected boolean getErrorInsteadOfQueue() {
        return BLCSystemProperty.resolveBooleanSystemProperty("order.lock.errorInsteadOfQueue");
    }

    protected BroadleafCurrency findActiveCurrency(){
        if(BroadleafRequestContext.hasLocale()){
            return BroadleafRequestContext.getBroadleafRequestContext().getBroadleafCurrency();
        }
        return null;
    }

    protected boolean checkAvailabilityInLocale(DiscreteOrderItem doi, BroadleafCurrency currency) {
        if (doi.getSku() != null && extensionManager != null) {
            Sku sku = doi.getSku();
            return sku.isAvailable();
        }
        
        return false;
    }

    @Override
    public void setSavedCurrency(BroadleafCurrency savedCurrency) {
        this.savedCurrency = savedCurrency;
    }

    @Override
    public BroadleafCurrency getSavedCurrency() {
        return savedCurrency;
    }


}
