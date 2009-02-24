package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.testng.annotations.DataProvider;

public class CustomerDataProvider {

    @DataProvider(name = "setupCustomers")
    public static Object[][] createCustomers() {
        Customer customer1 = new CustomerImpl();
        customer1.setPassword("customer1Password");
        customer1.setUsername("customer1");

        Customer customer2 = new CustomerImpl();
        customer2.setPassword("customer2Password");
        customer2.setUsername("customer2");

        return new Object[][] { new Object[] { customer1 }, new Object[] { customer2 } };
    }
}
