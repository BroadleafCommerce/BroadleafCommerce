package org.broadleafcommerce.order.service;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.util.money.Money;
import org.springframework.stereotype.Service;

@Service("orderItemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Resource
    private OrderItemDao orderItemDao;

    @Override
    public OrderItem readOrderItemById(Long orderItemId) {
        return orderItemDao.readOrderItemById(orderItemId);
    }

    @Override
    public DiscreteOrderItem createDiscreteOrderItem(DiscreteOrderItemRequest itemRequest) {
        DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        item.setSku(itemRequest.getSku());
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setProduct(itemRequest.getProduct());
        item.setPrice(itemRequest.getSku().getSalePrice());

        return item;
    }

    @Override
    public BundleOrderItem createBundleOrderItem(BundleOrderItemRequest itemRequest) {
        BundleOrderItem item = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setName(itemRequest.getName());
        item.setPrice(new Money(0D));

        for (DiscreteOrderItemRequest discreteItemRequest : itemRequest.getDiscreteOrderItems()) {
            DiscreteOrderItem discreteOrderItem = createDiscreteOrderItem(discreteItemRequest);
            discreteOrderItem.setBundleOrderItem(item);
            item.getDiscreteOrderItems().add(discreteOrderItem);
            item.setPrice(item.getPrice().add(discreteOrderItem.getPrice()));
        }

        return item;
    }

    @Override
    public void delete(OrderItem item) {
        orderItemDao.delete(item);
    }
}
