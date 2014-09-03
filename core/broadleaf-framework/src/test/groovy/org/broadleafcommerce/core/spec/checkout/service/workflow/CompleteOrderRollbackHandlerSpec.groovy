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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CompleteOrderRollbackHandler
import org.broadleafcommerce.core.order.service.type.OrderStatus
import org.broadleafcommerce.core.workflow.state.RollbackHandler

class CompleteOrderRollbackHandlerSpec extends BaseCheckoutRollbackSpec {

    def "Test that seed data has been sent to correct values"() {
        RollbackHandler rollbackHandler = new CompleteOrderRollbackHandler()

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "seedData's order status is set to IN_Process, and its order number and submite date are nulled"
        context.seedData.order.status == OrderStatus.IN_PROCESS
        context.seedData.order.orderNumber == null
        context.seedData.order.submitDate == null
    }
}
