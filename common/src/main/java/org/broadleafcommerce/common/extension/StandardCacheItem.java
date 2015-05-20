package org.broadleafcommerce.common.extension;

import java.io.Serializable;

/**
 * Represents a member of a query result list for a multitenant sparsely populated cache scenario (see {@link org.broadleafcommerce.common.extension.SparselyPopulatedQueryExtensionHandler}).
 * Denotes whether the item is a normal/active item in a standard site, or if it's a deleted/archived item in a standard site.
 *
 * @author Jeff Fischer
 */
public class StandardCacheItem implements Serializable {

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

}
