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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.order.service

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl
import org.broadleafcommerce.core.workflow.ProcessContext
import org.broadleafcommerce.profile.core.domain.Customer
import org.broadleafcommerce.profile.core.domain.CustomerImpl
import spock.lang.Specification

class BaseBuildOrderItemFromDTOSpec extends Specification {

    OrderItemService orderItemService
    ProcessContext<CartOperationRequest> context
    def setup() {
        context = new DefaultProcessContextImpl<CartOperationRequest>().with() {
            Customer customer = new CustomerImpl()
            customer.id = 1
            Order order = new OrderImpl()
            order.id = 1
            order.customer = customer
            OrderItemRequestDTO itemRequest = Spy(OrderItemRequestDTO).with {
                skuId = 1
                productId = 1
                categoryId = 1
                quantity = 1
                overrideSalePrice = new Money("1.00")
                overrideRetailPrice = new Money("1.50")
                it
            }
            seedData = new CartOperationRequest(order,itemRequest,true)
            it
        }
    }
}
