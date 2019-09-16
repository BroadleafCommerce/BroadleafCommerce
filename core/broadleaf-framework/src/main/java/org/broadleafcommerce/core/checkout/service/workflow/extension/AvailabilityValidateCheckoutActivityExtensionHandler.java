/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.core.checkout.service.workflow.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * This will check the availability and quantities (if applicable) all order items in checkout request.
 * Very similar to the {@link CheckUpdateAvailabilityActivity} but in the blCheckoutWorkflow instead.
 * This should prevent succeed checkout, in case, when Sku became unavailable in range
 * after it was adding to the cart and before completing the order.
 */
@Service("blAvailabilityValidateCheckoutActivityExtensionHandler")
public class AvailabilityValidateCheckoutActivityExtensionHandler extends AbstractValidateCheckoutActivityExtensionHandler
        implements ValidateCheckoutActivityExtensionHandler {

    private static final Log LOG = LogFactory.getLog(AvailabilityValidateCheckoutActivityExtensionHandler.class);

    @Resource(name = "blValidateCheckoutActivityExtensionManager")
    protected ValidateCheckoutActivityExtensionManager extensionManager;

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;

    @PostConstruct
    public void init() {
        boolean shouldAdd = true;
        for (ValidateCheckoutActivityExtensionHandler h : extensionManager.getHandlers()) {
            if (h instanceof AvailabilityValidateCheckoutActivityExtensionHandler) {
                shouldAdd = false;
                break;
            }
        }
        if (shouldAdd) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType validateCheckout(CheckoutSeed request, ExtensionResultHolder<Exception> resultHolder) {
        Order order = request.getOrder();
        if (order == null) {
            return ExtensionResultStatusType.NOT_HANDLED;
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku;
            if (orderItem instanceof DiscreteOrderItem) {
                sku = ((DiscreteOrderItem) orderItem).getSku();
            } else if (orderItem instanceof BundleOrderItem) {
                sku = ((BundleOrderItem) orderItem).getSku();
            } else {
                LOG.warn("Could not check availability; did not recognize passed-in item " + orderItem.getClass().getName());
                return ExtensionResultStatusType.NOT_HANDLED;
            }

            try {
                Integer requestedQuantity = orderItem.getQuantity();
                checkSkuAvailability(order, sku, requestedQuantity);

                Integer previousQty = orderItem.getQuantity();
                for (OrderItem child : orderItem.getChildOrderItems()) {
                    Sku childSku = ((DiscreteOrderItem) child).getSku();
                    Integer childQuantity = child.getQuantity();
                    childQuantity = childQuantity / previousQty;
                    checkSkuAvailability(order, childSku, childQuantity * requestedQuantity);

                }
            } catch (InventoryUnavailableException ex) {
                resultHolder.setResult(ex);
                return ExtensionResultStatusType.HANDLED_STOP;
            }
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    protected void checkSkuAvailability(Order order, Sku sku, Integer requestedQuantity) throws InventoryUnavailableException {
        // First check if this Sku is available
        if (!sku.isAvailable()) {
            throw new InventoryUnavailableException("The referenced Sku " + sku.getId() + " is marked as unavailable", sku.getId(), requestedQuantity, 0);
        }

        if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
            Map<String, Object> inventoryContext = new HashMap<>();
            inventoryContext.put(ContextualInventoryService.ORDER_KEY, order);
            boolean available = inventoryService.isAvailable(sku, requestedQuantity, inventoryContext);
            if (!available) {
                throw new InventoryUnavailableException(sku.getId(),
                        requestedQuantity, inventoryService.retrieveQuantityAvailable(sku, inventoryContext));
            }
        }

        // the other case here is ALWAYS_AVAILABLE and null, which we are treating as being available
    }
}




