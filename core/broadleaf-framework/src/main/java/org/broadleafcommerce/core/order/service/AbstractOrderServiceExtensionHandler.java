/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.profile.core.domain.Customer;


/**
 * @author bpolster
 */
public abstract class AbstractOrderServiceExtensionHandler extends AbstractExtensionHandler implements
        OrderServiceExtensionHandler {
    
    public ExtensionResultStatusType attachAdditionalDataToNewNamedCart(Customer customer, Order cart) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    public ExtensionResultStatusType preValidateCartOperation(Order cart, ExtensionResultHolder erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    public ExtensionResultStatusType preValidateUpdateQuantityOperation(Order cart, OrderItemRequestDTO dto, 
            ExtensionResultHolder erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    public ExtensionResultStatusType attachAdditionalDataToOrder(Order order, boolean priceOrder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
