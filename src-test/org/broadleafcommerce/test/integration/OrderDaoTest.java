package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderDaoTest extends BaseTest {

    String userName = new String();
    Long orderId;

    @Resource
    private OrderDao orderDao;

    @Resource
    private CustomerService customerService;

    @Test(groups = { "createOrder" }, dataProvider = "basicOrder", dataProviderClass = OrderDataProvider.class, dependsOnGroups = { "readCustomer1", "createPhone" })
    @Rollback(false)
    public void createOrder(Order order) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert order.getId() == null;
        order.setCustomer(customer);
        order = orderDao.maintianOrder(order);
        assert order.getId() != null;
        orderId = order.getId();
    }

    @Test(groups = { "readOrder" }, dependsOnGroups = { "createOrder" })
    public void readOrderById() {
        Order result = orderDao.readOrderById(orderId);
        assert result != null;
    }

    @Test(groups = { "readOrdersForCustomer" }, dependsOnGroups = { "readCustomer1", "createOrder" })
    public void readOrdersForCustomer() {
        userName = "customer1";
        Customer user = customerService.readCustomerByUsername(userName);
        List<Order> orders = orderDao.readOrdersForCustomer(user.getId());
        assert orders.size() > 0;
    }

    @Test(groups = {"deleteOrderForCustomer"}, dependsOnGroups = {"createOrder"})
    public void deleteOrderForCustomer(){
        Order order = orderDao.readOrderById(orderId);
        assert order != null;
        assert order.getId() != null;
        orderDao.deleteOrderForCustomer(order);
    }
}
