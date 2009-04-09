package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class FulfillmentGroupItemDaoTest extends BaseTest {

    private FulfillmentGroup fulfillmentGroup;
    private Long fulfillmentGroupItemId;

    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Resource
    private FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Test(groups={"createFulfillmentGroupItem"},dependsOnGroups={"createOrder","createOrderItem","createDefaultFulfillmentGroup"})
    @Rollback(false)
    public void createFulfillmentGroupItem(){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order salesOrder= (orderDao.readOrdersForCustomer(customer.getId())).get(0);
        //OrderItem orderItem = orderItemDao.readOrderItemsForOrder(salesOrder).get(0);
        OrderItem orderItem = salesOrder.getOrderItems().get(0);
        //fulfillmentGroup = fulfillmentGroupDao.readFulfillmentGroupsForOrder(salesOrder).get(0);
        fulfillmentGroup = salesOrder.getFulfillmentGroups().get(0);
        Long fulfillmentGroupId = fulfillmentGroup.getId();

        assert fulfillmentGroup != null;

        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroupId(fulfillmentGroupId);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());

        assert fgi.getId() == null;
        fgi = fulfillmentGroupItemDao.maintainFulfillmentGroupItem(fgi);
        assert fgi.getId() != null;
        fulfillmentGroupItemId = fgi.getId();

    }

    @Test (groups = {"readFulfillmentGroupItemsForFulfillmentGroup"}, dependsOnGroups={"createFulfillmentGroupItem"})
    public void readFulfillmentGroupItemsForFulfillmentGroup(){
        List<FulfillmentGroupItem> fgis = fulfillmentGroupItemDao.readFulfillmentGroupItemsForFulfillmentGroup(fulfillmentGroup);
        assert fgis != null;
        assert fgis.size() > 0;
    }

    @Test (groups = {"readFulfillmentGroupItemsById"}, dependsOnGroups={"createFulfillmentGroupItem"})
    public void readFulfillmentGroupItemsById(){
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.readFulfillmentGroupItemById(fulfillmentGroupItemId);
        assert fgi != null;
        assert fgi.getId() != null;
    }
}
