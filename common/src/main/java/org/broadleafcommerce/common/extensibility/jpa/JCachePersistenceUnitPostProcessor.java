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
package org.broadleafcommerce.common.extensibility.jpa;

import org.broadleafcommerce.common.extensibility.cache.jcache.JCacheUriProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Persistence unit post processor for dynamically modifying the persistence unit.
 * <p>
 * jcache.disable.cache - default: false, disables hibernate L2 and query cache if true
 * hibernate.javax.cache.provider - default: null, overrides the cache provider defined in the persistence.xml
 * hibernate.cache.region.factory_class - default: null, overrides the cache region factory defined in the persistence.xml
 * <p>
 * uriProvider - A configurable provider class that returns the URI to be used for caching
 * overrideCacheProperties - A map used to set any additional properties on the persistence unit, presumeably related to caching
 *
 * @author Jay Aisenbrey (cja769)
 */
public class JCachePersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

    @Value("${jcache.disable.cache:false}")
    protected Boolean disableCache;

    @Value("${hibernate.javax.cache.provider:#{null}}")
    protected String cacheProvider;

    @Value("${hibernate.cache.region.factory_class:#{null}}")
    protected String cacheRegionFactory;

    @Autowired
    protected JCacheUriProvider uriProvider;

    @Autowired(required = false)
    @Qualifier("blJCachePUPostProcessorOverrideProperties")
    protected Map<String, String> overrideCacheProperties = new HashMap<>();

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        Properties properties = pui.getProperties();
        if (disableCache) {
            properties.setProperty("hibernate.cache.use_second_level_cache", "false");
            properties.setProperty("hibernate.cache.use_query_cache", "false");
        }

        URI cacheUri = uriProvider.getJCacheUri();
        properties.setProperty("hibernate.javax.cache.uri", cacheUri != null ? uriProvider.getJCacheUri().toString() : "");

        if (cacheRegionFactory != null) {
            properties.setProperty("hibernate.cache.region.factory_class", cacheRegionFactory);
        }

        if (cacheProvider != null) {
            properties.setProperty("hibernate.javax.cache.provider", cacheProvider);
        }

        properties.putAll(overrideCacheProperties);
    }

}
