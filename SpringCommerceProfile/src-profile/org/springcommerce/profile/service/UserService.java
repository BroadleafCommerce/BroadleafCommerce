package org.springcommerce.profile.service;

import org.springcommerce.profile.domain.User;

public interface UserService {

    public User saveUser(User user);

    public User readUserById(Long userId);
}
