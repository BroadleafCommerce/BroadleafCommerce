package org.broadleafcommerce.order.web.model;

import org.broadleafcommerce.order.domain.OrderItem;

public class CartOrderItem {
    private OrderItem orderItem;
    private long addressId;
    private int quantity;

    public long getAddressId() {
        return addressId;
    }
    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public OrderItem getOrderItem() {
        return orderItem;
    }
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
