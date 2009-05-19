package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.broadleafcommerce.profile.web.model.PhoneNameForm;
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
