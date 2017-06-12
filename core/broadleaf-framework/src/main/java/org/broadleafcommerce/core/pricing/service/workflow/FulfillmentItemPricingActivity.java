/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.springframework.stereotype.Component;

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
@Component("blFulfillmentItemPricingActivity")
public class FulfillmentItemPricingActivity extends BaseActivity<ProcessContext<Order>> {
    
    private static final Log LOG = LogFactory.getLog(FulfillmentItemPricingActivity.class);

    public static final int ORDER = 3000;
    
    public FulfillmentItemPricingActivity() {
        setOrder(ORDER);
    }
    
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
        Map<OrderItem,List<FulfillmentGroupItem>> partialOrderItemMap = new HashMap<>();

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
                        fgItemList = new ArrayList<>();
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
     * This method may not be needed because the sum of the item amounts is derived from a double price (OrderItem's total)
     * being multiplied and divided by whole numbers of which guarantees that each item amount is a clean multiple
     * of the price of a single unit of that item. This behavior being enforced in populateItemTotalAmount. So we will
     * never get a fraction of a cent that could cause totalItemAmount and totalFGItemAmount to be different values.
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
