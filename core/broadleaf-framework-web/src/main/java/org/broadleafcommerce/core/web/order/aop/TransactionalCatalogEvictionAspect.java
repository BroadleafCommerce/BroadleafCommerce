/*-
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Since we cannot modify the collections or bypass the L2 cache without breaking Broadleaf's
 * architecture, we introduce a targeted AOP Aspect to seamlessly hide these entities from
 * Hibernate 7's aggressive FlushVisitor while preserving the Open Entity Manager In
 * View (OSIV) pattern for the front-end.
 *
 */
@Aspect
@Component
public class TransactionalCatalogEvictionAspect {

    protected static final Log LOG = LogFactory.getLog(TransactionalCatalogEvictionAspect.class);


    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Around("execution(* org.broadleafcommerce.core.order.service.OrderService.updateItemQuantity(..)) ")
    public Object evictAndResetCartSkus(ProceedingJoinPoint pjp) throws Throwable {

        // Run  cart logic
        Object result = pjp.proceed();

        if (result instanceof Order order && TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    try {
                        // Collect all Catalog entities from the session
                        Session session = em.unwrap(Session.class);
                        SessionImplementor sessionImpl = session.unwrap(SessionImplementor.class);
                        List<Object> entitiesToDetach = getEntitiesToDetach(sessionImpl);

                        // Detach them completely to bypass the Hibernate 7 FlushVisitor aggressive
                        // behavior that causes false positives on 'share collection' errors.
                        for (Object entity : entitiesToDetach) {
                            em.detach(entity);
                        }

                        // Because we detached the previous instances to prevent database flush errors,
                        // clearing these fields forces the Open Entity Manager In View pattern to fetch fresh,
                        // fully attached entities from the database so the view template can render properly.
                        if (order.getOrderItems() != null) {
                            for (OrderItem item : order.getOrderItems()) {
                                if (item instanceof DiscreteOrderItem) {
                                    // Clear deproxiedSku
                                    Field skuField = ReflectionUtils.findField(item.getClass(), "deproxiedSku");
                                    if (skuField != null) {
                                        ReflectionUtils.makeAccessible(skuField);
                                        ReflectionUtils.setField(skuField, item, null);
                                    }

                                    // Clear deproxiedProduct
                                    Field prodField = ReflectionUtils.findField(item.getClass(), "deproxiedProduct");
                                    if (prodField != null) {
                                        ReflectionUtils.makeAccessible(prodField);
                                        ReflectionUtils.setField(prodField, item, null);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Emit warning and fail silently so the cart commit proceeds
                        LOG.warn("Failed to evict and reset SKUs.", e);
                    }
                }
            });
        }

        return result;
    }

    private static @NonNull List<Object> getEntitiesToDetach(SessionImplementor sessionImpl) {
        org.hibernate.engine.spi.PersistenceContext pc = sessionImpl.getPersistenceContext();

        List<Object> entitiesToDetach = new ArrayList<>();
        Map.Entry<Object, org.hibernate.engine.spi.EntityEntry>[] entries = pc.reentrantSafeEntityEntries();

        for (Map.Entry<Object, org.hibernate.engine.spi.EntityEntry> entry : entries) {
            Object entity = entry.getKey();
            if (entity instanceof Sku || entity instanceof Product || entity instanceof Category) {
                entitiesToDetach.add(entity);
            }
        }
        return entitiesToDetach;
    }
}