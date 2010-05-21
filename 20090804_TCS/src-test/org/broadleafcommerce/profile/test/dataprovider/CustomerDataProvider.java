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
import org.broadleafcommerce.time.SystemTime;
import org.testng.annotations.DataProvider;

public class CustomerDataProvider {

    @DataProvider(name = "setupCustomers")
    public static Object[][] createCustomers() {
        Customer customer1 = new CustomerImpl();
        Auditable auditable = new Auditable();
        auditable.setDateCreated(SystemTime.asDate());
        customer1.setAuditable(auditable);
        customer1.setPassword("customer1Password");
        customer1.setUsername("customer1");

        Customer customer2 = new CustomerImpl();
        Auditable auditable2 = new Auditable();
        auditable2.setDateCreated(SystemTime.asDate());
        customer2.setAuditable(auditable2);
        customer2.setPassword("customer2Password");
        customer2.setUsername("customer2");

        return new Object[][] { new Object[] { customer1 }, new Object[] { customer2 } };
    }
}
