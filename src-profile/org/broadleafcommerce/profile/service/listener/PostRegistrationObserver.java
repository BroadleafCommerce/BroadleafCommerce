package org.broadleafcommerce.profile.service.listener;

import org.broadleafcommerce.profile.domain.Customer;

public interface PostRegistrationObserver {

    public void processRegistrationEvent(Customer customer);
}
