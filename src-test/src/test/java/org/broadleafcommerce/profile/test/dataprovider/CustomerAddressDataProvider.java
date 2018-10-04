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

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.testng.annotations.DataProvider;

public class CustomerAddressDataProvider {

    @DataProvider(name = "setupCustomerAddress")
    public static Object[][] createCustomerAddress() {
        CustomerAddress ca1 = new CustomerAddressImpl();
        Address address1 = new AddressImpl();
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Bozeman");
        State state = new StateImpl();
        state.setAbbreviation("MO");
        state.setName("Michigan");
        address1.setState(state);
        address1.setPostalCode("75251");
        ca1.setAddress(address1);

        CustomerAddress ca2 = new CustomerAddressImpl();
        Address address2 = new AddressImpl();
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("Portland");
        state = new StateImpl();
        state.setAbbreviation("OR");
        state.setName("Oregon");
        address2.setState(state);
        address2.setPostalCode("75251");
        ca2.setAddress(address2);

        return new Object[][] { new Object[] { ca1 }, new Object[] { ca2 } };
    }
}
