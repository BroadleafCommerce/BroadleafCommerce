/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.config;

import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.core.web.seo.BasicSeoPropertyGeneratorImpl;
import org.broadleafcommerce.core.web.seo.SeoPropertyGenerator;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.web.filter.RequestContextFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Fischer
 */
@Configuration
public class FrameworkWebConfig {

    /**
     * Place a RequestContextFilter very high in the pre-security filter chain in order to guarantee
     * the RequestContext is set for any subsequent need. This should only impact Spring Boot implementations.
     *
     * @return the RequestFilter bean
     */
    @Bean
    public RequestContextFilter blRequestContextFilter() {
        OrderedRequestContextFilter filter = new OrderedRequestContextFilter();
        filter.setOrder(FilterOrdered.PRE_SECURITY_HIGH - 1000);
        return filter;
    }
    
    @Bean
    public DeviceResolver blDeviceResolver() {
        return new LiteDeviceResolver();
    }

    @Bean
    @ConditionalOnTemplating
    public List<SeoPropertyGenerator> blSeoPropertyGenerators(@Qualifier("blBasicSeoPropertyGenerator") BasicSeoPropertyGeneratorImpl basicSeo) {
        List<SeoPropertyGenerator> generators = new ArrayList<>();
        generators.add(basicSeo);
        return generators;
    }

}
