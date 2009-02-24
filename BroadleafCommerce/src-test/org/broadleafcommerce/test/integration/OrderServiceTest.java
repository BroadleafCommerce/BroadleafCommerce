package org.broadleafcommerce.test.integration;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDaoJpa;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.FulfillmentGroupDaoJpa;
import org.broadleafcommerce.order.dao.PaymentInfoDaoJpa;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.service.OrderServiceImpl;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.ContactInfoServiceImpl;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.FulfillmentGroupDataProvider;
import org.broadleafcommerce.test.dataprovider.PaymentInfoDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderServiceTest extends BaseTest {

    private Order order = null;
    private int numOrderItems = 0;
    private FulfillmentGroup fulfillmentGroup;

    private List<OrderItem> orderItems = null;

    @Resource
    private AddressDaoJpa addressDao;

    @Resource
    private OrderServiceImpl soService;

    @Resource
    private CustomerService customerService;

    @Resource
    private ContactInfoServiceImpl contactService;

    @Resource
    private SkuDaoJpa skuDao;

    @Resource
    PaymentInfoDaoJpa paymentInfoDao;

    @Resource
    FulfillmentGroupDaoJpa fulfillmentGroupDao;

    @Test(groups = { "findCurrentBasketForCustomerBeforeCreation" }, dependsOnGroups = { "readCustomer1", "createContactInfo" })
    @Rollback(false)
    public void findCurrentBasketForCustomerBeforeCreation() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = soService.findCurrentBasketForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        this.order = order;
    }

    @Test(groups = { "findCurrentBasketForCustomerAfterCreation" }, dependsOnGroups = { "findCurrentBasketForCustomerBeforeCreation" })
    @Rollback(false)
    public void findCurrentBasketForCustomerAfterCreation() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);

        Order order = soService.findCurrentBasketForCustomer(customer);
        assert order != null;
        assert order.getId() != null;
        assert order.getId().equals(this.order.getId());
        this.order = order;
    }

    @Test(groups = { "addContactInfoToOrder" }, dependsOnGroups = { "findCurrentBasketForCustomerBeforeCreation" })
    public void addContactInfoToOrder() {
        ContactInfo contactInfo = (contactService.readContactInfoByUserId(order.getCustomer().getId())).get(0);
        assert contactInfo.getId() != null;
        Order order = soService.addContactInfoToOrder(this.order, contactInfo);
        assert order != null;
        assert order.getContactInfo() != null;
        assert order.getContactInfo().getId().equals(contactInfo.getId());
    }

    @Test(groups = { "addItemToOrder" }, dependsOnGroups = { "findCurrentBasketForCustomerAfterCreation", "createSku" })
    @Rollback(false)
    public void addItemToOrder() {
        numOrderItems++;
        Sku sku = skuDao.readFirstSku();
        assert sku.getId() != null;
        OrderItem item = soService.addItemToOrder(order, sku, 1);
        assert item != null;
        assert item.getQuantity() == numOrderItems;
        assert item.getOrder() != null;
        assert item.getOrder().getId().equals(order.getId());
        assert item.getSku() != null;
        assert item.getSku().getId().equals(sku.getId());
    }

    @Test(groups = { "getItemsForOrder" }, dependsOnGroups = { "addItemToOrder" })
    @Rollback(false)
    public void getItemsForOrder() {
        List<OrderItem> orderItems = soService.findItemsForOrder(order);
        assert orderItems != null;
        assert orderItems.size() == numOrderItems;
        this.orderItems = orderItems;
    }

    @Test(groups = { "updateItemsInOrder" }, dependsOnGroups = { "getItemsForOrder" })
    public void updateItemsInOrder() {
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        item.setFinalPrice(BigDecimal.valueOf(10000));
        item.setQuantity(10);
        OrderItem updatedItem = soService.updateItemInOrder(order, item);
        assert updatedItem != null;
        assert updatedItem.getQuantity() == 10;
        // assert updatedItem.getFinalPrice() == (updatedItem.getSku().getPrice() * updatedItem.getQuantity());
    }

    @Test(groups = { "removeItemFromOrder" }, dependsOnGroups = { "getItemsForOrder" })
    public void removeItemFromOrder() {
        assert orderItems.size() > 0;
        int startingSize = orderItems.size();
        OrderItem item = orderItems.get(0);
        assert item != null;
        soService.removeItemFromOrder(order, item);
        List<OrderItem> items = soService.findItemsForOrder(order);
        assert items != null;
        assert items.size() == startingSize - 1;
    }

    @Test(groups = { "addPaymentToOrder" }, dataProvider = "basicPaymentInfo", dataProviderClass = PaymentInfoDataProvider.class, dependsOnGroups = { "readCustomer1", "findCurrentBasketForCustomerAfterCreation", "createPaymentInfo" })
    @Rollback(false)
    public void addPaymentToOrder(PaymentInfo paymentInfo) {
        paymentInfo = paymentInfoDao.maintainPaymentInfo(paymentInfo);
        assert paymentInfo.getId() != null;
        PaymentInfo payment = soService.addPaymentToOrder(order, paymentInfo);
        assert payment != null;
        assert payment.getId() != null;
        assert payment.getOrder() != null;
        assert payment.getOrder().getId().equals(order.getId());
    }

    @Test(groups = { "addFulfillmentGroupToOrderFirst" }, dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "createAddress", "findCurrentBasketForCustomerAfterCreation", "addItemToOrder" })
    @Rollback(false)
    public void addFulfillmentGroupToOrderFirst(FulfillmentGroup fulfillmentGroup) {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readAddressByUserId(customer.getId())).get(0);

        fulfillmentGroup.setOrderId(order.getId());
        fulfillmentGroup.setAddress(address);

        FulfillmentGroup fg = soService.addFulfillmentGroupToOrder(order, fulfillmentGroup);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getAddress().equals(fulfillmentGroup.getAddress());
        assert fg.getCost() == fulfillmentGroup.getCost();
        assert fg.getOrderId().equals(order.getId());
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
        this.fulfillmentGroup = fg;
    }

    @Test(groups = { "findFulFillmentGroupForOrderFirst" }, dependsOnGroups = { "findCurrentBasketForCustomerAfterCreation", "addFulfillmentGroupToOrderFirst" })
    public void findFillmentGroupForOrderFirst() {
        FulfillmentGroup fg = soService.findFulfillmentGroupsForOrder(order).get(0);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getAddress().getId().equals(fulfillmentGroup.getAddress().getId());
        assert fg.getCost().equals(fulfillmentGroup.getCost());
        assert fg.getOrderId().equals(order.getId());
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
    }

    @Test(groups = { "removeFulFillmentGroupForOrderFirst" }, dependsOnGroups = { "findCurrentBasketForCustomerAfterCreation", "addFulfillmentGroupToOrderFirst" })
    @Rollback(false)
    public void removeFulFillmentGroupForOrderFirst() {
        int beforeRemove = soService.findFulfillmentGroupsForOrder(order).size();
        soService.removeFulfillmentGroupFromOrder(order, fulfillmentGroup);
        int afterRemove = soService.findFulfillmentGroupsForOrder(order).size();
        assert (beforeRemove - afterRemove) == 1;
    }

    @Test(groups = { "findDefaultFulFillmentGroupForOrder" }, dependsOnGroups = { "findCurrentBasketForCustomerAfterCreation", "addFulfillmentGroupToOrderFirst" })
    public void findDefaultFillmentGroupForOrder() {
        FulfillmentGroupImpl fg = soService.findDefaultFulfillmentGroupForOrder(order);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getAddress().getId().equals(fulfillmentGroup.getAddress().getId());
        assert fg.getCost().equals(fulfillmentGroup.getCost());
        assert fg.getOrderId().equals(order.getId());
        assert fg.getMethod().equals(fulfillmentGroup.getMethod());
        assert fg.getReferenceNumber().equals(fulfillmentGroup.getReferenceNumber());
    }

    @Test(groups = { "removeDefaultFulFillmentGroupForOrder" }, dependsOnGroups = { "findCurrentBasketForCustomerAfterCreation", "addFulfillmentGroupToOrderFirst" })
    public void removeDefaultFulFillmentGroupForOrder() {
        int beforeRemove = soService.findFulfillmentGroupsForOrder(order).size();
        soService.removeFulfillmentGroupFromOrder(order, fulfillmentGroup);
        int afterRemove = fulfillmentGroupDao.readFulfillmentGroupsForOrder(order).size();
        assert (beforeRemove - afterRemove) == 1;
    }

    @Test(groups = { "removeItemFromOrderAfterDefaultFulfillmentGroup" }, dependsOnGroups = { "addFulfillmentGroupToOrderFirst" })
    public void removeItemFromOrderAfterFulfillmentGroups() {
        assert orderItems.size() > 0;
        OrderItem item = orderItems.get(0);
        assert item != null;
        soService.removeItemFromOrder(order, item);
        FulfillmentGroupImpl fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        for (FulfillmentGroupItem fulfillmentGroupItem : fg.getFulfillmentGroupItems()) {
            assert fulfillmentGroupItem.getOrderItem().getId() != item.getId();
        }
    }

    @Test(groups = { "getOrdersForCustomer" }, dependsOnGroups = { "readCustomer1", "findCurrentBasketForCustomerAfterCreation" })
    public void getOrdersForCustomer() {
        String username = "customer1";
        Customer customer = customerService.readCustomerByUsername(username);
        List<Order> orders = soService.findOrdersForCustomer(customer);
        assert orders != null;
        assert orders.size() > 0;
    }
}
