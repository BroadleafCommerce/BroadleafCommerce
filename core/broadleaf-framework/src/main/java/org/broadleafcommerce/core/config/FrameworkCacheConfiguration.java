/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.config;

import org.broadleafcommerce.common.extensibility.cache.JCacheRegionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrameworkCacheConfiguration {

    @Bean
    public JCacheRegionConfiguration blCategoryProduct() {
        return new JCacheRegionConfiguration("blCategoryProduct", 86400, 40000);
    }

    @Bean
    public JCacheRegionConfiguration blCategoryRelationships() {
        return new JCacheRegionConfiguration("blCategoryRelationships", 86400, 10000);
    }

    @Bean
    public JCacheRegionConfiguration blFulfillmentOptionElements() {
        return new JCacheRegionConfiguration("blFulfillmentOptionElements", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blProductAttributes() {
        return new JCacheRegionConfiguration("blProductAttributes", 86400, 10000);
    }

    @Bean
    public JCacheRegionConfiguration blProductOptions() {
        return new JCacheRegionConfiguration("blProductOptions", 86400, 5000);
    }

    @Bean
    public JCacheRegionConfiguration blProductRelationships() {
        return new JCacheRegionConfiguration("blProductRelationships", 86400, 30000);
    }

    @Bean
    public JCacheRegionConfiguration blRelatedProducts() {
        return new JCacheRegionConfiguration("blRelatedProducts", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blSearchElements() {
        return new JCacheRegionConfiguration("blSearchElements", 86400, 5000);
    }

    @Bean
    public JCacheRegionConfiguration blSkuMedia() {
        return new JCacheRegionConfiguration("blSkuMedia", 86400, 40000);
    }

    @Bean
    public JCacheRegionConfiguration blStoreElements() {
        return new JCacheRegionConfiguration("blStoreElements", 86400, 1000);
    }

}
