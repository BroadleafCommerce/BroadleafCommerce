/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;

import junit.framework.TestCase;

public class OrderServiceImplTest extends TestCase {

    public void testAddItemClearsPriceOverridesOnWholeRequestTree() throws Exception {
        OrderItemRequestDTO grandChild = createRequestWithOverrides();
        OrderItemRequestDTO child = createRequestWithOverrides();
        child.getChildOrderItems().add(grandChild);
        OrderItemRequestDTO parent = createRequestWithOverrides();
        parent.getChildOrderItems().add(child);

        CapturingOrderService orderService = new CapturingOrderService();
        orderService.addItem(1L, parent, false);

        assertSame(parent, orderService.capturedRequest);
        assertNoOverrides(parent);
        assertNoOverrides(child);
        assertNoOverrides(grandChild);
    }

    public void testAddItemToleratesNullChildOrderItems() throws Exception {
        OrderItemRequestDTO parent = createRequestWithOverrides();
        parent.setChildOrderItems(null);

        CapturingOrderService orderService = new CapturingOrderService();
        orderService.addItem(1L, parent, false);

        assertNoOverrides(parent);
    }

    protected OrderItemRequestDTO createRequestWithOverrides() {
        OrderItemRequestDTO request = new OrderItemRequestDTO();
        request.setOverrideSalePrice(new Money("0.01"));
        request.setOverrideRetailPrice(new Money("0.01"));
        return request;
    }

    protected void assertNoOverrides(OrderItemRequestDTO request) {
        assertNull(request.getOverrideSalePrice());
        assertNull(request.getOverrideRetailPrice());
    }

    /**
     * Captures the request handed to addItemWithPriceOverrides instead of running the add to cart workflow.
     */
    protected static class CapturingOrderService extends OrderServiceImpl {

        protected OrderItemRequestDTO capturedRequest;

        @Override
        public Order addItemWithPriceOverrides(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) {
            capturedRequest = orderItemRequestDTO;
            return null;
        }
    }

}
