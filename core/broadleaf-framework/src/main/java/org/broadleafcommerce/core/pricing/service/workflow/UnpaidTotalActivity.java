/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;

/**
 * @author Jerry Ocanas (jocanas)
 */
public class UnpaidTotalActivity extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();

        Money unpaidTotal = order.getTotal();
        Money totalPayments = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, order.getCurrency());
        for(PaymentInfo paymentInfo : order.getPaymentInfos()){
            if(paymentInfo.getAmount() != null){
                totalPayments = totalPayments.add(paymentInfo.getAmount());
            }
        }

        if(totalPayments.lessThanOrEqual(unpaidTotal)){
            unpaidTotal = unpaidTotal.subtract(totalPayments);
        } else {
            unpaidTotal = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, order.getCurrency());
        }

        order.setUnpaidTotal(unpaidTotal);
        context.setSeedData(order);
        return context;
    }
}
