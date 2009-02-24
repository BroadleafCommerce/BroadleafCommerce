package org.broadleafcommerce.profile.service;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private CustomerService customerService;

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
        if (pwChangeRequired) {
            if (forcePasswordChange) {
                returnUser = new User(username, customer.getPassword(), true, true, !customer.isPasswordChangeRequired(), true, new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER") });
            } else {
                returnUser = new User(username, customer.getPassword(), true, true, true, true, new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER"), new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED") });
            }
        } else {
            returnUser = new User(username, customer.getPassword(), true, true, !customer.isPasswordChangeRequired(), true, new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER") });
        }
        return returnUser;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
