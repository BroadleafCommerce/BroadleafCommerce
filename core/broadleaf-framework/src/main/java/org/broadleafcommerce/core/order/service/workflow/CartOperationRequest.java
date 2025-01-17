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
package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents the basic context necessary for the execution
 * of a particular order process workflow operation.
 *
 * @author apazzolini
 */
public class CartOperationRequest {

    protected OrderItemRequestDTO itemRequest;

    protected Order order;

    protected boolean priceOrder;

    // Set during the course of the workflow for use in subsequent workflow steps
    protected OrderItem orderItem;

    // Set during the course of the workflow for use in subsequent workflow steps
    protected Integer orderItemQuantityDelta;

    protected List<Long[]> multishipOptionsToDelete = new ArrayList<Long[]>();
    protected List<FulfillmentGroupItem> fgisToDelete = new ArrayList<FulfillmentGroupItem>();
    protected List<OrderItem> oisToDelete = new ArrayList<OrderItem>();

    public CartOperationRequest(Order order, OrderItemRequestDTO itemRequest, boolean priceOrder) {
        setOrder(order);
        sortAllDescendantChildItems(itemRequest);
        setItemRequest(itemRequest);
        setPriceOrder(priceOrder);
    }

    protected void sortAllDescendantChildItems(final OrderItemRequestDTO orderItemRequestDTO) {
        if (CollectionUtils.isEmpty(orderItemRequestDTO.getChildOrderItems())) {
            return;
        }

        Collections.sort(orderItemRequestDTO.getChildOrderItems(), new Comparator<OrderItemRequestDTO>() {
            @Override
            public int compare(OrderItemRequestDTO o1, OrderItemRequestDTO o2) {
                BigDecimal o1DisplayOrder = new BigDecimal(o1.getAdditionalAttributes().get("addOnDisplayOrder"));
                BigDecimal o2DisplayOrder = new BigDecimal(o2.getAdditionalAttributes().get("addOnDisplayOrder"));
                return o1DisplayOrder.compareTo(o2DisplayOrder);
            }
        });

        for (final OrderItemRequestDTO childOrderItem : orderItemRequestDTO.getChildOrderItems()) {
            sortAllDescendantChildItems(childOrderItem);
        }
    }

    public OrderItemRequestDTO getItemRequest() {
        return itemRequest;
    }

    public void setItemRequest(OrderItemRequestDTO itemRequest) {
        this.itemRequest = itemRequest;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isPriceOrder() {
        return priceOrder;
    }

    public void setPriceOrder(boolean priceOrder) {
        this.priceOrder = priceOrder;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    /**
     * @deprecated in favor of {@link #getOrderItem()}. Keeping this method for backwards compatibility
     */
    public OrderItem getAddedOrderItem() {
        return orderItem;
    }

    /**
     * @deprecated in favor of {@link #setOrderItem(OrderItem)}. Keeping this method for backwards compatibility
     */
    public void setAddedOrderItem(OrderItem addedOrderItem) {
        this.orderItem = addedOrderItem;
    }

    public Integer getOrderItemQuantityDelta() {
        return orderItemQuantityDelta;
    }

    public void setOrderItemQuantityDelta(Integer orderItemQuantityDelta) {
        this.orderItemQuantityDelta = orderItemQuantityDelta;
    }

    public List<Long[]> getMultishipOptionsToDelete() {
        return multishipOptionsToDelete;
    }

    public void setMultishipOptionsToDelete(List<Long[]> multishipOptionsToDelete) {
        this.multishipOptionsToDelete = multishipOptionsToDelete;
    }

    public List<FulfillmentGroupItem> getFgisToDelete() {
        return fgisToDelete;
    }

    public void setFgisToDelete(List<FulfillmentGroupItem> fgisToDelete) {
        this.fgisToDelete = fgisToDelete;
    }

    public List<OrderItem> getOisToDelete() {
        return oisToDelete;
    }

    public void setOisToDelete(List<OrderItem> oisToDelete) {
        this.oisToDelete = oisToDelete;
    }

}
