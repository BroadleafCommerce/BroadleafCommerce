/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.pricing.service.workflow;

import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.math.BigDecimal;

public class FulfillmentItemPricingActivityTest extends TestCase {

    private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    private FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity();

    protected Money sumProratedOfferAdjustments(Order order) {
        Money returnVal = new Money(order.getCurrency());
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fulfillmentGroupItem.getProratedOrderAdjustmentAmount() != null) {
                    returnVal = returnVal.add(fulfillmentGroupItem.getProratedOrderAdjustmentAmount());
                }
            }
        }
        return returnVal;
    }

    public void testZeroOrderSavings() throws Exception {
        Order order = dataProvider.createBasicOrder();
        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);
        assertTrue(sumProratedOfferAdjustments(order).getAmount().compareTo(BigDecimal.ZERO) == 0);
    }

    public void testDistributeOneDollarAcrossFiveEqualItems() throws Exception {
        Order order = dataProvider.createBasicOrder();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setSalePrice(new Money(10D));
            orderItem.getOrderItemPriceDetails().clear();
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }

        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        order.getOrderAdjustments().add(adjustment);
        adjustment.setOrder(order);
        order.setSubTotal(subTotal);

        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);

        // Each item is equally priced, so the adjustment should be .20 per item.
        Money proratedAdjustment = new Money(".20");
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                assertTrue(fulfillmentGroupItem.getProratedOrderAdjustmentAmount().compareTo(
                        proratedAdjustment.multiply(fulfillmentGroupItem.getQuantity())) == 0);
            }
        }
    }

    public void testDistributeOneDollarAcrossFiveItems() throws Exception {
        Order order = dataProvider.createBasicOrder();

        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);

        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);

        Money adj1 = new Money(".31");
        Money adj2 = new Money(".69");

        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fulfillmentGroupItem.getSalePrice().equals(new Money("19.99"))) {
                    assertTrue(fulfillmentGroupItem.getProratedOrderAdjustmentAmount().equals(adj1));
                } else {
                    assertTrue(fulfillmentGroupItem.getProratedOrderAdjustmentAmount().equals(adj2));
                }
            }
        }
    }

    public void testRoundingRequired() throws Exception {
        Order order = dataProvider.createBasicOrder();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.getOrderItemPriceDetails().clear();
            orderItem.setQuantity(2);
            orderItem.setSalePrice(new Money(10D));
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);

        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal(".05"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);

        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);

        assertTrue(sumProratedOfferAdjustments(order).equals(
                new Money(new BigDecimal(".05"), order.getCurrency())));
    }

    public void testBundleDistribution() throws Exception {
        Order order = dataProvider.createOrderWithBundle();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);

        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);

        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);

        assertTrue(sumProratedOfferAdjustments(order).equals(
                new Money(new BigDecimal("1"), order.getCurrency())));
    }

    public void testBundleDistributionWithoutItemSum() throws Exception {
        Order order = dataProvider.createOrderWithBundle();

        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bItem = (BundleOrderItem) orderItem;
                bItem.getProductBundle().setPricingModel(ProductBundlePricingModelType.BUNDLE);
            }
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);

        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);

        PricingContext context = new PricingContext();
        context.setSeedData(order);

        fulfillmentItemPricingActivity.execute(context);

        assertTrue(sumProratedOfferAdjustments(order).equals(
                new Money(new BigDecimal("1"), order.getCurrency())));
    }
}
