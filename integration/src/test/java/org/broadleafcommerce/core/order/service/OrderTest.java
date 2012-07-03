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

import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.InventoryUnavailableException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.PaymentInfoDataProvider;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.pricing.service.ShippingRateService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import java.util.Calendar;
import java.util.List;

@SuppressWarnings("deprecation")
public class OrderTest extends OrderBaseTest {

    private Long orderId = null;
    private int numOrderItems = 0;
    private Long fulfillmentGroupId;
    private Long bundleOrderItemId;

    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;
    
    @Resource
    private SkuDao skuDao;
    
    @Resource
    private ShippingRateService shippingRateService;
    
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
        Sku sku = skuDao.readFirstSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        
        List<Sku> allSkus = skuDao.readAllSkus();
        
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setQuantity(1);
        itemRequest.setSkuId(sku.getId());
        order = orderService.addItem(orderId, itemRequest, true);
        
        DiscreteOrderItem item = (DiscreteOrderItem) orderService.findLastMatchingItem(order, sku.getId(), null);
        
        assert item != null;
        assert item.getQuantity() == numOrderItems;
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
    }
    
    @Test(groups = { "addAnotherItemToOrder" }, dependsOnGroups = { "addItemToOrder" })
    @Rollback(false)
    @Transactional
    public void addAnotherItemToOrder() throws AddToCartException {
        Sku sku = skuDao.readFirstSku();
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
        	assert e.getCause() instanceof InventoryUnavailableException;
        }
        assert !addSuccessful;
        
    	// Products that have SKUs marked as inactive should not be added either
        itemRequest = new OrderItemRequestDTO().setQuantity(1).setProductId(inactiveProduct.getId());
        addSuccessful = true;
        try {
        	order = orderService.addItem(orderId, itemRequest, true);
        } catch (AddToCartException e) {
        	addSuccessful = false;
        	assert e.getCause() instanceof InventoryUnavailableException;
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
        assert order.getOrderItems().size() == 2;
        
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
        assert order.getRemainingTotal() == null;

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
    public void addPaymentToOrder(PaymentInfo paymentInfo) {
        Order order = orderService.findOrderById(orderId);
        orderService.addPaymentToOrder(order, paymentInfo, null);

        order = orderService.findOrderById(orderId);
        PaymentInfo payment = order.getPaymentInfos().get(order.getPaymentInfos().indexOf(paymentInfo));
        assert payment != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().equals(order);
    }

    @Test(groups = { "testOrderPaymentInfos" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class)
    @Transactional
    public void testOrderPaymentInfos(PaymentInfo info) throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        Order order = orderService.createNewCartForCustomer(customer);
        orderService.addPaymentToOrder(order, info, null);

        boolean foundInfo = false;
        assert order.getPaymentInfos() != null;
        for (PaymentInfo testInfo : order.getPaymentInfos()) {
            if (testInfo.equals(info)) {
                foundInfo = true;
            }
        }
        assert foundInfo == true;
        assert orderService.findPaymentInfosForOrder(order) != null;
    }

    @Test
    public void findCartForNullCustomerId() {
        assert orderService.findCartForCustomer(new CustomerImpl()) == null;
    }

    /*
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

    @Test(groups = "addFulfillmentGroupToOrderFirst", dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "addPaymentToOrder" })
    @Rollback(false)
    @Transactional
    public void addFulfillmentGroupToOrderFirst(FulfillmentGroup fulfillmentGroup) throws PricingException {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()).get(0).getAddress();
        Order order = orderService.findOrderById(orderId);

        fulfillmentGroup.setOrder(order);
        fulfillmentGroup.setAddress(address);
        FulfillmentGroup fg = orderService.addFulfillmentGroupToOrder(order, fulfillmentGroup);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getAddress().equals(fulfillmentGroup.getAddress());
        assert fg.getOrder().equals(order);
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
        this.fulfillmentGroupId = fg.getId();
    }

    @Test(groups = { "removeFulfillmentGroupFromOrder" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
    @Transactional
    public void removeFulfillmentGroupFromOrder() throws PricingException {
        Order order = orderService.findOrderById(orderId);
        List<FulfillmentGroup> fgItems = order.getFulfillmentGroups();
        assert fgItems.size() > 0;
        int startingSize = fgItems.size();
        FulfillmentGroup item = fgItems.get(0);
        assert item != null;
        orderService.removeFulfillmentGroupFromOrder(order, item);
        order = orderService.findOrderById(orderId);
        List<FulfillmentGroup> items = order.getFulfillmentGroups();
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "findFulFillmentGroupForOrderFirst" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
    @Transactional
    public void findFillmentGroupForOrderFirst() {
        Order order = orderService.findOrderById(orderId);
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg != null;
        assert fg.getId() != null;
        FulfillmentGroup fulfillmentGroup = em.find(FulfillmentGroupImpl.class, fulfillmentGroupId);
        assert fg.getAddress().getId().equals(fulfillmentGroup.getAddress().getId());
        assert fg.getOrder().equals(order);
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
    }

    @Test(groups= {"addItemToFulfillmentGroupSecond"}, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
    @Transactional
    public void addItemToFulfillmentgroupSecond() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()).get(0).getAddress();
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert(orderItems.size() > 0);
        FulfillmentGroup newFg = new FulfillmentGroupImpl();
        newFg.setAddress(address);
        newFg.setMethod("standard");
        newFg.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        try {
            newFg = orderService.addItemToFulfillmentGroup(orderItems.get(0), newFg, 1);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        order = orderService.findOrderById(orderId);
        FulfillmentGroup newNewFg = order.getFulfillmentGroups().get(order.getFulfillmentGroups().indexOf(newFg));
        assert(newNewFg.getFulfillmentGroupItems().size() == 1);
        assert(newNewFg.getFulfillmentGroupItems().get(0).getOrderItem().equals(orderItems.get(0)));

    }

    @Test(groups = { "findDefaultFulFillmentGroupForOrder" }, dependsOnGroups = { "findCurrentCartForCustomer", "addFulfillmentGroupToOrderFirst" })
    @Transactional
    public void findDefaultFillmentGroupForOrder() {
        Order order = orderService.findOrderById(orderId);
        FulfillmentGroup fg = orderService.findDefaultFulfillmentGroupForOrder(order);
        assert fg != null;
        assert fg.getId() != null;
        FulfillmentGroup fulfillmentGroup = em.find(FulfillmentGroupImpl.class, fulfillmentGroupId);
        assert fg.getAddress().getId().equals(fulfillmentGroup.getAddress().getId());
        assert fg.getOrder().equals(order);
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
    }

    @Test(groups = { "removeItemFromOrderAfterDefaultFulfillmentGroup" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
    @Transactional
    public void removeItemFromOrderAfterFulfillmentGroups() {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        assert item != null;
        try {
            orderService.removeItemFromOrder(order, item);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        FulfillmentGroup fg = orderService.findDefaultFulfillmentGroupForOrder(order);
        for (FulfillmentGroupItem fulfillmentGroupItem : fg.getFulfillmentGroupItems()) {
            assert !fulfillmentGroupItem.getOrderItem().equals(item);
        }
    }

    @Test(groups = { "testOrderFulfillmentGroups" }, dataProvider = "basicShippingRates", dataProviderClass = ShippingRateDataProvider.class)
    @Transactional
    public void testAddFulfillmentGroupToOrder(ShippingRate shippingRate, ShippingRate sr2) throws PricingException, ItemNotFoundException{
        shippingRate = shippingRateService.save(shippingRate);
        sr2 = shippingRateService.save(sr2);
    	Customer customer = createCustomerWithAddresses();
    	Order order = initializeExistingCart(customer);
    	CustomerAddress customerAddress = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()).get(0);
    	
    	FulfillmentGroupRequest fgRequest = new FulfillmentGroupRequest();
    	
    	List<FulfillmentGroupItemRequest> fgiRequests = new ArrayList<FulfillmentGroupItemRequest>();

    	for (OrderItem orderItem : order.getOrderItems()) {
    		FulfillmentGroupItemRequest fgiRequest = new FulfillmentGroupItemRequest();
    		fgiRequest.setOrderItem(orderItem);
    		fgiRequest.setQuantity(1);
    		fgiRequests.add(fgiRequest);
    	}
    	
    	fgRequest.setAddress(customerAddress.getAddress());
    	fgRequest.setFulfillmentGroupItemRequests(fgiRequests);
    	fgRequest.setOrder(orderService.findCartForCustomer(customer));
    	fgRequest.setMethod("standard");
    	fgRequest.setService(ShippingServiceType.BANDED_SHIPPING.getType());
    	orderService.addFulfillmentGroupToOrder(fgRequest);
    	
    	Order resultOrder = orderService.findOrderById(order.getId());
    	assert resultOrder.getFulfillmentGroups().size() == 1;
    	assert resultOrder.getFulfillmentGroups().get(0).getFulfillmentGroupItems().size() == 2;
    	
    	orderService.removeAllFulfillmentGroupsFromOrder(order, false);
    	resultOrder = orderService.findOrderById(order.getId());
    	assert resultOrder.getFulfillmentGroups().size() == 0;
    	
    	FulfillmentGroup defaultFg = orderService.createDefaultFulfillmentGroup(order, customerAddress.getAddress());
    	defaultFg.setMethod("standard");
    	defaultFg.setService(ShippingServiceType.BANDED_SHIPPING.getType());
    	assert defaultFg.isPrimary();
    	orderService.addFulfillmentGroupToOrder(order, defaultFg);
    	resultOrder = orderService.findOrderById(order.getId());
    	assert resultOrder.getFulfillmentGroups().size() == 1;
    }
    */
    
}
