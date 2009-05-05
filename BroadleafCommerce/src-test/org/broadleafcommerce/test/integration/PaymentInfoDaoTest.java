package org.broadleafcommerce.test.integration;


import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.PaymentInfoDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.dao.CustomerAddressDao;
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
    private PaymentInfoDao paymentInfoDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private CustomerAddressDao customerAddressDao;

    @Resource
    private CustomerService customerService;

    @Test(groups={"createPaymentInfo"}, dataProvider="basicPaymentInfo", dataProviderClass=PaymentInfoDataProvider.class, dependsOnGroups={"readCustomer1","createOrder"})
    @Rollback(false)
    public void createPaymentInfo(PaymentInfo paymentInfo){
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId())).get(0).getAddress();
        Order salesOrder = orderDao.readCartForCustomer(customer);

        paymentInfo.setAddress(address);
        paymentInfo.setOrder(salesOrder);

        assert paymentInfo.getId() == null;
        paymentInfo = paymentInfoDao.save(paymentInfo);
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
