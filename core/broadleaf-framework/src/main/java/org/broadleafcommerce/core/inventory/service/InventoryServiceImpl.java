/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

package org.broadleafcommerce.core.inventory.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

@Service("blInventoryService")
public class InventoryServiceImpl implements InventoryService {

    private static final Log LOG = LogFactory.getLog(InventoryServiceImpl.class);
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    protected boolean checkBasicAvailablility(Sku sku) {
        Boolean available = sku.isAvailable();
        if (available == null) {
            available = true;
        }
        if (sku != null && available && sku.isActive() && !InventoryType.UNAVAILABLE.equals(sku.getInventoryType())) {
            return true;
        }
        return false;
    }

    @Override
    public Integer retrieveQuantityAvailable(Sku sku, Map<String, Object> context) {
        if (checkBasicAvailablility(sku)) {
            if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                return sku.getQuantityAvailable();
            } else if (InventoryType.ALWAYS_AVAILABLE.equals(sku.getInventoryType()) ||
                    sku.getInventoryType() == null) {
                return null;
            }
        }
        return 0;
    }

    @Override
    public Map<Sku, Integer> retrieveQuantitiesAvailable(Set<Sku> skus, Map<String, Object> context) {
        Map<Sku, Integer> inventories = new HashMap<Sku, Integer>();
        for (Sku sku : skus) {
            if (checkBasicAvailablility(sku)) {
                if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                    inventories.put(sku, sku.getQuantityAvailable());
                } else if (sku.getInventoryType() == null || InventoryType.ALWAYS_AVAILABLE.equals(sku.getInventoryType())) {
                    inventories.put(sku, Integer.MAX_VALUE);
                } else {
                    inventories.put(sku, 0);
                }
            } else {
                inventories.put(sku, 0);
            }
        }

        return inventories;
    }

    @Override
    public boolean isAvailable(Sku sku, int quantity, Map<String, Object> context) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero.");
        }
        if (checkBasicAvailablility(sku)) {
            if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                Integer quantityAvailable = retrieveQuantityAvailable(sku, context);
                return quantityAvailable != null && quantity <= quantityAvailable;
            }
        }
        return false;
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Sku sku, int quantity) throws InventoryUnavailableException {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero.");
        }
        if (checkBasicAvailablility(sku)) {
            if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                Integer inventoryAvailable = retrieveQuantityAvailable(sku, null);
                if (inventoryAvailable == null) {
                    return;
                }
                if (inventoryAvailable < quantity) {
                    throw new InventoryUnavailableException(
                            "There was not enough inventory to fulfill this request.", sku.getId(), quantity, inventoryAvailable);
                }
                int newInventory = inventoryAvailable - quantity;
                sku.setQuantityAvailable(newInventory);
                catalogService.saveSku(sku);
            } else {
                LOG.info("Not decrementing inventory as the Sku has been marked as always available");
            }
        } else {
            throw new InventoryUnavailableException("The Sku has been marked as unavailable", sku.getId(), quantity, 0);
        }
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Map<Sku, Integer> skuQuantities) throws InventoryUnavailableException {
        for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
            Sku sku = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity == null) {
                throw new IllegalArgumentException("Quantity was null for skuId " + sku.getId());
            }
            decrementInventory(entry.getKey(), quantity);
        }
    }

    @Override
    @Transactional("blTransactionManager")
    public void incrementInventory(Sku sku, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero.");
        }
        if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
            int inventoryAvailable = retrieveQuantityAvailable(sku, null);
            int newInventory = inventoryAvailable + quantity;
            sku.setQuantityAvailable(newInventory);
            catalogService.saveSku(sku);
        } else {
            LOG.info("Not incrementing inventory as the Sku has been marked as always available");
        }
    }

    @Override
    @Transactional("blTransactionManager")
    public void incrementInventory(Map<Sku, Integer> skuQuantities) {
        for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
            Sku sku = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity == null) {
                throw new IllegalArgumentException("Quantity was null for skuId " + sku.getId());
            }
            incrementInventory(entry.getKey(), quantity);
        }
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

}
