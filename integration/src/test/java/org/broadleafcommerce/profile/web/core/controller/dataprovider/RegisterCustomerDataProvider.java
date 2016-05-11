/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.web.core.controller.dataprovider;

import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestionImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.testng.annotations.DataProvider;

public class RegisterCustomerDataProvider {

    @DataProvider(name = "setupCustomerControllerData")
    public static Object[][] createCustomer() {
        Customer customer = new CustomerImpl();
        customer.setEmailAddress("testCase@test.com");
        customer.setFirstName("TestFirstName");
        customer.setLastName("TestLastName");
        customer.setUsername("TestCase");
        ChallengeQuestion question = new ChallengeQuestionImpl();
        question.setId(1L);
        customer.setChallengeQuestion(question);
        customer.setChallengeAnswer("Challenge CandidateItemOfferAnswer");
        RegisterCustomerForm registerCustomer = new RegisterCustomerForm();
        registerCustomer.setCustomer(customer);
        registerCustomer.setPassword("TestPassword");
        registerCustomer.setPasswordConfirm("TestPassword");
        return new Object[][] { new Object[] { registerCustomer } };
    }
}
