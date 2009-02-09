package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SellableItemDaoJpa;
import org.broadleafcommerce.catalog.domain.SellableItem;
import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.OrderItemDaoJpa;
import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderItemDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderItemDaoTest extends BaseTest {

    private Long orderItemId;
    private BroadleafOrder order;

    @Resource
    private OrderItemDaoJpa orderItemDao;

    @Resource
    private OrderDaoJpa orderDao;

    @Resource
    private SellableItemDaoJpa sellableItemDao;

    @Resource
    private CustomerService customerService;

    @Test(groups={"createOrderItem"},dataProvider="basicOrderItem", dataProviderClass=OrderItemDataProvider.class, dependsOnGroups={"createOrder","createSellableItem"})
    @Rollback(false)
    public void createOrderItem(OrderItem orderItem){
        String userName = "customer1";
        SellableItem si = sellableItemDao.readFirstSellableItem();
        orderItem.setSellableItem(si);
        Customer customer = customerService.readCustomerByUsername(userName);
        BroadleafOrder so = (orderDao.readOrdersForCustomer(customer)).get(0);
        orderItem.setOrder(so);
        assert orderItem.getId() == null;

        orderItem = orderItemDao.maintainOrderItem(orderItem);
        assert orderItem.getId() != null;
        order = so;
        orderItemId = orderItem.getId();
    }

    @Test(groups={"readOrderItemsById"},dependsOnGroups={"createOrderItem"})
    public void readOrderItemsById(){
        OrderItem result = orderItemDao.readOrderItemById(orderItemId);
        assert result != null;
        assert result.getId() == orderItemId;
    }

    @Test(groups={"readOrderItemsForOrder"}, dependsOnGroups={"createOrderItem"})
    public void readOrderItemsForOrder(){
        List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
        assert orderItems != null;
        assert orderItems.size() > 0;
    }

}
