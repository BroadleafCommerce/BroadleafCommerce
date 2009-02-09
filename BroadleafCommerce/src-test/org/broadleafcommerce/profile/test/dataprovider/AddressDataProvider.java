package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.BroadleafCustomerAddress;
import org.testng.annotations.DataProvider;

public class AddressDataProvider {

    @DataProvider(name = "setupAddress")
    public static Object[][] createAddress() {
        Address address1 = new BroadleafCustomerAddress();
        address1.setAddressName("WORK");
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Dallas");
        address1.setStateProvRegion("TX");
        address1.setPostalCode("75251");

        Address address2 = new BroadleafCustomerAddress();
        address2.setAddressName("HOME");
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("Dallas");
        address2.setStateProvRegion("TX");
        address2.setPostalCode("75251");

        return new Object[][] { new Object[] { address1 }, new Object[] { address2 } };
    }
}
