package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.CustomerAddressDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CustomerAddressTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    List<Long> customerAddressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Resource
    private CustomerAddressService customerAddressService;

    @Resource
    private CustomerService customerService;

    @Test(groups = "createCustomerAddress", dataProvider = "setupCustomerAddress", dataProviderClass = CustomerAddressDataProvider.class, dependsOnGroups = "readCustomer1")
    @Rollback(false)
    public void createCustomerAddress(CustomerAddress customerAddress) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert customerAddress.getId() == null;
        customerAddress.setCustomerId(customer.getId());
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        assert customer.getId() == customerAddress.getCustomerId();
        userId = customerAddress.getCustomerId();
    }

    @Test(groups = "readCustomerAddress", dependsOnGroups = "createCustomerAddress")
    public void readCustomerAddressByUserId() {
        List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(userId);
        for (CustomerAddress customerAddress : customerAddressList) {
            assert customerAddress != null;
        }
    }
}
