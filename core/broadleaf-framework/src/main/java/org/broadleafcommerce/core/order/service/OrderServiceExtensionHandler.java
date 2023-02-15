/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;

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
    public ExtensionResultStatusType attachAdditionalDataToOrder(Order order, boolean priceOrder);

    public ExtensionResultStatusType addOfferCodes(Order order, List<OfferCode> offerCodes, boolean priceOrder);

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
