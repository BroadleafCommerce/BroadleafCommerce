package org.broadleafcommerce.test.integration;


import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.OrderPaymentDaoJpa;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderPaymentDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderPaymentDaoTest extends BaseTest {

    String userName = new String();
    private OrderPayment orderPayment;

    @Resource
    private OrderPaymentDaoJpa orderPaymentDao;

    @Resource
    private OrderDaoJpa orderDao;

    @Resource
    private AddressDaoJpa addressDao;

    @Resource
    private CustomerService customerService;

    @Test(groups={"createOrderPayment"}, dataProvider="basicOrderPayment", dataProviderClass=OrderPaymentDataProvider.class, dependsOnGroups={"readCustomer1","createOrder","readAddress"})
    @Rollback(false)
    public void createOrderPayment(OrderPayment orderPayment){
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readAddressByUserId(customer.getId())).get(0);
        Order salesOrder = (orderDao.readOrdersForCustomer(customer)).get(0);

        orderPayment.setAddress(address);
        orderPayment.setOrder(salesOrder);

        assert orderPayment.getId() == null;
        orderPaymentDao.maintainOrderPayment(orderPayment);
        assert orderPayment.getId() != null;
        this.orderPayment = orderPayment;
    }

    @Test(groups={"readOrderPaymentById"}, dependsOnGroups={"createOrderPayment"})
    public void readOrderPaymentById(){
        OrderPayment sop = orderPaymentDao.readOrderPaymentById(orderPayment.getId());
        assert sop !=null;
        assert sop.getId().equals(orderPayment.getId());
    }

    @Test(groups={"readOrderPaymentsByOrder"}, dependsOnGroups={"createOrderPayment"})
    public void readSaleOrderPaymentByOrder(){
        List<OrderPayment> payments = orderPaymentDao.readOrderPaymentsForOrder(orderPayment.getOrder());
        assert payments != null;
        assert payments.size() > 0;
    }
}
