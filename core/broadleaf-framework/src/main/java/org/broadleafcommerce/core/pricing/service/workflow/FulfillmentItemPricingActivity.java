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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Called during the pricing workflow to set each item's merchandise total and taxable total
 * 
 * @author Brian Polster 
 */
public class FulfillmentItemPricingActivity extends BaseActivity {

    protected BroadleafCurrency getCurrency(FulfillmentGroup fg) {
        return fg.getOrder().getCurrency();
    }

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        Map<OrderItem, List<FulfillmentGroupItem>> partialOrderItemMap =
                new HashMap<OrderItem, List<FulfillmentGroupItem>>();

        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for(FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {

                OrderItem orderItem = fgItem.getOrderItem();
                int fgItemQty = fgItem.getQuantity();
                int orderItemQty = orderItem.getQuantity();
                if (fgItemQty != orderItemQty) {
                    // We need to keep track of all of these items in case we need to distribute a remainder 
                    // to one or more of the items.
                    List<FulfillmentGroupItem> fgItemList = partialOrderItemMap.get(orderItem);
                    if (fgItemList == null) {
                        fgItemList = new ArrayList<FulfillmentGroupItem>();
                        partialOrderItemMap.put(orderItem, fgItemList);
                    }
                    fgItemList.add(fgItem);


                    Money totalItemAmount = orderItem.getTotalPrice();
                    // Update the prorated totals
                    Money proratedOrderAdjustment = orderItem.getProratedOrderAdjustment();
                    if (proratedOrderAdjustment != null) {
                        totalItemAmount = totalItemAmount.subtract(orderItem.getProratedOrderAdjustment());
                    }

                    fgItem.setTotalItemAmount(totalItemAmount.multiply(fgItemQty).divide(orderItemQty));
                    if (orderItem.isTaxable()) {
                        Money totalTaxAmount = orderItem.getTotalTaxableAmount();
                        fgItem.setTotalItemTaxableAmount(totalTaxAmount.multiply(fgItemQty).divide(orderItemQty));
                    } else {
                        // Taxes are zero
                        fgItem.setTotalItemTaxableAmount(new Money(getCurrency(fulfillmentGroup)));
                    }
                } else {
                    Money totalItemAmount = orderItem.getTotalPrice();
                    Money proratedOrderAdjustment = orderItem.getProratedOrderAdjustment();
                    if (proratedOrderAdjustment != null) {
                        totalItemAmount = totalItemAmount.subtract(orderItem.getProratedOrderAdjustment());
                    }
                    // Quantity matches, just bring over the itemTotals                    
                    fgItem.setTotalItemAmount(totalItemAmount);
                    if (orderItem.isTaxable()) {
                        fgItem.setTotalItemTaxableAmount(orderItem.getTotalTaxableAmount());
                    } else {
                        fgItem.setTotalItemTaxableAmount(new Money(getCurrency(fulfillmentGroup)));
                    }
                }
            }
        }

        // Fix any rounding issues that might have occurred with items split across fulfillment items.
        for (OrderItem orderItem : partialOrderItemMap.keySet()) {
            Money totalItemAmount = orderItem.getTotalPrice().subtract(orderItem.getProratedOrderAdjustment());
            Money totalFGItemAmount = sumItemAmount(partialOrderItemMap.get(orderItem), order);
            Money amountDiff = totalItemAmount.subtract(totalFGItemAmount);

            if (!(amountDiff.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
                long numApplicationsNeeded = countNumberOfUnits(amountDiff);
                Money unitAmount = getUnitAmount(amountDiff);
                for (FulfillmentGroupItem fgItem : partialOrderItemMap.get(orderItem)) {
                    numApplicationsNeeded = numApplicationsNeeded -
                            applyDifference(fgItem, numApplicationsNeeded, unitAmount);
                    if (numApplicationsNeeded == 0) {
                        break;
                    }
                }
            }
        }

        // Fix any rounding issues that might have occurred for tax with items split across fulfillment items.
        for (OrderItem orderItem : partialOrderItemMap.keySet()) {
            Money totalTaxAmount = orderItem.getTotalTaxableAmount();
            Money totalFGTaxAmount = sumTaxAmount(partialOrderItemMap.get(orderItem), order);
            Money taxDiff = totalTaxAmount.subtract(totalFGTaxAmount);

            if (!(taxDiff.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
                long numApplicationsNeeded = countNumberOfUnits(taxDiff);
                Money unitAmount = getUnitAmount(taxDiff);
                for (FulfillmentGroupItem fgItem : partialOrderItemMap.get(orderItem)) {
                    numApplicationsNeeded = numApplicationsNeeded -
                            applyTaxDifference(fgItem, numApplicationsNeeded, unitAmount);
                    if (numApplicationsNeeded == 0) {
                        break;
                    }
                }
            }
        }

        context.setSeedData(order);

        return context;
    }

    protected Money sumItemAmount(List<FulfillmentGroupItem> items, Order order) {
        Money totalAmount = new Money(order.getCurrency());
        for (FulfillmentGroupItem fgItem : items) {
            totalAmount = totalAmount.add(fgItem.getTotalItemAmount());
        }
        return totalAmount;
    }

    protected Money sumTaxAmount(List<FulfillmentGroupItem> items, Order order) {
        Money taxAmount = new Money(order.getCurrency());
        for (FulfillmentGroupItem fgItem : items) {
            taxAmount = taxAmount.add(fgItem.getTotalItemTaxableAmount());
        }
        return taxAmount;
    }

    public long countNumberOfUnits(Money difference) {
        double numUnits = difference.multiply(Math.pow(10, difference.getCurrency().getDefaultFractionDigits())).doubleValue();
        return Math.round(numUnits);
    }

    /**
     * Returns the unit amount (e.g. .01 for US)
     * @param currency
     * @return
     */
    public Money getUnitAmount(Money difference) {
        Currency currency = difference.getCurrency();
        BigDecimal divisor = new BigDecimal(Math.pow(10, currency.getDefaultFractionDigits()));
        BigDecimal unitAmount = new BigDecimal("1").divide(divisor);

        if (difference.lessThan(BigDecimal.ZERO)) {
            unitAmount = unitAmount.negate();
        }
        return new Money(unitAmount, currency);
    }

    public long applyDifference(FulfillmentGroupItem fgItem, long numApplicationsNeeded, Money unitAmount) {
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, fgItem.getQuantity()));

        Money oldAmount = fgItem.getTotalItemAmount();
        Money changeToAmount = unitAmount.multiply(numTimesToApply);

        fgItem.setTotalItemAmount(oldAmount.add(changeToAmount));
        return numTimesToApply.longValue();
    }

    public long applyTaxDifference(FulfillmentGroupItem fgItem, long numApplicationsNeeded, Money unitAmount) {
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, fgItem.getQuantity()));

        Money oldAmount = fgItem.getTotalItemTaxableAmount();
        Money changeToAmount = unitAmount.multiply(numTimesToApply);

        fgItem.setTotalItemTaxableAmount(oldAmount.add(changeToAmount));
        return numTimesToApply.longValue();
    }

}
