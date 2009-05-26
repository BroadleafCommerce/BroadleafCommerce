package org.broadleafcommerce.order.service.type;

public class OrderItemType {

    public static OrderItemType DISCRETE  = new OrderItemType("org.broadleafcommerce.order.domain.DiscreteOrderItem");
    public static OrderItemType BUNDLE = new OrderItemType("org.broadleafcommerce.order.domain.BundleOrderItem");
    public static OrderItemType GIFTWRAP = new OrderItemType("org.broadleafcommerce.order.domain.GiftWrapOrderItem");

    private final String className;

    protected OrderItemType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}