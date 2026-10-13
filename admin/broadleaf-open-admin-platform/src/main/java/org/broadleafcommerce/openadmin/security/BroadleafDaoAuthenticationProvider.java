package org.broadleafcommerce.openadmin.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class BroadleafDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Value("${validate.admin.user.password:false}")
    protected boolean validateAdminUserPassword;

    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (validateAdminUserPassword) {
            Object credentials = authentication.getCredentials();
            if (credentials == null || credentials.toString().trim().isEmpty()) {
                this.logger.warn("Failed to authenticate since password cannot be blank");
                throw new BadCredentialsException("Password cannot be blank");
            }
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }

}
