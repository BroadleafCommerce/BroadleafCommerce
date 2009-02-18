package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FullfillmentGroupDaoJpa;
import org.broadleafcommerce.order.dao.FullfillmentGroupItemDaoJpa;
import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.OrderItemDaoJpa;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class FullfillmentGroupItemDaoTest extends BaseTest {

    private FullfillmentGroup fulfillmentGroup;
    private Long fulfillmentGroupItemId;

    @Resource
    private FullfillmentGroupDaoJpa fullfillmentGroupDao;
    
    @Resource
    private FullfillmentGroupItemDaoJpa fullfillmentGroupItemDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderDaoJpa orderDao;
    
    @Resource
    private OrderItemDaoJpa orderItemDao;

    @Test(groups={"createFullfillmentGroupItem"},dependsOnGroups={"createOrder","createOrderItem","createDefaultFullfillmentGroup"})
    @Rollback(false)
    public void createFullfillmentGroupItem(){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order salesOrder= (orderDao.readOrdersForCustomer(customer)).get(0);        
        OrderItem orderItem = orderItemDao.readOrderItemsForOrder(salesOrder).get(0);
        fulfillmentGroup = fullfillmentGroupDao.readFullfillmentGroupsForOrder(salesOrder).get(0);
        Long fullfillmentGroupId = fulfillmentGroup.getId();

        assert fulfillmentGroup != null;
        
        FullfillmentGroupItem fgi = fullfillmentGroupItemDao.create();
        fgi.setFullfillmentGroupId(fullfillmentGroupId);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());
        
        assert fgi.getId() == null;
        fgi = fullfillmentGroupItemDao.maintainFullfillmentGroupItem(fgi);
        assert fgi.getId() != null;
        fulfillmentGroupItemId = fgi.getId();
        
    }

    @Test (groups = {"readFullfillmentGroupItemsForFullfillmentGroup"}, dependsOnGroups={"createFullfillmentGroupItem"})
    public void readFullfillmentGroupItemsForFullfillmentGroup(){
        List<FullfillmentGroupItem> fgis = fullfillmentGroupItemDao.readFullfillmentGroupItemsForFullfillmentGroup(fulfillmentGroup);
        assert fgis != null;
        assert fgis.size() > 0;
    }

    @Test (groups = {"readFullfillmentGroupItemsById"}, dependsOnGroups={"createFullfillmentGroupItem"})
    public void readFullfillmentGroupItemsById(){
        FullfillmentGroupItem fgi = fullfillmentGroupItemDao.readFullfillmentGroupItemById(fulfillmentGroupItemId);
        assert fgi != null;
        assert fgi.getId() != null;
    }
}
