/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.order.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.util.BLCRequestUtils;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;
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
 * @see {@link ServletWebRequest}
 */
@Component("blCartStateRequestProcessor")
public class CartStateRequestProcessor {

    /** Logger for this class and subclasses */
    protected final Log LOG = LogFactory.getLog(getClass());

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    private final String mergeCartResponseKey = "bl_merge_cart_response";

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blCartService")
    protected CartService cartService;

    @Resource(name = "blCustomerState")
    protected CustomerState customerState;

    protected static String cartRequestAttributeName = "cart";

    protected static String anonymousCartSessionAttributeName = "anonymousCart";

    public static final String OVERRIDE_CART_ATTR_NAME = "_blc_overrideCartId";

    public void process(WebRequest request) {
        Customer customer = customerState.getCustomer(request);

        if (customer == null) {
            LOG.warn("No customer was found on the current request, no cart will be added to the current request. Ensure that the"
                     + " blCustomerStateFilter occurs prior to the blCartStateFilter");
            return;
        }

        Order cart = getOverrideCart(request);

        if (cart == null && mergeCartNeeded(customer, request)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Merge cart required, calling mergeCart " + customer.getId());
            }
            cart = mergeCart(customer, request);
        } else if (cart == null) {
            cart = cartService.findCartForCustomer(customer);
        }

        orderService.reloadOrder(cart);

        request.setAttribute(cartRequestAttributeName, cart, WebRequest.SCOPE_REQUEST);

        // Setup cart for content rule processing
        @SuppressWarnings("unchecked")
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

    public Order getOverrideCart(WebRequest request) {
        Long orderId = null;
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            orderId = (Long) request.getAttribute(OVERRIDE_CART_ATTR_NAME, WebRequest.SCOPE_GLOBAL_SESSION);
        }
        Order cart = null;
        if (orderId != null) {
            cart = orderService.findOrderById(orderId);

            if (cart == null ||
                cart.getStatus().equals(OrderStatus.SUBMITTED)) {
                return null;
            }
        }

        return cart;
    }

    /**
     * Returns true if the given <b>customer</b> is different than the previous anonymous customer, implying that this is
     * the logged in customer and we need to merge the carts
     */
    public boolean mergeCartNeeded(Customer customer, WebRequest request) {
        Customer anonymousCustomer = customerState.getAnonymousCustomer(request);
        return (anonymousCustomer != null && customer.getId() != null && !customer.getId().equals(anonymousCustomer.getId()));
    }

    /**
     * Looks up the anonymous customer and merges that cart with the cart from the given logged in <b>customer</b>. This
     * will also remove the customer from session after it has finished since it is no longer needed
     */
    public Order mergeCart(Customer customer, WebRequest request) {
        Customer anonymousCustomer = customerState.getAnonymousCustomer(request);
        MergeCartResponse mergeCartResponse;
        try {
            Order cart = cartService.findCartForCustomer(anonymousCustomer);
            mergeCartResponse = cartService.mergeCart(customer, cart);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (BLCRequestUtils.isOKtoUseSession(request)) {
            // The anonymous customer from session is no longer needed; it can be safely removed
            request.removeAttribute("_blc_anonymousCustomer",
                                    WebRequest.SCOPE_GLOBAL_SESSION);
            request.removeAttribute("_blc_anonymousCustomerId",
                                    WebRequest.SCOPE_GLOBAL_SESSION);

            request.setAttribute(mergeCartResponseKey, mergeCartResponse, WebRequest.SCOPE_GLOBAL_SESSION);
        }
        return mergeCartResponse.getOrder();
    }

    public static String getCartRequestAttributeName() {
        return cartRequestAttributeName;
    }

    public static void setCartRequestAttributeName(String cartRequestAttributeName) {
        CartStateRequestProcessor.cartRequestAttributeName = cartRequestAttributeName;
    }
}