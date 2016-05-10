/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.security;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.web.context.request.WebRequest;

public interface CustomerMergeExtensionHandler extends ExtensionHandler {

    /**
     * This allows multiple extensions to modify or copy attributes from the anonymous customer, to the 
     * customer.  The customer is stored on the customerHolder.  If the customer is modified and / or saved to the DB, 
     * the new instance must be reset on the customerHolder.  Each implementation can expect that the result holder will 
     * have the latest version of the customer.  Implementors are not required to save the customer.  
     * It is the responsibility of each implementation to reset the reference.
     * Additionally and alternatively, this method allows for copying and / or modification of the request 
     * attributes from session or request.  Implementors should carefully consider security implications of 
     * copying customer data.  
     * 
     * The anonymous customer may be null.  If the request parameter, the customerHolder parameter, or the customer stored 
     * on the customerHolder is null, then an IllegalArgumentException should be thrown.
     * 
     * The return value, generally, should be ExtensionResultStatusType.HANDLED_CONTINUE.  If an implementation wishes 
     * to return an exception in the customerHolder, it should instantiate and set an exception on the customerHolder 
     * and return ExtensionResultStatusType.HANDLED_STOP.
     * 
     * @param customerHolder
     * @param request
     * @param anonymousCustomer
     * @return
     */
    public ExtensionResultStatusType merge(ExtensionResultHolder<Customer> customerHolder, WebRequest request, Customer anonymousCustomer);

}
