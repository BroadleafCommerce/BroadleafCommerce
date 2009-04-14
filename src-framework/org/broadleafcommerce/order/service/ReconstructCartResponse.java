package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;

public class ReconstructCartResponse {

    private Order order;

    private List<OrderItem> removedItems = new ArrayList<OrderItem>();

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getRemovedItems() {
        return removedItems;
    }

    public void setRemovedItems(List<OrderItem> removedItems) {
        this.removedItems = removedItems;
    }
}
