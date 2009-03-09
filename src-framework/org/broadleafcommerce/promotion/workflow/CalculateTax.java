package org.broadleafcommerce.promotion.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateTax extends BaseActivity {

	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		Order order = ((OfferContext)context).getSeedData();
		
		// TODO Add code to calculate tax
		context.setSeedData(order);		
		return context;
	}

}
