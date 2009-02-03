package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.AddressService;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.profile.test.dataprovider.AddressDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class AddressTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;
    
    @Resource(name = "addressService")
    private AddressService addressService;
    @Resource(name="userService")
    private UserService userService;

    @Test(groups =  {"createAddress"}, dataProvider = "setupAddress", dataProviderClass = AddressDataProvider.class, dependsOnGroups={ "readUser1" })
    @Rollback(false)
    public void createAddress(Address address) {
    		userName = "user1";
    		User user = userService.readUserByUsername(userName);
    		assert address.getId() == null;
    		address.setUser(user);
    		address = addressService.saveAddress(address);
    		assert user.getId() == address.getUser().getId();
    		userId = address.getUser().getId();
    }

    @Test(groups =  {"readAddress"}, dependsOnGroups =  {"createAddress"})
    public void readAddressByUserId() {
        List<Address> addressList = addressService.readAddressByUserId(userId);
        	for (Address address : addressList) {
        		assert address!=null;
        	}
        }
}
