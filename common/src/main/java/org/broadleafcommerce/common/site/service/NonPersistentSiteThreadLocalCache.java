/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.site.domain.Site;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class NonPersistentSiteThreadLocalCache {

    private static final ThreadLocal<NonPersistentSiteThreadLocalCache> SITES_CACHE = ThreadLocalManager.createThreadLocal(NonPersistentSiteThreadLocalCache.class);

    public static NonPersistentSiteThreadLocalCache getSitesCache() {
        return SITES_CACHE.get();
    }

    public static void setSitesCache(NonPersistentSiteThreadLocalCache sitesCache) {
        SITES_CACHE.set(sitesCache);
    }

    protected Map<Long, Site> sites = new HashMap<Long, Site>();

    public Map<Long, Site> getSites() {
        return sites;
    }

    public void setSites(Map<Long, Site> sites) {
        this.sites = sites;
    }

    public void clear() {
        SITES_CACHE.remove();
    }
}
