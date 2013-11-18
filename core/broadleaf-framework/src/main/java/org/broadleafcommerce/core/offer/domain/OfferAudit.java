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
package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Captures when an offer was applied to a customer.
 *
 * Utilized by the offer process to enforce max use by customer rules and as
 * a high-level audit of what orders and customers have used an offer.
 *
 */
public interface OfferAudit extends Serializable {

    /**
     * System generated unique id for this audit record.
     * @return
     */
    public Long getId();

    /**
     * Sets the id.
     * @param id
     */
    public void setId(Long id);

    /**
     * The associated offer id.
     * @return
     */
    public Long getOfferId();

    /**
     * Sets the associated offer id.
     * @param offerId
     */
    public void setOfferId(Long offerId);

    /**
     * The associated order id.
     * @return
     */
    public Long getOrderId();

    /**
     * Sets the associated order id.
     * @param orderId
     */
    public void setOrderId(Long orderId);

    /**
     * The id of the associated customer.
     * @return
     */
    public Long getCustomerId();

    /**
     * Sets the customer id.
     * @param customerId
     */
    public void setCustomerId(Long customerId);

    /**
     * The date the offer was applied to the order.
     * @return
     */
    public Date getRedeemedDate();

    /**
     * Sets the offer redeemed date.
     * @param redeemedDate
     */
    public void setRedeemedDate(Date redeemedDate);
}
