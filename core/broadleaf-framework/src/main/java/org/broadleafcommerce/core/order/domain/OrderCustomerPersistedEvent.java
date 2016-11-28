package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPersistedEntityListener;
import org.springframework.context.ApplicationEvent;


public class OrderCustomerPersistedEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;

    /**
     * @param customer the newly persisted customer
     */
    public OrderCustomerPersistedEvent(OrderCustomer orderCustomer) {
        super(orderCustomer);
    }
    
    /**
     * Gets the newly-persisted {@link Customer} set by the {@link CustomerPersistedEntityListener}
     * 
     * @return
     */
    public OrderCustomer getOrderCustomer() {
        return (OrderCustomer)source;
    }
}
