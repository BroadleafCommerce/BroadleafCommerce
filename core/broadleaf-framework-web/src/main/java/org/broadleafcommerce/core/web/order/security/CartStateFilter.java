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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.security.CustomerStateFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component("blCartStateFilter")
/**
 * <p>
 * This filter should be configured after the BroadleafCommerce CustomerStateFilter listener from Spring Security.
 * Retrieves the cart for the current BroadleafCommerce Customer based using the authenticated user OR creates an empty non-modifiable cart and
 * stores it in the request.
 * </p>
 *
 * @author bpolster
 */
public class CartStateFilter extends GenericFilterBean implements  Ordered {

    /** Logger for this class and subclasses */
    protected final Log LOG = LogFactory.getLog(getClass());

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    @Resource(name="blCartService")
    protected CartService cartService;

    private static String cartRequestAttributeName = "cart";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {		
		Customer customer = (Customer) request.getAttribute(CustomerStateFilter.getCustomerRequestAttributeName());
		
		if (customer != null) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Looking up cart for customer " + customer.getId());
			}
		   	Order cart = cartService.findCartForCustomer(customer);
	    	
	    	if (cart == null) { 
	    		cart = cartService.createNewCartForCustomer(customer);
	    	}

	    	request.setAttribute(cartRequestAttributeName, cart);

	        // Setup cart for content rule processing
	        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
	        if (ruleMap == null) {
	            ruleMap = new HashMap<String,Object>();
	        }
	        ruleMap.put("cart", cart);
	        request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap);
		}

        chain.doFilter(request, response);
    }

    public int getOrder() {
    	//FilterChainOrder has been dropped from Spring Security 3
        //return FilterChainOrder.REMEMBER_ME_FILTER+1;
    	return 1502;
    }
    
	public static String getCartRequestAttributeName() {
		return cartRequestAttributeName;
	}

	public static void setCartRequestAttributeName(String cartRequestAttributeName) {
		CartStateFilter.cartRequestAttributeName = cartRequestAttributeName;
	}


}