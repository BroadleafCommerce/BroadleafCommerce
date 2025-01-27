/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.config;

import org.broadleafcommerce.common.extensibility.cache.JCacheRegionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining cache regions via Java config when either not using ehcache or when jcache.create.cache.forceJavaConfig is true
 *
 * @author Jay Aisenbrey (cja769)
 */
@Configuration
public class CommonCacheConfiguration {

    @Bean
    public JCacheRegionConfiguration defaultUpdateTimestampsRegion() {
        return new JCacheRegionConfiguration(
                "default-update-timestamps-region",
                -1,
                5000
        );
    }

    @Bean
    public JCacheRegionConfiguration defaultQueryResultsRegion() {
        return new JCacheRegionConfiguration(
                "default-query-results-region",
                600,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blStandardElements() {
        return new JCacheRegionConfiguration("blStandardElements", 86400, 5000);
    }

    @Bean
    public JCacheRegionConfiguration blProducts() {
        return new JCacheRegionConfiguration("blProducts", 86400, 100000);
    }

    @Bean
    public JCacheRegionConfiguration blProductUrlCache() {
        return new JCacheRegionConfiguration("blProductUrlCache", 3600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blCategories() {
        return new JCacheRegionConfiguration("blCategories", 86400, 3000);
    }

    @Bean
    public JCacheRegionConfiguration blCategoryUrlCache() {
        return new JCacheRegionConfiguration("blCategoryUrlCache", 3600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blOffers() {
        return new JCacheRegionConfiguration("blOffers", 86400, 100000);
    }

    @Bean
    public JCacheRegionConfiguration blInventoryElements() {
        return new JCacheRegionConfiguration("blInventoryElements", 60, 100000);
    }

    @Bean
    public JCacheRegionConfiguration queryCatalog() {
        return new JCacheRegionConfiguration("query.Catalog", 600, 10000);
    }

    @Bean
    public JCacheRegionConfiguration queryPriceList() {
        return new JCacheRegionConfiguration("query.PriceList", 600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration queryCms() {
        return new JCacheRegionConfiguration("query.Cms", 600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration queryOffer() {
        return new JCacheRegionConfiguration("query.Offer", 600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blOrderElements() {
        return new JCacheRegionConfiguration("blOrderElements", 600, 100000);
    }

    @Bean
    public JCacheRegionConfiguration blCustomerElements() {
        return new JCacheRegionConfiguration("blCustomerElements", 600, 100000);
    }

    @Bean
    public JCacheRegionConfiguration blCustomerAddress() {
        return new JCacheRegionConfiguration("blCustomerAddress", 600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration queryOrder() {
        return new JCacheRegionConfiguration("query.Order", 60, 1000);
    }

    @Bean
    public JCacheRegionConfiguration querySearch() {
        return new JCacheRegionConfiguration("query.Search", 600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration generatedResourceCache() {
        return new JCacheRegionConfiguration("generatedResourceCache", 600, 100);
    }

    @Bean
    public JCacheRegionConfiguration blTemplateElements() {
        return new JCacheRegionConfiguration("blTemplateElements", 3600, 5000);
    }

    @Bean
    public JCacheRegionConfiguration blTranslationElements() {
        return new JCacheRegionConfiguration(
                "blTranslationElements",
                3600,
                10000000
        );
    }

    @Bean
    public JCacheRegionConfiguration blBatchTranslationCache() {
        return new JCacheRegionConfiguration(
                "blBatchTranslationCache",
                -1,
                10000
        );
    }

    @Bean
    public JCacheRegionConfiguration blConfigurationModuleElements() {
        return new JCacheRegionConfiguration(
                "blConfigurationModuleElements",
                600,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration queryConfigurationModuleElements() {
        return new JCacheRegionConfiguration(
                "query.ConfigurationModuleElements",
                600,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blSystemPropertyElements() {
        return new JCacheRegionConfiguration(
                "blSystemPropertyElements",
                600,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blSystemPropertyNullCheckCache() {
        return new JCacheRegionConfiguration(
                "blSystemPropertyNullCheckCache",
                600,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blBundleElements() {
        return new JCacheRegionConfiguration("blBundleElements", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blResourceCacheElements() {
        return new JCacheRegionConfiguration(
                "blResourceCacheElements",
                86400,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blResourceTransformerCacheElements() {
        return new JCacheRegionConfiguration(
                "blResourceTransformerCacheElements",
                86400,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blSandBoxElements() {
        return new JCacheRegionConfiguration("blSandBoxElements", 3, 2000);
    }

    @Bean
    public JCacheRegionConfiguration queryblSandBoxElements() {
        return new JCacheRegionConfiguration("query.blSandBoxElements", 3, 500);
    }

    @Bean
    public JCacheRegionConfiguration blSecurityElements() {
        return new JCacheRegionConfiguration("blSecurityElements", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blSiteElements() {
        return new JCacheRegionConfiguration("blSiteElements", 3600, 5000);
    }

    @Bean
    public JCacheRegionConfiguration blSiteElementsQuery() {
        return new JCacheRegionConfiguration("blSiteElementsQuery", 3600, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blProductOverrideCache() {
        return new JCacheRegionConfiguration("blProductOverrideCache", -1, 100);
    }

    @Bean
    public JCacheRegionConfiguration blCountryElements() {
        return new JCacheRegionConfiguration("blCountryElements", -1, 2000);
    }

    @Bean
    public JCacheRegionConfiguration blDataDrivenEnumeration() {
        return new JCacheRegionConfiguration(
                "blDataDrivenEnumeration",
                86400,
                1000
        );
    }

    @Bean
    public JCacheRegionConfiguration blMediaElements() {
        return new JCacheRegionConfiguration("blMediaElements", 86400, 20000);
    }

    @Bean
    public JCacheRegionConfiguration blSystemProperties() {
        return new JCacheRegionConfiguration("blSystemProperties", 86400, 2000);
    }

}
