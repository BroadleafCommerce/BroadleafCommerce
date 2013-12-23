/*
 * Copyright 2008-2013 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.MergeCartService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.service.UpdateCartService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Ensures that the customer's current cart is available to the request.  
 * 
 * Also invokes blMergeCartProcessor" if the user has just logged in.   
 * 
 * Genericized version of the CartStateFilter. This was made to facilitate reuse between Servlet Filters, Portlet Filters 
 * and Spring MVC interceptors. Spring has an easy way of converting HttpRequests and PortletRequests into WebRequests 
 * via <br />
 * new ServletWebRequest(httpServletRequest); new PortletWebRequest(portletRequest); <br />
 * For the interceptor pattern, you can simply implement a WebRequestInterceptor to invoke from there.
 * 
 * @author Phillip Verheyden
 * @see {@link CartStateFilter}
 * @see {@link BroadleafWebRequestProcessor}
 * @see {@link ServletWebRequest}
 * @see {@link org.springframework.web.portlet.context.PortletWebRequest}
 */
@Component("blCartStateRequestProcessor")
public class CartStateRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    /** Logger for this class and subclasses */
    protected final Log LOG = LogFactory.getLog(getClass());

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    private String mergeCartResponseKey = "bl_merge_cart_response";

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blUpdateCartService")
    protected UpdateCartService updateCartService;

    @Resource(name = "blMergeCartService")
    private MergeCartService mergeCartService;

    @Resource(name = "blCustomerStateRequestProcessor")
    protected CustomerStateRequestProcessor customerStateRequestProcessor;

    protected static String cartRequestAttributeName = "cart";
        
    @Override
    public void process(WebRequest request) {
        Customer customer = (Customer) request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(),
                WebRequest.SCOPE_REQUEST);

        if (customer == null) {
            return;
        }

        Order cart = null;
        if (mergeCartNeeded(customer, request)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Merge cart required, calling mergeCart " + customer.getId());
            }
            cart = mergeCart(customer, request);
        } else {
            cart = orderService.findCartForCustomer(customer);
        }

        if (cart == null) {
            cart = orderService.getNullOrder();
        } else {
            updateCartService.updateAndValidateCart(cart);
        }

        request.setAttribute(cartRequestAttributeName, cart, WebRequest.SCOPE_REQUEST);

        // Setup cart for content rule processing
        Map<String, Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM, WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            ruleMap = new HashMap<String, Object>();
        }
        ruleMap.put("order", cart);

        // Leaving the following line in for backwards compatibility, but all rules should use order as the 
        // variable name.
        ruleMap.put("cart", cart);
        request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap, WebRequest.SCOPE_REQUEST);

    }
    
    public boolean mergeCartNeeded(Customer customer, WebRequest request) {
        Customer anonymousCustomer = customerStateRequestProcessor.resolveAnonymousCustomer(request);
        return (anonymousCustomer != null && customer.getId() != null && !customer.getId().equals(anonymousCustomer.getId()));
    }

    public Order mergeCart(Customer customer, WebRequest request) {
        Customer anonymousCustomer = customerStateRequestProcessor.resolveAnonymousCustomer(request);
        MergeCartResponse mergeCartResponse;
        try {
            Order cart = orderService.findCartForCustomer(anonymousCustomer);
            mergeCartResponse = mergeCartService.mergeCart(customer, cart);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        } catch (RemoveFromCartException e) {
            throw new RuntimeException(e);
        }

        request.setAttribute(mergeCartResponseKey, mergeCartResponse, WebRequest.SCOPE_GLOBAL_SESSION);
        return mergeCartResponse.getOrder();
    }

    public static String getCartRequestAttributeName() {
        return cartRequestAttributeName;
    }

    public static void setCartRequestAttributeName(String cartRequestAttributeName) {
        CartStateRequestProcessor.cartRequestAttributeName = cartRequestAttributeName;
    }
}
