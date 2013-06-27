/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.dataprovider;

import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.testng.annotations.DataProvider;

public class CustomerPhoneDataProvider {

    @DataProvider(name = "setupCustomerPhone")
    public static Object[][] createCustomerPhone() {
        CustomerPhone cp1 = new CustomerPhoneImpl();
        Phone phone1 = new PhoneImpl();
        phone1.setPhoneNumber("111-111-1111");
        cp1.setPhone(phone1);
        cp1.setPhoneName("phone1");

        CustomerPhone cp2 = new CustomerPhoneImpl();
        Phone phone2 = new PhoneImpl();
        phone1.setPhoneNumber("222-222-2222");
        cp2.setPhone(phone2);
        cp2.setPhoneName("phone2");

        return new Object[][] { new Object[] { cp1 }, new Object[] { cp2 } };
    }
}
