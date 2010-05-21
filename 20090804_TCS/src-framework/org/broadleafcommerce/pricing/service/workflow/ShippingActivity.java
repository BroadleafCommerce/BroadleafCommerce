/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.pricing.service.workflow;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.ShippingService;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class ShippingActivity extends BaseActivity {

    private ShippingService shippingService;

    public void setShippingService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext)context).getSeedData();

        /*
         * 1. Get FGs from Order
         * 2. take each FG and call shipping module with the shipping svc
         * 3. add FG back to order
         */

        Money totalShipping = new Money(0D);
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            fulfillmentGroup = shippingService.calculateShippingForFulfillmentGroup(fulfillmentGroup);
            totalShipping = totalShipping.add(fulfillmentGroup.getShippingPrice());
        }
        order.setTotalShipping(totalShipping);
        context.setSeedData(order);
        return context;
    }

}
