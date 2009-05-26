package org.broadleafcommerce.order.service.type;

/**
 * Rather than create an Java enum type here, it is better to set up a more
 * "manual" enumeration class that can be extended. As a result, implementors
 * may add additional values in an extension that the framework can still
 * use.
 * 
 * @author jfischer
 */
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