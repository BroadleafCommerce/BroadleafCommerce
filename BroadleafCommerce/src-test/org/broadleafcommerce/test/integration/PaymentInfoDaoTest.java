package org.broadleafcommerce.test.integration;


import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.PaymentInfoDaoJpa;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.PaymentInfoDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class PaymentInfoDaoTest extends BaseTest {

    String userName = new String();
    private PaymentInfo paymentInfo;

    @Resource
    private PaymentInfoDaoJpa paymentInfoDao;

    @Resource
    private OrderDaoJpa orderDao;

    @Resource
    private AddressDaoJpa addressDao;

    @Resource
    private CustomerService customerService;

    @Test(groups={"createPaymentInfo"}, dataProvider="basicPaymentInfo", dataProviderClass=PaymentInfoDataProvider.class, dependsOnGroups={"readCustomer1","createOrder","readAddress"})
    @Rollback(false)
    public void createPaymentInfo(PaymentInfo paymentInfo){
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readActiveAddressesByCustomerId(customer.getId())).get(0);
        Order salesOrder = (orderDao.readOrdersForCustomer(customer)).get(0);

        paymentInfo.setAddress(address);
        paymentInfo.setOrder(salesOrder);

        assert paymentInfo.getId() == null;
        paymentInfoDao.maintainPaymentInfo(paymentInfo);
        assert paymentInfo.getId() != null;
        this.paymentInfo = paymentInfo;
    }

    @Test(groups={"readPaymentInfoById"}, dependsOnGroups={"createPaymentInfo"})
    public void readPaymentInfoById(){
        PaymentInfo sop = paymentInfoDao.readPaymentInfoById(paymentInfo.getId());
        assert sop !=null;
        assert sop.getId().equals(paymentInfo.getId());
    }

    @Test(groups={"readPaymentInfosByOrder"}, dependsOnGroups={"createPaymentInfo"})
    public void readPaymentInfoByOrder(){
        List<PaymentInfo> payments = paymentInfoDao.readPaymentInfosForOrder(paymentInfo.getOrder());
        assert payments != null;
        assert payments.size() > 0;
    }
}
