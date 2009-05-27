package org.broadleafcommerce.profile.web.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.CustomerCookie;
import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.AbstractRememberMeServices;
import org.springframework.security.ui.rememberme.RememberMeAuthenticationException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class BCRememberMeServices extends AbstractRememberMeServices {

    @Resource
    private CustomerService customerService;

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        Customer customer = customerService.readCustomerByUsername((String) successfulAuthentication.getPrincipal());
        CustomerCookie.getInstance().write(response, customer.getId());
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
        if (!CustomerCookie.getInstance().isValid(request)) {
            throw new RememberMeAuthenticationException("Invalid Authentication");
        }

        Long customerId = CustomerCookie.getInstance().getCustomerIdFromCookie(request);
        Customer customer = customerService.readCustomerById(customerId);
        return this.getUserDetailsService().loadUserByUsername(customer.getUsername());
    }

}
