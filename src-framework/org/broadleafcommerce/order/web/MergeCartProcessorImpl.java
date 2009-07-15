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
package org.broadleafcommerce.order.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.profile.web.MergeCartProcessor;
import org.springframework.security.Authentication;
import org.springframework.stereotype.Service;

@Service("blMergeCartProcessor")
public class MergeCartProcessorImpl implements MergeCartProcessor {

    private String mergeCartItemsAddedKey = "merge_cart_items_added";

    private String mergeCartItemsRemovedKey = "merge_cart_items_removed";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    @Resource(name="blCartService")
    private CartService cartService;

    @Resource(name="blCustomerState")
    private CustomerState customerState;

    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer loggedInCustomer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        Customer anonymousCustomer = customerState.getCustomer(request);
        Order cart = cartService.findCartForCustomer(anonymousCustomer);
        Long anonymousCartId;
        if (cart != null) {
            anonymousCartId = cart.getId();
        } else {
            anonymousCartId = null;
        }
        MergeCartResponse mergeCartResponse;
        try {
            mergeCartResponse = cartService.mergeCart(loggedInCustomer, anonymousCartId);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        if (!mergeCartResponse.getAddedItems().isEmpty()) {
            request.getSession().setAttribute(mergeCartItemsAddedKey, mergeCartResponse.getAddedItems());
        }
        if (!mergeCartResponse.getRemovedItems().isEmpty()) {
            request.getSession().setAttribute(mergeCartItemsRemovedKey, mergeCartResponse.getRemovedItems());
        }
    }

    public String getMergeCartItemsAddedKey() {
        return mergeCartItemsAddedKey;
    }

    public void setMergeCartItemsAddedKey(String mergeCartItemsAddedKey) {
        this.mergeCartItemsAddedKey = mergeCartItemsAddedKey;
    }

    public String getMergeCartItemsRemovedKey() {
        return mergeCartItemsRemovedKey;
    }

    public void setMergeCartItemsRemovedKey(String mergeCartItemsRemovedKey) {
        this.mergeCartItemsRemovedKey = mergeCartItemsRemovedKey;
    }
}
