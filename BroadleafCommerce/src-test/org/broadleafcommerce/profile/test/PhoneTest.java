package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.service.CustomerService;
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

    @Resource
    private CustomerService customerService;

    List<Long> phoneIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    private Long phoneId;

    @Test(groups = { "createPhone" }, dataProvider = "setupPhone", dataProviderClass = PhoneDataProvider.class, dependsOnGroups = { "readCustomer1" })
    @Rollback(false)
    public void createPhone(Phone phone) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert phone.getId() == null;
        phone.setCustomer(customer);
        phone = phoneService.savePhone(phone);
        assert phone.getId() != null;
        userId = phone.getCustomer().getId();
        phoneId = phone.getId();
    }

    @Test(groups = { "readPhone" }, dependsOnGroups = { "createPhone" })
    public void readPhoneByUserId() {
        List<Phone> phoneList = phoneService.readPhoneByUserId(userId);
        for (Phone phone : phoneList) {
            assert phone != null;
        }
    }

    @Test(groups = { "readPhoneById" }, dependsOnGroups = { "createPhone" })
    public void readPhoneById() {
        Phone phone = phoneService.readPhoneById(phoneId);
        assert phone != null;
        assert phone.getId() == phoneId;
    }
}
