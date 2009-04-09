package org.broadleafcommerce.test.integration;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderItemDataProvider;
import org.broadleafcommerce.type.OrderStatus;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderItemDaoTest extends BaseTest {

    private Long orderItemId;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private SkuDao skuDao;

    @Resource
    private CustomerService customerService;

    @Test(groups = { "createOrderItem" }, dataProvider = "basicOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "createOrder", "createSku" })
    @Rollback(false)
    public void createOrderItem(OrderItem orderItem) {
        String userName = "customer1";
        Sku si = skuDao.readFirstSku();
        assert si.getId() != null;
        orderItem.setSku(si);
        Customer customer = customerService.readCustomerByUsername(userName);
        Order so = orderDao.readCartForCustomer(customer, false);
        assert so.getStatus() == OrderStatus.IN_PROCESS;
        assert so.getId() != null;
        assert orderItem.getId() == null;

        orderItem = orderItemDao.maintainOrderItem(orderItem);
        assert orderItem.getId() != null;
        orderItemId = orderItem.getId();
    }

    @Test(groups = { "readOrderItemsById" }, dependsOnGroups = { "createOrderItem" })
    public void readOrderItemsById() {
        assert orderItemId != null;
        OrderItem result = orderItemDao.readOrderItemById(orderItemId);
        assert result != null;
        assert result.getId() == orderItemId;
    }
}
