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
 * Helper class for building {@link Configuration} classes
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public interface JCacheConfigurationBuilder {

    /**
     * Given a {@link JCacheRegionConfiguration} build the appropriate {@link Configuration} class
     * 
     * The purpose of this method is to allow for extensibility and customization when using specific JCache implementations since most
     * support more configuration than the JSR-107 spec allows
     * 
     * @param regionInformation The {@link JCacheRegionConfiguration} to use to build the {@link Configuration}
     * @return The {@link Configuration}
     */
    public Configuration buildConfiguration(JCacheRegionConfiguration regionInformation);

    /**
     * Similar to {@link #buildConfiguration(JCacheRegionConfiguration)} however it requires more specifc arguments.
     * 
     * The purpose of this method was for internal Broadleaf usages where we're sending the exact arguments
     * 
     * @deprecated use {@link #buildConfiguration(JCacheRegionConfiguration)} as this will be removed in the future
     * @param <K> The key class of the {@link Configuration}
     * @param <V> The value class of the {@link Configuration}
     * @param ttlSeconds The time to live for cache items in seconds
     * @param maxElementsInMemory The maximum number of elments allowed in the cache. Note that in some JCache implementations this is not used
     * @param keyClass The key class of the {@link Configuration}
     * @param valueClass The value class of the {@link Configuration}
     * @return
     */
    @Deprecated
    public <K, V> Configuration<K, V> buildConfiguration(int ttlSeconds, int maxElementsInMemory, Class<K> keyClass, Class<V> valueClass);
    
}
