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

import javax.cache.configuration.Configuration;

/**
 * DTO object to represent the configuration of a cache region
 * 
 * If {@link #configuration} is set then it will be used to create the region otherwise the other properties will be used.
 * If this class is subclassed then {@link JCacheConfigurationBuilder} will likely also need to be overridden in order to utilize any new properties.
 * 
 * By default instances of this class are defined in Broadleaf to set the default configuration for known cache regions. If you would like to override
 * cache regions using this DTO when targeting ehcache then set the property jcache.create.cache.forceJavaConfig to true. When targeting any other jcache
 * implementation simply create an instance of this DTO for each new region or region override as a bean.
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class JCacheRegionConfiguration {

    protected String cacheName;
    protected int ttlSeconds;
    protected int maxElementsInMemory;
    protected Class<?> key;
    protected Class<?> value;
    protected Boolean enableManagement;
    protected Boolean enableStatistics;
    protected Configuration<?, ?> configuration;

    public JCacheRegionConfiguration(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<?> key, Class<?> value, Boolean enableManagement, Boolean enableStatistics) {
        this.cacheName = cacheName;
        this.ttlSeconds = ttlSeconds;
        this.maxElementsInMemory = maxElementsInMemory;
        this.key = key;
        this.value = value;
        this.enableManagement = enableManagement;
        this.enableStatistics = enableStatistics;
    }

    public JCacheRegionConfiguration(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<?> key, Class<?> value) {
        this(cacheName, ttlSeconds, maxElementsInMemory, key, value, true, true);
    }

    public JCacheRegionConfiguration(String cacheName, int ttlSeconds, int maxElementsInMemory) {
        this(cacheName, ttlSeconds, maxElementsInMemory, Object.class, Object.class);
    }

    public JCacheRegionConfiguration(String cacheName) {
        this(cacheName, -1, 1000, Object.class, Object.class);
    }

    public JCacheRegionConfiguration(String cacheName, Configuration<?, ?> configuration) {
        this.cacheName = cacheName;
        this.configuration = configuration;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public Class<?> getKey() {
        return key;
    }

    public void setKey(Class<?> key) {
        this.key = key;
    }

    public Class<?> getValue() {
        return value;
    }

    public void setValue(Class<?> value) {
        this.value = value;
    }

    public Boolean getEnableManagement() {
        return enableManagement;
    }

    public void setEnableManagement(Boolean enableManagement) {
        this.enableManagement = enableManagement;
    }

    public Boolean getEnableStatistics() {
        return enableStatistics;
    }

    public void setEnableStatistics(Boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
    }

    public Configuration<?, ?> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration<?, ?> configuration) {
        this.configuration = configuration;
    }

}
