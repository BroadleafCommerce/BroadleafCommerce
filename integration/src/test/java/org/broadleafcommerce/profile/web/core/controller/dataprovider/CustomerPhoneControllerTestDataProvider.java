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

package org.broadleafcommerce.profile.web.core.controller.dataprovider;

import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.web.core.model.PhoneNameForm;
import org.testng.annotations.DataProvider;

public class CustomerPhoneControllerTestDataProvider {

    @DataProvider(name = "setupCustomerPhoneControllerData")
    public static Object[][] createCustomerPhone() {
        PhoneNameForm pnf1 = new PhoneNameForm();
        Phone phone1 = new PhoneImpl();
        phone1.setPhoneNumber("111-222-3333");
        pnf1.setPhone(phone1);
        pnf1.setPhoneName("phone_1");

        PhoneNameForm pnf2 = new PhoneNameForm();
        Phone phone2 = new PhoneImpl();
        phone2.setPhoneNumber("222-333-4444");
        pnf2.setPhone(phone2);
        pnf2.setPhoneName("phone_2");

        return new Object[][] { new Object[] { pnf1 }, new Object[] { pnf2 } };
    }
}
