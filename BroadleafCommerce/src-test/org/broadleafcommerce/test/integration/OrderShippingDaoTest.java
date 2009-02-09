package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.OrderShippingDao;
import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderShippingDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderShippingDaoTest extends BaseTest {

    private BroadleafOrder order;

    @Resource
    private OrderShippingDao orderShippingDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private AddressDaoJpa addressDao;

    @Resource
    private OrderDaoJpa orderDao;

    @Test(groups={"createOrderShipping"}, dataProvider="basicOrderShipping", dataProviderClass=OrderShippingDataProvider.class,dependsOnGroups={"createOrder","createAddress"})
    @Rollback(false)
    public void createOrderShipping(OrderShipping orderShipping){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readAddressByUserId(customer.getId())).get(0);
        BroadleafOrder salesOrder= (orderDao.readOrdersForCustomer(customer)).get(0);

        orderShipping.setAddress(address);
        orderShipping.setOrder(salesOrder);

        assert orderShipping.getId() == null;
        orderShippingDao.maintainOrderShipping(orderShipping);
        assert orderShipping.getId() != null;
        order = salesOrder;
    }

    @Test (groups = {"readOrderShippingForOrder"}, dependsOnGroups={"createOrderShipping"})
    public void readOrderShippingForOrder(){
        List<OrderShipping> shippings = orderShippingDao.readOrderShippingForOrder(order);
        assert shippings != null;
        assert shippings.size() > 0;
    }

}
