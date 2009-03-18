package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.AddressService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.AddressDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class AddressTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Resource
    private AddressService addressService;

    @Resource
    private CustomerService customerService;

    @Test(groups =  {"createAddress"}, dataProvider = "setupAddress", dataProviderClass = AddressDataProvider.class, dependsOnGroups={ "readCustomer1" })
    @Rollback(false)
    public void createAddress(Address address) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert address.getId() == null;
        address.setCustomer(customer);
        address = addressService.saveAddress(address);
        assert customer.getId() == address.getCustomer().getId();
        userId = address.getCustomer().getId();
    }

    @Test(groups =  {"readAddress"}, dependsOnGroups =  {"createAddress"})
    public void readAddressByUserId() {
        List<Address> addressList = addressService.readActiveAddressesByCustomerId(userId);
        for (Address address : addressList) {
            assert address!=null;
        }
    }
}
