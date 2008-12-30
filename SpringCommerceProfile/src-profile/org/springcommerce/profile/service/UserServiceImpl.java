package org.springcommerce.profile.service;

import javax.annotation.Resource;

import org.springcommerce.profile.dao.UserDao;
import org.springcommerce.profile.domain.User;

public class UserServiceImpl implements UserService {

    @Resource(name = "userDao")
    private UserDao userDao;

    public User saveUser(User user) {
        return userDao.maintainUser(user);
    }

    @Override
    public User readUserById(Long userId) {
        return userDao.readUserById(userId);
    }
}
