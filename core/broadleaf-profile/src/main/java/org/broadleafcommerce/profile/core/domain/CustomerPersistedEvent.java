/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.domain;

import org.springframework.context.ApplicationEvent;


/**
 * An event for whenever a {@link CustomerImpl} has been persisted
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link CustomerPersistedEntityListener}
 */
public class CustomerPersistedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * @param customer the newly persisted customer
     */
    public CustomerPersistedEvent(Customer customer) {
        super(customer);
    }
    
    /**
     * Gets the newly-persisted {@link Customer} set by the {@link CustomerPersistedEntityListener}
     * 
     * @return
     */
    public Customer getCustomer() {
        return (Customer)source;
    }

}
