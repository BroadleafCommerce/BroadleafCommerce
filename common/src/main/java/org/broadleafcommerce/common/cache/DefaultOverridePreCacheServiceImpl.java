/*
 * #%L
 * BroadleafCommerce Common Enterprise
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
package org.broadleafcommerce.common.cache;

import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation of {@link OverridePreCacheService}. Simply contains no-ops methods.
 *
 * @author Jeff Fischer
 */
@Service("blOverridePreCacheService")
public class DefaultOverridePreCacheServiceImpl implements OverridePreCacheService {

    @Override
    public List<StandardCacheItem> findElements(String... cacheKeys) {
        return null;
    }

    @Override
    public boolean isActiveForType(String type) {
        return false;
    }

    @Override
    public boolean isActiveIsolatedSiteForType(Long siteId, String entityType) {
        return false;
    }

    @Override
    public void groomCacheBySiteOverride(String entityType, Long cloneId, boolean isRemove) {
        //do nothing
    }

    @Override
    public void groomCacheByTargetEntity(String entityType, Serializable id) {
        //do nothing
    }

    @Override
    public void refreshCache() {
        //do nothing
    }
}
