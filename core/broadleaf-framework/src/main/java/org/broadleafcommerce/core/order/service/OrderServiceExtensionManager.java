/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Service;


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
