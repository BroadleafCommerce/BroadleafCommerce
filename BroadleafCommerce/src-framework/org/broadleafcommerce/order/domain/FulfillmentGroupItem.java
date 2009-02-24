package org.broadleafcommerce.order.domain;

public interface FulfillmentGroupItem {

    public Long getId();

    public void setId(Long id);

    public Long getFulfillmentGroupId();

    public void setFulfillmentGroupId(Long fulfillmentGroupId);

    public OrderItem getOrderItem();

    public void setOrderItem(OrderItem orderItem);

    public int getQuantity();

    public void setQuantity(int quantity);
}
