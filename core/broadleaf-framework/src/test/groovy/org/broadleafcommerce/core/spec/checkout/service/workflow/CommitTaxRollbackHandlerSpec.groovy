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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CommitTaxRollbackHandler
import org.broadleafcommerce.core.pricing.service.TaxService
import org.broadleafcommerce.core.pricing.service.exception.TaxException
import org.broadleafcommerce.core.workflow.state.RollbackFailureException
import org.broadleafcommerce.core.workflow.state.RollbackHandler

class CommitTaxRollbackHandlerSpec extends BaseCheckoutRollbackSpec{

	def"Test that Exception is thrown when an error occurs attemping to cancel a tax"(){
		TaxService mockTaxService = Mock()
		mockTaxService.cancelTax(_) >> {throw new TaxException()}
		RollbackHandler rollbackHandler = new CommitTaxRollbackHandler().with(){
			taxService = mockTaxService
			it
		}
		
		when:"rollbackState is executed"
		rollbackHandler.rollbackState(activity, context, stateConfiguration)
		
		then:"RollbackFailureException is thrown"
		thrown(RollbackFailureException)
	}
	
	def"Test that rollbackHandler executes with no issues with valid input"(){
		TaxService mockTaxService = Mock()
		RollbackHandler rollbackHandler = new CommitTaxRollbackHandler().with(){
			taxService = mockTaxService
			it
		}
		when:"rollbackState is executed"
		rollbackHandler.rollbackState(activity, context, stateConfiguration)
		
		then:"taxService's cancelTax method is executed once"
		1 * mockTaxService.cancelTax(_)
		
	}
}
