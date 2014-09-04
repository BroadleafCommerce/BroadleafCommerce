/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class FulfillmentItemPricingActivity extends BaseActivity<ProcessContext<Order>> {
    
    private static final Log LOG = LogFactory.getLog(FulfillmentItemPricingActivity.class);

    protected BroadleafCurrency getCurrency(FulfillmentGroup fg) {
        return fg.getOrder().getCurrency();
    }
    
    /**
     * Returns the order adjustment value or zero if none exists
     * @param order
     * @return
     */
    protected Money getOrderSavingsToDistribute(Order order) {
        if (order.getOrderAdjustmentsValue() == null) {
            return new Money(order.getCurrency());
        } else {
            Money adjustmentValue = order.getOrderAdjustmentsValue();
            
            Money orderSubTotal = order.getSubTotal();
            if (orderSubTotal == null || orderSubTotal.lessThan(adjustmentValue)) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Subtotal is null or less than orderSavings in DistributeOrderSavingsActivity.java.  " +
                            "No distribution is taking place.");
                }
                return new Money(order.getCurrency());
            } 
            return adjustmentValue;
        }
    }
    
    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();
        Map<OrderItem,List<FulfillmentGroupItem>> partialOrderItemMap = new HashMap<OrderItem,List<FulfillmentGroupItem>>();

        // Calculate the fulfillmentGroupItem total
        populateItemTotalAmount(order, partialOrderItemMap);
        fixItemTotalRoundingIssues(order, partialOrderItemMap);
        
        // Calculate the fulfillmentGroupItem prorated orderSavings
        Money totalAllItemsAmount = calculateTotalPriceForAllFulfillmentItems(order);
        Money totalOrderAdjustmentDistributed = distributeOrderSavingsToItems(order, totalAllItemsAmount.getAmount());
        fixOrderSavingsRoundingIssues(order, totalOrderAdjustmentDistributed);
        
        // Step 3: Finalize the taxable amounts
        updateTaxableAmountsOnItems(order);
        context.setSeedData(order);

        return context;
    }

    /**
     * Sets the fulfillment amount which includes the relative portion of the total price for 
     * the corresponding order item.
     * 
     * @param order
     * @param partialOrderItemMap
     */
    protected void populateItemTotalAmount(Order order, Map<OrderItem, List<FulfillmentGroupItem>> partialOrderItemMap) {
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                int fgItemQty = fgItem.getQuantity();
                int orderItemQty = orderItem.getQuantity();
                Money totalItemAmount = orderItem.getTotalPrice();

                if (fgItemQty != orderItemQty) {
                    // We need to keep track of all of these items in case we need to distribute a remainder 
                    // to one or more of the items.
                    List<FulfillmentGroupItem> fgItemList = partialOrderItemMap.get(orderItem);
                    if (fgItemList == null) {
                        fgItemList = new ArrayList<FulfillmentGroupItem>();
                        partialOrderItemMap.put(orderItem, fgItemList);
                    }
                    fgItemList.add(fgItem);
                    fgItem.setTotalItemAmount(totalItemAmount.multiply(fgItemQty).divide(orderItemQty));
                } else {
                    fgItem.setTotalItemAmount(totalItemAmount);
                }
            }
        }
    }

    /**
     * Because an item may have multiple price details that don't round cleanly, we may have pennies
     * left over that need to be distributed.
     * 
     * @param order
     * @param partialOrderItemMap
     */
    protected void fixItemTotalRoundingIssues(Order order, Map<OrderItem, List<FulfillmentGroupItem>> partialOrderItemMap) {
        for (OrderItem orderItem : partialOrderItemMap.keySet()) {
            Money totalItemAmount = orderItem.getTotalPrice();
            Money totalFGItemAmount = sumItemAmount(partialOrderItemMap.get(orderItem), order);
            Money amountDiff = totalItemAmount.subtract(totalFGItemAmount);

            if (!(amountDiff.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
                long numApplicationsNeeded = countNumberOfUnits(amountDiff);
                Money unitAmount = getUnitAmount(amountDiff);
                for (FulfillmentGroupItem fgItem : partialOrderItemMap.get(orderItem)) {
                    numApplicationsNeeded = numApplicationsNeeded -
                            applyDifferenceToAmount(fgItem, numApplicationsNeeded, unitAmount);
                    if (numApplicationsNeeded == 0) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns the total price for all fulfillment items.
     * @param order
     * @return
     */
    protected Money calculateTotalPriceForAllFulfillmentItems(Order order) {
        Money totalAllItemsAmount = new Money(order.getCurrency());
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                totalAllItemsAmount = totalAllItemsAmount.add(fgItem.getTotalItemAmount());
            }
        }
        return totalAllItemsAmount;
    }

    /**
     * Distributes the order adjustments (if any) to the individual fulfillment group items.
     * @param order
     * @param totalAllItems
     * @return
     */
    protected Money distributeOrderSavingsToItems(Order order, BigDecimal totalAllItems) {
        Money returnAmount = new Money(order.getCurrency());

        BigDecimal orderAdjAmt = order.getOrderAdjustmentsValue().getAmount();

        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                BigDecimal fgItemAmount = fgItem.getTotalItemAmount().getAmount();
                BigDecimal proratedAdjAmt = totalAllItems.compareTo(BigDecimal.ZERO) == 0 ? totalAllItems : orderAdjAmt.multiply(fgItemAmount).divide(totalAllItems, RoundingMode.FLOOR);
                fgItem.setProratedOrderAdjustmentAmount(new Money(proratedAdjAmt, order.getCurrency()));
                returnAmount = returnAmount.add(fgItem.getProratedOrderAdjustmentAmount());
            }
        }
        return returnAmount;
    }

    /**
     * It is possible due to rounding that the order adjustments do not match the 
     * total.   This method fixes by adding or removing the pennies.
     * @param order
     * @param partialOrderItemMap
     */
    protected void fixOrderSavingsRoundingIssues(Order order, Money totalOrderAdjustmentDistributed) {
        if (!order.getHasOrderAdjustments()) {
            return;
        }

        Money orderAdjustmentTotal = order.getOrderAdjustmentsValue();
        Money amountDiff = orderAdjustmentTotal.subtract(totalOrderAdjustmentDistributed);

        if (!(amountDiff.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
            long numApplicationsNeeded = countNumberOfUnits(amountDiff);
            Money unitAmount = getUnitAmount(amountDiff);

            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    numApplicationsNeeded = numApplicationsNeeded -
                            applyDifferenceToProratedAdj(fgItem, numApplicationsNeeded, unitAmount);
                    if (numApplicationsNeeded == 0) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns the total price for all fulfillment items.
     * @param order
     * @return
     */
    protected void updateTaxableAmountsOnItems(Order order) {
        Money zero = new Money(order.getCurrency());
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fgItem.getOrderItem().isTaxable()) {
                    Money proratedOrderAdjAmt = fgItem.getProratedOrderAdjustmentAmount();
                    if (proratedOrderAdjAmt != null) {
                        fgItem.setTotalItemTaxableAmount(fgItem.getTotalItemAmount().subtract(proratedOrderAdjAmt));
                    } else {
                        fgItem.setTotalItemTaxableAmount(fgItem.getTotalItemAmount());
                    }
                } else {
                    fgItem.setTotalItemTaxableAmount(zero);
                }              
            }
        }        
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
        return Math.round(Math.abs(numUnits));
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

    public long applyDifferenceToAmount(FulfillmentGroupItem fgItem, long numApplicationsNeeded, Money unitAmount) {
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, fgItem.getQuantity()));

        Money oldAmount = fgItem.getTotalItemAmount();
        Money changeToAmount = unitAmount.multiply(numTimesToApply);

        fgItem.setTotalItemAmount(oldAmount.add(changeToAmount));
        return numTimesToApply.longValue();
    }

    public long applyDifferenceToProratedAdj(FulfillmentGroupItem fgItem, long numApplicationsNeeded, Money unitAmount) {
        BigDecimal numTimesToApply = new BigDecimal(Math.min(numApplicationsNeeded, fgItem.getQuantity()));

        Money oldAmount = fgItem.getProratedOrderAdjustmentAmount();
        Money changeToAmount = unitAmount.multiply(numTimesToApply);

        fgItem.setProratedOrderAdjustmentAmount(oldAmount.add(changeToAmount));
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
