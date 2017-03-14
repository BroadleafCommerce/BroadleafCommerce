package org.broadleafcommerce.common.context.override.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Nick Crum ncrum
 */
@Configuration
@ImportResource("classpath:context/config/xml-import-override.xml")
public class XMLImportOverrideConfiguration {

    @Configuration
    public static class InnerConfiguration {

        @Bean
        public PasswordEncoder blPasswordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }
}
