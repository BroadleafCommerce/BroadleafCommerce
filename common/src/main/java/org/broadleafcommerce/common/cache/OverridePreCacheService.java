/*
 * #%L
 * broadleaf-multitenant-singleschema
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

import java.io.Serializable;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public interface OverridePreCacheService {

    List<StandardCacheItem> findElements(String... cacheKeys);

    boolean isActiveForType(String type);

    void groomCacheBySiteOverride(String entityType, Long cloneId, boolean isRemove);

    void groomCacheByTargetEntity(final String entityType, final Serializable id);

}
