/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.order.security;

import org.broadleafcommerce.common.security.MergeCartProcessor;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.security.CustomerStateFilter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("blMergeCartProcessor")
public class MergeCartProcessorImpl implements MergeCartProcessor {

    private String mergeCartResponseKey = "bl_merge_cart_response";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    @Resource(name="blOrderService")
    private OrderService orderService;

    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer loggedInCustomer = customerService.readCustomerByUsername(authResult.getName());
        Customer anonymousCustomer = (Customer) request.getSession(true).getAttribute(CustomerStateFilter.ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME);
        
        Order cart = null;
        if (anonymousCustomer != null) {
            cart = orderService.findCartForCustomer(anonymousCustomer);
        }
        MergeCartResponse mergeCartResponse;
        try {
            mergeCartResponse = orderService.mergeCart(loggedInCustomer, cart);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }

        request.getSession().setAttribute(mergeCartResponseKey, mergeCartResponse);
    }

    public String getMergeCartResponseKey() {
        return mergeCartResponseKey;
    }

    public void setMergeCartResponseKey(String mergeCartResponseKey) {
        this.mergeCartResponseKey = mergeCartResponseKey;
    }

}
