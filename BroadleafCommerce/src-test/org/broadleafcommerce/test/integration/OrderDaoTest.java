package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.dao.ContactInfoDaoJpa;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderDaoTest extends BaseTest {

    String userName = new String();
    Long orderId;

    @Resource
    private OrderDaoJpa orderDao;

    @Resource(name = "customerService")
    private CustomerService customerService;

    @Resource
    private ContactInfoDaoJpa contactInfoDao;

    @Test(groups = { "createOrder" }, dataProvider = "basicOrder", dataProviderClass = OrderDataProvider.class, dependsOnGroups = { "readCustomer1", "createContactInfo" })
    @Rollback(false)
    public void createOrder(Order order) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        ContactInfo ci = (contactInfoDao.readContactInfoByUserId(customer.getId())).get(0);
        assert order.getId() == null;
        order.setCustomer(customer);
        order.setContactInfo(ci);
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
        List<Order> orders = orderDao.readOrdersForCustomer(user);
        assert orders.size() > 0;
    }
    
    @Test(groups = {"deleteOrderForCustomer"}, dependsOnGroups = {"createOrder"})
    @Rollback(false)
    public void deleteOrder(){
    	Order order = orderDao.readOrderById(orderId);
    	assert order != null;
    	assert order.getId() != null;
    	orderDao.deleteOrderForCustomer(order);
    	Order shouldntexist = orderDao.readOrderById(orderId);
    	assert shouldntexist == null;
    }
}
