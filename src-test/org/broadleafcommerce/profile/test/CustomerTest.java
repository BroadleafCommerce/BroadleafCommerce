package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.CustomerDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CustomerTest extends BaseTest {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "customerService")
    private CustomerService customerService;

    @Resource(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    List<Long> userIds = new ArrayList<Long>();

    List<String> userNames = new ArrayList<String>();

    @Test(groups = { "createCustomers" }, dataProvider = "setupCustomers", dataProviderClass = CustomerDataProvider.class)
    @Rollback(false)
    public void createCustomer(Customer customer) {
        assert customer.getId() == null;
        customer = customerService.saveCustomer(customer);
        assert customer.getId() != null;
        userIds.add(customer.getId());
        userNames.add(customer.getUsername());
    }

    @Test(groups = { "readCustomer" }, dependsOnGroups = { "createCustomers" })
    public void readCustomersById() {
        for (Long userId : userIds) {
            Customer customer = customerService.readCustomerById(userId);
            assert customer.getId() == userId;
        }
    }

    @Test(groups = { "readCustomer1" }, dependsOnGroups = { "createCustomers" })
    public void readCustomersByUsername1() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password");
        }
    }

    @Test(groups = { "changeCustomerPassword" }, dependsOnGroups = { "readCustomer1" })
    @Rollback(false)
    public void changeCustomerPasswords() {
        for (String userName : userNames) {
            Customer customer = customerService.readCustomerByUsername(userName);
            customer.setPassword(customer.getPassword() + "-Changed");
            customerService.saveCustomer(customer);
        }
    }

    @Test(groups = { "readCustomer2" }, dependsOnGroups = { "changeCustomerPassword" })
    public void readCustomersByUsername2() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password-Changed");
        }
    }
}
