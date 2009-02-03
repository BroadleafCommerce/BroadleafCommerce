package org.broadleafcommerce.profile.test.dataprovider;

import org.broadleafcommerce.profile.domain.ContactInfo;
import org.testng.annotations.DataProvider;

public class ContactInfoDataProvider {

    @DataProvider(name = "setupContactInfo")
    public static Object[][] createAddress() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("test@credera.com");
        contactInfo.setFax("214-890-9999");
        contactInfo.setPrimaryPhone("999-999-9999");
        contactInfo.setSecondaryPhone("333-333-3333");
 

        
        return new Object[][] { new Object[] { contactInfo }};
    }
}
