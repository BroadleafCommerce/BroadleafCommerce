/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.inventory.dao;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.broadleafcommerce.core.inventory.domain.Inventory;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository("blInventoryDao")
public class InventoryDaoImpl implements InventoryDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public Inventory save(Inventory inventory) throws ConcurrentInventoryModificationException {
        try {
            inventory = em.merge(inventory);
            
            //This should cause an OptimisticLockException immediately if someone has 
            //already modified this object, rather than waiting for the transaction to complete.
            em.flush();
            return inventory;
        } catch (OptimisticLockException ex) {
            throw new ConcurrentInventoryModificationException("Error saving inventory with id: " + inventory.getId());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Inventory readInventory(Sku sku, FulfillmentLocation fulfillmentLocation) {
        Query query = em.createNamedQuery("BC_READ_SKU_INVENTORY_FOR_LOCATION");
        query.setLockMode(LockModeType.OPTIMISTIC);
        query.setParameter("skuId", sku.getId());
        query.setParameter("fulfillmentLocationId", fulfillmentLocation.getId());

        List<Inventory> inventories = query.getResultList();
        if (CollectionUtils.isNotEmpty(inventories)) {
        	return inventories.get(0);
        }

        return null;
    }
    
    @Override
    public Inventory readInventoryForUpdate(Sku sku, FulfillmentLocation fulfillmentLocation) throws ConcurrentInventoryModificationException {
        Inventory inventory = readInventory(sku, fulfillmentLocation);
        if (inventory != null) {
        	try {
	        	em.refresh(inventory, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
	        	em.flush();
        	} catch (OptimisticLockException ex) {
                throw new ConcurrentInventoryModificationException("Error locking inventory object with id: " + inventory.getId());
            }
        } 
        return inventory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Inventory readInventoryForDefaultFulfillmentLocation(Sku sku) {
        Query query = em.createNamedQuery("BC_READ_SKU_INVENTORY_FOR_DEFAULT_LOCATION");
        query.setParameter("skuId", sku.getId());
        query.setLockMode(LockModeType.OPTIMISTIC);
        query.setMaxResults(1);
        List<Inventory> inventories = query.getResultList();
        if (CollectionUtils.isNotEmpty(inventories)) {
            return inventories.get(0);
        }
        return null;
    }
    
    @Override
    public Inventory readInventoryForUpdateForDefaultFulfillmentLocation(Sku sku) throws ConcurrentInventoryModificationException {
        Inventory inventory = readInventoryForDefaultFulfillmentLocation(sku);
        if (inventory != null) {
        	try {
	        	em.refresh(inventory, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
	        	em.flush();
        	} catch (OptimisticLockException ex) {
                throw new ConcurrentInventoryModificationException("Error locking inventory object with id: " + inventory.getId());
            }
        }
        return inventory;
    }

    @Override
    public void delete(Inventory inventory) {
        em.remove(inventory);
    }

    @Override
    public Inventory readById(Long id) {
        return em.find(Inventory.class, id, LockModeType.OPTIMISTIC);
    }
    
    @Override
    public Inventory readForUpdateById(Long id) throws ConcurrentInventoryModificationException {
        Inventory inventory = readById(id);
        if (inventory != null) {
        	try {
	        	em.refresh(inventory, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
	        	em.flush();
        	} catch (OptimisticLockException ex) {
                throw new ConcurrentInventoryModificationException("Error locking inventory object with id: " + inventory.getId());
            }
        }
        return inventory;
    }
    
}
