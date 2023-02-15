/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

/**
 * The Interface OrderAttribute.   Allows for arbitrary data to be
 * persisted with the order.
 *
 */
public interface OrderAttribute extends Serializable, MultiTenantCloneable<OrderAttribute> {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    Long getId();

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    void setId(Long id);

    /**
     * Gets the value.
     * 
     * @return the value
     */
    String getValue();

    /**
     * Sets the value.
     * 
     * @param value the new value
     */
    void setValue(String value);   

    /**
     * Gets the associated order.
     * 
     * @return the order
     */
    Order getOrder();

    /**
     * Sets the order.
     * 
     * @param order the associated order
     */
    void setOrder(Order order);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    void setName(String name);
}
