package org.broadleafcommerce.common.context.override.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Nick Crum ncrum
 */
@Configuration
public class InnerStaticImportChildConfiguration {

    @Bean
    public PasswordEncoder blPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
