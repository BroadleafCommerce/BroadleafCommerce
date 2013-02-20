package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class DistributeOrderSavingsActivityTest extends TestCase {

    private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    private DistributeOrderSavingsActivity distributeSavingsActivity = new DistributeOrderSavingsActivity();

    protected Money sumProratedOfferAdjustments(Order order) {
        Money returnVal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            returnVal = returnVal.add(orderItem.getProratedOrderAdjustment());
        }
        return returnVal;
    }

    public void testZeroOrderSavings() throws Exception {
        Order order = dataProvider.createBasicOrder();
        PricingContext context = new PricingContext();
        context.setSeedData(order);

        distributeSavingsActivity.execute(context);
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

        distributeSavingsActivity.execute(context);

        // Each item is equally priced, so the adjustment should be .20 per item.
        Money proratedAdjustment = new Money(".20");
        for (OrderItem orderItem : order.getOrderItems()) {
            assertTrue(orderItem.getProratedOrderAdjustment().compareTo(
                    proratedAdjustment.multiply(orderItem.getQuantity())) == 0);
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

        distributeSavingsActivity.execute(context);

        // Each item is equally priced, so the adjustment should be .20 per item.
        Money adj1 = new Money(".31");
        Money adj2 = new Money(".69");
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getSalePrice().equals(new Money("19.99"))) {
                assertTrue(orderItem.getProratedOrderAdjustment().equals(adj1));
            } else {
                assertTrue(orderItem.getProratedOrderAdjustment().equals(adj2));
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

        distributeSavingsActivity.execute(context);

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

        distributeSavingsActivity.execute(context);

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

        distributeSavingsActivity.execute(context);

        assertTrue(sumProratedOfferAdjustments(order).equals(
                new Money(new BigDecimal("1"), order.getCurrency())));
    }
}
