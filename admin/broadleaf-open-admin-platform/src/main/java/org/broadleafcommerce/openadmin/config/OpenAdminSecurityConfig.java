/**
 * 
 */

package org.broadleafcommerce.openadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration common to the open admin platform. Be cautious about what you do an {@link AuthenticationManager} or other Spring Security
 * configuration here; it easily collides with any {@code <sec:http>} entries in a client-side XML. The only way to do more
 * here is to include a migration away from anything custom in a client application.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 */
@Configuration
public class OpenAdminSecurityConfig {

    @Autowired
    @Qualifier("blAdminUserDetailsService")
    UserDetailsService userDetailsService;
    
    @Autowired
    @Qualifier("blAdminPasswordEncoder")
    PasswordEncoder adminPasswordEncoder;

    @Bean
    public AuthenticationProvider blAdminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(adminPasswordEncoder);
        return provider;
    }
}
