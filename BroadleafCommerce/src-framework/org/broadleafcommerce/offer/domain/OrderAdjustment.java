package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.Order;

public interface OrderAdjustment extends Adjustment {

    public Order getOrder();

//    public void setOrder(Order order);

    public void computeAdjustmentValue();

}
