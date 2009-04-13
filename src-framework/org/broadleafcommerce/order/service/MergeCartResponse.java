package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;

public class MergeCartResponse {

    private Order order;

    private List<OrderItem> addedItems;

    private List<OrderItem> removedItems;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getAddedItems() {
        return addedItems;
    }

    public void setAddedItems(List<OrderItem> addedItems) {
        this.addedItems = addedItems;
    }

    public List<OrderItem> getRemovedItems() {
        return removedItems;
    }

    public void setRemovedItems(List<OrderItem> removedItems) {
        this.removedItems = removedItems;
    }
}
