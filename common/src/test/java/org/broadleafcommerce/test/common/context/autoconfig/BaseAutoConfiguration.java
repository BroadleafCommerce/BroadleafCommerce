/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.test.common.context.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
public class BaseAutoConfiguration {
    
    @Bean
    public String overridingBean() {
        return BaseAutoConfiguration.class.getName();
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "nonOverridingBean")
    public String nonOverridingBean() {
        return BaseAutoConfiguration.class.getName();
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "nestedRunsFirst")
    public String nestedRunsFirst() {
        return BaseAutoConfiguration.class.getName();
    }
}
