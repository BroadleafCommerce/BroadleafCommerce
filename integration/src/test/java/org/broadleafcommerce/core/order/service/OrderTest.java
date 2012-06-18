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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.*;
import org.broadleafcommerce.core.order.FulfillmentGroupDataProvider;
import org.broadleafcommerce.core.order.domain.*;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.PaymentInfoDataProvider;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.pricing.ShippingRateDataProvider;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.service.ShippingRateService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderTest extends OrderBaseTest {

    private Long orderId = null;
    private int numOrderItems = 0;
    private Long fulfillmentGroupId;
    private Long bundleOrderItemId;

    @Resource
    private OrderItemService orderItemService;
    @Resource
    private SkuDao skuDao;
    @Resource
    private ShippingRateService shippingRateService;

    @Test(groups = { "createCartForCustomer" }, dependsOnGroups = { "readCustomer", "createPhone" })
    @Transactional
    @Rollback(false)
    public void createCartForCustomer() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = cartService.createNewCartForCustomer(customer);
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

        Order order = cartService.findCartForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        this.orderId = order.getId();
    }

    @Test(groups = { "addItemToOrder" }, dependsOnGroups = { "findCurrentCartForCustomer", "createSku" })
    @Rollback(false)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addItemToOrder() throws PricingException {
        numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = cartService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        DiscreteOrderItem item = (DiscreteOrderItem) cartService.addDiscreteItemToOrder(order, itemRequest);
        assert item != null;
        assert item.getQuantity() == numOrderItems;
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
    }

    @Test(groups = { "addAnotherItemToOrder" }, dependsOnGroups = { "addItemToOrder" })
    @Rollback(false)
    @Transactional
    public void addAnotherItemToOrder() throws PricingException {
    	numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        orderService.setAutomaticallyMergeLikeItems(true);

        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        DiscreteOrderItem item = (DiscreteOrderItem) orderService.addDiscreteItemToOrder(order, itemRequest, true);
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
        assert item.getQuantity() == 2;  // item-was merged with prior item.

        order = orderService.findOrderById(orderId);

        assert(order.getOrderItems().size()==1);
        assert(order.getOrderItems().get(0).getQuantity()==2);

        // re-price the order without automatically merging.
        orderService.setAutomaticallyMergeLikeItems(false);
        DiscreteOrderItemRequest itemRequest2 = new DiscreteOrderItemRequest();
        itemRequest2.setQuantity(1);
        itemRequest2.setSku(sku);
        DiscreteOrderItem item2 = (DiscreteOrderItem) orderService.addDiscreteItemToOrder(order, itemRequest2, true);

        assert item2.getSku() != null;
        assert item2.getSku().equals(sku);
        assert item2.getQuantity() == 1;  // item-was not auto-merged with prior items.

        order = orderService.findOrderById(orderId);

        assert(order.getOrderItems().size()==2);
        assert(order.getOrderItems().get(0).getQuantity()==2);
        assert(order.getOrderItems().get(1).getQuantity()==1);


    }

    @Test(groups = { "addBundleToOrder" }, dependsOnGroups = { "addAnotherItemToOrder" })
    @Rollback(false)
    @Transactional
    public void addBundleToOrder() throws PricingException {
        numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = orderService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;

        BundleOrderItemRequest bundleRequest = new BundleOrderItemRequest();
        bundleRequest.setQuantity(1);
        bundleRequest.setName("myBundle");
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        bundleRequest.getDiscreteOrderItems().add(itemRequest);

        BundleOrderItem item = (BundleOrderItem) orderService.addBundleItemToOrder(order, bundleRequest);
        bundleOrderItemId = item.getId();
        assert item != null;
        assert item.getQuantity() == 1;
    }

    @Test(groups = { "removeBundleFromOrder" }, dependsOnGroups = { "addBundleToOrder" })
    @Rollback(false)
    @Transactional
    public void removeBundleFromOrder() throws PricingException {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() == numOrderItems;
        int startingSize = orderItems.size();
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem != null;
        assert bundleOrderItem.getDiscreteOrderItems() != null;
        assert bundleOrderItem.getDiscreteOrderItems().size() == 1;
        orderService.removeItemFromOrder(order, bundleOrderItem);
        order = orderService.findOrderById(orderId);
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
    public void updateItemsInOrder() throws ItemNotFoundException, PricingException {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        //item.setSalePrice(new Money(BigDecimal.valueOf(10000)));
        ((DiscreteOrderItem) item).getSku().setSalePrice(new Money(BigDecimal.valueOf(10000)));
        ((DiscreteOrderItem) item).getSku().setRetailPrice(new Money(BigDecimal.valueOf(10000)));
        item.setQuantity(10);
        orderService.updateItemQuantity(order, item);
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getPrice().equals(new Money(BigDecimal.valueOf(10000)));
        assert updatedItem.getQuantity() == 10;
        
        List<OrderItem> updateItems = new ArrayList<OrderItem> (order.getOrderItems());
        updateItems.get(0).setQuantity(15);
        orderService.updateItemQuantity(order, updatedItem);
        order = orderService.findOrderById(orderId);
        assert order.getOrderItems().get(0).getQuantity() == 15;
        
    }

    @Test(groups = { "removeItemFromOrder" }, dependsOnGroups = { "getItemsForOrder" })
    @Transactional
    public void removeItemFromOrder() throws PricingException {
        Order order = orderService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        int startingSize = orderItems.size();
        OrderItem item = orderItems.get(0);
        assert item != null;
        orderService.removeItemFromOrder(order, item);
        order = orderService.findOrderById(orderId);
        List<OrderItem> items = order.getOrderItems();
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "checkOrderItems" }, dependsOnGroups = { "removeItemFromOrder" })
    @Transactional
    public void checkOrderItems() throws PricingException {
        Order order = orderService.findOrderById(orderId);
        //the removal from the previous test was rolled back
        assert order.getOrderItems().size() == 2;
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem == null;
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
        //assert payment.getId() != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().equals(order);
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

    /*
     * @Test(groups = { "removeFulFillmentGroupForOrderFirst" }, dependsOnGroups
     * = { "findCurrentCartForCustomer",
     * "addFulfillmentGroupToOrderFirst" }) public void
     * removeFulFillmentGroupForOrderFirst() { int beforeRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size();
     * FulfillmentGroup fulfillmentGroup = entityManager.find(FulfillmentGroupImpl.class,
     * fulfillmentGroupId); orderService.removeFulfillmentGroupFromOrder(order,
     * fulfillmentGroup); int afterRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
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

    /*
     * @Test(groups = { "removeDefaultFulFillmentGroupForOrder" },
     * dependsOnGroups = { "findCurrentCartForCustomer",
     * "addFulfillmentGroupToOrderFirst" }) public void
     * removeDefaultFulFillmentGroupForOrder() { int beforeRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size();
     * orderService.removeFulfillmentGroupFromOrder(order, fulfillmentGroup);
     * int afterRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
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
        Order order = cartService.findCartForCustomer(customer);
        assert order == null;
        order = cartService.createNewCartForCustomer(customer);
        Long orderId = order.getId();
        Order newOrder = orderService.findOrderById(orderId);
        assert newOrder != null;
        assert newOrder.getCustomer() != null;
    }

    @Test(groups = { "findOrderByOrderNumber" }, dependsOnGroups = { "findCartForAnonymousCustomer" })
    @Transactional
    public void findOrderByOrderNumber() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
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
        Order order = cartService.createNewCartForCustomer(customer);
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
        Order order = cartService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = orderService.save(order, false);

        List<Order> newOrders = orderService.findOrdersForCustomer(customer, OrderStatus.IN_PROCESS);
        boolean containsOrder = false;

        if (newOrders.contains(order))
        {
            containsOrder = true;
        }

        assert containsOrder == true;

        containsOrder = false;
        newOrders = orderService.findOrdersForCustomer(customer, null);

        if (newOrders.contains(order))
        {
            containsOrder = true;
        }

        assert containsOrder == true;
    }

    @Test(groups = { "testOrderProperties" }, dependsOnGroups = { "testReadOrdersForCustomer" })
    public void testOrderProperties() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);

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

        orderService.removeNamedOrderForCustomer("Birthday Order", customer);
        assert orderService.findOrderById(orderId) == null;

    }

    @Test(groups = { "testAddSkuToOrder" })
    @Transactional
    public void testAddSkuToOrder() throws PricingException {
    	Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));

        Category category = new CategoryImpl();
        category.setName("Pants");
        category = catalogService.saveCategory(category);
        
        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        
        Sku newDefaultSku = new SkuImpl();
        newDefaultSku.setName("Leather Pants");
        newDefaultSku.setRetailPrice(new Money(44.99));
        newDefaultSku.setActiveStartDate(activeStartCal.getTime());
        newDefaultSku.setDiscountable(true);
        newDefaultSku = catalogService.saveSku(newDefaultSku);
        
        Product newProduct = new ProductImpl();
        newProduct.setDefaultCategory(category);
        newProduct.setDefaultSku(newDefaultSku);
        newProduct = catalogService.saveProduct(newProduct);

        Order order = orderService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = orderService.addSkuToOrder(order.getId(), null,
                null, category.getId(), 2);
        OrderItem orderNullOrderItem = orderService.addSkuToOrder(null, newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), null, 2);
    	
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
    }

    @Test(groups = { "testOrderPaymentInfos" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class)
    @Transactional
    public void testOrderPaymentInfos(PaymentInfo info) throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        Order order = cartService.createNewCartForCustomer(customer);
        orderService.addPaymentToOrder(order, info);

        boolean foundInfo = false;
        assert order.getPaymentInfos() != null;
        for (PaymentInfo testInfo : order.getPaymentInfos())
        {
            if (testInfo.equals(info))
            {
                foundInfo = true;
            }
        }
        assert foundInfo == true;
        assert orderService.readPaymentInfosForOrder(order) != null;

        //orderService.removeAllPaymentsFromOrder(order);
        //assert order.getPaymentInfos().size() == 0;
    }

    @Test(groups = { "testSubmitOrder" }, dependsOnGroups = { "findNamedOrderForCustomer" })
    public void testSubmitOrder() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = orderService.save(order, false);
        Long orderId = order.getId();

        Order confirmedOrder = orderService.confirmOrder(order);

        confirmedOrder = orderService.findOrderById(confirmedOrder.getId());
        Long confirmedOrderId = confirmedOrder.getId();

        assert orderId.equals(confirmedOrderId);
        assert confirmedOrder.getStatus().equals(OrderStatus.SUBMITTED);
    }

    @Test
    public void findCartForNullCustomerId() {
        assert cartService.findCartForCustomer(new CustomerImpl()) == null;
    }
    
    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testCreateNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));

        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        Category category = new CategoryImpl();
        category.setName("Pants");
        category.setActiveStartDate(activeStartCal.getTime());
        category = catalogService.saveCategory(category);
        
        Sku newDefaultSku = new SkuImpl();
        newDefaultSku.setName("Leather Pants");
        newDefaultSku.setRetailPrice(new Money(44.99));
        newDefaultSku.setActiveStartDate(activeStartCal.getTime());
        newDefaultSku.setDiscountable(true);
        newDefaultSku = catalogService.saveSku(newDefaultSku);
        
        Product newProduct = new ProductImpl();
        newProduct.setDefaultCategory(category);
        newProduct.setDefaultSku(newDefaultSku);
        newProduct = catalogService.saveProduct(newProduct);        

        Order order = orderService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = orderService.addSkuToOrder(order.getId(), null,
                null, category.getId(), 2);
        OrderItem orderNullOrderItem = orderService.addSkuToOrder(null, newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = orderService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), null, 2);
        
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
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
    	fgRequest.setOrder(cartService.findCartForCustomer(customer));
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
    
}
