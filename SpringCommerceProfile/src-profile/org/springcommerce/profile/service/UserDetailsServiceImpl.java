package org.springcommerce.profile.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.dao.UserDao;
import org.springcommerce.profile.domain.UserRole;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource(name = "userDao")
    private UserDao userDao;

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        org.springcommerce.profile.domain.User user = userDao.readUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("The user was not found");
        }

        List<UserRole> roles = userDao.readRolesByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (roles != null) {
            for (UserRole role : roles) {
                authorities.add(new GrantedAuthorityImpl(role.getRoleName()));
            }
        }

        return new User(username, user.getPassword(), true, true, true, true, authorities.toArray(new GrantedAuthority[0]));
    }
}
