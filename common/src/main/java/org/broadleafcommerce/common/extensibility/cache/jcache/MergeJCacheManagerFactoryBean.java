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
package org.broadleafcommerce.common.extensibility.cache.jcache;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.extensibility.cache.JCacheConfigurationBuilder;
import org.broadleafcommerce.common.extensibility.cache.JCacheRegionConfiguration;
import org.broadleafcommerce.common.extensibility.cache.ehcache.NoOpCacheManager;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

/**
 * Generic Spring Bean Factory to merge various XML files together and pass them to the JCache {@link CachingProvider} to create a {@link CacheManager}.
 * 
 * The defaults assume you are using EhCache, but this could be used for other JCache implementations.
 * 
 * @author Kelly Tisdell
 *
 */
public class MergeJCacheManagerFactoryBean implements FactoryBean<CacheManager>, BeanClassLoaderAware, InitializingBean, DisposableBean {
    
    @Nullable
    private Properties cacheManagerProperties;

    @Nullable
    private ClassLoader beanClassLoader;
    
    @Nullable
    private CacheManager cacheManager;

    @Value("${jcache.disable.cache:false}")
    protected boolean disableCache;
    
    @Autowired
    protected JCacheUriProvider uriProvider;

    @Autowired
    protected JCacheConfigurationBuilder configBuilder;

    @Autowired(required = false)
    protected List<JCacheRegionConfiguration> cacheConfiguration;

    @Value("${jcache.create.cache.ifMissing:true}")
    protected boolean createIfMissing;

    @Value("${jcache.create.cache.forceJavaConfig:false}")
    protected boolean overrideWithJavaConfig;

    @Override
    public void afterPropertiesSet() {
        if(disableCache){
            this.cacheManager = new NoOpCacheManager();
            return;
        }

        if (getObject() != null && !getObject().isClosed() && getObject().getURI().equals(uriProvider.getJCacheUri())) {
            return;
        }
        
        Caching.setDefaultClassLoader(getDefaultClassLoaderForProvider());
        CachingProvider provider = Caching.getCachingProvider();
        
        //The ClassLoader needs to be the same as what Hibernate expects (see org.hibernate.cache.jcache.internal.JCacheRegionFactory).
        this.cacheManager = provider.getCacheManager(uriProvider.getJCacheUri(), 
                provider.getDefaultClassLoader(), cacheManagerProperties);

        if (createIfMissing) {
            for (JCacheRegionConfiguration config : CollectionUtils.emptyIfNull(cacheConfiguration)) {
                createCacheIfNotExists(config);
            }
        }

    }

    @Override
    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.cacheManager != null) {
            this.cacheManager.close();
        }
    }
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    public void setCacheManagerProperties(@Nullable Properties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }
    
    protected ClassLoader getDefaultClassLoaderForProvider() {
        if (beanClassLoader != null) {
            return beanClassLoader;
        }
        return getClass().getClassLoader();
    }
    
    protected void createCacheIfNotExists(JCacheRegionConfiguration config) {
        boolean cacheMissing = cacheManager.getCache(config.getCacheName()) == null;
        if (cacheMissing || overrideWithJavaConfig) {
            if (!cacheMissing) {
                cacheManager.destroyCache(config.getCacheName());
            }
            Configuration configuration = config.getConfiguration() != null ? config.getConfiguration() : configBuilder.buildConfiguration(config);
            cacheManager.createCache(config.getCacheName(), configuration);
            if (config.getEnableManagement() != null) {
                cacheManager.enableManagement(config.getCacheName(), config.getEnableManagement());
            }
            if (config.getEnableStatistics() != null) {
                cacheManager.enableStatistics(config.getCacheName(), config.getEnableStatistics());
            }
        }
    }


}
