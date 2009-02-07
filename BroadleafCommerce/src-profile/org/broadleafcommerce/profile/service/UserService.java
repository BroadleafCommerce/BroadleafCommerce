package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.PasswordChange;

public interface UserService {

    public User saveUser(User user);

    public User readUserByUsername(String username);

    public List<UserRole> readUserRolesByUserId(Long userId);

    public User readUserByEmail(String emailAddress);

    public User changePassword(PasswordChange passwordChange);

    public User readUserById(Long userId);
}
