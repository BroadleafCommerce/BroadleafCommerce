package org.broadleafcommerce.promotion.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class DistributeItemOffers extends BaseActivity {

	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		Order order = ((OfferContext)context).getSeedData();
		
		// TODO Add code to distribute item offers
		context.setSeedData(order);		
		return context;
	}

}
