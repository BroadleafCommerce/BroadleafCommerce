/*-
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestCustomerResolverImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Convenient class to get the active customer from the current request. This state is kept up-to-date in regards to the database
 * throughout the lifetime of the request via the {@link CustomerStateRefresher}.
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCustomerState")
public class CustomerState {
    
    public static Customer getCustomer(HttpServletRequest request) {
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer(request);
    }
    
    public static Customer getCustomer(WebRequest request) {
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer(request);
    }
    
    public static Customer getCustomer() {
        if (BroadleafRequestContext.getBroadleafRequestContext() == null
                || BroadleafRequestContext.getBroadleafRequestContext().getWebRequest() == null) {
            return null;
        }
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer();
    }
    
    public static void setCustomer(Customer customer) {
        BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().setCustomer(customer);
    }

}
