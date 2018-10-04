/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.UserDao;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("blUserService")
public class UserServiceImpl implements UserService {

    @Resource(name="blUserDao")
    protected UserDao userDao;

    @Resource(name="passwordEncoder")
    protected PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        if (user.getUnencodedPassword() != null) {
            user.setPassword(passwordEncoder.encodePassword(user.getUnencodedPassword(), null));
        }
        return userDao.save(user);
    }

    public List<UserRole> readUserRolesByUserId(Long userId) {
        return userDao.readUserRolesByUserId(userId);
    }

    public User readUserByEmail(String emailAddress) {
        return userDao.readUserByEmail(emailAddress);
    }

    public User changePassword(PasswordChange passwordChange) {
        User user = readUserByUsername(passwordChange.getUsername());
        user.setUnencodedPassword(passwordChange.getNewPassword());
        user.setPasswordChangeRequired(false);
        user = saveUser(user);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(passwordChange.getUsername(), passwordChange.getNewPassword(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        auth.setAuthenticated(false);
        return user;
    }

    public User readUserByUsername(String username) {
        return userDao.readUserByUsername(username);
    }

    public User readUserById(Long id) {
        return userDao.readUserById(id);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
