#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.order.service.workflow;

import org.broadleafcommerce.${artifactId}.checkout.service.workflow.CheckoutContext;
import org.broadleafcommerce.${artifactId}.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.${artifactId}.workflow.BaseActivity;
import org.broadleafcommerce.${artifactId}.workflow.ProcessContext;

public class MyCompanyOrderManagementCheckoutActivity extends BaseActivity {
	
	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        
        /*
		 * TODO do some logic to submit this order to the external OMS
		 */
		System.out.println(seed.getOrder().getOrderNumber());
		
        return context;
    }

}
