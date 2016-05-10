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

import org.broadleafcommerce.core.offer.domain.Offer;

import java.io.Serializable;

public interface OrderItemQualifier extends Serializable {
    
    /**
     * Unique id of the item qualifier.
     * @return
     */
    Long getId();

    /**
     * Sets the id for this OrderItemQualifier
     * @param id
     */
    void setId(Long id);

    /**
     * The related order item.
     * @return
     */
    OrderItem getOrderItem();

    /**
     * Sets the related order item.
     * @param orderItem
     */
    void setOrderItem(OrderItem orderItem);

    /**
     * Sets the related offer.
     * @param offer
     */
    void setOffer(Offer offer);

    /**
     * Returns the related offer
     * @return
     */
    Offer getOffer();

    /**
     * Sets the quantity of the associated OrderItem that was used as a qualifier.
     * @param quantity
     */
    void setQuantity(Long quantity);

    /**
     * Returns the quantity of the associated OrderItem that was used as a qualifier.
     * @return
     */
    Long getQuantity();
}
