package org.broadleafcommerce.core.spec.order.service.workflow

import org.broadleafcommerce.core.order.service.workflow.VerifyFulfillmentGroupItemsActivity
import org.broadleafcommerce.core.order.strategy.FulfillmentGroupItemStrategy


class VerifyFulfillmentGroupItemsActivitySpec extends BaseOrderWorkflowSpec {
    
    FulfillmentGroupItemStrategy mockFgItemStrategy = Mock()
    
    def setup(){
        activity = Spy(VerifyFulfillmentGroupItemsActivity).with{
            fgItemStrategy = mockFgItemStrategy
            it
        }
    }
    
    def "If the process is executed, the request is verified by the fgItemStrategy"(){
        when:
        context = activity.execute(context)
        
        then:
        1 * mockFgItemStrategy.verify(context.getSeedData()) >> context.getSeedData()
    }
}
