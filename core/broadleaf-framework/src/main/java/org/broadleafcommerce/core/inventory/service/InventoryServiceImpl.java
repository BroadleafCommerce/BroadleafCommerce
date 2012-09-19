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
package org.broadleafcommerce.core.inventory.service;


import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.dao.InventoryDao;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.broadleafcommerce.core.inventory.domain.Inventory;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.exception.InventoryUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service("blInventoryService")
@Transactional(value="blTransactionManager", rollbackFor={InventoryUnavailableException.class,ConcurrentInventoryModificationException.class})
public class InventoryServiceImpl implements InventoryService {

    @Resource(name="blInventoryDao")
    protected InventoryDao inventoryDao;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public boolean isQuantityAvailable(Sku sku, Integer quantity) {
        return isQuantityAvailable(sku, quantity, null);
    }

    @Override
    public boolean isQuantityAvailable(Sku sku, Integer quantity, FulfillmentLocation fulfillmentLocation) {

        //if the sku does not exist or is not active, there is no quantity available
        if (!sku.isActive()) {
            return false;
        }

        if (sku.getInventoryType() == null 
        		&& (sku.getDefaultProduct().getDefaultCategory() == null
        		|| sku.getDefaultProduct().getDefaultCategory().getInventoryType() == null)) {
            return true;
        } else if (InventoryType.NONE.equals(sku.getInventoryType()) 
        		|| (sku.getDefaultProduct().getDefaultCategory() != null 
        		&& InventoryType.NONE.equals(sku.getDefaultProduct().getDefaultCategory().getInventoryType()))){
            return true;
        } 

        //quantity must be greater than 0
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }

        Inventory inventory = null;

        if (fulfillmentLocation == null) {
            inventory = inventoryDao.readInventoryForDefaultFulfillmentLocation(sku);
        } else {
            inventory = inventoryDao.readInventory(sku, fulfillmentLocation);
        }

        return inventory != null && inventory.getQuantityAvailable() >= quantity;

    }

    @Override
    public void decrementInventory(Map<Sku, Integer> skuInventory) throws ConcurrentInventoryModificationException, InventoryUnavailableException {
        decrementInventory(skuInventory, null);
    }

    @Override
    public void decrementInventory(Map<Sku, Integer> skuInventory, FulfillmentLocation fulfillmentLocation) throws ConcurrentInventoryModificationException, InventoryUnavailableException {

        Set<Sku> skus = skuInventory.keySet();
        Map<Long, Integer> unavailableInventoryHolder = new HashMap<Long, Integer>();

        for (Sku sku : skus) {

            Integer quantity = skuInventory.get(sku);

            /*
             * If the inventory type of the sku or category is null or InventoryType.NONE, do not adjust inventory
             */
            if (sku.getInventoryType() == null && sku.getDefaultProduct().getDefaultCategory().getInventoryType() == null) {
               continue;
            } else if (InventoryType.NONE.equals(sku.getInventoryType()) || InventoryType.NONE.equals(sku.getDefaultProduct().getDefaultCategory())) {
               continue;
            }

            //quantity must not be null
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity must not be a positive integer");
            }

            if (quantity == 0) {
                continue;
            }

            //check available inventory
            Inventory inventory = null;
            if (fulfillmentLocation != null) {
                inventory = inventoryDao.readInventory(sku, fulfillmentLocation);
            } else {
                inventory = inventoryDao.readInventoryForDefaultFulfillmentLocation(sku);
            }

            if (inventory != null) {
                Integer quantityAvailable = inventory.getQuantityAvailable();

                int qtyToUpdate = quantityAvailable - quantity;
                if (qtyToUpdate < 0) {
                    //there is not enough inventory available
                    unavailableInventoryHolder.put(sku.getId(), quantityAvailable);
                } else {
                    inventory.setQuantityAvailable(qtyToUpdate);
                    inventoryDao.save(inventory); //this call could throw ConcurrentInventoryModificationException
                }

            } else {
               unavailableInventoryHolder.put(sku.getId(), 0);
            }

        }

        if (!unavailableInventoryHolder.isEmpty()) {
            InventoryUnavailableException ex = new InventoryUnavailableException("Inventory is unavailable for " + unavailableInventoryHolder.size() + " skus");
            ex.setSkuInventoryAvailable(unavailableInventoryHolder);
            throw ex;
        }

    }

    @Override
    public void incrementInventory(Map<Sku, Integer> skuInventory, FulfillmentLocation fulfillmentLocation) throws ConcurrentInventoryModificationException {
        //TODO
        Set<Sku> skus = skuInventory.keySet();
        for (Sku sku : skus) {
            Integer quantity = skuInventory.get(sku);

            /*
             * If the inventory type of the sku or category is null or InventoryType.NONE, do not adjust inventory
             */
            if (sku.getInventoryType() == null && sku.getDefaultProduct().getDefaultCategory().getInventoryType() == null) {
                continue;
            } else if (InventoryType.NONE.equals(sku.getInventoryType()) || InventoryType.NONE.equals(sku.getDefaultProduct().getDefaultCategory())) {
                continue;
            }

            //quantity must not be null
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity must not be a positive integer");
            }

            if (quantity == 0) {
                continue;
            }

            Inventory inventory = readInventory(sku, fulfillmentLocation);

            if (inventory != null) {
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
                inventoryDao.save(inventory);
            } else {
                /*
                 * create a new inventory record if one does not exist
                 */
                inventory = (Inventory) entityConfiguration.createEntityInstance(Inventory.class.getName());
                inventory.setQuantityAvailable(quantity);
                inventory.setQuantityOnHand(quantity);
                inventory.setSku(sku);
                inventory.setFulfillmentLocation(fulfillmentLocation);
                inventoryDao.save(inventory);
            }

        }
    }

    @Override
    public Inventory readInventory(Sku sku, FulfillmentLocation fulfillmentLocation) {
        return inventoryDao.readInventory(sku, fulfillmentLocation);
    }

    @Override
    public Inventory readInventory(Sku sku) {
        return inventoryDao.readInventoryForDefaultFulfillmentLocation(sku);
    }

}
