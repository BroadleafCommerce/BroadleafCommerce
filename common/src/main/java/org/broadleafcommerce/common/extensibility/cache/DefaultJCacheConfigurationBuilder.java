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
package org.broadleafcommerce.common.extensibility.cache;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;

@Service("blJCacheConfigurationBuilder")
@ConditionalOnEhCacheMissing
public class DefaultJCacheConfigurationBuilder implements JCacheConfigurationBuilder {

    @Override
    public Configuration buildConfiguration(JCacheRegionConfiguration regionInformation) {
        return buildConfiguration(regionInformation.getTtlSeconds(), regionInformation.getMaxElementsInMemory(), regionInformation.getKey(), regionInformation.getValue());
    }

    @Override
    public <K, V> Configuration<K, V> buildConfiguration(int ttlSeconds, int maxElementsInMemory, Class<K> keyClass, Class<V> valueClass) {
        final Factory<ExpiryPolicy> expiryPolicy;
        if (ttlSeconds < 0) {
            //Eternal
            expiryPolicy = EternalExpiryPolicy.factoryOf();
        } else {
            //Number of seconds since created in cache
            expiryPolicy = CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, ttlSeconds));
        }

        final MutableConfiguration<K, V> config = new MutableConfiguration<>();
        config.setTypes(keyClass, valueClass);
        config.setExpiryPolicyFactory(expiryPolicy);
        return config;
    }

}
