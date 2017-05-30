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
/**
 * 
 */
package org.broadleafcommerce.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Main configuration class for the broadleaf-common module
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
public class BroadleafCommonConfig {

    /**
     * Other enterprise/mulititenant modules override this adapter to provide one that supports dynamic filtration
     */
    @Bean
    @ConditionalOnMissingBean(name = "blJpaVendorAdapter")
    public JpaVendorAdapter blJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        //TODO see https://jira.spring.io/browse/SPR-13269. Since we're still on Hibernate 4.1, we want to revert to the previous
        // Spring behavior, which was not to prepare the connection. This avoids some warnings and extra connection acquisitions
        // for read only transactions. When we advance Hibernate, we should look at not blocking Spring's connection preparation.
        vendorAdapter.setPrepareConnection(false);
        return vendorAdapter;
    }

}
