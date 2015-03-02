/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.order.service.workflow.add.AddFulfillmentGroupItemActivity
import org.broadleafcommerce.core.order.strategy.FulfillmentGroupItemStrategy
import org.broadleafcommerce.profile.core.domain.Customer
import org.broadleafcommerce.profile.core.domain.CustomerImpl


class AddFulfillmentGroupItemActivitySpec extends BaseAddItemActivitySpec {
    
    /*
     *  execute(context) is called:
     *  request is set to context.getSeedData()
     *  request is modified using FulfillmentGroupItemStrategy.onItemAdded(request)
     *  context seed data updated for new request
     *  context returned
     */
    
    FulfillmentGroupItemStrategy mockFgItemStrategy = Mock();
    
    def setup(){
        activity = new AddFulfillmentGroupItemActivity().with {
            fgItemStrategy = mockFgItemStrategy
            it
        }
    }
    
    def "Test that execute updates the seedData to a changed CartOperationRequest"() {
        setup: "setting up the mock request"
        Customer testCustomer = new CustomerImpl()
        testCustomer.id = 1
        Order testOrder = new OrderImpl()
        testOrder.id = 1
        testOrder.customer = testCustomer
        OrderItemRequestDTO testItemRequest = new OrderItemRequestDTO().with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            it
        }
        CartOperationRequest testRequest = new CartOperationRequest(testOrder,testItemRequest,true)
        CartOperationRequest oldRequest = context.getSeedData();
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "The seedData is updated to a different CartOperationRequest"
        1 * activity.fgItemStrategy.onItemAdded(_) >> testRequest
        context.seedData instanceof CartOperationRequest
        oldRequest instanceof CartOperationRequest
        context.seedData != oldRequest
        
    }

}
