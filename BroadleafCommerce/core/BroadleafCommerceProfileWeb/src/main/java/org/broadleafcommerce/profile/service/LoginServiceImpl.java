package org.broadleafcommerce.profile.service;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service("blLoginService")
public class LoginServiceImpl implements LoginService {

    @Resource(name="blAuthenticationManager")
    private AuthenticationManager authenticationManager;
    
    @Resource(name="blUserDetailsService")
    private UserDetailsService userDetailsService;

    
    public void loginCustomer(Customer customer) {
        UserDetails principal = userDetailsService.loadUserByUsername(customer.getUsername());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, customer.getUnencodedPassword(), principal.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

}
