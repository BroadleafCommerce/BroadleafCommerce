package org.springcommerce.profile.dao;

import java.util.List;

import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.domain.UserRole;

public interface UserDao {

    public User readUserByUsername(String username);

    public List<UserRole> readRolesByUserId(Long userId);
}
