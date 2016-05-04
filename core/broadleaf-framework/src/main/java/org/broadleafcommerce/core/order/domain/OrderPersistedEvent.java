/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;


/**
 * An event for whenever an {@link OrderImpl} has been persisted
 *
 * @author Phillip Verheyden (phillipuniverse)
 * 
 * @see {@link OrderPersistedEntityListener}
 */
public class OrderPersistedEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * @param order the newly persisted customer
     */
    public OrderPersistedEvent(Order order) {
        super(order);
    }
    
    /**
     * Gets the newly-persisted {@link Order} set by the {@link OrderPersistedEntityListener}
     * 
     * @return
     */
    public Order getOrder() {
        return (Order)source;
    }

}
