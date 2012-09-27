/*
 * Copyright 2012 the original author or authors.
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
import org.broadleafcommerce.common.pricelist.domain.NullPriceList;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.RepriceOrderResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/26/12
 */
@Service("blCurrencyRepriceOrderService")
public class RepriceOrderServiceImpl implements RepriceOrderService {
    protected static final Log LOG = LogFactory.getLog(RepriceOrderServiceImpl.class);

    protected static BroadleafCurrency savedCurrency;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Override
    public boolean needsRepricing() {
        BroadleafCurrency currency = findActiveCurrency();
        if (getSavedCurrency() == null) {
            setSavedCurrency(currency);
        } else if (getSavedCurrency() != currency){
            return true;
        }
        return false;
    }

    @Override
    public RepriceOrderResponse repriceOrder(Order currentCart)
            throws PricingException, RemoveFromCartException, AddToCartException {
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
            currentCart = orderService.removeItem(currentCart.getId(), orderItem.getId(), false);
        }

        for(AddToCartItem itemRequest: itemsToReprice){
            currentCart = orderService.addItem(currentCart.getId(), itemRequest, false);
        }

        // Reprice and save the cart
        currentCart = orderService.save(currentCart, repriceOrder);
        setSavedCurrency(currency);

        RepriceOrderResponse repriceOrderResponse = new RepriceOrderResponse();
        repriceOrderResponse.setRemovedItems(itemsToRemove);
        repriceOrderResponse.setOrder(currentCart);

        return repriceOrderResponse;
    }

    protected BroadleafCurrency findActiveCurrency(){
        if(BroadleafRequestContext.hasLocale()){
            return BroadleafRequestContext.getBroadleafRequestContext().getBroadleafCurrency();
        }
        return null;
    }

    protected PriceList findActivePriceList(){
        if(BroadleafRequestContext.hasLocale()){
            return BroadleafRequestContext.getBroadleafRequestContext().getPriceList();
        }
        return new NullPriceList();
    }

    protected boolean checkAvailabilityInLocale(DiscreteOrderItem doi, BroadleafCurrency currency){
       if(doi.getSku() != null){
           if (doi.getSku().getPriceDataMap().containsKey(currency.getCurrencyCode()) ){
               return true;
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
