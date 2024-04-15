package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SomeUtil {

    public static Collection someMethod(FulfillmentGroup fulfillmentGroup, String compareWith) {
        ArrayList<String> test = new ArrayList<>();
        final Order order = fulfillmentGroup.getOrder();
        for (DiscreteOrderItem orderItem : order.getDiscreteOrderItems()) {
            if (orderItem.getSku() != null && orderItem.getSku().isActive() &&
                compareWith.contains(orderItem.getSku().getName())) {
                return Collections.singletonList("");
            }
        }
        return Collections.EMPTY_LIST;
    }
}
