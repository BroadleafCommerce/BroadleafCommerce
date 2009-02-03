package org.broadleafcommerce.profile.test.dataprovider;

import java.util.HashSet;
import java.util.Set;

import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider(name = "setupUsers")
    public static Object[][] createUsers() {
        User user1 = new User();
        user1.setPassword("user1Password");
        Set<UserRole> userRoles = new HashSet<UserRole>();
        UserRole userRole = new UserRole();
        userRole.setRoleName("ROLE_USER");
        userRoles.add(userRole);
        user1.setUserRoles(userRoles);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setPassword("user2Password");
        userRoles = new HashSet<UserRole>();
        userRole = new UserRole();
        userRole.setRoleName("ROLE_ADMIN");
        userRoles.add(userRole);
        user2.setUserRoles(userRoles);
        user2.setUsername("user2");

        return new Object[][] { new Object[] { user1 }, new Object[] { user2 } };
    }
}
