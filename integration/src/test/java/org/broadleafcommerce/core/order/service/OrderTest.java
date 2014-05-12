/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.PaymentInfoDataProvider;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

public class OrderTest extends OrderBaseTest {

    private Long orderId = null;
    private int numOrderItems = 0;
    private Long bundleOrderItemId;

    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;
    
    @Resource
    private SkuDao skuDao;
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;
    
    @Resource(name = "blAddItemWorkflow")
    protected SequenceProcessor addItemWorkflow;
    
    @Test(groups = { "createCartForCustomer" }, dependsOnGroups = { "readCustomer", "createPhone" })
    @Transactional
    @Rollback(false)
    public void createCartForCustomer() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = orderService.createNewCartForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        this.orderId = order.getId();
    }

    @Test(groups = { "findCurrentCartForCustomer" }, dependsOnGroups = { "readCustomer", "createPhone", "createCartForCustomer" })
    @Transactional
    @Rollback(false)
    public void findCurrentCartForCustomer() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = orderService.findCartForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        this.orderId = order.getId();
    }

    @Test(groups = { "addItemToOrder" }, dependsOnGroups = { "findCurrentCartForCustomer", "createSku", "testCatalog" })
    @Rollback(false)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addItemToOrder() throws AddToCartException {
        numOrderItems++;
        // In the database, some Skus are inactive and some are active. This ensures that we pull back an active one
        // to test a successful cart add
        Sku sku = getFirstActiveSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setQuantity(1);
        itemRequest.setSkuId(sku.getId());
        order = orderService.addItem(orderId, itemRequest, true);
        
        DiscreteOrderItem item = (DiscreteOrderItem) orderService.findLastMatchingItem(order, sku.getId(), null);
        
        assert item != null;
        assert item.getQuantity() == numOrderItems;
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
        
        assert order.getFulfillmentGroups().size() == 1;
        
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 1;
        
        FulfillmentGroupItem fgItem = fg.getFulfillmentGroupItems().get(0);
        assert fgItem.getOrderItem().equals(item);
        assert fgItem.getQuantity() == item.getQuantity();
    }
    
    @Test(groups = { "addAnotherItemToOrder" }, dependsOnGroups = { "addItemToOrder" })
    @Rollback(false)
    @Transactional
    public void addAnotherItemToOrder() throws AddToCartException {
     // In the database, some Skus are inactive and some are active. This ensures that we pull back an active one
        // to test a successful cart add
        Sku sku = getFirstActiveSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        orderService.setAutomaticallyMergeLikeItems(true); 
        
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setQuantity(1);
        itemRequest.setSkuId(sku.getId());
        // Note that we are not incrementing the numOrderItems count because it should have gotten merged
        order = orderService.addItem(orderId, itemRequest, true);
        DiscreteOrderItem item = (DiscreteOrderItem) orderService.findLastMatchingItem(order, sku.getId(), null);
        
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
        assert item.getQuantity() == 2;  // item-was merged with prior item.

        order = orderService.findOrderById(orderId);

        assert(order.getOrderItems().size()==1);
        assert(order.getOrderItems().get(0).getQuantity()==2);
        
        assert order.getFulfillmentGroups().size() == 1;
        
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 1;
        
        FulfillmentGroupItem fgItem = fg.getFulfillmentGroupItems().get(0);
        assert fgItem.getOrderItem().equals(item);
        assert fgItem.getQuantity() == item.getQuantity();

        /*
        This test is not supported currently, as the order service may only do like item merging

        // re-price the order without automatically merging.
        orderService.setAutomaticallyMergeLikeItems(false);
        numOrderItems++;
        
        itemRequest = new OrderItemRequestDTO();
        itemRequest.setQuantity(1);
        itemRequest.setSkuId(sku.getId());
        order = orderService.addItem(orderId, itemRequest, true);
        DiscreteOrderItem item2 = (DiscreteOrderItem) orderService.findLastMatchingItem(order, sku.getId(), null);

        assert item2.getSku() != null;
        assert item2.getSku().equals(sku);
        assert item2.getQuantity() == 1;  // item-was not auto-merged with prior items.

        order = orderService.findOrderById(orderId);

        assert(order.getOrderItems().size()==2);
        assert(order.getOrderItems().get(0).getQuantity()==2);
        assert(order.getOrderItems().get(1).getQuantity()==1);
        
        assert order.getFulfillmentGroups().size() == 1;
        
        fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 2;
        
        for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
            assert fgi.getQuantity() == fgi.getOrderItem().getQuantity();
        }*/
    }
    
    /**
     * From the list of all Skus in the database, gets a Sku that is active
     * @return
     */
    public Sku getFirstActiveSku() {
        List<Sku> skus = skuDao.readAllSkus();
        return CollectionUtils.find(skus, new Predicate<Sku>() {

            @Override
            public boolean evaluate(Sku sku) {
                return sku.isActive();
            }
        });
    }
    
    @Test(groups = { "testIllegalAddScenarios" }, dependsOnGroups = { "addItemToOrder" })
    @Transactional
    public void testIllegalAddScenarios() throws AddToCartException {
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        
        Product activeProduct = addTestProduct("mug", "cups", true);
        Product inactiveProduct = addTestProduct("cup", "cups", false);
        
        // Inactive skus should not be added
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO().setQuantity(1).setSkuId(inactiveProduct.getDefaultSku().getId());
        boolean addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
        }
        assert !addSuccessful;
        
        // Products that have SKUs marked as inactive should not be added either
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setProductId(inactiveProduct.getId());
        addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
        }
        assert !addSuccessful;
        
        // Negative quantities are not allowed
        itemRequest = new OrderItemRequestDTO().setQuantity(-1).setSkuId(activeProduct.getDefaultSku().getId());
        addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
            assert e.getCause() instanceof IllegalArgumentException;
        }
        assert !addSuccessful;
        
        // Order must exist
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setSkuId(activeProduct.getDefaultSku().getId());
        addSuccessful = true;
        try {
            order = orderService.addItem(-1L, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
            assert e.getCause() instanceof IllegalArgumentException;
        }
        assert !addSuccessful;
        
        // If a product is provided, it must exist
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setProductId(-1L);
        addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
            assert e.getCause() instanceof IllegalArgumentException;
        }
        assert !addSuccessful;
        
        // The SKU must exist
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setSkuId(-1L);
        addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
            assert e.getCause() instanceof IllegalArgumentException;
        }
        assert !addSuccessful;
        
    }
    
    @Test(groups = { "testIllegalUpdateScenarios" }, dependsOnGroups = { "addItemToOrder" })
    @Transactional
    public void testIllegalUpdateScenarios() throws UpdateCartException, AddToCartException, RemoveFromCartException {
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        
        Product activeProduct = addTestProduct("mug", "cups", true);
        Product inactiveProduct = addTestProduct("cup", "cups", false);
        
        // Inactive skus should not be added
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO().setQuantity(1).setSkuId(activeProduct.getDefaultSku().getId());
        boolean addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
        }
        assert addSuccessful;
        
        // should not be able to update to negative quantity
        OrderItem item = orderService.findLastMatchingItem(order, activeProduct.getDefaultSku().getId(), activeProduct.getId());
        itemRequest = new OrderItemRequestDTO().setQuantity(-3).setOrderItemId(item.getId());
        boolean updateSuccessful = true;
        try {
            orderService.updateItemQuantity(orderId, itemRequest, true);
        } catch (UpdateCartException e) {
            updateSuccessful = false;
        }
        assert !updateSuccessful;
        
        //shouldn't be able to update the quantity of a DOI inside of a bundle
        ProductBundle bundle = addProductBundle();
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setProductId(bundle.getId()).setSkuId(bundle.getDefaultSku().getId());
        addSuccessful = true;
        try {
            order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
            addSuccessful = false;
        }
        assert addSuccessful;
        
        BundleOrderItem bundleItem = (BundleOrderItem) orderService.findLastMatchingItem(order,
                                                                                         bundle.getDefaultSku().getId(),
                                                                                         bundle.getId());
        //should just be a single DOI inside the bundle
        DiscreteOrderItem doi = bundleItem.getDiscreteOrderItems().get(0);
        itemRequest = new OrderItemRequestDTO().setQuantity(4).setOrderItemId(doi.getId());
        try {
            orderService.updateItemQuantity(orderId, itemRequest, true);
        } catch (UpdateCartException e) {
            updateSuccessful = false;
        }
        assert !updateSuccessful;
    }
    
    @Test(groups = { "addBundleToOrder" }, dependsOnGroups = { "addAnotherItemToOrder" })
    @Rollback(false)
    @Transactional
    public void addBundleToOrder() throws AddToCartException {
        numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        
        ProductBundle bundleItem = addProductBundle();
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(bundleItem.getId());
        orderItemRequestDTO.setSkuId(bundleItem.getDefaultSku().getId());
        orderItemRequestDTO.setQuantity(1);
        
        order = orderService.addItem(order.getId(), orderItemRequestDTO, true);
        BundleOrderItem item = (BundleOrderItem) orderService.findLastMatchingItem(order, bundleItem.getDefaultSku().getId(), null);
        bundleOrderItemId = item.getId();
        assert item != null;
        assert item.getQuantity() == 1;
        assert item.getDiscreteOrderItems().size() == 1;
    }
    
    @Test(groups = { "removeBundleFromOrder" }, dependsOnGroups = { "addBundleToOrder" })
    @Rollback(false)
    @Transactional
    public void removeBundleFromOrder() throws RemoveFromCartException {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() == numOrderItems;
        int startingSize = orderItems.size();
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem != null;
        assert bundleOrderItem.getDiscreteOrderItems() != null;
        assert bundleOrderItem.getDiscreteOrderItems().size() == 1;
        order = orderService.removeItem(order.getId(), bundleOrderItem.getId(), true);
        List<OrderItem> items = order.getOrderItems();
        assert items != null;
        assert items.size() == startingSize - 1;
    }
    
    @Test(groups = { "getItemsForOrder" }, dependsOnGroups = { "removeBundleFromOrder" })
    @Transactional
    public void getItemsForOrder() {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems != null;
        assert orderItems.size() == numOrderItems - 1;
    }
    
    @Test(groups = { "testManyToOneFGItemToOrderItem" }, dependsOnGroups = { "getItemsForOrder" })
    @Transactional
    public void testManyToOneFGItemToOrderItem() throws UpdateCartException, RemoveFromCartException, PricingException {
        // Grab the order and the first OrderItem
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        
        // Set the quantity of the first OrderItem to 10
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(10);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the quantity has changed
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 10;
        
        // Assert that the appropriate fulfillment group item has changed
        assert order.getFulfillmentGroups().size() == 1;
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 1;
        FulfillmentGroupItem fgItem = null;
        for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
            if (fgi.getOrderItem().equals(updatedItem)) {
                fgItem = fgi;
            }
        }
        
        assert fgItem != null;


        /*
        TODO because of the merging that takes place in the offer service, these tests do not
        work unless multiship options are incorporated

        // Split one of the fulfillment group items to simulate a OneToMany relationship between
        // OrderItems and FulfillmentGroupItems
        FulfillmentGroup secondFg = fulfillmentGroupService.createEmptyFulfillmentGroup();
        secondFg.setOrder(order);
        secondFg = fulfillmentGroupService.save(secondFg);
        fgItem.setQuantity(5);
        FulfillmentGroupItem clonedFgItem = fgItem.clone();
        clonedFgItem.setFulfillmentGroup(secondFg);
        secondFg.addFulfillmentGroupItem(clonedFgItem);
        order.getFulfillmentGroups().add(secondFg);
        order = orderService.save(order, false);
        
        // Set the quantity of the first OrderItem to 15
        orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(15);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the quantity has changed
        updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 15;
        
        // Assert that the appropriate fulfillment group item has changed
        assert order.getFulfillmentGroups().size() == 2;
        int fgItemQuantity = 0;
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgi : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fgi.getOrderItem().equals(updatedItem)) {
                    fgItemQuantity += fgi.getQuantity();
                }
            }
        }
        assert fgItemQuantity == 15;
        
        // Set the quantity of the first OrderItem to 3
        orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(3);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the quantity has changed
        updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 3;
        
        // Assert that the appropriate fulfillment group item has changed
        assert order.getFulfillmentGroups().size() == 2;
        boolean fgItemFound = false;
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgi : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fgi.getOrderItem().equals(updatedItem)) {
                    assert fgItemFound == false;
                    assert fgi.getQuantity() == 3;
                    fgItemFound = true;
                }
            }
        }
        assert fgItemFound;
        */
    }

    @Test(groups = { "updateItemsInOrder" }, dependsOnGroups = { "getItemsForOrder" })
    @Transactional
    public void updateItemsInOrder() throws UpdateCartException, RemoveFromCartException {
        // Grab the order and the first OrderItem
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        
        // Set the quantity of the first OrderItem to 10
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(10);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the quantity has changed
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 10;
        
        // Assert that the appropriate fulfillment group item has changed
        assert order.getFulfillmentGroups().size() == 1;
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 1;
        boolean fgItemUpdated = false;
        for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
            if (fgi.getOrderItem().equals(updatedItem)) {
                assert fgi.getQuantity() == 10;
                fgItemUpdated = true;
            }
        }
        assert fgItemUpdated;
        
        // Set the quantity of the first OrderItem to 5
        orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(5);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the quantity has changed - going to a smaller quantity is also ok
        updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 5;
        
        // Assert that the appropriate fulfillment group item has changed
        assert order.getFulfillmentGroups().size() == 1;
        fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == 1;
        fgItemUpdated = false;
        for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
            if (fgi.getOrderItem().equals(updatedItem)) {
                assert fgi.getQuantity() == 5;
                fgItemUpdated = true;
            }
        }
        assert fgItemUpdated;
        
        // Setting the quantity to 0 should in fact remove the item completely
        int startingSize = order.getOrderItems().size();
        orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(item.getId());
        orderItemRequestDTO.setQuantity(0);
        order = orderService.updateItemQuantity(order.getId(), orderItemRequestDTO, true);
        
        // Assert that the item has been removed
        updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem == null;
        assert order.getOrderItems().size() == startingSize - 1;
        
        // Assert that the appropriate fulfillment group item has been removed
        assert order.getFulfillmentGroups().size() == 0;
        /*
        TODO Since we commented out some tests above, there is no longer an additional item
        in the cart, hence the fulfillment group is removed

        fg = order.getFulfillmentGroups().get(0);
        assert fg.getFulfillmentGroupItems().size() == startingSize - 1;
        boolean fgItemRemoved = true;
        for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
            if (fgi.getOrderItem().equals(updatedItem)) {
                fgItemRemoved = false;
            }
        }
        assert fgItemRemoved;*/
    }

    @Test(groups = { "removeItemFromOrder" }, dependsOnGroups = { "getItemsForOrder" })
    @Transactional
    public void removeItemFromOrder() throws RemoveFromCartException {
        // Grab the order and the first OrderItem
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        int startingSize = orderItems.size();
        OrderItem item = orderItems.get(0);
        Long itemId = item.getId();
        assert item != null;
        
        // Remove the item
        order = orderService.removeItem(order.getId(), item.getId(), true);
        List<OrderItem> items = order.getOrderItems();
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        
        // Assert that the item has been removed
        assert items != null;
        assert items.size() == startingSize - 1;
        assert updatedItem == null;
    }

    @Test(groups = { "checkOrderItems" }, dependsOnGroups = { "removeItemFromOrder" })
    @Transactional
    public void checkOrderItems() throws PricingException {
        Order order = orderService.findOrderById(orderId);
        // The removal from the removeBundleFromOrder() has actually persisted.
        // However, the previous two transactions were rolled back and thus the items still exist.
        assert order.getOrderItems().size() == 1;
        
        // As mentioned, the bundleOrderItem however has gone away
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem == null;
    }

    
    @Test(groups = { "getOrdersForCustomer" }, dependsOnGroups = { "readCustomer", "findCurrentCartForCustomer" })
    @Transactional
    public void getOrdersForCustomer() {
        String username = "customer1";
        Customer customer = customerService.readCustomerByUsername(username);
        List<Order> orders = orderService.findOrdersForCustomer(customer);
        assert orders != null;
        assert orders.size() > 0;
    }

    @Test(groups = { "findCartForAnonymousCustomer" }, dependsOnGroups = { "getOrdersForCustomer" })
    public void findCartForAnonymousCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.findCartForCustomer(customer);
        assert order == null;
        order = orderService.createNewCartForCustomer(customer);
        Long orderId = order.getId();
        Order newOrder = orderService.findOrderById(orderId);
        assert newOrder != null;
        assert newOrder.getCustomer() != null;
    }

    @Test(groups = { "findOrderByOrderNumber" }, dependsOnGroups = { "findCartForAnonymousCustomer" })
    @Transactional
    public void findOrderByOrderNumber() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);
        order.setOrderNumber("3456");
        order = orderService.save(order, false);
        Long orderId = order.getId();

        Order newOrder = orderService.findOrderByOrderNumber("3456");
        assert newOrder.getId().equals(orderId);

        Order nullOrder = orderService.findOrderByOrderNumber(null);
        assert nullOrder == null;

        nullOrder = orderService.findOrderByOrderNumber("");
        assert nullOrder == null;
    }

    @Test(groups = { "findNamedOrderForCustomer" }, dependsOnGroups = { "findOrderByOrderNumber" })
    @Transactional
    public void findNamedOrderForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.NAMED);
        order.setName("COOL ORDER");
        order = orderService.save(order, false);
        Long orderId = order.getId();

        Order newOrder = orderService.findNamedOrderForCustomer("COOL ORDER", customer);
        assert newOrder.getId().equals(orderId);
    }

    @Test(groups = { "testReadOrdersForCustomer" }, dependsOnGroups = { "findNamedOrderForCustomer" })
    @Transactional
    public void testReadOrdersForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = orderService.save(order, false);

        List<Order> newOrders = orderService.findOrdersForCustomer(customer, OrderStatus.IN_PROCESS);
        boolean containsOrder = false;

        if (newOrders.contains(order)) {
            containsOrder = true;
        }

        assert containsOrder == true;

        containsOrder = false;
        newOrders = orderService.findOrdersForCustomer(customer, null);

        if (newOrders.contains(order)) {
            containsOrder = true;
        }

        assert containsOrder == true;
    }

    @Test(groups = { "testOrderProperties" }, dependsOnGroups = { "testReadOrdersForCustomer" })
    public void testOrderProperties() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);

        assert order.getSubTotal() == null;
        assert order.getTotal() == null;

        Calendar testCalendar = Calendar.getInstance();
        order.setSubmitDate(testCalendar.getTime());
        assert order.getSubmitDate().equals(testCalendar.getTime());
    }

    @Test(groups = { "testNamedOrderForCustomer" }, dependsOnGroups = { "testOrderProperties" })
    public void testNamedOrderForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        customer = customerService.saveCustomer(customer);
        Order order = orderService.createNamedOrderForCustomer("Birthday Order", customer);
        Long orderId = order.getId();
        assert order != null;
        assert order.getName().equals("Birthday Order");
        assert order.getCustomer().equals(customer);
        
        orderService.cancelOrder(order);

        assert orderService.findOrderById(orderId) == null;
    }

    @Test(groups = { "addPaymentToOrder" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class, dependsOnGroups = { "checkOrderItems" })
    @Rollback(false)
    @Transactional
    public void addPaymentToOrder(OrderPayment paymentInfo) {
        Order order = orderService.findOrderById(orderId);
        orderService.addPaymentToOrder(order, paymentInfo, null);

        order = orderService.findOrderById(orderId);
        OrderPayment payment = order.getPayments().get(order.getPayments().indexOf(paymentInfo));
        assert payment != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().equals(order);
    }

    @Test(groups = { "testOrderPaymentInfos" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class)
    @Transactional
    public void testOrderPaymentInfos(OrderPayment info) throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        Order order = orderService.createNewCartForCustomer(customer);
        info = orderService.addPaymentToOrder(order, info, null);

        boolean foundInfo = false;
        assert order.getPayments() != null;
        for (OrderPayment testInfo : order.getPayments()) {
            if (testInfo.equals(info)) {
                foundInfo = true;
            }
        }
        assert foundInfo == true;
        assert orderService.findPaymentsForOrder(order) != null;
    }

    @Test
    public void findCartForNullCustomerId() {
        assert orderService.findCartForCustomer(new CustomerImpl()) == null;
    }

    @Test(groups = { "testSubmitOrder" }, dependsOnGroups = { "findNamedOrderForCustomer" })
    public void testSubmitOrder() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = orderService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = orderService.save(order, false);
        Long orderId = order.getId();

        Order confirmedOrder = orderService.confirmOrder(order);

        confirmedOrder = orderService.findOrderById(confirmedOrder.getId());
        Long confirmedOrderId = confirmedOrder.getId();

        assert orderId.equals(confirmedOrderId);
        assert confirmedOrder.getStatus().equals(OrderStatus.SUBMITTED);
    }

}
