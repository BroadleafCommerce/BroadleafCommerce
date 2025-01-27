/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.web.controller.config;

import org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin;
import org.broadleafcommerce.common.config.PostAutoConfigurationImport;
import org.broadleafcommerce.common.web.BroadleafCookieLocaleResolver;
import org.broadleafcommerce.openadmin.web.compatibility.JSFieldNameCompatibilityInterceptor;
import org.broadleafcommerce.openadmin.web.controller.AdminBasicErrorController;
import org.broadleafcommerce.openadmin.web.controller.AdminRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Locale;

/**
 * Configure WebMvc for the admin application. This plays nicely with and without Spring AutoConfiguration support
 *
 * @author Philip Baggett (pbaggett)
 */
@Configuration
@PostAutoConfigurationImport(AdminWebMvcConfigurationSupport.class)
@ConditionalOnAdmin
public class AdminWebMvcConfiguration {

    @Configuration
    public static class AdminDefaultWebMvcConfigurerAdapter implements WebMvcConfigurer {

        /**
         * Set the default media type to JSON because AJAX calls from the admin UI are expecting JSON.
         */
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
        }

        /**
         * Since in the admin we have multiple handler mappings, we need to register a {@link MappedInterceptor}
         * to apply to all of them. Modifying just tine {@link InterceptorRegistry} will apply those
         * interceptors to _only_ the default RequestMappingHandlerMapping.
         */
        @Bean
        public MappedInterceptor blJsFieldNameCompatibilityInterceptor() {
            return new MappedInterceptor(null, new JSFieldNameCompatibilityInterceptor());
        }

        @Bean
        public MappedInterceptor blLocaleChangeInterceptor() {
            LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
            localeInterceptor.setParamName("blLocaleCode");
            return new MappedInterceptor(null, localeInterceptor);
        }

        @Bean
        public LocaleResolver localeResolver() {
            CookieLocaleResolver resolver = new BroadleafCookieLocaleResolver();
            resolver.setCookieHttpOnly(true);
            resolver.setDefaultLocale(Locale.ENGLISH);
            return resolver;
        }

        /**
         * At time of writing, this bean only gets hooked up within {@link WebMvcAutoConfiguration}. Providing it here
         * regardless since it is harmless in general, and it is likely that perhaps Spring Web in its {@link DelegatingWebMvcConfiguration}
         * eventually goes to this pattern instead of requiring the workaround from {@link AdminWebMvcConfigurationSupport}
         * SPRING-UPGRADE-CHECK
         */
        @Bean
        public WebMvcRegistrations blAdminMvcRegistrations() {
            return new WebMvcRegistrations() {
                @Override
                public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                    return new AdminRequestMappingHandlerMapping();
                }
            };
        }

        @Bean
        public AdminBasicErrorController basicErrorController() {
            return new AdminBasicErrorController(new DefaultErrorAttributes(), new ErrorProperties());
        }
    }

}
