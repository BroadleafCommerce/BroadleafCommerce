package org.broadleafcommerce.order.domain;

import java.util.List;


public interface BundleOrderItem extends OrderItem {

    public String getName();

    public void setName(String name);

    public List<DiscreteOrderItem> getDiscreteOrderItems();

    public void setDiscreteOrderItems(List<DiscreteOrderItem> discreteOrderItems);

}
