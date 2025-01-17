/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

package org.broadleafcommerce.core.inventory.service;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.BroadleafSystemEvent;
import org.broadleafcommerce.common.event.BroadleafSystemEvent.BroadleafEventScopeType;
import org.broadleafcommerce.common.event.BroadleafSystemEvent.BroadleafEventWorkerType;
import org.broadleafcommerce.common.event.BroadleafSystemEventDetail;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.catalog.domain.ProductSkuUsage;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    protected ApplicationContext applicationContext;

    @Value("${enable.weave.use.default.sku.inventory:false}")
    protected boolean enableUseDefaultSkuInventory = false;

    @Override
    public boolean checkBasicAvailablility(Sku sku) {
        if(sku != null) {
            if (sku.isActive() && !InventoryType.UNAVAILABLE.equals(sku.getInventoryType())) {
                return true;
            }
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
            Map<Sku, Integer> inventories = new HashMap<>();

            for (Sku sku : skus) {
                Sku skuForInventory = sku;
                if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
                    skuForInventory = sku.getProduct().getDefaultSku();
                }
                Integer quantityAvailable = 0;
                if(checkBasicAvailablility(skuForInventory)) {
                    InventoryType skuInventoryType = skuForInventory.getInventoryType();
                    if(InventoryType.CHECK_QUANTITY.equals(skuInventoryType)) {
                        if(skuForInventory.getQuantityAvailable() != null) {
                            quantityAvailable = skuForInventory.getQuantityAvailable();
                        }
                    } else if(skuForInventory.getInventoryType() == null || InventoryType.ALWAYS_AVAILABLE.equals(skuInventoryType)) {
                        quantityAvailable = null;
                    }
                }
                inventories.put(sku, quantityAvailable);
            }

            return inventories;
        } else {
            return holder.getResult();
        }
    }

    //here
    @Override
    public boolean isAvailable(Sku sku, int quantity, Map<String, Object> context) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero.");
        }
        Sku skuForInventory = sku;
        if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()) {
            skuForInventory = sku.getProduct().getDefaultSku();
        }
        if (checkBasicAvailablility(skuForInventory)) {
            if (InventoryType.CHECK_QUANTITY.equals(skuForInventory.getInventoryType())) {
                Integer quantityAvailable = retrieveQuantityAvailable(skuForInventory, context);
                
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
            decrementSku(skuQuantities, context);
        }
    }

    protected void decrementSku(Map<Sku, Integer> skuQuantities, Map<String, Object> context) throws InventoryUnavailableException {
        for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
            Sku sku = entry.getKey();
            Sku skuForInventory = sku;
            if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
                skuForInventory = sku.getProduct().getDefaultSku();
            }
            Integer quantity = entry.getValue();
            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero and not null.");
            }

            if (checkBasicAvailablility(skuForInventory)) {
                if (InventoryType.CHECK_QUANTITY.equals(skuForInventory.getInventoryType())) {
                    Integer inventoryAvailable = retrieveQuantityAvailable(skuForInventory, context);
                    if (inventoryAvailable == null) {
                        return;
                    }
                    if (inventoryAvailable < quantity) {
                        throw new InventoryUnavailableException(
                                "There was not enough inventory to fulfill this request.", skuForInventory.getId(), quantity, inventoryAvailable);
                    }
                    int newInventory = inventoryAvailable - quantity;
                    skuForInventory.setQuantityAvailable(newInventory);
                    catalogService.saveSku(skuForInventory);
                    invalidateSkuInventory(skuForInventory);
                } else {
                    LOG.info("Not decrementing inventory as the Sku has been marked as always available");
                }
            } else {
                throw new InventoryUnavailableException("The Sku has been marked as unavailable", sku.getId(), quantity, 0);
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
            incrementSku(skuQuantities, context);
        }
    }

    protected void incrementSku(Map<Sku, Integer> skuQuantities, Map<String, Object> context) {
        for (Entry<Sku, Integer> entry : skuQuantities.entrySet()) {
            Sku sku = entry.getKey();

            Sku skuForInventory = sku;
            if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
                skuForInventory = sku.getProduct().getDefaultSku();
            }
            Integer quantity = entry.getValue();
            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException("Quantity " + quantity + " is not valid. Must be greater than zero and not null.");
            }
            if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                Integer currentInventoryAvailable = retrieveQuantityAvailable(skuForInventory, context);
                if (currentInventoryAvailable == null) {
                    throw new IllegalArgumentException("The current inventory for this Sku is null");
                }
                int newInventory = currentInventoryAvailable + quantity;
                skuForInventory.setQuantityAvailable(newInventory);
                catalogService.saveSku(skuForInventory);
                invalidateSkuInventory(skuForInventory);
            } else {
                LOG.info("Not incrementing inventory as the Sku has been marked as always available");
            }
        }
    }

    @Override
    public void reconcileChangeOrderInventory(Map<Sku, Integer> decrementSkuQuantities, Map<Sku, Integer> incrementSkuQuantities, Map<String, Object> context) throws InventoryUnavailableException {
        ExtensionResultStatusType res = extensionManager.getProxy().reconcileChangeOrderInventory(decrementSkuQuantities, incrementSkuQuantities, context);
        if (ExtensionResultStatusType.NOT_HANDLED.equals(res)) {
            if (!decrementSkuQuantities.isEmpty()) {
                decrementSku(decrementSkuQuantities, context);
            }

            if (!incrementSkuQuantities.isEmpty()) {
                incrementSku(incrementSkuQuantities, context);
            }
        }
    }

    @Override
    public Map<Sku, Integer> buildSkuInventoryMap(Order order) {
        //map to hold skus and quantity purchased
        HashMap<Sku, Integer> skuInventoryMap = new HashMap<Sku, Integer>();

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof DiscreteOrderItem) {
                Sku sku = ((DiscreteOrderItem) orderItem).getSku();
                Sku skuForInventory = sku;
                if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
                    skuForInventory = sku.getProduct().getDefaultSku();
                }
                Integer quantity = skuInventoryMap.get(skuForInventory);
                if (quantity == null) {
                    quantity = orderItem.getQuantity();
                } else {
                    quantity += orderItem.getQuantity();
                }
                if (InventoryType.CHECK_QUANTITY.equals(skuForInventory.getInventoryType())) {
                    skuInventoryMap.put(skuForInventory, quantity);
                }
            } else if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleItem = (BundleOrderItem) orderItem;
                Sku bundleSku = bundleItem.getSku();
                if (enableUseDefaultSkuInventory && ((ProductSkuUsage) bundleSku.getProduct()).getUseDefaultSkuInInventory()){
                    bundleSku = bundleSku.getProduct().getDefaultSku();
                }
                if (InventoryType.CHECK_QUANTITY.equals(bundleSku.getInventoryType())) {
                    // add the bundle sku of quantities to decrement
                    skuInventoryMap.put(bundleSku, bundleItem.getQuantity());
                }

                // Now add all of the discrete items within the bundl
                List<DiscreteOrderItem> discreteItems = bundleItem.getDiscreteOrderItems();
                for (DiscreteOrderItem discreteItem : discreteItems) {
                    Sku sku = discreteItem.getSku();
                    if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
                        sku = sku.getProduct().getDefaultSku();
                    }
                    if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                        Integer quantity = skuInventoryMap.get(sku);
                        if (quantity == null) {
                            quantity = (discreteItem.getQuantity() * bundleItem.getQuantity());
                        } else {
                            quantity += (discreteItem.getQuantity() * bundleItem.getQuantity());
                        }
                        skuInventoryMap.put(sku, quantity);
                    }
                }
            }
        }

        return skuInventoryMap;
    }

    /**
     * Invalidates the cache for a given sku
     * 
     * @param sku The Sku to be invalidated from cache
     */
    protected void invalidateSkuInventory(Sku sku) {
        final Class<?> clazz = sku.getClass();
        final String id = sku.getId().toString();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

                @Override
                public void afterCommit() {
                    invalidateEntity(clazz, id);
                }
            });
        } else {
            invalidateEntity(clazz, id);
        }
    }

    /**
     * Given the class and identifier of an entity, invalidates the cache for that entity 
     * 
     * @param clazz The class of the entity to invalidate the cache for
     * @param id The id of the entity to invalidate the cache for
     */
    protected void invalidateEntity(Class<?> clazz, String id) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Invalidating entity of type " + clazz.getName() + " with id " + id);
        }
        for (Class<?> superclazz : ClassUtils.hierarchy(clazz)) {
            Cache cacheAnnotation = superclazz.getAnnotation(Cache.class);
            if (cacheAnnotation != null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Cache invalidation event to be sent");
                    LOG.info("superclazz : " + superclazz.getName());
                    LOG.info("region : " + cacheAnnotation.region());
                    LOG.info("id : " + id);
                }
                Map<String, BroadleafSystemEventDetail> detailMap = new HashMap<>();
                detailMap.put("CACHE_REGION", new BroadleafSystemEventDetail("Cache Region", cacheAnnotation.region()));
                detailMap.put("ENTITY_TYPE", new BroadleafSystemEventDetail("Entity Type", superclazz.getName()));
                detailMap.put("IDENTIFIER", new BroadleafSystemEventDetail("Identifier", id));
                BroadleafSystemEvent event = new BroadleafSystemEvent("CACHE", detailMap, BroadleafEventScopeType.GLOBAL, BroadleafEventWorkerType.ANY, true);
                applicationContext.publishEvent(event);
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("No region found so no cache invalidation event to be created");
                    LOG.info("superclazz : " + superclazz.getName());
                    LOG.info("region : N/A");
                    LOG.info("id : " + id);
                }
            }
        }
    }

    @Override
    public void checkSkuAvailability(Order order, Sku sku, Integer requestedQuantity) throws InventoryUnavailableException {
        Sku skuForInventory = sku;
        if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
            skuForInventory = sku.getProduct().getDefaultSku();
        }
        // First check if this Sku is available
        if (!sku.isAvailable()) {
            throw new InventoryUnavailableException("The referenced Sku " + sku.getId() + " is marked as unavailable", sku.getId(), requestedQuantity, 0);
        }
        if (InventoryType.CHECK_QUANTITY.equals(skuForInventory.getInventoryType())) {
            Map<String, Object> inventoryContext = new HashMap<>();
            inventoryContext.put(ContextualInventoryService.ORDER_KEY, order);
            boolean available = isAvailable(sku, requestedQuantity, inventoryContext);
            if (!available) {
                throw new InventoryUnavailableException(sku.getId(),
                        requestedQuantity, retrieveQuantityAvailable(sku, inventoryContext));
            }
        }

        // the other case here is ALWAYS_AVAILABLE and null, which we are treating as being available
    }
}
