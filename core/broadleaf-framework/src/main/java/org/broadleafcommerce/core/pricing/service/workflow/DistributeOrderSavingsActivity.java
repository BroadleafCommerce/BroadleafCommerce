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
import org.broadleafcommerce.core.order.domain.OrderItemContainer;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * This activity distributes the order savings to the OrderItems in the order.
 * This is useful as some tax implementations will want to subtract the proportional order savings 
 * prior to computing taxes.   
 * 
 * Having this value on the orderItem also facilitates return and refund processing.
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
            Money unitAmount = getUnitAmount(difference);
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
        if (orderItem instanceof OrderItemContainer) {
            OrderItemContainer container = (OrderItemContainer) orderItem;
            if (!container.isPricingAtContainerLevel()) {
                long totalTimesApplied = 0;
                for (OrderItem containedItem : container.getOrderItems()) {
                    long timesApplied = applyDifference(containedItem, numApplicationsNeeded, unitAmount);
                    totalTimesApplied += timesApplied;
                    numApplicationsNeeded = numApplicationsNeeded - timesApplied;
                    if (numApplicationsNeeded == 0) {
                        return totalTimesApplied;
                    }
                }
                return totalTimesApplied;
            }
        }
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, orderItem.getQuantity()));

        Money oldOrderAdjustment = orderItem.getProratedOrderAdjustment();
        Money changeToAdjustment = unitAmount.multiply(numTimesToApply);
        
        orderItem.setProratedOrderAdjustment(oldOrderAdjustment.add(changeToAdjustment));
        return numTimesToApply.longValue();
    }

    public Money updateOrderItemSavingsTotal(OrderItem orderItem, Money subTotal, Money orderSavings) {
        if (orderItem instanceof OrderItemContainer) {
            OrderItemContainer container = (OrderItemContainer) orderItem;
            if (!container.isPricingAtContainerLevel()) {
                Money returnSavings = new Money(BroadleafCurrencyUtils.getCurrency(orderItem.getOrder().getCurrency()));
                for (OrderItem containedItem : container.getOrderItems()) {
                    Money prorataOrderSavings = updateOrderItemSavingsTotal(containedItem, subTotal, orderSavings);
                    containedItem.setProratedOrderAdjustment(prorataOrderSavings.multiply(orderItem.getQuantity()));
                    returnSavings = returnSavings.add(containedItem.getProratedOrderAdjustment());
                }
                return returnSavings;
            }
        }
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

    /**
     * Returns the unit amount (e.g. .01 for US)
     * @param currency
     * @return
     */
    public Money getUnitAmount(Money difference) {
        Currency currency = BroadleafCurrencyUtils.getCurrency(difference);
        BigDecimal divisor = new BigDecimal(Math.pow(10, currency.getDefaultFractionDigits()));
        BigDecimal unitAmount = new BigDecimal("1").divide(divisor);

        if (difference.lessThan(BigDecimal.ZERO)) {
            unitAmount = unitAmount.negate();
        }
        return new Money(unitAmount, currency);
    }


}
