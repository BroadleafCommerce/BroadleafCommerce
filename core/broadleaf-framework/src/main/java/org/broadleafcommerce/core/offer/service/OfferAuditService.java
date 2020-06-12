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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;
import java.util.List;


/**
 * Service for managing {@link OfferAudit}s. An {@link OfferAudit} is used to track usage of an offer and offer code
 * for a particular {@link Order} and {@link Customer}. This provides easy and fast tracking of verifying max uses on
 * particular {@link Offer}s.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface OfferAuditService {

    OfferAudit readAuditById(Long offerAuditId);
    
    /**
     * Persists an audit record to the database
     */
    OfferAudit save(OfferAudit offerAudit);
    
    void delete(OfferAudit offerAudit);

    /**
     * Creates a new offer audit
     */
    OfferAudit create();

    /**
     * Counts how many times the an offer has been used by a customer. 
     * This method will take into account if the Offer has already been 
     * applied to the Order so as not to prevent the Offer from applying 
     * to new items added to the Order by a CRS.
     *
     * @param order
     * @param customerId
     * @param offerId
     * @return number of times and offer has been used by a customer
     */
    Long countUsesByCustomer(Order order, Long customerId, Long offerId);

    /**
     * Counts how many times the an offer has been used by a customer
     *
     * @param customerId
     * @param offerId
     * @return number of times and offer has been used by a customer
     * @deprecated use {@link #countUsesByCustomer(Order, Long, Long)}
     */
    Long countUsesByCustomer(Long customerId, Long offerId);

    /**
     * Counts how many times the given offer code has been used in the system. 
     * This method will take into account if the OfferCode has already been 
     * applied to the Order so as not to prevent the OfferCODE from applying 
     * to new items added to the Order by a CRS.
     *
     * @param order
     * @param offerCodeId
     * @return number of times the offer code has been used
     */
    Long countOfferCodeUses(Order order, Long offerCodeId);

    /**
     * Counts how many times the given offer code has been used in the system
     *
     * @param offerCodeId
     * @return number of times the offer code has been used
     * @deprecated use {@link #countOfferCodeUses(Order, Long)}
     */
    @Deprecated
    Long countOfferCodeUses(Long offerCodeId);

    /**
     * Read all audits by order id
     * @param orderId
     * @return
     */
    List<OfferAudit> readOfferAuditsByOrderId(Long orderId);

}
