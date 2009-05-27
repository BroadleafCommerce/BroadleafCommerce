package org.broadleafcommerce.profile.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.CustomerRole;
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

    @Resource
    private CustomerService customerService;

    @Resource
    private RoleService roleService;

    private boolean forcePasswordChange = false;

    public void setForcePasswordChange(boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        org.broadleafcommerce.profile.domain.Customer customer = customerService.readCustomerByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("The customer was not found");
        }

        User returnUser = null;

        boolean pwChangeRequired = customer.isPasswordChangeRequired();
        List<GrantedAuthority> grantedAuthorities = createGrantedAuthorities(roleService.findCustomerRolesByCustomerId(customer.getId()));
        if (pwChangeRequired) {
            if (forcePasswordChange) {
                returnUser = new User(username, customer.getPassword(), true, true, !customer.isPasswordChangeRequired(), true, grantedAuthorities.toArray(new GrantedAuthority[0]));
            } else {
                grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED"));
                returnUser = new User(username, customer.getPassword(), true, true, true, true, grantedAuthorities.toArray(new GrantedAuthority[0]));
            }
        } else {
            returnUser = new User(username, customer.getPassword(), true, true, !customer.isPasswordChangeRequired(), true, grantedAuthorities.toArray(new GrantedAuthority[0]));
        }
        return returnUser;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    private List<GrantedAuthority> createGrantedAuthorities(List<CustomerRole> customerRoles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        for (CustomerRole role : customerRoles) {
            grantedAuthorities.add(new GrantedAuthorityImpl(role.getRoleName()));
        }
        return grantedAuthorities;
    }
}
