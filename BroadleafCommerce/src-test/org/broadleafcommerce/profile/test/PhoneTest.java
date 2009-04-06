package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.service.PhoneService;
import org.broadleafcommerce.profile.test.dataprovider.PhoneDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class PhoneTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private PhoneService phoneService;

    List<Long> phoneIds = new ArrayList<Long>();
    String userName = new String();

    private Long phoneId;

    @Test(groups = { "createPhone" }, dataProvider = "setupPhone", dataProviderClass = PhoneDataProvider.class, dependsOnGroups = { "readCustomer1" })
    @Rollback(false)
    public void createPhone(Phone phone) {
        userName = "customer1";
        assert phone.getId() == null;
        phone = phoneService.savePhone(phone);
        assert phone.getId() != null;
        phoneId = phone.getId();
    }

    @Test(groups = { "readPhoneById" }, dependsOnGroups = { "createPhone" })
    public void readPhoneById() {
        Phone phone = phoneService.readPhoneById(phoneId);
        assert phone != null;
        assert phone.getId() == phoneId;
    }
}
