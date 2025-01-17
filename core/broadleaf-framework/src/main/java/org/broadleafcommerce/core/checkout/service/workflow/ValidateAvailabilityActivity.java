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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

/**
 * This will check the availability and quantities (if applicable) all order items in checkout request.
 * Very similar to the {@link CheckUpdateAvailabilityActivity} but in the blCheckoutWorkflow instead.
 * This should prevent succeed checkout, in case, when Sku became unavailable in range
 * after it was adding to the cart and before completing the order.
 */
@Component("blValidateAvailabilityActivity")
public class ValidateAvailabilityActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    public static final int ORDER = 750;
    private static final Log LOG = LogFactory.getLog(ValidateAvailabilityActivity.class);

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;

    public ValidateAvailabilityActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        if (order == null) {
            return context;
        }

        Map<Sku, Integer> skuItems = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku;
            if (orderItem instanceof DiscreteOrderItem) {
                sku = ((DiscreteOrderItem) orderItem).getSku();
            } else if (orderItem instanceof BundleOrderItem) {
                sku = ((BundleOrderItem) orderItem).getSku();
            } else {
                LOG.warn("Could not check availability; did not recognize passed-in item " + orderItem.getClass().getName());
                return context;
            }
            if (!sku.isActive()) {
                throw new IllegalArgumentException("The requested skuId (" + sku.getId() + ") is no longer active");
            }
            skuItems.merge(sku, orderItem.getQuantity(), (oldVal, newVal) -> oldVal + newVal);
        }
        for (Map.Entry<Sku, Integer> entry : skuItems.entrySet()) {
            inventoryService.checkSkuAvailability(order, entry.getKey(), entry.getValue());
        }

        return context;
    }

}
