/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.pricing.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;

/**
 * This activity distributes the order savings to the OrderItems in the order.
 * This is useful as some tax implementations will want to subtract the proportional order savings 
 * prior to computing taxes.   
 * 
 * Having this value on the orderItem also facilitates return and refund processing.
 * 
 * Note:  For BundleOrderItem (or any other implementation of OrderItemContainer), the 
 * pro-rated savings are stored with the BundleOrderItem and not the constituent items.   This is due to the fact
 * that in some scenarios it would be impossible to handle penny rounding issues if the value was stored at the item
 * levels and the bundle quantity was > 1.    Downstream processes must account for this detail (e.g. such as in an OMS
 * system's item calculation methods used for returns).
 * 
 * @author bpolster
 */
public class DistributeOrderSavingsActivity extends BaseActivity {

    private static final Log LOG = LogFactory.getLog(DistributeOrderSavingsActivity.class);
    
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        
        Money orderSavings = order.getOrderAdjustmentsValue();
        if (orderSavings == null) {
            orderSavings = new Money(BroadleafCurrencyUtils.getCurrency(order.getCurrency()));
        }
        Money orderSubTotal = order.getSubTotal();
        if (orderSubTotal == null || orderSubTotal.lessThan(orderSavings)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Subtotal is null or less than orderSavings in DistributeOrderSavingsActivity.java.  " +
                        "No distribution is taking place.");
            }
            return context;
        }                                       
        
        Money savingsDistributed = new Money(BroadleafCurrencyUtils.getCurrency(order.getCurrency()));
        for (OrderItem orderItem : order.getOrderItems()) {
            savingsDistributed = savingsDistributed.add(updateOrderItemSavingsTotal(orderItem, orderSubTotal, orderSavings));
        }

        Money difference = orderSavings.subtract(savingsDistributed);
        if (!(difference.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
            long numApplicationsNeeded = countNumberOfUnits(difference);
            Money unitAmount = BroadleafCurrencyUtils.getUnitAmount(difference);
            for (OrderItem orderItem : order.getOrderItems()) {
                numApplicationsNeeded = numApplicationsNeeded -
                        applyDifference(orderItem, numApplicationsNeeded, unitAmount);
                if (numApplicationsNeeded == 0) {
                    break;
                }
            }
        }
        
        return context;
    }

    public long applyDifference(OrderItem orderItem, long numApplicationsNeeded, Money unitAmount) {
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, orderItem.getQuantity()));

        Money oldOrderAdjustment = orderItem.getProratedOrderAdjustment();
        Money changeToAdjustment = unitAmount.multiply(numTimesToApply);
        
        orderItem.setProratedOrderAdjustment(oldOrderAdjustment.add(changeToAdjustment));
        return numTimesToApply.longValue();
    }

    public Money updateOrderItemSavingsTotal(OrderItem orderItem, Money subTotal, Money orderSavings) {
        Money itemTotal = orderItem.getTotalPrice();
        Money prorataOrderSavings = new Money(BroadleafCurrencyUtils.getCurrency(orderSavings));
        if (orderSavings == null || (orderSavings.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
            orderItem.setProratedOrderAdjustment(new Money(BroadleafCurrencyUtils.getCurrency(orderSavings)));
        } else {
            prorataOrderSavings = itemTotal.divide(subTotal.getAmount()).multiply(orderSavings.getAmount());
            orderItem.setProratedOrderAdjustment(prorataOrderSavings);
        }
        return prorataOrderSavings;

    }

    public long countNumberOfUnits(Money difference) {
        double numUnits = difference.multiply(Math.pow(10,
                BroadleafCurrencyUtils.getCurrency(difference).getDefaultFractionDigits())).doubleValue();
        return Math.round(numUnits);
    }
}
