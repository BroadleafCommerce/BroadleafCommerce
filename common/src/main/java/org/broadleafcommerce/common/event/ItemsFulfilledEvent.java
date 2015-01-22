package org.broadleafcommerce.common.event;

import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

import java.util.Collections;
import java.util.Map;

/**
 * Event that may be raised to indicate that items have been fulfilled.
 * 
 * @author Kelly Tisdell
 *
 */
public class ItemsFulfilledEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected final Map<Long, Integer> itemsAndQuantitiesFulfilled;

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled,
            boolean asynchronous, ErrorHandler errorHandler) {
        super(fulfillmentGroupId, asynchronous, errorHandler);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled,
            boolean asynchronous) {
        super(fulfillmentGroupId, asynchronous);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, ErrorHandler errorHandler,
            Map<Long, Integer> fulfilled) {
        super(fulfillmentGroupId, errorHandler);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled) {
        super(fulfillmentGroupId);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public Long getFulfillmentGroupId() {
        return (Long) super.getSource();
    }

    public Map<Long, Integer> getItemsAndQuantitiesFulfilled() {
        return itemsAndQuantitiesFulfilled;
    }
}
