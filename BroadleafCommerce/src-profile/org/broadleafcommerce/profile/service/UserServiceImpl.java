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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.dao.UserDao;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("blUserService")
public class UserServiceImpl implements UserService {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private UserDao userDao;

    @Resource
    private PasswordEncoder passwordEncoder;

    // @Resource(name = "saltSource")
    // private SaltSource saltSource;

    @Transactional(propagation = Propagation.REQUIRED)
    public User saveUser(User user) {
        if (user.getUnencodedPassword() != null) {
            user.setPassword(passwordEncoder.encodePassword(user.getUnencodedPassword(), null));
        }
        return userDao.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<UserRole> readUserRolesByUserId(Long userId) {
        return userDao.readUserRolesByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User readUserByEmail(String emailAddress) {
        return userDao.readUserByEmail(emailAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

    @Transactional(propagation = Propagation.REQUIRED)
    public User readUserByUsername(String username) {
        return userDao.readUserByUsername(username);
    }

    @Override
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
