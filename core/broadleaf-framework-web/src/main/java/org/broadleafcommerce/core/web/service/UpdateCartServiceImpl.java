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

package org.broadleafcommerce.core.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.UpdateCartResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
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
        List<AddToCartItem> itemsToReprice = new ArrayList<AddToCartItem>();
        List<OrderItem> itemsToRemove = new ArrayList<OrderItem>();
        List<OrderItem> itemsToReset = new ArrayList<OrderItem>();
        boolean repriceOrder = true;

        for(OrderItem orderItem: currentCart.getOrderItems()){
            //Lookup price in price list, if null, then add to itemsToRemove
            if (orderItem instanceof DiscreteOrderItem){
                DiscreteOrderItem doi = (DiscreteOrderItem) orderItem;
                if(checkAvailabilityInLocale(doi, currency)){
                    AddToCartItem itemRequest = new AddToCartItem();
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
                        AddToCartItem itemRequest = new AddToCartItem();
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
                e.printStackTrace();
            }
        }

        for(AddToCartItem itemRequest: itemsToReprice){
            try {
                currentCart = orderService.addItem(currentCart.getId(), itemRequest, false);
            } catch (AddToCartException e) {
                e.printStackTrace();
            }
        }

        // Reprice and save the cart
        try {
         currentCart = orderService.save(currentCart, repriceOrder);
        } catch (PricingException e) {
         e.printStackTrace();
        }
        setSavedCurrency(currency);

        UpdateCartResponse updateCartResponse = new UpdateCartResponse();
        updateCartResponse.setRemovedItems(itemsToRemove);
        updateCartResponse.setOrder(currentCart);

        return updateCartResponse;
    }

    @Override
    public void validateCart(Order cart) {
        if (BroadleafRequestContext.hasLocale()) {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            String validationMsg;
            if (!brc.getLocale().getLocaleCode().matches(cart.getLocale().getLocaleCode())) {
                validationMsg = "The cart Locale [" + cart.getLocale().getLocaleCode() +
                        "] does not match the current locale [" + brc.getLocale().getLocaleCode() + "]";
                LOG.error(validationMsg);
                throw new IllegalArgumentException(validationMsg);
            }
        }
        
        if (extensionManager != null) {
            extensionManager.getProxy().validateCart(cart);
        }
    }

    protected BroadleafCurrency findActiveCurrency(){
        if(BroadleafRequestContext.hasLocale()){
            return BroadleafRequestContext.getBroadleafRequestContext().getBroadleafCurrency();
        }
        return null;
    }

    protected boolean checkAvailabilityInLocale(DiscreteOrderItem doi, BroadleafCurrency currency) {
        if (doi.getSku() != null && extensionManager != null) {
            ExtensionResultHolder erh = new ExtensionResultHolder();
            extensionManager.getProxy().isAvailable(doi, currency, erh);
            Object result = erh.getResult();
            if (result != null && result instanceof Boolean) {
                return ((Boolean) result).booleanValue();
            }

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
