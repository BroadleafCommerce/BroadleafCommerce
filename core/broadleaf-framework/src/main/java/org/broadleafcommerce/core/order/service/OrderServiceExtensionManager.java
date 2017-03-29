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

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * @author Andre Azzolini (apazzolini), bpolster
 */
@Service("blOrderServiceExtensionManager")
public class OrderServiceExtensionManager extends ExtensionManager<OrderServiceExtensionHandler> implements OrderServiceExtensionHandler {

    public static final ExtensionManagerOperation attachAdditionalDataToNewNamedCart = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).attachAdditionalDataToNewNamedCart((Customer) params[0], (Order) params[1]);
        }
    };

    public static final ExtensionManagerOperation preValidateCartOperation = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).preValidateCartOperation((Order) params[0], (ExtensionResultHolder) params[1]);
        }
    };

    public static final ExtensionManagerOperation preValidateUpdateQuantityOperation = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).preValidateUpdateQuantityOperation((Order) params[0], (OrderItemRequestDTO) params[1], (ExtensionResultHolder) params[2]);
        }
    };

    public static final ExtensionManagerOperation attachAdditionalDataToOrder = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).attachAdditionalDataToOrder((Order) params[0], (Boolean) params[1]);
        }
    };

    public static final ExtensionManagerOperation addOfferCodes = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).addOfferCodes((Order) params[0], (List<OfferCode>) params[1], (Boolean) params[2]);
        }
    };

    public static final ExtensionManagerOperation findStaleCacheAwareCartForCustomer = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).findCartForCustomerWithEnhancements((Customer) params[0], (ExtensionResultHolder) params[1]);
        }
    };

    public static final ExtensionManagerOperation findStaleCacheAwareCartForCustomer2 = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OrderServiceExtensionHandler) handler).findCartForCustomerWithEnhancements((Customer) params[0], (Order) params[1], (ExtensionResultHolder) params[2]);
        }
    };

    public OrderServiceExtensionManager() {
        super(OrderServiceExtensionHandler.class);
    }

    /**
     * By default,this extension manager will continue on handled allowing multiple handlers to interact with the order.
     */
    public boolean continueOnHandled() {
        return true;
    }

    @Override
    public ExtensionResultStatusType attachAdditionalDataToNewNamedCart(Customer customer, Order cart) {
        return execute(attachAdditionalDataToNewNamedCart, customer, cart);
    }

    @Override
    public ExtensionResultStatusType preValidateCartOperation(Order cart, ExtensionResultHolder erh) {
        return execute(preValidateCartOperation, cart, erh);
    }

    @Override
    public ExtensionResultStatusType preValidateUpdateQuantityOperation(Order cart, OrderItemRequestDTO dto, ExtensionResultHolder erh) {
        return execute(preValidateUpdateQuantityOperation, cart, dto, erh);
    }

    @Override
    public ExtensionResultStatusType attachAdditionalDataToOrder(Order order, boolean priceOrder) {
        return execute(attachAdditionalDataToOrder, order, priceOrder);
    }

    @Override
    public ExtensionResultStatusType addOfferCodes(Order order, List<OfferCode> offerCodes, boolean priceOrder) {
        return execute(addOfferCodes, order, offerCodes, priceOrder);
    }

    @Override
    public ExtensionResultStatusType findCartForCustomerWithEnhancements(Customer customer, ExtensionResultHolder erh) {
        return execute(findStaleCacheAwareCartForCustomer, customer, erh);
    }

    @Override
    public ExtensionResultStatusType findCartForCustomerWithEnhancements(Customer customer, Order candidateCart, ExtensionResultHolder erh) {
        return execute(findStaleCacheAwareCartForCustomer2, customer, candidateCart, erh);
    }

    @Override
    public boolean isEnabled() {
        //Not used
        return true;
    }
}
