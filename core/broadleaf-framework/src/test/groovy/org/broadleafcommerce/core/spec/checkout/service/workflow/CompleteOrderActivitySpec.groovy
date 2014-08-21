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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CompleteOrderActivity
import org.broadleafcommerce.core.order.service.type.OrderStatus

/**
 * @author Elbert Bautista (elbertbautista)
 */
class CompleteOrderActivitySpec extends BaseCheckoutActivitySpec {

    def setup() {
        activity = new CompleteOrderActivity()
    }

    def "Test that the properties on the Order are properly set"() {
        when: "I execute the CompleteOrderActivity"
        context = activity.execute(context);

        then: "The status on the order is submitted, and the order number and submit date are set"
        context.seedData.order.id == 1
        context.seedData.order.status == OrderStatus.SUBMITTED
        context.seedData.order.orderNumber != null
        context.seedData.order.submitDate != null

    }

}