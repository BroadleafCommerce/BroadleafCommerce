package org.broadleafcommerce.common.event;

import org.springframework.util.ErrorHandler;

/**
 * Concrete event that is raised when an order is submitted.
 * 
 * @author Kelly Tisdell
 *
 */
public class OrderSubmittedEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected final String orderNumber;

    public OrderSubmittedEvent(Long orderId, String orderNumber, boolean asynchronous, ErrorHandler errorHandler) {
        super(orderId, asynchronous, errorHandler);
        this.orderNumber = orderNumber;
    }

    public OrderSubmittedEvent(Long orderId, String orderNumber, boolean asynchronous) {
        super(orderId, asynchronous);
        this.orderNumber = orderNumber;
    }

    public OrderSubmittedEvent(Long orderId, String orderNumber, ErrorHandler errorHandler) {
        super(orderId, errorHandler);
        this.orderNumber = orderNumber;
    }

    public OrderSubmittedEvent(Long orderId, String orderNumber) {
        super(orderId);
        this.orderNumber = orderNumber;
    }

    public Long getOrderId() {
        return (Long) super.getSource();
    }

    public String getOrderNumber() {
        return (String) orderNumber;
    }
}
