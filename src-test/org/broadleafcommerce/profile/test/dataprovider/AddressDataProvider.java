package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.testng.annotations.DataProvider;

public class AddressDataProvider {

    @DataProvider(name = "setupAddress")
    public static Object[][] createAddress() {
        Address address1 = new AddressImpl();
        address1.setAddressLine1("1234 Merit Drive");
        address1.setCity("Dallas");
        State state = new StateImpl();
        state.setAbbreviation("TX");
        address1.setState(state);
        address1.setPostalCode("75251");

        Address address2 = new AddressImpl();
        address2.setAddressLine1("12 Testing Drive");
        address2.setCity("San Jose");
        state = new StateImpl();
        state.setAbbreviation("CA");
        address2.setState(state);
        address2.setPostalCode("75251");

        return new Object[][] { new Object[] { address1 }, new Object[] { address2 } };
    }
}
