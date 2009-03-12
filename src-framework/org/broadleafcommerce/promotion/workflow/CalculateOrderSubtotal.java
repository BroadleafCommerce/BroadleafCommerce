package org.broadleafcommerce.promotion.workflow;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateOrderSubtotal extends BaseActivity {

	@Resource
	private OrderService orderService;
	
	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		Order order = ((OfferContext)context).getSeedData();

		BigDecimal subTotal = new BigDecimal("0");
		List<OrderItem> orderItems =  orderService.findItemsForOrder(order);
		
		for (OrderItem orderItem : orderItems) {
			subTotal.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
		}
		
		order.setSubTotal(subTotal);
		
		context.setSeedData(order);		
		return context;
	}

}
