/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service.workflow;

import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;

import java.util.ArrayList;
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
        setItemRequest(itemRequest);
        setPriceOrder(priceOrder);
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
