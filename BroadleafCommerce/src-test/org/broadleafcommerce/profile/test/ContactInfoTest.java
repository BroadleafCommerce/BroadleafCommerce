package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.ContactInfoService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.ContactInfoDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class ContactInfoTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    List<Long> contactInfoIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    private Long contactId;

    @Resource(name = "contactInfoService")
    private ContactInfoService contactInfoService;
    @Resource(name="customerService")
    private CustomerService customerService;

    @Test(groups =  {"createContactInfo"}, dataProvider = "setupContactInfo", dataProviderClass = ContactInfoDataProvider.class, dependsOnGroups={ "readCustomer1" })
    @Rollback(false)
    public void createContactInfo(ContactInfo contactInfo) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert contactInfo.getId() == null;
        contactInfo.setCustomer(customer);
        contactInfo = contactInfoService.saveContactInfo(contactInfo);
        assert customer.getId() == contactInfo.getCustomer().getId();
        userId = contactInfo.getCustomer().getId();
        contactId = contactInfo.getId();
    }

    @Test(groups =  {"readContactInfo"}, dependsOnGroups =  {"createContactInfo"})
    public void readContactInfoByUserId() {
        List<ContactInfo> contactInfoList = contactInfoService.readContactInfoByUserId(userId);
        for (ContactInfo contactInfo : contactInfoList) {
            assert contactInfo!=null;
        }
    }

    @Test(groups = {"readContactInfoById"}, dependsOnGroups={"createContactInfo"})
    public void readContactInfoById() {
        ContactInfo ci = contactInfoService.readContactInfoById(contactId);
        assert ci != null;
        assert ci.getId() == contactId;
    }
}
