/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
