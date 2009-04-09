package org.broadleafcommerce.order.dao;

import org.broadleafcommerce.order.domain.OrderItem;

public interface OrderItemDao {

    public OrderItem readOrderItemById(Long orderItemId);

    public OrderItem maintainOrderItem(OrderItem orderItem);

    public void deleteOrderItem(OrderItem orderItem);

    public OrderItem create();
}
