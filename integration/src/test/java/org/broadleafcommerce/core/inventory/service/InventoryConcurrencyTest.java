/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Concurrent checkouts of the same {@link InventoryType#CHECK_QUANTITY} sku must not oversell it (QA#5357).
 * <p>
 * Each thread mimics a storefront request: it binds its own EntityManager (as OpenEntityManagerInView does) and loads
 * the sku into it before any thread decrements, then all threads decrement at once through the transactional service.
 */
public class InventoryConcurrencyTest extends CommonSetupBaseTest {

    protected static final int STOCK = 5;
    protected static final int CHECKOUTS = 10;

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;

    @Resource(name = "blTransactionManager")
    protected JpaTransactionManager transactionManager;

    @Test(groups = "testInventoryConcurrency")
    public void testConcurrentDecrementsDoNotOversell() throws Exception {
        Long skuId = createInventorySku(STOCK);
        EntityManagerFactory emf = transactionManager.getEntityManagerFactory();

        CyclicBarrier allSkusLoaded = new CyclicBarrier(CHECKOUTS);
        AtomicInteger accepted = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(CHECKOUTS);
        List<Future<?>> checkouts = new ArrayList<>();
        try {
            for (int i = 0; i < CHECKOUTS; i++) {
                checkouts.add(pool.submit(() -> {
                    EntityManager em = emf.createEntityManager();
                    TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
                    try {
                        Sku sku = em.find(SkuImpl.class, skuId);
                        sku.getProduct();
                        allSkusLoaded.await(30, TimeUnit.SECONDS);
                        try {
                            inventoryService.decrementInventory(sku, 1);
                            accepted.incrementAndGet();
                        } catch (InventoryUnavailableException e) {
                            rejected.incrementAndGet();
                        }
                    } finally {
                        TransactionSynchronizationManager.unbindResource(emf);
                        EntityManagerFactoryUtils.closeEntityManager(em);
                    }
                    return null;
                }));
            }
            for (Future<?> checkout : checkouts) {
                checkout.get(60, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        Integer finalQuantity = readQuantityAvailable(emf, skuId);
        assertEquals(accepted.get() + rejected.get(), CHECKOUTS);
        assertTrue(accepted.get() <= STOCK, "Oversold: accepted " + accepted.get() + " orders for a stock of " + STOCK);
        assertEquals(accepted.get() + finalQuantity.intValue(), STOCK,
                "Lost update: accepted " + accepted.get() + " orders but quantity left is " + finalQuantity);
    }

    protected Long createInventorySku(int quantity) {
        Product product = addTestProduct("Inventory Concurrency Product", "Inventory Concurrency");
        Sku sku = product.getDefaultSku();
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(quantity);
        return catalogService.saveSku(sku).getId();
    }

    protected Integer readQuantityAvailable(EntityManagerFactory emf, Long skuId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(SkuImpl.class, skuId, Map.of("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS))
                    .getQuantityAvailable();
        } finally {
            em.close();
        }
    }
}
