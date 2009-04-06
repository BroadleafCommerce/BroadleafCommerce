package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.testng.annotations.DataProvider;

public class CustomerPhoneDataProvider {

    @DataProvider(name = "setupCustomerPhone")
    public static Object[][] createCustomerPhone() {
        CustomerPhone cp1 = new CustomerPhoneImpl();
        Phone phone1 = new PhoneImpl();
        phone1.setPhoneNumber("111-111-1111");
        cp1.setPhone(phone1);

        CustomerPhone cp2 = new CustomerPhoneImpl();
        Phone phone2 = new PhoneImpl();
        phone1.setPhoneNumber("222-222-2222");
        cp2.setPhone(phone2);

        return new Object[][] { new Object[] { cp1 }, new Object[] { cp2 } };
    }
}
