/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentGroupMerchandiseTotalActivity



class FulfillmentGroupMerchandiseTotalActivitySpec extends BasePricingActivitySpec {
    /*
     * This activity will require the following:
     * Order
     *      FulfillmentGroup
     *          FulfillmentGroupItem
     *              OrderItem
     *                  totalPrice <- some value (Money)
     * May want to have multiple FulfillmentGroupItems for a good test
     */
    def setup() {
        OrderItem orderItem1 = new OrderItemImpl().with {
            salePrice = new Money('5.00')
            quantity = 1
            order = context.seedData
            it
        }
        OrderItem orderItem2 = new OrderItemImpl().with {
            salePrice = new Money('3.00')
            quantity = 5
            order = context.seedData
            it
        }
        context.seedData.fulfillmentGroups = [
            new FulfillmentGroupImpl().with() {
                fulfillmentGroupItems << new FulfillmentGroupItemImpl().with() {
                    orderItem = orderItem1
                    quantity = 1
                    it
                }
                fulfillmentGroupItems << new FulfillmentGroupItemImpl().with() {
                    orderItem = orderItem2
                    quantity = 2
                    it
                }
                order = context.seedData
                it
            },
            new FulfillmentGroupImpl().with() {
                fulfillmentGroupItems << new FulfillmentGroupItemImpl().with() {
                    orderItem = orderItem2
                    quantity = 3
                    it
                }
                order = context.seedData
                it
            }
        ]
    }

    def "Test FulfillmentGroupMerchandiseTotalActivity with valid data"() {
        activity = new FulfillmentGroupMerchandiseTotalActivity()

        when: "I execute FulfillmentGroupMerchandiseTotalActivity"
        context = activity.execute(context)

        then: "fulfillmentGroup1 should have a merchandiseTotal of 20 and fulfillmentGroup2 should have 15"
        context.seedData.fulfillmentGroups.get(0).merchandiseTotal.amount == 20.00
        context.seedData.fulfillmentGroups.get(1).merchandiseTotal.amount == 15.00
    }
}
