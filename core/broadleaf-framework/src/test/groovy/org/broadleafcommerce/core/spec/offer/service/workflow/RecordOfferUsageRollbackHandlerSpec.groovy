package org.broadleafcommerce.core.spec.offer.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed
import org.broadleafcommerce.core.offer.domain.OfferAudit
import org.broadleafcommerce.core.offer.domain.OfferAuditImpl
import org.broadleafcommerce.core.offer.service.OfferAuditService
import org.broadleafcommerce.core.offer.service.workflow.RecordOfferUsageActivity
import org.broadleafcommerce.core.offer.service.workflow.RecordOfferUsageRollbackHandler
import org.broadleafcommerce.core.spec.checkout.service.workflow.BaseCheckoutRollbackSpec
import org.broadleafcommerce.core.workflow.state.RollbackHandler

class RecordOfferUsageRollbackHandlerSpec extends BaseCheckoutRollbackSpec {

	//need to set up the list of OfferAudits in the stateConfiguration under RecordOfferUsageActivity.SAVED_AUDITS, then delete them
	
	def "Test that the SAVED_AUDITS in the stateConfiguration are deleted"(){
		setup:"Placing one OfferAudit into the stateConfiguration"
		OfferAudit offerAudit = new OfferAuditImpl()
		List<OfferAudit> offerAudits = new ArrayList()
		offerAudits.add(offerAudit)
		stateConfiguration = new HashMap<String, List>()
		stateConfiguration.put(RecordOfferUsageActivity.SAVED_AUDITS, offerAudits)
		OfferAuditService mockOfferAuditService = Mock()
		RollbackHandler<CheckoutSeed> rollbackHandler = new RecordOfferUsageRollbackHandler().with{
			offerAuditService = mockOfferAuditService
			it
		}
		
		when:"rollbackState is executed"
		rollbackHandler.rollbackState(activity, context, stateConfiguration)
		
		then:"stateConfiguration's savedAudit's should be empty"
		1 * mockOfferAuditService.delete(_)
	}
	
	
}
