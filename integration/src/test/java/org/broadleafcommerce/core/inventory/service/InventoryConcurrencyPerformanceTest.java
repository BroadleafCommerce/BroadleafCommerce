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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Measures {@code decrementInventory} latency under concurrency (QA#5357). Not an assertion test: it prints one
 * {@code PERF|} line per scenario. Disabled by default; enable it to run it on demand.
 */
public class InventoryConcurrencyPerformanceTest extends CommonSetupBaseTest {

    protected static final int[] CONCURRENCY = {10, 50, 100};
    protected static final int STOCK = 100_000;

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;

    @Resource(name = "blTransactionManager")
    protected JpaTransactionManager transactionManager;

    @Test(groups = "testInventoryConcurrencyPerformance", enabled = false)
    public void measureDecrementLatency() throws Exception {
        int maxConcurrency = CONCURRENCY[CONCURRENCY.length - 1];
        List<Long> skuIds = new ArrayList<>();
        for (int i = 0; i < maxConcurrency; i++) {
            skuIds.add(createInventorySku(i));
        }

        // warm-up: JIT, Hibernate metadata, connection pool
        run(skuIds, maxConcurrency, false);
        run(skuIds, maxConcurrency, true);

        for (int threads : CONCURRENCY) {
            report("same-sku", threads, run(skuIds, threads, true));
            report("different-skus", threads, run(skuIds, threads, false));
        }
    }

    /**
     * @return per-call latencies in microseconds, with the wall time of the whole round as the last element
     */
    protected List<Long> run(List<Long> skuIds, int threads, boolean sameSku) throws Exception {
        EntityManagerFactory emf = transactionManager.getEntityManagerFactory();
        CyclicBarrier allSkusLoaded = new CyclicBarrier(threads);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> calls = new ArrayList<>();
        long[] start = new long[1];
        try {
            for (int i = 0; i < threads; i++) {
                Long skuId = sameSku ? skuIds.get(0) : skuIds.get(i);
                calls.add(pool.submit(() -> {
                    EntityManager em = emf.createEntityManager();
                    TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
                    try {
                        Sku sku = em.find(SkuImpl.class, skuId);
                        sku.getProduct();
                        if (allSkusLoaded.await(60, TimeUnit.SECONDS) == 0) {
                            start[0] = System.nanoTime();
                        }
                        long callStart = System.nanoTime();
                        inventoryService.decrementInventory(sku, 1);
                        latencies.add((System.nanoTime() - callStart) / 1_000);
                    } finally {
                        TransactionSynchronizationManager.unbindResource(emf);
                        EntityManagerFactoryUtils.closeEntityManager(em);
                    }
                    return null;
                }));
            }
            for (Future<?> call : calls) {
                call.get(120, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
        List<Long> result = new ArrayList<>(latencies);
        Collections.sort(result);
        result.add((System.nanoTime() - start[0]) / 1_000);
        return result;
    }

    protected void report(String scenario, int threads, List<Long> result) {
        long wallMicros = result.remove(result.size() - 1);
        System.out.printf("PERF|%s|threads=%d|p50=%.1fms|p95=%.1fms|max=%.1fms|wall=%.1fms%n",
                scenario, threads, percentile(result, 50) / 1000.0, percentile(result, 95) / 1000.0,
                result.get(result.size() - 1) / 1000.0, wallMicros / 1000.0);
    }

    protected long percentile(List<Long> sorted, int percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(index, 0));
    }

    protected Long createInventorySku(int index) {
        Product product = addTestProduct("Inventory Perf Product " + index, "Inventory Perf");
        Sku sku = product.getDefaultSku();
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(STOCK);
        return catalogService.saveSku(sku).getId();
    }
}
