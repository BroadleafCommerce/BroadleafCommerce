package org.broadleafcommerce.common.event;

import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

import java.util.Collections;
import java.util.Map;

public class ItemsReturnedEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected final Map<Long, Integer> itemsAndQuantitiesReturned;

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems,
            boolean asynchronous, ErrorHandler errorHandler) {
        super(orderId, asynchronous, errorHandler);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems,
            boolean asynchronous) {
        super(orderId, asynchronous);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, ErrorHandler errorHandler,
            Map<Long, Integer> returnedItems) {
        super(orderId, errorHandler);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems) {
        super(orderId);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public Long getOrderId() {
        return (Long) super.getSource();
    }

    public Map<Long, Integer> getItemsAndQuantitiesReturned() {
        return itemsAndQuantitiesReturned;
    }

}
