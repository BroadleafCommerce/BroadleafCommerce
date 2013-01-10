/*
 * Copyright 2008-2009 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service("blCartService")
/*
 * TODO setup other BLC items to be JMX managed resources like this one. This would include other services, and singleton beans
 * that are configured via Spring and property files (i.e. payment modules, etc...)
 */
@ManagedResource(objectName="org.broadleafcommerce:name=CartService", description="Cart Service", currencyTimeLimit=15)
public class CartServiceImpl extends OrderServiceImpl implements CartService {

    @Resource(name="blCustomerService")
    protected CustomerService customerService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    protected boolean moveNamedOrderItems = true;
    protected boolean deleteEmptyNamedOrders = true;

    public Order createNewCartForCustomer(Customer customer) {
        return orderDao.createNewCartForCustomer(customer);
    }

    public Order findCartForCustomer(Customer customer) {
        return orderDao.readCartForCustomer(customer);
    }
    
    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        return addAllItemsToCartFromNamedOrder(namedOrder, true);
    }

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        List<OrderItem> items = new ArrayList<OrderItem>(namedOrder.getOrderItems());
        for (int i = 0; i < items.size(); i++) {
            OrderItem orderItem = items.get(i);
            if (moveNamedOrderItems) {
                moveItemToOrder(namedOrder, cartOrder, orderItem, priceOrder);
            } else {
                addOrderItemToOrder(cartOrder, orderItem, priceOrder);
            }
            
        }
        return cartOrder;
    }
    
    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity) throws PricingException {
        return moveItemToCartFromNamedOrder(customerId, orderName, orderItemId, quantity, true);
    }

    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity, boolean priceOrder) throws PricingException {
        Order wishlistOrder = findNamedOrderForCustomer(orderName, customerService.createCustomerFromId(customerId));
        OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
        orderItem.setQuantity(quantity);
        return moveItemToCartFromNamedOrder(wishlistOrder, orderItem, priceOrder);
    }
    
    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, OrderItem orderItem) throws PricingException {
        return moveItemToCartFromNamedOrder(namedOrder, orderItem, true);
    }

    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, OrderItem orderItem, boolean priceOrder) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        if (moveNamedOrderItems) {
            moveItemToOrder(namedOrder, cartOrder, orderItem, priceOrder);
            if (namedOrder.getOrderItems().size() == 0 && deleteEmptyNamedOrders) {
                cancelOrder(namedOrder);
            }
        } else {
            orderItem = addOrderItemToOrder(cartOrder, orderItem, priceOrder);
        }
        
        return orderItem;
    }
    
    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        return moveAllItemsToCartFromNamedOrder(namedOrder, true);
    }

    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException {
        Order cartOrder = addAllItemsToCartFromNamedOrder(namedOrder, priceOrder);
        if (deleteEmptyNamedOrders) {
            cancelOrder(namedOrder);
        }
        return cartOrder;
    }

    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException {
        return mergeCart(customer, anonymousCart, true);
    }
    
    /*
     * (non-Javadoc)
     * @seeorg.broadleafcommerce.core.order.service.OrderService#mergeCart(org.
     * broadleafcommerce.profile.domain.Customer, java.lang.Long)
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart, boolean priceOrder) throws PricingException {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        // reconstruct cart items (make sure they are valid)
        ReconstructCartResponse reconstructCartResponse = reconstructCart(customer, priceOrder);
        mergeCartResponse.setRemovedItems(reconstructCartResponse.getRemovedItems());
        Order customerCart = reconstructCartResponse.getOrder();
        
        if (anonymousCart != null && customerCart != null && anonymousCart.getId().equals(customerCart.getId())) {
            /*
             * Set merged to false if the cart ids are equal (cookied customer
             * logs in).
             */
            mergeCartResponse.setMerged(false);
        } else {
            /*
             * Set the response to merged if the saved cart has any items
             * available to merge in.
             */
            mergeCartResponse.setMerged(customerCart != null && customerCart.getOrderItems().size() > 0);
        }

        // add anonymous cart items (make sure they are valid)
        if (anonymousCart != null && (customerCart == null || !customerCart.getId().equals(anonymousCart.getId()))) {
            //Order anonymousCart = findOrderById(anonymousCartId);
            if (anonymousCart != null && anonymousCart.getOrderItems() != null && !anonymousCart.getOrderItems().isEmpty()) {
                if (customerCart == null) {
                    customerCart = createNewCartForCustomer(customer);
                }
                // currently we'll just add items
                for (OrderItem orderItem : anonymousCart.getOrderItems()) {
                    if (orderItem instanceof DiscreteOrderItem) {
                        orderItem.removeAllAdjustments();
                        orderItem.removeAllCandidateItemOffers();
                        DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                        if (discreteOrderItem.getSku().isActive(discreteOrderItem.getProduct(), orderItem.getCategory())) {
                            DiscreteOrderItemRequest itemRequest = createDiscreteOrderItemRequest(discreteOrderItem);
                            addDiscreteItemToOrder(customerCart, itemRequest, priceOrder);
                            mergeCartResponse.getAddedItems().add(orderItem);
                        } else {
                            mergeCartResponse.getRemovedItems().add(orderItem);
                        }
                    } else if (orderItem instanceof BundleOrderItem) {
                        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                        orderItem.removeAllAdjustments();
                        orderItem.removeAllCandidateItemOffers();
                        boolean removeBundle = false;
                        List<DiscreteOrderItemRequest> discreteOrderItemRequests = new ArrayList<DiscreteOrderItemRequest>();
                        for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()){
                            discreteOrderItem.removeAllAdjustments();
                            discreteOrderItem.removeAllCandidateItemOffers();
                            DiscreteOrderItemRequest itemRequest = createDiscreteOrderItemRequest(discreteOrderItem);
                            discreteOrderItemRequests.add(itemRequest);
                            if (!discreteOrderItem.getSku().isActive(discreteOrderItem.getProduct(), orderItem.getCategory())) {
                                /*
                                 * Bundle has an inactive item in it -- remove the whole bundle
                                 */
                                removeBundle = true;
                            }
                        }
                        BundleOrderItemRequest bundleOrderItemRequest = createBundleOrderItemRequest(bundleOrderItem, discreteOrderItemRequests);
                        if (!removeBundle) {
                            addBundleItemToOrder(customerCart, bundleOrderItemRequest, priceOrder);
                            mergeCartResponse.getAddedItems().add(orderItem);
                        } else {
                            mergeCartResponse.getRemovedItems().add(orderItem);
                        }
                    }
                }

                // add all offers from anonymous order
                Map<String, OfferCode> customerOffersMap = new HashMap<String, OfferCode>();
                for (OfferCode customerOffer : customerCart.getAddedOfferCodes()) {
                    customerOffersMap.put(customerOffer.getOfferCode(), customerOffer);
                }

                for (OfferCode anonymousOffer : anonymousCart.getAddedOfferCodes()) {
                    if (!customerOffersMap.containsKey(anonymousOffer.getOfferCode())) {
                        OfferCode transferredCode = offerService.lookupOfferCodeByCode(anonymousOffer.getOfferCode());
                        OfferInfo info = anonymousCart.getAdditionalOfferInformation().get(anonymousOffer.getOffer());
                        OfferInfo offerInfo = offerDao.createOfferInfo();
                        for (String key : info.getFieldValues().keySet()) {
                            offerInfo.getFieldValues().put(key, info.getFieldValues().get(key));
                        }
                        customerCart.getAdditionalOfferInformation().put(transferredCode.getOffer(), offerInfo);
                        customerCart.addOfferCode(transferredCode);
                    }
                }
                customerCart = save(customerCart, true);
                cancelOrder(anonymousCart);
            }
        }
        mergeCartResponse.setOrder(customerCart);
        return mergeCartResponse;
    }
    
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException {
        return reconstructCart(customer, true);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.order.service.OrderService#reconstructCart(org.
     * broadleafcommerce.profile.domain.Customer)
     */
    public ReconstructCartResponse reconstructCart(Customer customer, boolean priceOrder) throws PricingException {
        ReconstructCartResponse reconstructCartResponse = new ReconstructCartResponse();
        Order customerCart = findCartForCustomer(customer);
        if (customerCart != null) {
            List<OrderItem> itemsToRemove = new ArrayList<OrderItem>();
            for (OrderItem orderItem : customerCart.getOrderItems()) {
                 if (orderItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                    if (!discreteOrderItem.getSku().isActive(
                            discreteOrderItem.getProduct(),
                            orderItem.getCategory())) {
                        itemsToRemove.add(orderItem);
                    }
                } else if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    boolean removeBundle = false;
                    for (DiscreteOrderItem discreteOrderItem : bundleOrderItem
                            .getDiscreteOrderItems()) {
                        if (!discreteOrderItem.getSku().isActive(
                                discreteOrderItem.getProduct(),
                                orderItem.getCategory())) {
                            /*
                             * Bundle has an inactive item in it -- remove the
                             * whole bundle
                             */
                            removeBundle = true;
                            break;
                        }
                    }
                    if (removeBundle) {
                        itemsToRemove.add(orderItem);
                    }
                }
            }
            for (OrderItem item : itemsToRemove) {
                removeItemFromOrder(customerCart, item, priceOrder);
            }
            reconstructCartResponse.setRemovedItems(itemsToRemove);
        }
        reconstructCartResponse.setOrder(customerCart);
        return reconstructCartResponse;
    }

    @ManagedAttribute(description="The move item from named order when adding to the cart attribute", currencyTimeLimit=15)
    public boolean isMoveNamedOrderItems() {
        return moveNamedOrderItems;
    }

    @ManagedAttribute(description="The move item from named order when adding to the cart attribute", currencyTimeLimit=15)
    public void setMoveNamedOrderItems(boolean moveNamedOrderItems) {
        this.moveNamedOrderItems = moveNamedOrderItems;
    }

    @ManagedAttribute(description="The delete empty named order after adding items to cart attribute", currencyTimeLimit=15)
    public boolean isDeleteEmptyNamedOrders() {
        return deleteEmptyNamedOrders;
    }

    @ManagedAttribute(description="The delete empty named order after adding items to cart attribute", currencyTimeLimit=15)
    public void setDeleteEmptyNamedOrders(boolean deleteEmptyNamedOrders) {
        this.deleteEmptyNamedOrders = deleteEmptyNamedOrders;
    }
}
