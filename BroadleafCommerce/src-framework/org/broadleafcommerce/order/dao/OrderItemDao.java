package org.broadleafcommerce.order.dao;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.OrderItemType;

public interface OrderItemDao {

    public OrderItem readOrderItemById(Long orderItemId);

    public OrderItem save(OrderItem orderItem);

    public void delete(OrderItem orderItem);

    public OrderItem create(OrderItemType orderItemType);

    public OrderItem cloneOrderItem(OrderItem orderItem, OrderItemType orderItemType);

}
