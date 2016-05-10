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
package org.broadleafcommerce.common.cache.engine;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * 
 * @author jfischer
 *
 */
@Deprecated
public class HydratedCache extends Hashtable<String, Object> {

    private static final long serialVersionUID = 1L;

    private String cacheName;
    private String cacheRegion;

    public HydratedCache(String cacheRegion, String cacheName) {
        this.cacheRegion = cacheRegion;
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public HydratedCacheElement getCacheElement(String cacheRegion, String cacheName, Serializable key) {
        return (HydratedCacheElement) get(cacheRegion + "_" + cacheName + "_" + key);
    }
    
    public HydratedCacheElement removeCacheElement(String cacheRegion, String cacheName, Serializable key) {
        String myKey = cacheRegion + "_" + cacheName + "_" + key;
        return (HydratedCacheElement) remove(myKey);
    }
    
    public void addCacheElement(String cacheRegion, String cacheName, Serializable key, Object value) {
        String myKey = cacheRegion + "_" + cacheName + "_" + key;
        put(myKey, value);
    }
}
