/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
