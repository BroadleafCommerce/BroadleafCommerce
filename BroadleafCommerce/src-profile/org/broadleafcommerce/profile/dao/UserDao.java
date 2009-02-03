package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;

public interface UserDao {

    public User readUserByUsername(String username);

    public List<UserRole> readUserRolesByUserId(Long userId);

    public User maintainUser(User user);

    public User readUserById(Long userId);

    public User readUserByEmail(String emailAddress);
}
