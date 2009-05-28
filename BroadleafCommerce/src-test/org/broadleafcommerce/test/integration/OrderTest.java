package org.broadleafcommerce.test.integration;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.OrderItemService;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.FulfillmentGroupDataProvider;
import org.broadleafcommerce.test.dataprovider.PaymentInfoDataProvider;
import org.broadleafcommerce.util.money.Money;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderTest extends BaseTest {

    private Long orderId = null;
    private int numOrderItems = 0;
    private Long fulfillmentGroupId;
    private Long bundleOrderItemId;

    @Resource
    private CustomerAddressService customerAddressService;

    @Resource
    private CartService cartService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private CustomerService customerService;

    @Resource
    private SkuDao skuDao;

    @Test(groups = { "createCartForCustomer" }, dependsOnGroups = { "readCustomer1", "createPhone" })
    @Rollback(false)
    public void createCartForCustomer() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = cartService.createNewCartForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        this.orderId = order.getId();
    }

    @Test(groups = { "findCurrentCartForCustomer" }, dependsOnGroups = { "readCustomer1", "createPhone", "createCartForCustomer" })
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
    public void addAnotherItemToOrder() throws PricingException {
        Sku sku = skuDao.readFirstSku();
        Order order = cartService.findOrderById(orderId);
        assert order != null;
        assert sku.getId() != null;
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setQuantity(1);
        itemRequest.setSku(sku);
        DiscreteOrderItem item = (DiscreteOrderItem) cartService.addDiscreteItemToOrder(order, itemRequest);
        assert item != null;
        assert item.getQuantity() == 2;
        assert item.getSku() != null;
        assert item.getSku().equals(sku);
    }

    @Test(groups = { "addBundleToOrder" }, dependsOnGroups = { "addAnotherItemToOrder" })
    @Rollback(false)
    public void addBundleToOrder() throws PricingException {
        numOrderItems++;
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

    @Test(groups = { "removeBundleFromOrder" }, dependsOnGroups = { "addBundleToOrder" })
    @Rollback(false)
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

    @Test(groups = { "getItemsForOrder" }, dependsOnGroups = { "removeBundleFromOrder" })
    public void getItemsForOrder() {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems != null;
        assert orderItems.size() == numOrderItems - 1;
    }

    @Test(groups = { "updateItemsInOrder" }, dependsOnGroups = { "getItemsForOrder" })
    public void updateItemsInOrder() throws ItemNotFoundException, PricingException {
        Order order = cartService.findOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        item.setSalePrice(new Money(BigDecimal.valueOf(10000)));
        item.setQuantity(10);
        cartService.updateItemInOrder(order, item);
        OrderItem updatedItem = orderItemService.readOrderItemById(item.getId());
        assert updatedItem != null;
        // TODO temporary to get TCS up and running - uncomment when PricingTest fixed
        //        assert updatedItem.getPrice().equals(new Money(BigDecimal.valueOf(10000)));
        assert updatedItem.getQuantity() == 10;
    }

    @Test(groups = { "removeItemFromOrder" }, dependsOnGroups = { "getItemsForOrder" })
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

    @Test(groups = { "checkOrderItems" }, dependsOnGroups = { "removeItemFromOrder" })
    public void checkOrderItems() throws PricingException {
        Order order = cartService.findOrderById(orderId);
        assert order.getOrderItems().size() == 1;
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemService.readOrderItemById(bundleOrderItemId);
        assert bundleOrderItem == null;
    }

    @Test(groups = { "addPaymentToOrder" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class, dependsOnGroups = { "checkOrderItems" })
    @Rollback(false)
    public void addPaymentToOrder(PaymentInfo paymentInfo) {
        Order order = cartService.findOrderById(orderId);
        cartService.addPaymentToOrder(order, paymentInfo, null);

        order = cartService.findOrderById(orderId);
        PaymentInfo payment = order.getPaymentInfos().get(order.getPaymentInfos().indexOf(paymentInfo));
        assert payment != null;
        assert payment.getId() != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().equals(order);
    }

    @Test(groups = "addFulfillmentGroupToOrderFirst", dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "addPaymentToOrder" })
    @Rollback(false)
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

    @Test(groups = { "removeFulfillmentGroupFromOrder" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
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

    @Test(groups = { "findFulFillmentGroupForOrderFirst" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
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

    @Test(groups= {"addItemToFulfillmentGroupSecond"}, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
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
     * orderService.findFulfillmentGroupsForOrder(order).size();
     * FulfillmentGroup fulfillmentGroup = em.find(FulfillmentGroupImpl.class,
     * fulfillmentGroupId); orderService.removeFulfillmentGroupFromOrder(order,
     * fulfillmentGroup); int afterRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
    @Test(groups = { "findDefaultFulFillmentGroupForOrder" }, dependsOnGroups = { "findCurrentCartForCustomer", "addFulfillmentGroupToOrderFirst" })
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
     * orderService.findFulfillmentGroupsForOrder(order).size();
     * orderService.removeFulfillmentGroupFromOrder(order, fulfillmentGroup);
     * int afterRemove =
     * orderService.findFulfillmentGroupsForOrder(order).size(); assert
     * (beforeRemove - afterRemove) == 1; }
     */
    @Test(groups = { "removeItemFromOrderAfterDefaultFulfillmentGroup" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
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

    @Test(groups = { "getOrdersForCustomer" }, dependsOnGroups = { "readCustomer1", "findCurrentCartForCustomer" })
    public void getOrdersForCustomer() {
        String username = "customer1";
        Customer customer = customerService.readCustomerByUsername(username);
        List<Order> orders = cartService.findOrdersForCustomer(customer);
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
        Order newOrder = cartService.findOrderById(orderId);
        assert newOrder != null;
        assert newOrder.getCustomer() != null;
    }

    @Test
    public void findCartForNullCustomerId() {
        assert cartService.findCartForCustomer(new CustomerImpl()) == null;
    }
}
