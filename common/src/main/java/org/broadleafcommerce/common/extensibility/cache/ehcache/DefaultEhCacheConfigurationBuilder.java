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
package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheConfigurationBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.stereotype.Service;

import javax.cache.configuration.Configuration;

@Service("blJCacheConfigurationBuilder")
@ConditionalOnEhCache
public class DefaultEhCacheConfigurationBuilder extends DefaultJCacheConfigurationBuilder {

    @Override
    public <K, V> Configuration<K, V> buildConfiguration(
            int ttlSeconds,
            int maxElementsInMemory,
            Class<K> keyClass,
            Class<V> valueClass
    ) {
        ExpiryPolicy<Object, Object> expiryPolicy = new DefaultExpiryPolicy(ttlSeconds);

        CacheConfiguration<K, V> config = CacheConfigurationBuilder.
                newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(maxElementsInMemory))
                .withExpiry(expiryPolicy)
                .build();

        return Eh107Configuration.fromEhcacheCacheConfiguration(config);
    }

}
