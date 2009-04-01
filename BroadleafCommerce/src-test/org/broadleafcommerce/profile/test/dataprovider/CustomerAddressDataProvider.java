package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.domain.StateProvince;
import org.broadleafcommerce.profile.domain.StateProvinceImpl;
import org.testng.annotations.DataProvider;

public class CustomerAddressDataProvider {

    @DataProvider(name = "setupCustomerAddress")
    public static Object[][] createCustomerAddress() {
        CustomerAddress ca1 = new CustomerAddressImpl();
        Address address1 = new AddressImpl();
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Bozeman");
        StateProvince stateProvince = new StateProvinceImpl();
        stateProvince.setShortName("MO");
        address1.setStateProvRegion(stateProvince);
        address1.setPostalCode("75251");
        ca1.setAddress(address1);

        CustomerAddress ca2 = new CustomerAddressImpl();
        Address address2 = new AddressImpl();
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("Portland");
        stateProvince = new StateProvinceImpl();
        stateProvince.setShortName("OR");
        address2.setStateProvRegion(stateProvince);
        address2.setPostalCode("75251");
        ca2.setAddress(address2);

        return new Object[][] { new Object[] { ca1 }, new Object[] { ca2 } };
    }
}
