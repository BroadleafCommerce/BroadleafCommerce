/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order.security;

import org.broadleafcommerce.common.security.MergeCartProcessor;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.MergeCartService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @deprecated this has been replaced by invoking {@link MergeCartService} explicitly within the
 * {@link CartStateRequestProcessor}
 */
@Deprecated
@Component("blMergeCartProcessor")
public class MergeCartProcessorImpl implements MergeCartProcessor {

    protected String mergeCartResponseKey = "bl_merge_cart_response";

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Resource(name="blOrderService")
    protected OrderService orderService;
    
    @Resource(name="blMergeCartService")
    protected MergeCartService mergeCartService;
    
    @Resource(name = "blCustomerStateRequestProcessor")
    protected CustomerStateRequestProcessor customerStateRequestProcessor;
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        execute(new ServletWebRequest(request, response), authResult);
    }
    
    @Override
    public void execute(WebRequest request, Authentication authResult) {
        Customer loggedInCustomer = customerService.readCustomerByUsername(authResult.getName());
        Customer anonymousCustomer = customerStateRequestProcessor.getAnonymousCustomer(request);
        
        Order cart = null;
        if (anonymousCustomer != null) {
            cart = orderService.findCartForCustomer(anonymousCustomer);
        }
        MergeCartResponse mergeCartResponse;
        try {
            mergeCartResponse = mergeCartService.mergeCart(loggedInCustomer, cart);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        } catch (RemoveFromCartException e) {
            throw new RuntimeException(e);
        }

        if (BLCRequestUtils.isOKtoUseSession(request)) {
            request.setAttribute(mergeCartResponseKey, mergeCartResponse, WebRequest.SCOPE_SESSION);
        }
    }

    public String getMergeCartResponseKey() {
        return mergeCartResponseKey;
    }

    public void setMergeCartResponseKey(String mergeCartResponseKey) {
        this.mergeCartResponseKey = mergeCartResponseKey;
    }

}
