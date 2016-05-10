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
package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.order.security.CartStateRequestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component("blCartState")
public class CartState {

    /**
     * Gets the current cart based on the current request
     * 
     * @return the current customer's cart
     */
    public static Order getCart() {
        if (BroadleafRequestContext.getBroadleafRequestContext() == null ||
                BroadleafRequestContext.getBroadleafRequestContext().getWebRequest() == null) {
            return null;
        }

        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        return (Order) request.getAttribute(CartStateRequestProcessor.getCartRequestAttributeName(), WebRequest.SCOPE_REQUEST);
    }
    
    /**
     * Sets the current cart on the current request
     * 
     * @param cart the new cart to set
     */
    public static void setCart(Order cart) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        request.setAttribute(CartStateRequestProcessor.getCartRequestAttributeName(), cart, WebRequest.SCOPE_REQUEST);
    }

}
