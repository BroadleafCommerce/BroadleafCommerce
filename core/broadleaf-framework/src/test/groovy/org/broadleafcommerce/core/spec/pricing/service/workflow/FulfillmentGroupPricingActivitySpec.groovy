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
