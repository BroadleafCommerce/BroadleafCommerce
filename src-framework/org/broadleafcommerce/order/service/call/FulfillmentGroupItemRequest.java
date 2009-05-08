package org.broadleafcommerce.order.service.call;

import org.broadleafcommerce.order.domain.OrderItem;

public class FulfillmentGroupItemRequest {

    protected OrderItem orderItem;
    protected int quantity;

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
