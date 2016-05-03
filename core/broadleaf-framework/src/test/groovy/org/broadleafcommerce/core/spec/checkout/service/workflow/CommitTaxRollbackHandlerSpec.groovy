/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CommitTaxRollbackHandler
import org.broadleafcommerce.core.pricing.service.TaxService
import org.broadleafcommerce.core.pricing.service.exception.TaxException
import org.broadleafcommerce.core.workflow.state.RollbackFailureException
import org.broadleafcommerce.core.workflow.state.RollbackHandler

class CommitTaxRollbackHandlerSpec extends BaseCheckoutRollbackSpec {

    def "Test that Exception is thrown when an error occurs attemping to cancel a tax"() {
        TaxService mockTaxService = Mock()
        mockTaxService.cancelTax(_) >> {throw new TaxException()}
        RollbackHandler rollbackHandler = new CommitTaxRollbackHandler().with() {
            taxService = mockTaxService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "RollbackFailureException is thrown"
        thrown(RollbackFailureException)
    }

    def "Test that rollbackHandler executes with no issues with valid input"() {
        TaxService mockTaxService = Mock()
        RollbackHandler rollbackHandler = new CommitTaxRollbackHandler().with(){
            taxService = mockTaxService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "taxService's cancelTax method is executed once"
        1 * mockTaxService.cancelTax(_)
    }
}
