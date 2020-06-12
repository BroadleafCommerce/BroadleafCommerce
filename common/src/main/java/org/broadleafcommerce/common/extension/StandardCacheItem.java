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
package org.broadleafcommerce.common.extension;

import java.io.Serializable;

/**
 * Represents a member of a query result list for a multitenant sparsely populated cache scenario (see {@link org.broadleafcommerce.common.extension.SparselyPopulatedQueryExtensionHandler}).
 * Denotes whether the item is a normal/active item in a standard site, or if it's a deleted/archived item in a standard site.
 *
 * @author Jeff Fischer
 */
public class StandardCacheItem implements Serializable {

    private String key;
    private Object cacheItem;
    private ItemStatus itemStatus;

    public Object getCacheItem() {
        return cacheItem;
    }

    public void setCacheItem(Object cacheItem) {
        this.cacheItem = cacheItem;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
