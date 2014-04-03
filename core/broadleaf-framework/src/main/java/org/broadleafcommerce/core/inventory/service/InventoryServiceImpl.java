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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

@Service("blInventoryService")
public class InventoryServiceImpl implements ContextualInventoryService {

    private static final Log LOG = LogFactory.getLog(InventoryServiceImpl.class);
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blInventoryServiceExtensionManager")
    protected InventoryServiceExtensionManager extensionManager;

    @Override
    public boolean checkBasicAvailablility(Sku sku) {
        Boolean available = sku.isAvailable();
        if (available == null) {
            available = true;
        }
        if (sku != null && available && sku.isActive() && !InventoryType.UNAVAILABLE.equals(sku.getInventoryType())) {
            return true;
        }
        return false;
    }
    
    /* ******************************** */
    /* InventoryService Implementations */
    /* ******************************** */
    
    @Override
    public Integer retrieveQuantityAvailable(Sku sku) {
        return retrieveQuantityAvailable(sku, null);
    }

    @Override
    public Map<Sku, Integer> retrieveQuantitiesAvailable(Collection<Sku> skus) {
        return retrieveQuantitiesAvailable(skus, null);
    }
    
    @Override
    public boolean isAvailable(Sku sku, int quantity) {
        return isAvailable(sku, quantity, null);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER, rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Sku sku, int quantity) throws InventoryUnavailableException {
        decrementInventory(sku, quantity, null);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER, rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Map<Sku, Integer> skuQuantities) throws InventoryUnavailableException {
        decrementInventory(skuQuantities, null);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void incrementInventory(Sku sku, int quantity) {
        incrementInventory(sku, quantity, null);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void incrementInventory(Map<Sku, Integer> skuQuantities) {
        incrementInventory(skuQuantities, null);
    }

    
    /* ****************************************** */
    /* ContextualInventoryService Implementations */
    /* ****************************************** */
    
    @Override
    public Integer retrieveQuantityAvailable(Sku sku, Map<String, Object> context) {
        return retrieveQuantitiesAvailable(Arrays.asList(sku), context).get(sku);
    }

    @Override
    public Map<Sku, Integer> retrieveQuantitiesAvailable(Collection<Sku> skus, Map<String, Object> context) {
        ExtensionResultHolder<Map<Sku, Integer>> holder = new ExtensionResultHolder<Map<Sku, Integer>>();
        ExtensionResultStatusType res = extensionManager.getProxy().retrieveQuantitiesAvailable(skus, context, holder);
        if (ExtensionResultStatusType.NOT_HANDLED.equals(res)) {
            Map<Sku, Integer> inventories = new HashMap<Sku, Integer>();
            for (Sku sku : skus) {
                if (checkBasicAvailablility(sku)) {
                    if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                        if (sku.getQuantityAvailable() == null) {
                            inventories.put(sku, 0);
                        }
                        inventories.put(sku, sku.getQuantityAvailable());
                    } else if (sku.getInventoryType() == null || InventoryType.ALWAYS_AVAILABLE.equals(sku.getInventoryType())) {
                        inventories.put(sku, null);
                    } else {
                        inventories.put(sku, 0);
                    }
                } else {
                    inventories.put(sku, 0);
                }
            }
    
            return inventories;
        } else {
            return holder.getResult();
        }
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
            } else {
                // basically available but we do not need to check quantity, definitely available
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER, rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Sku sku, int quantity, Map<String, Object> context) throws InventoryUnavailableException {
        Map<Sku, Integer> quantities = new HashMap<Sku, Integer>();
        quantities.put(sku, quantity);
        decrementInventory(quantities, context);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER, rollbackFor = { InventoryUnavailableException.class })
    public void decrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) throws InventoryUnavailableException {
        ExtensionResultStatusType res = extensionManager.getProxy().decrementInventory(skuQuantities, context);
        if (ExtensionResultStatusType.NOT_HANDLED.equals(res)) {
            for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
                Sku sku = entry.getKey();
                Integer quantity = entry.getValue();
                if (quantity == null || quantity < 1) {
                    throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero and not null.");
                }
                
                if (checkBasicAvailablility(sku)) {
                    if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                        Integer inventoryAvailable = retrieveQuantityAvailable(sku, context);
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
        }
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void incrementInventory(Sku sku, int quantity, Map<String, Object> context) {
        Map<Sku, Integer> quantities = new HashMap<Sku, Integer>();
        quantities.put(sku, quantity);
        incrementInventory(quantities, context);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void incrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) {
        ExtensionResultStatusType res = extensionManager.getProxy().incrementInventory(skuQuantities, context);
        if (ExtensionResultStatusType.NOT_HANDLED.equals(res)) {
            for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
                Sku sku = entry.getKey();
                Integer quantity = entry.getValue();
                if (quantity == null || quantity < 1) {
                    throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero and not null.");
                }
                if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                    Integer currentInventoryAvailable = retrieveQuantityAvailable(sku, context);
                    if (currentInventoryAvailable == null) {
                        throw new IllegalArgumentException("The current inventory for this Sku is null");
                    }
                    int newInventory = currentInventoryAvailable + quantity;
                    sku.setQuantityAvailable(newInventory);
                    catalogService.saveSku(sku);
                } else {
                    LOG.info("Not incrementing inventory as the Sku has been marked as always available");
                }
            }
        }
    }

}
