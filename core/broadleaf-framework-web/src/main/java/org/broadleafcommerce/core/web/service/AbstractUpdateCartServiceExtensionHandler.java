/*-
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;


/**
 * @author Andre Azzolini (apazzolini), bpolster
 */
public abstract class AbstractUpdateCartServiceExtensionHandler extends AbstractExtensionHandler
        implements UpdateCartServiceExtensionHandler {

    /**
     * Throws an exception if cart is invalid.
     * 
     * @param cart
     * @param resultHolder
     * @return
     */
    @Override
    public ExtensionResultStatusType updateAndValidateCart(Order cart, ExtensionResultHolder resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType validateAddToCartItem(OrderItemRequestDTO itemRequest, Order cart) throws IllegalArgumentException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
