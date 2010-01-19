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

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class TotalActivity extends BaseActivity {

    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        Money total = new Money(BigDecimal.ZERO);
        total = total.add(order.getSubTotal());
        total = total.add(order.getTotalTax());
        total = total.add(order.getTotalShipping());

        Money fees = new Money(BigDecimal.ZERO);
        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotal = new Money(0D);
            fgTotal = fgTotal.add(fulfillmentGroup.getMerchandiseTotal());
            fgTotal = fgTotal.add(fulfillmentGroup.getShippingPrice());
            fgTotal = fgTotal.add(fulfillmentGroup.getTotalTax());

            for (FulfillmentGroupFee fulfillmentGroupFee : fulfillmentGroup.getFulfillmentGroupFees()) {
                fgTotal = fgTotal.add(fulfillmentGroupFee.getAmount());
                fees = fees.add(fulfillmentGroupFee.getAmount());
            }

            fulfillmentGroup.setTotal(fgTotal);
        }

        total = total.add(fees);
        order.setTotal(total);

        context.setSeedData(order);
        return context;
    }

}
