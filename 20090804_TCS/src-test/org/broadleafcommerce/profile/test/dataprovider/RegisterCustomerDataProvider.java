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
package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.profile.web.form.RegisterCustomerForm;
import org.broadleafcommerce.time.SystemTime;
import org.testng.annotations.DataProvider;

public class RegisterCustomerDataProvider {

    @DataProvider(name = "setupCustomerControllerData")
    public static Object[][] createCustomer() {
        Customer customer = new CustomerImpl();
        Auditable auditable = new Auditable();
        auditable.setDateCreated(SystemTime.asDate());
        customer.setAuditable(auditable);
        customer.setEmailAddress("testCase@test.com");
        customer.setFirstName("TestFirstName");
        customer.setLastName("TestLastName");
        customer.setUsername("TestCase");
        customer.setChallengeQuestionId(1L);
        customer.setChallengeAnswer("Challenge Answer");
        RegisterCustomerForm registerCustomer = new RegisterCustomerForm();
        registerCustomer.setCustomer(customer);
        registerCustomer.setPassword("TestPassword");
        registerCustomer.setPasswordConfirm("TestPassword");
        return new Object[][] { new Object[] { registerCustomer } };
    }
}
