package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;

public interface UserDao {

    public User readUserByUsername(String username);

    public List<UserRole> readUserRolesByUserId(Long userId);

    public User save(User user);

    public User readUserByEmail(String emailAddress);

    public User readUserById(Long id);
}
