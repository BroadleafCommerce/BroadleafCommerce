/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class NonPersistentSiteTheadLocalCache {

    private static final ThreadLocal<NonPersistentSiteTheadLocalCache> SITES_CACHE = ThreadLocalManager.createThreadLocal(NonPersistentSiteTheadLocalCache.class);

    public static NonPersistentSiteTheadLocalCache getSitesCache() {
        return SITES_CACHE.get();
    }

    public static void setSitesCache(NonPersistentSiteTheadLocalCache sitesCache) {
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
