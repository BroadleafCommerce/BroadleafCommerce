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
import org.broadleafcommerce.core.order.domain.FulfillmentGroup
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentGroupPricingActivity


class FulfillmentGroupPricingActivitySpec extends BasePricingActivitySpec {
    /*
     * This activity will need the following
     * 
     * Order
     *      FulfillmentGroup
     *          shippingOverride = false
     *          
     *      FulfillmentGroup
     *          fulfillmentPrice = some amount (Money)
     *          
     * Simple test to make sure that the branches are taken with valid data
     */
    FulfillmentPricingService mockFulfillmentPricingService
    def setup() {
        context.seedData.fulfillmentGroups = [
            null,
            new FulfillmentGroupImpl().with {
                shippingOverride = false
                order = context.seedData
                it
            },
            new FulfillmentGroupImpl().with {
                shippingOverride = true
                fulfillmentPrice = new Money('1.00')
                order = context.seedData
                it
            }
        ]
        mockFulfillmentPricingService = Mock()
    }

    def "Test FulfillmentGroupPricingActivity with valid data"() {
        FulfillmentGroup fulfillmentGroup1 = new FulfillmentGroupImpl().with {
            shippingOverride = true
            order = context.seedData
            fulfillmentPrice = new Money('1.00')
            it
        }
        activity = new FulfillmentGroupPricingActivity().with() {
            fulfillmentPricingService = mockFulfillmentPricingService
            it
        }

        when: "I execute FulfillmentGroupPricingActivity"
        context = activity.execute(context)

        then: "MockFulfillmentPricingService will be invoked once and the sum total of the charges will be set in the order"
        1 * mockFulfillmentPricingService.calculateCostForFulfillmentGroup(_) >> fulfillmentGroup1
        context.seedData.totalFulfillmentCharges.amount == 2.00
    }
}
