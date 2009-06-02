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
package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.stereotype.Service;

@Service("blCartService")
public class CartServiceImpl extends OrderServiceImpl implements CartService {

    @Resource
    protected CustomerService customerService;

    protected boolean moveNamedOrderItems = true;
    protected boolean deleteEmptyNamedOrders = true;

    @Override
    public Order createNewCartForCustomer(Customer customer) {
        return orderDao.createNewCartForCustomer(customer);
    }

    @Override
    public Order findCartForCustomer(Customer customer) {
        return orderDao.readCartForCustomer(customer);
    }

    @Override
    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        for (OrderItem orderItem : namedOrder.getOrderItems()) {
            if (moveNamedOrderItems) {
                removeItemFromOrder(namedOrder, orderItem);
            }
            addOrderItemToOrder(cartOrder, orderItem);
        }
        return cartOrder;
    }

    @Override
    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity) throws PricingException {
        Order wishlistOrder = findNamedOrderForCustomer(orderName, customerService.createCustomerFromId(customerId));
        OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
        orderItem.setQuantity(quantity);
        return moveItemToCartFromNamedOrder(wishlistOrder, orderItem);
    }

    @Override
    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, OrderItem orderItem) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        if (moveNamedOrderItems) {
            Order updatedNamedOrder = removeItemFromOrder(namedOrder, orderItem);
            if (updatedNamedOrder.getOrderItems().size() == 0 && deleteEmptyNamedOrders) {
                orderDao.delete(updatedNamedOrder);
            }
        }
        return addOrderItemToOrder(cartOrder, orderItem);
    }

    @Override
    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        Order cartOrder = addAllItemsToCartFromNamedOrder(namedOrder);
        if (deleteEmptyNamedOrders) {
            orderDao.delete(namedOrder);
        }
        return cartOrder;
    }

    /*
     * (non-Javadoc)
     * @seeorg.broadleafcommerce.order.service.OrderService#mergeCart(org.
     * broadleafcommerce.profile.domain.Customer, java.lang.Long)
     */
    @Override
    public MergeCartResponse mergeCart(Customer customer, Long anonymousCartId) throws PricingException {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        // reconstruct cart items (make sure they are valid)
        ReconstructCartResponse reconstructCartResponse = reconstructCart(customer);
        mergeCartResponse.setRemovedItems(reconstructCartResponse.getRemovedItems());
        Order customerCart = reconstructCartResponse.getOrder();

        // add anonymous cart items (make sure they are valid)
        if ((customerCart == null || !customerCart.getId().equals(anonymousCartId)) && anonymousCartId != null) {
            Order anonymousCart = findOrderById(anonymousCartId);
            if (anonymousCart != null && anonymousCart.getOrderItems() != null && !anonymousCart.getOrderItems().isEmpty()) {
                if (customerCart == null) {
                    customerCart = createNewCartForCustomer(customer);
                }
                // TODO improve merge algorithm to support various requirements
                // currently we'll just add items
                for (OrderItem orderItem : anonymousCart.getOrderItems()) {
                    if (orderItem instanceof DiscreteOrderItem) {
                        DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                        if (discreteOrderItem.getSku().isActive(discreteOrderItem.getProduct(), orderItem.getCategory())) {
                            DiscreteOrderItemRequest itemRequest = createDiscreteOrderItemRequest(discreteOrderItem);
                            addDiscreteItemToOrder(customerCart, itemRequest);
                            mergeCartResponse.getAddedItems().add(orderItem);
                        } else {
                            mergeCartResponse.getRemovedItems().add(orderItem);
                        }
                    } else if (orderItem instanceof BundleOrderItem) {
                        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                        boolean removeBundle = false;
                        List<DiscreteOrderItemRequest> discreteOrderItemRequests = new ArrayList<DiscreteOrderItemRequest>();
                        for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()){
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
                            addBundleItemToOrder(customerCart, bundleOrderItemRequest);
                            mergeCartResponse.getAddedItems().add(orderItem);
                        } else {
                            mergeCartResponse.getRemovedItems().add(orderItem);
                        }
                    }
                }
                orderDao.delete(anonymousCart);
            }
        }
        mergeCartResponse.setOrder(customerCart);
        return mergeCartResponse;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.order.service.OrderService#reconstructCart(org.
     * broadleafcommerce.profile.domain.Customer)
     */
    @Override
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException {
        ReconstructCartResponse reconstructCartResponse = new ReconstructCartResponse();
        Order customerCart = findCartForCustomer(customer);
        if (customerCart != null) {
            for (OrderItem orderItem : customerCart.getOrderItems()) {
                if (orderItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                    if (!discreteOrderItem.getSku().isActive(discreteOrderItem.getProduct(), orderItem.getCategory())) {
                        reconstructCartResponse.getRemovedItems().add(orderItem);
                        removeItemFromOrder(customerCart, discreteOrderItem);
                    }
                } else if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    boolean removeBundle = false;
                    for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()){
                        if (!discreteOrderItem.getSku().isActive(discreteOrderItem.getProduct(), orderItem.getCategory())) {
                            /*
                             * Bundle has an inactive item in it -- remove the whole bundle
                             */
                            removeBundle = true;
                            break;
                        }
                    }
                    if (removeBundle) {
                        reconstructCartResponse.getRemovedItems().add(orderItem);
                        removeItemFromOrder(customerCart, bundleOrderItem);
                    }
                }
            }
        }
        reconstructCartResponse.setOrder(customerCart);
        return reconstructCartResponse;
    }

    public boolean isMoveNamedOrderItems() {
        return moveNamedOrderItems;
    }

    public void setMoveNamedOrderItems(boolean moveNamedOrderItems) {
        this.moveNamedOrderItems = moveNamedOrderItems;
    }

    public boolean isDeleteEmptyNamedOrders() {
        return deleteEmptyNamedOrders;
    }

    public void setDeleteEmptyNamedOrders(boolean deleteEmptyNamedOrders) {
        this.deleteEmptyNamedOrders = deleteEmptyNamedOrders;
    }
}
