package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Address;
import org.testng.annotations.DataProvider;

public class AddressDataProvider {

    @DataProvider(name = "setupAddress")
    public static Object[][] createAddress() {
        Address address1 = new Address();
        address1.setAddressName("WORK");
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Dallas");
        address1.setStateCode("TX");
        address1.setZipCode("75251");

        Address address2 = new Address();
        address2.setAddressName("HOME");
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("Dallas");
        address2.setStateCode("TX");
        address2.setZipCode("75251");

        return new Object[][] { new Object[] { address1 }, new Object[] { address2 } };
    }
}
