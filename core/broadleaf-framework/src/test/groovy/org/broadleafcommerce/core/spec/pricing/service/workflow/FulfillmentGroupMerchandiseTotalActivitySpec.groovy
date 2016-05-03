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
