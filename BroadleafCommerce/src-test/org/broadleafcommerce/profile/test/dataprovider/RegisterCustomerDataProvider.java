package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.web.form.CustomerRegistrationForm;
import org.testng.annotations.DataProvider;

public class RegisterCustomerDataProvider {

    @DataProvider(name = "setupCustomerControllerData")
    public static Object[][] createCustomer() {
        CustomerRegistrationForm registerCustomer = new CustomerRegistrationForm();
        registerCustomer.setEmailAddress("testCase@test.com");
        registerCustomer.setFirstName("TestFirstName");
        registerCustomer.setLastName("TestLastName");
        registerCustomer.setPassword("TestPassword");
        registerCustomer.setPasswordConfirm("TestPassword");
        registerCustomer.setUsername("TestCase");
        registerCustomer.setChallengeQuestion("Test Challenge Question");
        registerCustomer.setChallengeAnswer("Challenge Answer");
        return new Object[][] { new Object[] { registerCustomer } };
    }
}
