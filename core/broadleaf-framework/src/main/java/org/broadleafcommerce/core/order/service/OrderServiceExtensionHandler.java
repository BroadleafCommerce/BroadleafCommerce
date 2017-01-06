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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.profile.core.domain.Customer;


/**
 * @author Andre Azzolini (apazzolini), bpolster
 */
public interface OrderServiceExtensionHandler extends ExtensionHandler {
    
    ExtensionResultStatusType attachAdditionalDataToNewNamedCart(Customer customer, Order cart);

    ExtensionResultStatusType preValidateCartOperation(Order cart, ExtensionResultHolder erh);

    ExtensionResultStatusType preValidateUpdateQuantityOperation(Order cart, OrderItemRequestDTO dto,
            ExtensionResultHolder erh);
    
    /**
     * Can be used to attach or update fields must prior to saving an order.
     * @return
     */
    ExtensionResultStatusType attachAdditionalDataToOrder(Order order, boolean priceOrder);

    /**
     * Retrieve an enhanced version of the cart for the customer. Individual instances of {@link OrderServiceExtensionHandler}
     * can provide one or more interesting enhancements.
     *
     * @param customer the user for whom the cart is retrieved
     * @param erh the holder for the enhanced cart to be set by the handler
     * @return whether or not the enhancement was performed
     */
    ExtensionResultStatusType findCartForCustomerWithEnhancements(Customer customer, ExtensionResultHolder erh);

    /**
     * Retrieve an enhanced version of the cart for the customer. Use the candidateCart as the source cart to be enhanced.
     * Individual instances of {@link OrderServiceExtensionHandler} can provide one or more interesting enhancements.
     *
     * @param customer the user for whom the cart is enhanced
     * @param candidateCart the source cart to enhance
     * @param erh the holder for the enhanced cart to be set by the handler
     * @return whether or not the enhancement was performed
     */
    ExtensionResultStatusType findCartForCustomerWithEnhancements(Customer customer, Order candidateCart, ExtensionResultHolder erh);

}
