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
import org.broadleafcommerce.core.catalog.domain.SkuFeeImpl
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFeeImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.service.FulfillmentGroupService
import org.broadleafcommerce.core.pricing.service.workflow.ConsolidateFulfillmentFeesActivity

class ConsolidateFulfillmentFeesActivitySpec extends BasePricingActivitySpec {

    /*
     * The code coverage on this spec is only 69.4% due to not knowing what the format of
     * SkuFee expression statements are for the method shouldApplyFeeToFulfillmentGroup
     * to be further tested.
     * 
     * If someone, whom knows this information, would like to write a test to up the code
     * coverage, please do so.
     */
    FulfillmentGroupService mockFulfillmentGroupService
    Order order
    def setup() {
        //Setup a valid FulfillmentGroup with a FulfillmentItem inside
        // and place it inside the context.seedData order object
        order = context.seedData
        context.seedData.fulfillmentGroups = [
            new FulfillmentGroupImpl().with() {
                fulfillmentGroupItems = [
                    new FulfillmentGroupItemImpl().with() {
                        orderItem = new BundleOrderItemImpl().with() {
                            sku = new SkuImpl().with() {
                                id = 1
                                retailPrice = new Money('1.00')
                                fees = [
                                    new SkuFeeImpl().with() {
                                        feeType = SkuFeeType.FULFILLMENT
                                        name = 'Test'
                                        taxable = true
                                        amount = new Money('1.00')
                                        it
                                    }
                                ] as List
                                it
                            }
                            it
                        }
                        it
                    }
                ]
                it
            }
        ]
    }

    def "Test a valid run with valid data"() {
        mockFulfillmentGroupService = Mock()

        activity = new ConsolidateFulfillmentFeesActivity().with() {
            fulfillmentGroupService = mockFulfillmentGroupService
            it
        }

        when: "I execute ConsolidateFulfillmentfeesActivity"
        context = activity.execute(context)

        then: "FulfillmentGroupService's createFulfillmentGroupFee and save methods should run once"
        1 * mockFulfillmentGroupService.createFulfillmentGroupFee() >> { new FulfillmentGroupFeeImpl() }
        1 * mockFulfillmentGroupService.save(_)
        order == context.seedData
    }
}
