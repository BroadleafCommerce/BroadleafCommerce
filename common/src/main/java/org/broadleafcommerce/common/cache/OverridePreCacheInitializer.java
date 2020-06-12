/*
 * #%L
 * broadleaf-multitenant-singleschema
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
package org.broadleafcommerce.common.cache;

import org.broadleafcommerce.common.extension.StandardCacheItem;

/**
 * Performs cache item initialization for a specific entity type.
 *
 * @author Jeff Fischer
 */
public interface OverridePreCacheInitializer {

    /**
     * Whether or not this initializer is qualified to work on the given entity type
     *
     * @param type
     * @return
     */
    boolean isOverrideQualified(Class<?> type);

    /**
     * Perform any initialization tasks (e.g. exercising a lazy collection) and returns
     * a StandardCacheItem instance.
     *
     * @param entity
     * @return
     */
    StandardCacheItem initializeOverride(Object entity);

}
