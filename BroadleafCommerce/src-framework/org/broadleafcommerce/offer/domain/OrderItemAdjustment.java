package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.OrderItem;

public interface OrderItemAdjustment extends Adjustment {

    public OrderItem getOrderItem();

//    public void setOrderItem(OrderItem orderItem);

    public void computeAdjustmentValue();

}
