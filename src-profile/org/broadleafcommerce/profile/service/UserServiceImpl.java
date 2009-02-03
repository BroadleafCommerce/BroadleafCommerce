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

@Service("userService")
public class UserServiceImpl implements UserService {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    // @Resource(name = "saltSource")
    // private SaltSource saltSource;

    @Transactional(propagation = Propagation.REQUIRED)
    public User saveUser(User user) {
        if (user.getUnencodedPassword() != null) {
            user.setPassword(passwordEncoder.encodePassword(user.getUnencodedPassword(), null));
        }
        return userDao.maintainUser(user);
    }

    @Override
    public User readUserById(Long userId) {
        return userDao.readUserById(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User readUserByUsername(String username) {
        return userDao.readUserByUsername(username);
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
}
