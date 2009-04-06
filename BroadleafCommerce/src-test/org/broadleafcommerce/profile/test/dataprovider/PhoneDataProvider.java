package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.testng.annotations.DataProvider;

public class PhoneDataProvider {

    @DataProvider(name = "setupPhone")
    public static Object[][] createPhone() {
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber("999-999-9999");

        return new Object[][] { new Object[] { phone } };
    }
}
