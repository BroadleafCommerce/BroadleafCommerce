package org.broadleafcommerce.pricing.workflow;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class OrderSubtotalActivity extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        Money subTotal = new Money(0D);
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            subTotal = subTotal.add(orderItem.getPrice().multiply(orderItem.getQuantity()));
        }
        order.setSubTotal(subTotal);
        context.setSeedData(order);

        return context;
    }

    public static void main(String[] items) {
        //BigDecimal dec = new BigDecimal(10D);
        //System.out.println(dec.multiply(1));
    }
}
