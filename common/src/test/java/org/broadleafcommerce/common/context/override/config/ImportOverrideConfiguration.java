package org.broadleafcommerce.common.context.override.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Nick Crum ncrum
 */
@Configuration
@Import(ImportOverrideChildConfiguration.class)
public class ImportOverrideConfiguration {

    @Bean
    public PasswordEncoder blPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
