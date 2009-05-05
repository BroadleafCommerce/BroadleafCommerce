package org.broadleafcommerce.test.integration;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.FulfillmentGroupDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class FulfillmentGroupDaoTest extends BaseTest {

    private Long orderId;
    private Long defaultFulfillmentGroupId;
    private Long fulfillmentGroupId;

    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerAddressDao customerAddressDao;

    @Resource
    private OrderDao orderDao;

    @Test(groups = "createDefaultFulfillmentGroup", dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "createOrder", "createCustomerAddress" })
    @Rollback(false)
    public void createDefaultFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId())).get(0).getAddress();
        Order salesOrder = (orderDao.readOrdersForCustomer(customer.getId())).get(0);

        FulfillmentGroup newFG = fulfillmentGroupDao.createDefault();
        newFG.setAddress(address);
        newFG.setRetailPrice(fulfillmentGroup.getRetailPrice());
        newFG.setMethod(fulfillmentGroup.getMethod());
        newFG.setOrder(salesOrder);
        newFG.setReferenceNumber(fulfillmentGroup.getReferenceNumber());

        assert newFG.getId() == null;
        fulfillmentGroup = fulfillmentGroupDao.save(newFG);
        assert fulfillmentGroup.getId() != null;
        orderId = salesOrder.getId();
        defaultFulfillmentGroupId = fulfillmentGroup.getId();
    }

    @Test(groups = { "readDefaultFulfillmentGroupForOrder" }, dependsOnGroups = { "createDefaultFulfillmentGroup" })
    public void readDefaultFulfillmentGroupForOrder() {
        Order order = orderDao.readOrderById(orderId);
        assert order != null;
        assert order.getId() == orderId;
        FulfillmentGroup fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        assert fg.getId() != null;
        assert fg.getId().equals(defaultFulfillmentGroupId);
    }

    @Test(groups = { "readDefaultFulfillmentGroupForId" }, dependsOnGroups = { "createDefaultFulfillmentGroup" })
    public void readDefaultFulfillmentGroupForId() {
        FulfillmentGroup fg = fulfillmentGroupDao.readFulfillmentGroupById(defaultFulfillmentGroupId);
        assert fg != null;
        assert fg.getId() != null;
        assert fg.getId().equals(defaultFulfillmentGroupId);
    }

    @Test(groups = "createFulfillmentGroup", dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "createOrder", "createCustomerAddress" })
    @Rollback(false)
    public void createFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId())).get(0).getAddress();
        Order salesOrder = (orderDao.readOrdersForCustomer(customer.getId())).get(0);

        FulfillmentGroup newFG = fulfillmentGroupDao.create();
        newFG.setAddress(address);
        newFG.setRetailPrice(fulfillmentGroup.getRetailPrice());
        newFG.setMethod(fulfillmentGroup.getMethod());
        // newFG.setOrderId(salesOrder.getId());
        newFG.setReferenceNumber(fulfillmentGroup.getReferenceNumber());

        assert newFG.getId() == null;
        fulfillmentGroup = fulfillmentGroupDao.save(newFG);
        assert fulfillmentGroup.getId() != null;
        orderId = salesOrder.getId();
        fulfillmentGroupId = fulfillmentGroup.getId();
    }

    @Test(groups = { "readFulfillmentGroupsForId" }, dependsOnGroups = { "createFulfillmentGroup" })
    public void readFulfillmentGroupsForId() {
        FulfillmentGroup fg = fulfillmentGroupDao.readFulfillmentGroupById(fulfillmentGroupId);
        assert fg != null;
        assert fg.getId() != null;
    }
}
