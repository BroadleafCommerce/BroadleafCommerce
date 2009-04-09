package org.broadleafcommerce.pricing.workflow;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateOrderSubtotal extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((OfferContext) context).getSeedData();
        Money subTotal = new Money(BigDecimal.ZERO);
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            subTotal.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        }
        order.setSubTotal(subTotal);
        context.setSeedData(order);
        return context;
    }
}
