/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.config;

import org.broadleafcommerce.admin.web.rulebuilder.service.extension.CookieFieldServiceExtensionHandler;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceExtensionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Jeff Fischer
 */
@Configuration
public class AdminWebConfig {

    @Bean
    @ConditionalOnProperty("cookie.content.targeting.enabled")
    public CookieFieldServiceExtensionHandler blCookieFieldServiceExtensionHandler(@Qualifier("blCookieRuleConfigs") List configs,
                                                                                   @Qualifier("blRuleBuilderFieldServiceExtensionManager") RuleBuilderFieldServiceExtensionManager extensionManager) {
        CookieFieldServiceExtensionHandler handler = new CookieFieldServiceExtensionHandler(extensionManager, configs);
        return handler;
    }

}
