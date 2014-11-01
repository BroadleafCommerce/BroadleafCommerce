/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
