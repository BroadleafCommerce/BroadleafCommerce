package org.broadleafcommerce.order.web.model;

public class CartOrderItem {
    private long orderItemId;
    private long addressId;
    private int quantity;

    public long getAddressId() {
        return addressId;
    }
    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }
    public long getOrderItemId() {
        return orderItemId;
    }
    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
