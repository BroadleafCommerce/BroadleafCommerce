package org.broadleafcommerce.profile.core.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;

/**
 * @author Nick Crum ncrum
 */
public class RegisterCustomerEvent extends BroadleafApplicationEvent {

    protected Long customerId;

    public RegisterCustomerEvent(Object source, Long customerId) {
        super(source);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
