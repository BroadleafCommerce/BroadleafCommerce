package org.broadleafcommerce.profile.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.UserRole;
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

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "userService")
    private UserService userService;

    private boolean forcePasswordChange = false;

    public void setForcePasswordChange(boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        org.broadleafcommerce.profile.domain.User user = userService.readUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("The user was not found");
        }

        List<UserRole> roles = userService.readUserRolesByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (roles != null) {
            for (UserRole role : roles) {
                authorities.add(new GrantedAuthorityImpl(role.getRoleName()));
            }
        }

        if (user.isPasswordChangeRequired()) {
            authorities.add(new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED"));
        }

        User returnUser = null;

        if (!forcePasswordChange) {
            returnUser = new User(username, user.getPassword(), true, true, true, true, authorities.toArray(new GrantedAuthority[0]));
        } else {
            returnUser = new User(username, user.getPassword(), true, true, !user.isPasswordChangeRequired(), true, authorities.toArray(new GrantedAuthority[0]));
        }
        return returnUser;
    }
}
