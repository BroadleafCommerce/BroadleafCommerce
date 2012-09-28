package org.broadleafcommerce.core.inventory.dao;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.broadleafcommerce.core.inventory.domain.Inventory;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;

import java.util.List;

/**
 * Copyright 2012 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public interface InventoryDao {

    public Inventory readById(Long id);
    
    /**
     * Same as read, but refreshes and handles locking for concurrent updates
     * @param id
     * @return
     */
    public Inventory readForUpdateById(Long id) throws ConcurrentInventoryModificationException;

    /**
     * Retrieves the {@link Inventory} for the given {@link Sku} and {@link FulfillmentLocation}
     * @param sku {@link Sku}
     * @param fulfillmentLocation {@link FulfillmentLocation}
     * @return {@link Inventory}
     */
    public Inventory readInventory(Sku sku, FulfillmentLocation fulfillmentLocation);
    
    /**
     * Same as readInventory but refreshes and locks the object.
     * @param sku
     * @param fulfillmentLocation
     * @return
     * @throws ConcurrentInventoryModificationException
     */
    public Inventory readInventoryForUpdate(Sku sku, FulfillmentLocation fulfillmentLocation) throws ConcurrentInventoryModificationException;

    /**
     * Retrieves the {@link Inventory} for the given {@link Sku}
     *
     * @param sku {@link org.broadleafcommerce.core.catalog.domain.Sku}
     * @return {@link List}
     */
    public Inventory readInventoryForDefaultFulfillmentLocation(Sku sku);
    
    /**
     * Same as readInventoryForDefaultFulfillmentLocation but refreshes and locks the object.
     * @param sku
     * @return
     * @throws ConcurrentInventoryModificationException
     */
    public Inventory readInventoryForUpdateForDefaultFulfillmentLocation(Sku sku) throws ConcurrentInventoryModificationException;

    /**
     * Persists the {@link Inventory}
     * @param inventory {@link Inventory}
     * @return the persisted {@link Inventory}
     */
    public Inventory save(Inventory inventory) throws ConcurrentInventoryModificationException;

    /**
     * Deletes the {@link Inventory}
      * @param inventory
     */
    public void delete(Inventory inventory);

}
