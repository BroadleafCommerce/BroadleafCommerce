package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.ContactInfoService;
import org.broadleafcommerce.profile.service.UserService;
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
    @Resource(name="userService")
    private UserService userService;

    @Test(groups =  {"createContactInfo"}, dataProvider = "setupContactInfo", dataProviderClass = ContactInfoDataProvider.class, dependsOnGroups={ "readUser1" })
    @Rollback(false)
    public void createContactInfo(ContactInfo contactInfo) {
    		userName = "user1";
    		User user = userService.readUserByUsername(userName);
    		assert contactInfo.getId() == null;
    		contactInfo.setUser(user);
    		contactInfo = contactInfoService.saveContactInfo(contactInfo);
    		assert user.getId() == contactInfo.getUser().getId();
    		userId = contactInfo.getUser().getId();
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
