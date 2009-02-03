package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.profile.test.dataprovider.UserDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class UserTest extends BaseTest {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    List<Long> userIds = new ArrayList<Long>();

    List<String> userNames = new ArrayList<String>();

    @Test(groups = { "createUsers" }, dataProvider = "setupUsers", dataProviderClass = UserDataProvider.class)
    @Rollback(false)
    public void createUser(User user) {
        assert user.getId() == null;
        user = userService.saveUser(user);
        assert user.getId() != null;
        userIds.add(user.getId());
        userNames.add(user.getUsername());
    }

    @Test(groups = { "readUser" }, dependsOnGroups = { "createUsers" })
    public void readUsersById() {
        for (Long userId : userIds) {
            User user = userService.readUserById(userId);
            assert user.getId() == userId;
        }
    }

    @Test(groups = { "readUser1" }, dependsOnGroups = { "createUsers" })
    public void readUsersByUsername1() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password");
        }
    }

    @Test(groups = { "changeUserPassword" }, dependsOnGroups = { "readUser1" })
    @Rollback(false)
    public void changeUserPasswords() {
        for (String userName : userNames) {
            User user = userService.readUserByUsername(userName);
            user.setPassword(user.getPassword() + "-Changed");
            userService.saveUser(user);
        }
    }

    @Test(groups = { "readUser2" }, dependsOnGroups = { "changeUserPassword" })
    public void readUsersByUsername2() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password-Changed");
        }
    }
}
