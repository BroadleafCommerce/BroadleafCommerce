package org.broadleafcommerce.order.service;

import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.GiftWrapOrderItemRequest;

public interface OrderItemService {

    public DiscreteOrderItem createDiscreteOrderItem(DiscreteOrderItemRequest itemRequest);

    public GiftWrapOrderItem createGiftWrapOrderItem(GiftWrapOrderItemRequest itemRequest);

    public BundleOrderItem createBundleOrderItem(BundleOrderItemRequest itemRequest);

    public OrderItem readOrderItemById(Long orderItemId);

    public void delete(OrderItem item);

}
