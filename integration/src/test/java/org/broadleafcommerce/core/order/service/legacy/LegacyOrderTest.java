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
package org.broadleafcommerce.core.order.service.legacy;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.order.FulfillmentGroupDataProvider;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.PaymentInfoDataProvider;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

@SuppressWarnings("deprecation")
public class LegacyOrderTest extends LegacyOrderBaseTest {

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
    
    @Test(groups = { "createCartForCustomerLegacy" }, dependsOnGroups = { "readCustomer", "createPhone" })
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

    @Test(groups = { "findCurrentCartForCustomerLegacy" }, dependsOnGroups = { "readCustomer", "createPhone", "createCartForCustomerLegacy" })
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

    @Test(groups = { "addItemToOrderLegacy" }, dependsOnGroups = { "findCurrentCartForCustomerLegacy", "createSku", "testCatalog" })
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

    @Test(groups = { "addAnotherItemToOrderLegacy" }, dependsOnGroups = { "addItemToOrderLegacy" })
    @Rollback(false)
    @Transactional
    public void addAnotherItemToOrder() throws PricingException {
        numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = cartService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        cartService.setAutomaticallyMergeLikeItems(true); 

        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        DiscreteOrderItem item = (DiscreteOrderItem) cartService.addDiscreteItemToOrder(order, itemRequest, true);
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
        assert item.getQuantity() == 2;  // item-was merged with prior item.

        order = cartService.findOrderById(orderId);

        assert(order.getOrderItems().size()==1);
        assert(order.getOrderItems().get(0).getQuantity()==2);

        /*
        This test is currently not supported, as the order service only supports like item merging

        // re-price the order without automatically merging.
        cartService.setAutomaticallyMergeLikeItems(false);
        DiscreteOrderItemRequest itemRequest2 = new DiscreteOrderItemRequest();
        itemRequest2.setQuantity(1);
        itemRequest2.setSku(sku);
        DiscreteOrderItem item2 = (DiscreteOrderItem) cartService.addDiscreteItemToOrder(order, itemRequest2, true);

        assert item2.getSku() != null;
        assert item2.getSku().equals(sku);
        assert item2.getQuantity() == 1;  // item-was not auto-merged with prior items.

        order = cartService.findOrderById(orderId);

        assert(order.getOrderItems().size()==2);
        assert(order.getOrderItems().get(0).getQuantity()==2);
        assert(order.getOrderItems().get(1).getQuantity()==1);
        */
    }

    @Test(groups = { "addBundleToOrderLegacy" }, dependsOnGroups = { "addAnotherItemToOrderLegacy" })
    @Rollback(false)
    @Transactional
    public void addBundleToOrder() throws PricingException {
        //numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        Order order = cartService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;

        BundleOrderItemRequest bundleRequest = new BundleOrderItemRequest();
        bundleRequest.setQuantity(1);
        bundleRequest.setName("myBundle");
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        bundleRequest.getDiscreteOrderItems().add(itemRequest);

        BundleOrderItem item = (BundleOrderItem) cartService.addBundleItemToOrder(order, bundleRequest);
        bundleOrderItemId = item.getId();
        assert item != null;
        assert item.getQuantity() == 1;
    }

    @Test(groups = { "removeBundleFromOrderLegacy" }, dependsOnGroups = { "addBundleToOrderLegacy" })
    @Rollback(false)
    @Transactional
    public void removeBundleFromOrder() throws PricingException {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() == numOrderItems;
        int startingSize = orderItems.size();
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem != null;
        assert bundleOrderItem.getDiscreteOrderItems() != null;
        assert bundleOrderItem.getDiscreteOrderItems().size() == 1;
        cartService.removeItemFromOrder(order, bundleOrderItem);
        order = cartService.findOrderById(orderId);
        List<OrderItem> items = order.getOrderItems();
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "getItemsForOrderLegacy" }, dependsOnGroups = { "removeBundleFromOrderLegacy" })
    @Transactional
    public void getItemsForOrder() {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems != null;
        assert orderItems.size() == numOrderItems - 1;
    }

    @Test(groups = { "updateItemsInOrderLegacy" }, dependsOnGroups = { "getItemsForOrderLegacy" })
    @Transactional
    public void updateItemsInOrder() throws ItemNotFoundException, PricingException {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        //item.setSalePrice(new Money(BigDecimal.valueOf(10000)));
        ((DiscreteOrderItem) item).getSku().setSalePrice(new Money(BigDecimal.valueOf(10000)));
        ((DiscreteOrderItem) item).getSku().setRetailPrice(new Money(BigDecimal.valueOf(10000)));
        item.setQuantity(10);
        cartService.updateItemQuantity(order, item);
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        assert updatedItem.getPrice().equals(new Money(BigDecimal.valueOf(10000)));
        assert updatedItem.getQuantity() == 10;
        
        List<OrderItem> updateItems = new ArrayList<OrderItem> (order.getOrderItems());
        updateItems.get(0).setQuantity(15);
        cartService.updateItemQuantity(order, updatedItem);
        order = cartService.findOrderById(orderId);
        assert order.getOrderItems().get(0).getQuantity() == 15;
        
    }

    @Test(groups = { "removeItemFromOrderLegacy" }, dependsOnGroups = { "getItemsForOrderLegacy" })
    @Transactional
    public void removeItemFromOrder() throws PricingException {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        int startingSize = orderItems.size();
        OrderItem item = orderItems.get(0);
        assert item != null;
        cartService.removeItemFromOrder(order, item);
        order = cartService.findOrderById(orderId);
        List<OrderItem> items = order.getOrderItems();
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "checkOrderItemsLegacy" }, dependsOnGroups = { "removeItemFromOrderLegacy" })
    @Transactional
    public void checkOrderItems() throws PricingException {
        Order order = cartService.findOrderById(orderId);
        //the removal from the previous test was rolled back
        assert order.getOrderItems().size() == 1;
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem == null;
    }

    @Test(groups = { "addPaymentToOrderLegacy" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class, dependsOnGroups = { "checkOrderItemsLegacy" })
    @Rollback(false)
    @Transactional
    public void addPaymentToOrder(OrderPayment paymentInfo) {
        Order order = cartService.findOrderById(orderId);
        cartService.addPaymentToOrder(order, paymentInfo, null);

        order = cartService.findOrderById(orderId);
        OrderPayment payment = order.getPayments().get(order.getPayments().indexOf(paymentInfo));
        assert payment != null;
        //assert payment.getId() != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().equals(order);
    }

    @Test(groups = "addFulfillmentGroupToOrderFirstLegacy", dataProvider = "basicFulfillmentGroupLegacy", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "addPaymentToOrderLegacy" })
    @Rollback(false)
    @Transactional
    public void addFulfillmentGroupToOrderFirst(FulfillmentGroup fulfillmentGroup) throws PricingException {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()).get(0).getAddress();
        Order order = cartService.findOrderById(orderId);

        fulfillmentGroup.setOrder(order);
        fulfillmentGroup.setAddress(address);
        FulfillmentGroup fg = cartService.addFulfillmentGroupToOrder(order, fulfillmentGroup);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getAddress().equals(fulfillmentGroup.getAddress());
        assert fg.getOrder().equals(order);
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
        this.fulfillmentGroupId = fg.getId();
    }

    @Test(groups = { "removeFulfillmentGroupFromOrderLegacy" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirstLegacy" })
    @Transactional
    public void removeFulfillmentGroupFromOrder() throws PricingException {
        Order order = cartService.findOrderById(orderId);
        List<FulfillmentGroup> fgItems = order.getFulfillmentGroups();
        assert fgItems.size() > 0;
        int startingSize = fgItems.size();
        FulfillmentGroup item = fgItems.get(0);
        assert item != null;
        cartService.removeFulfillmentGroupFromOrder(order, item);
        order = cartService.findOrderById(orderId);
        List<FulfillmentGroup> items = order.getFulfillmentGroups();
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "findFulFillmentGroupForOrderFirstLegacy" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirstLegacy" })
    @Transactional
    public void findFillmentGroupForOrderFirst() {
        Order order = cartService.findOrderById(orderId);
        FulfillmentGroup fg = order.getFulfillmentGroups().get(0);
        assert fg != null;
        assert fg.getId() != null;
        FulfillmentGroup fulfillmentGroup = em.find(FulfillmentGroupImpl.class, fulfillmentGroupId);
        assert fg.getAddress().getId().equals(fulfillmentGroup.getAddress().getId());
        assert fg.getOrder().equals(order);
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
    }

    @Test(groups= {"addItemToFulfillmentGroupSecondLegacy"}, dependsOnGroups = { "addFulfillmentGroupToOrderFirstLegacy" })
    @Transactional
    public void addItemToFulfillmentgroupSecond() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()).get(0).getAddress();
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert(orderItems.size() > 0);
        FulfillmentGroup newFg = new FulfillmentGroupImpl();
        newFg.setAddress(address);
        newFg.setMethod("standard");
        newFg.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        try {
            newFg = cartService.addItemToFulfillmentGroup(orderItems.get(0), newFg, 1);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        order = cartService.findOrderById(orderId);
        FulfillmentGroup newNewFg = order.getFulfillmentGroups().get(order.getFulfillmentGroups().indexOf(newFg));
        assert(newNewFg.getFulfillmentGroupItems().size() == 1);
        assert(newNewFg.getFulfillmentGroupItems().get(0).getOrderItem().equals(orderItems.get(0)));

    }

    /*
     * @Test(groups = { "removeFulFillmentGroupForOrderFirst" }, dependsOnGroups
     * = { "findCurrentCartForCustomer",
     * "addFulfillmentGroupToOrderFirst" }) public void
     * removeFulFillmentGroupForOrderFirst() { int beforeRemove =
     * cartService.findFulfillmentGroupsForOrder(order).size();
     * FulfillmentGroup fulfillmentGroup = entityManager.find(FulfillmentGroupImpl.class,
     * fulfillmentGroupId); cartService.removeFulfillmentGroupFromOrder(order,
     * fulfillmentGroup); int afterRemove =
     * cartService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
    @Test(groups = { "findDefaultFulFillmentGroupForOrderLegacy" }, dependsOnGroups = { "findCurrentCartForCustomerLegacy", "addFulfillmentGroupToOrderFirstLegacy" })
    @Transactional
    public void findDefaultFillmentGroupForOrder() {
        Order order = cartService.findOrderById(orderId);
        FulfillmentGroup fg = cartService.findDefaultFulfillmentGroupForOrder(order);
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
     * cartService.findFulfillmentGroupsForOrder(order).size();
     * cartService.removeFulfillmentGroupFromOrder(order, fulfillmentGroup);
     * int afterRemove =
     * cartService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
    @Test(groups = { "removeItemFromOrderAfterDefaultFulfillmentGroupLegacy" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirstLegacy" })
    @Transactional
    public void removeItemFromOrderAfterFulfillmentGroups() {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        assert item != null;
        try {
            cartService.removeItemFromOrder(order, item);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        FulfillmentGroup fg = cartService.findDefaultFulfillmentGroupForOrder(order);
        for (FulfillmentGroupItem fulfillmentGroupItem : fg.getFulfillmentGroupItems()) {
            assert !fulfillmentGroupItem.getOrderItem().equals(item);
        }
    }

    @Test(groups = { "getOrdersForCustomerLegacy" }, dependsOnGroups = { "readCustomer", "findCurrentCartForCustomerLegacy" })
    @Transactional
    public void getOrdersForCustomer() {
        String username = "customer1";
        Customer customer = customerService.readCustomerByUsername(username);
        List<Order> orders = cartService.findOrdersForCustomer(customer);
        assert orders != null;
        assert orders.size() > 0;
    }

    @Test(groups = { "findCartForAnonymousCustomerLegacy" }, dependsOnGroups = { "getOrdersForCustomerLegacy" })
    public void findCartForAnonymousCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.findCartForCustomer(customer);
        assert order == null;
        order = cartService.createNewCartForCustomer(customer);
        Long orderId = order.getId();
        Order newOrder = cartService.findOrderById(orderId);
        assert newOrder != null;
        assert newOrder.getCustomer() != null;
    }

    @Test(groups = { "findOrderByOrderNumberLegacy" }, dependsOnGroups = { "findCartForAnonymousCustomerLegacy" })
    @Transactional
    public void findOrderByOrderNumber() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
        order.setOrderNumber("3456");
        order = cartService.save(order, false);
        Long orderId = order.getId();

        Order newOrder = cartService.findOrderByOrderNumber("3456");
        assert newOrder.getId().equals(orderId);

        Order nullOrder = cartService.findOrderByOrderNumber(null);
        assert nullOrder == null;

        nullOrder = cartService.findOrderByOrderNumber("");
        assert nullOrder == null;
    }

    @Test(groups = { "findNamedOrderForCustomerLegacy" }, dependsOnGroups = { "findOrderByOrderNumberLegacy" })
    @Transactional
    public void findNamedOrderForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.NAMED);
        order.setName("COOL ORDER");
        order = cartService.save(order, false);
        Long orderId = order.getId();

        Order newOrder = cartService.findNamedOrderForCustomer("COOL ORDER", customer);
        assert newOrder.getId().equals(orderId);
    }

    @Test(groups = { "testReadOrdersForCustomerLegacy" }, dependsOnGroups = { "findNamedOrderForCustomerLegacy" })
    @Transactional
    public void testReadOrdersForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = cartService.save(order, false);

        List<Order> newOrders = cartService.findOrdersForCustomer(customer, OrderStatus.IN_PROCESS);
        boolean containsOrder = false;

        if (newOrders.contains(order))
        {
            containsOrder = true;
        }

        assert containsOrder == true;

        containsOrder = false;
        newOrders = cartService.findOrdersForCustomer(customer, null);

        if (newOrders.contains(order))
        {
            containsOrder = true;
        }

        assert containsOrder == true;
    }

    @Test(groups = { "testOrderPropertiesLegacy" }, dependsOnGroups = { "testReadOrdersForCustomerLegacy" })
    public void testOrderProperties() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);

        assert order.getSubTotal() == null;
        assert order.getTotal() == null;

        Calendar testCalendar = Calendar.getInstance();
        order.setSubmitDate(testCalendar.getTime());
        assert order.getSubmitDate().equals(testCalendar.getTime());
    }

    @Test(groups = { "testNamedOrderForCustomerLegacy" }, dependsOnGroups = { "testOrderPropertiesLegacy" })
    public void testNamedOrderForCustomer() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        customer = customerService.saveCustomer(customer);
        Order order = cartService.createNamedOrderForCustomer("Birthday Order", customer);
        Long orderId = order.getId();
        assert order != null;
        assert order.getName().equals("Birthday Order");
        assert order.getCustomer().equals(customer);

        cartService.removeNamedOrderForCustomer("Birthday Order", customer);
        assert cartService.findOrderById(orderId) == null;

    }

    @Test(groups = { "testAddSkuToOrderLegacy" })
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

        Order order = cartService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = cartService.addSkuToOrder(order.getId(), null,
                null, category.getId(), 2);
        OrderItem orderNullOrderItem = cartService.addSkuToOrder(null, newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), null, 2);
        
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
    }

    @Test(groups = { "testOrderPaymentInfosLegacy" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class)
    @Transactional
    public void testOrderPaymentInfos(OrderPayment info) throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        Order order = cartService.createNewCartForCustomer(customer);
        cartService.addPaymentToOrder(order, info);

        boolean foundInfo = false;
        assert order.getPayments() != null;
        for (OrderPayment testInfo : order.getPayments())
        {
            if (testInfo.equals(info))
            {
                foundInfo = true;
            }
        }
        assert foundInfo == true;
        assert cartService.readPaymentInfosForOrder(order) != null;

        //cartService.removeAllPaymentsFromOrder(order);
        //assert order.getPaymentInfos().size() == 0;
    }

    @Test(groups = { "testSubmitOrderLegacy" }, dependsOnGroups = { "findNamedOrderForCustomerLegacy" })
    public void testSubmitOrder() throws PricingException {
        Customer customer = customerService.createCustomerFromId(null);
        Order order = cartService.createNewCartForCustomer(customer);
        order.setStatus(OrderStatus.IN_PROCESS);
        order = cartService.save(order, false);
        Long orderId = order.getId();

        Order confirmedOrder = cartService.confirmOrder(order);

        confirmedOrder = cartService.findOrderById(confirmedOrder.getId());
        Long confirmedOrderId = confirmedOrder.getId();

        assert orderId.equals(confirmedOrderId);
        assert confirmedOrder.getStatus().equals(OrderStatus.SUBMITTED);
    }

    @Test
    public void findCartForNullCustomerId() {
        assert cartService.findCartForCustomer(new CustomerImpl()) == null;
    }
    
    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

        Order order = cartService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = cartService.addSkuToOrder(order.getId(), null,
                null, category.getId(), 2);
        OrderItem orderNullOrderItem = cartService.addSkuToOrder(null, newDefaultSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = cartService.addSkuToOrder(order.getId(), newDefaultSku.getId(),
                newProduct.getId(), null, 2);
        
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
    }
    
    @Test(groups = { "testOrderFulfillmentGroupsLegacy" }, dataProvider = "basicShippingRates", dataProviderClass = ShippingRateDataProvider.class)
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
        cartService.addFulfillmentGroupToOrder(fgRequest);
        
        Order resultOrder = cartService.findOrderById(order.getId());
        assert resultOrder.getFulfillmentGroups().size() == 1;
        assert resultOrder.getFulfillmentGroups().get(0).getFulfillmentGroupItems().size() == 2;
        
        cartService.removeAllFulfillmentGroupsFromOrder(order, false);
        resultOrder = cartService.findOrderById(order.getId());
        assert resultOrder.getFulfillmentGroups().size() == 0;
        
        FulfillmentGroup defaultFg = cartService.createDefaultFulfillmentGroup(order, customerAddress.getAddress());
        defaultFg.setMethod("standard");
        defaultFg.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        assert defaultFg.isPrimary();
        cartService.addFulfillmentGroupToOrder(order, defaultFg);
        resultOrder = cartService.findOrderById(order.getId());
        assert resultOrder.getFulfillmentGroups().size() == 1;
    }
    
}
