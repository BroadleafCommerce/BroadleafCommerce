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
package org.broadleafcommerce.common.cache.engine;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ehcache 3 cache event listener for monitoring cache changes using the Ehcache hydrated cache.
 * <p>
 * Cache changes need to be monitored so that if an entity's field is populated via the hydrated cache manager
 * and that entity is modified we need to evict that cached value for that entity's field from our cache so that
 * we don't return stale data.
 *
 * @author Jay Aisenbrey (cja769)
 */
public class EhcacheHydratedCacheEventListener extends EhcacheHydratedCacheManagerImpl implements CacheEventListener {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void onEvent(CacheEvent cacheEvent) {
        if (EventType.EXPIRED.equals(cacheEvent.getType())) {
            removeCache((Serializable) cacheEvent.getKey());
        }
        if (EventType.REMOVED.equals(cacheEvent.getType())) {
            removeCache((Serializable) cacheEvent.getKey());
        }
        if (EventType.UPDATED.equals(cacheEvent.getType())) {
            removeCache((Serializable) cacheEvent.getKey());
        }
        if (EventType.EVICTED.equals(cacheEvent.getType())) {
            removeCache((Serializable) cacheEvent.getKey());
        }
    }

    @Override
    protected boolean useCacheRegionInKey() {
        return false;
    }

}
