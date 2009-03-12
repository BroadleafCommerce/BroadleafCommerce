package org.broadleafcommerce.pricing.workflow;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class DistributeItemOffers extends BaseActivity {

	@Resource
	private OrderService orderService;

	@Resource
	private OfferService offerService;
	
	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		Order order = ((OfferContext)context).getSeedData();
		
		// TODO Add code to distribute item offers
		List<OrderItem> orderItems = orderService.findItemsForOrder(order);
		for (OrderItem orderItem : orderItems) {
			List<OfferAudit> appliedOffers = offerService.findAppliedOffers(orderItem.getCandidateOffers(), orderItem);
			orderItem.setAppliedOffers(appliedOffers);
		}
		
		context.setSeedData(order);
		return context;
	}
	
	
}
