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
package org.broadleafcommerce.core.payment.domain.secure;

import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;

/**
 * Entity associated with sensitive, secured bank account data. This data is stored specifically in the blSecurePU persistence.
 * All fetches and creates should go through {@link SecureOrderPaymentService} in order to properly decrypt/encrypt the data
 * from/to the database.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link Referenced}
 */
public interface BankAccountPayment extends Referenced {

    /**
     * @return the id
     */
    @Override
    Long getId();

    /**
     * @param id the id to set
     */
    @Override
    void setId(Long id);

    /**
     * @return the account number
     */
    String getAccountNumber();

    /**
     * @param accountNumber the account number to set
     */
    void setAccountNumber(String accountNumber);

    /**
     * @return the routing number
     */
    String getRoutingNumber();

    /**
     * @param routingNumber the routing number to set
     */
    void setRoutingNumber(String routingNumber);

}
