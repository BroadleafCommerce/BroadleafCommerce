/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Captures when an offer was applied to a customer.
 * <p>
 * Utilized by the offer process to enforce max use by customer rules and as
 * a high-level audit of what orders and customers have used an offer.
 */
public interface OfferAudit extends Serializable {

    /**
     * System generated unique id for this audit record.
     *
     * @return
     */
    Long getId();

    /**
     * Sets the id.
     *
     * @param id
     */
    void setId(Long id);

    /**
     * The associated offer id.
     *
     * @return
     */
    Long getOfferId();

    /**
     * Sets the associated offer id.
     *
     * @param offerId
     */
    void setOfferId(Long offerId);

    /**
     * <p>The offer code that was used to retrieve the offer. This will be null if the offer was automatically applied
     * and not obtained by an {@link OfferCode}.</p>
     */
    Long getOfferCodeId();

    /**
     * <p>Sets the offer code that was used to retrieve the offer. This should be null if the offer was automatically applied
     * and not obtained by an {@link OfferCode}.</p>
     */
    void setOfferCodeId(Long offerCodeId);

    /**
     * The associated order id.
     *
     * @return
     */
    Long getOrderId();

    /**
     * Sets the associated order id.
     *
     * @param orderId
     */
    void setOrderId(Long orderId);

    /**
     * The id of the associated customer.
     *
     * @return
     */
    Long getCustomerId();

    /**
     * Sets the customer id.
     *
     * @param customerId
     */
    void setCustomerId(Long customerId);

    /**
     * The id of the associated account.
     *
     * @return
     */
    Long getAccountId();

    /**
     * Sets the associated account id.
     *
     * @param customerId
     */
    void setAccountId(Long customerId);

    /**
     * The date the offer was applied to the order.
     *
     * @return
     */
    Date getRedeemedDate();

    /**
     * Sets the offer redeemed date.
     *
     * @param redeemedDate
     */
    void setRedeemedDate(Date redeemedDate);

}
