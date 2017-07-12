/*
 * #%L
 * BroadleafCommerce Common Enterprise
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.cache;

import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
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
    public void groomCacheBySiteOverride(String entityType, Long cloneId, boolean isRemove) {
        //do nothing
    }

    @Override
    public void groomCacheByTargetEntity(String entityType, Serializable id) {
        //do nothing
    }
}
