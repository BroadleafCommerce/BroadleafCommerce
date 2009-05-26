package org.broadleafcommerce.order.service.type;

public class OrderStatus {

    public static OrderStatus NAMED = new OrderStatus("NAMED");
    public static OrderStatus IN_PROCESS = new OrderStatus("IN_PROCESS");
    public static OrderStatus SUBMITTED = new OrderStatus("SUBMITTED");

    private final String name;

    protected OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

}