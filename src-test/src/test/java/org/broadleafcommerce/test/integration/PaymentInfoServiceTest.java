/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.service.PaymentInfoService;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.PaymentInfoDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class PaymentInfoServiceTest extends BaseTest {

    String userName = new String();
    private PaymentInfo paymentInfo;

    @Resource
    private PaymentInfoService paymentInfoService;

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
        List<CustomerAddress> addresses = customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId());
        Address address = null;
        if (!addresses.isEmpty())
            address = addresses.get(0).getAddress();
        Order salesOrder = orderDao.readCartForCustomer(customer);

        paymentInfo.setAddress(address);
        paymentInfo.setOrder(salesOrder);
        paymentInfo.setType(PaymentInfoType.CREDIT_CARD);

        assert paymentInfo.getId() == null;
        paymentInfo = paymentInfoService.save(paymentInfo);
        assert paymentInfo.getId() != null;
        this.paymentInfo = paymentInfo;
    }

    @Test(groups={"readPaymentInfoById"}, dependsOnGroups={"createPaymentInfo"})
    public void readPaymentInfoById(){
        PaymentInfo sop = paymentInfoService.readPaymentInfoById(paymentInfo.getId());
        assert sop !=null;
        assert sop.getId().equals(paymentInfo.getId());
    }

    @Test(groups={"readPaymentInfosByOrder"}, dependsOnGroups={"createPaymentInfo"})
    public void readPaymentInfoByOrder(){
        List<PaymentInfo> payments = paymentInfoService.readPaymentInfosForOrder(paymentInfo.getOrder());
        assert payments != null;
        assert payments.size() > 0;
    }

    @Test(groups={"testCreatePaymentInfo"}, dependsOnGroups={"createPaymentInfo"})
    public void createTestPaymentInfo(){
        userName = "customer1";
        PaymentInfo paymentInfo = paymentInfoService.create();
        Customer customer = customerService.readCustomerByUsername(userName);
        List<CustomerAddress> addresses = customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId());
        Address address = null;
        if (!addresses.isEmpty())
            address = addresses.get(0).getAddress();
        Order salesOrder = orderDao.readCartForCustomer(customer);

        paymentInfo.setAddress(address);
        paymentInfo.setOrder(salesOrder);
        paymentInfo.setType(PaymentInfoType.CREDIT_CARD);

        assert paymentInfo != null;
        paymentInfo = paymentInfoService.save(paymentInfo);
        assert paymentInfo.getId() != null;
        Long paymentInfoId = paymentInfo.getId();
        paymentInfoService.delete(paymentInfo);
        paymentInfo = paymentInfoService.readPaymentInfoById(paymentInfoId);
        assert paymentInfo == null;
    }

}
