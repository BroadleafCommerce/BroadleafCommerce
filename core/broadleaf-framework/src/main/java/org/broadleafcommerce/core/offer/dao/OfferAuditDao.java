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
package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.workflow.RecordOfferUsageActivity;
import org.broadleafcommerce.core.offer.service.workflow.VerifyCustomerMaxOfferUsesActivity;
import org.broadleafcommerce.core.order.domain.Order;

import java.util.List;

/**
 * DAO for auditing what went on with offers being added to an order
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link VerifyCustomerMaxOfferUsesActivity}, {@link RecordOfferUsageActivity},
 * {@link OfferService#verifyMaxCustomerUsageThreshold(Order, OfferCode)}, 
 * {@link OfferService#verifyMaxCustomerUsageThreshold(Order, OfferCode)}
 */
public interface OfferAuditDao {
    
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
     * Return all offer audits for a particular order
     * @param orderId
     * @return
     */
    List<OfferAudit> readOfferAuditsByOrderId(Long orderId);

}
