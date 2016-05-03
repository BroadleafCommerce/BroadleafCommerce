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

import static org.junit.Assert.*

import org.broadleafcommerce.core.checkout.service.workflow.CommitTaxActivity
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.pricing.service.TaxService


class CommitTaxActivitySpec extends BaseCheckoutActivitySpec {

    TaxService mockTaxService = Mock()

    def setup() {
        activity = new CommitTaxActivity().with {
            taxService = mockTaxService
            it
        }
    }

    def "Test that tax is committed when the order says it should not be overridden"() {
        setup: "The order is an instance that doesn't override tax"
        context.seedData.order = new OrderImpl().with {
            taxOverride = false
            it
        }

        when: "The activity is executed"
        context = activity.execute(context)

        then: "The tax service commits tax for the order"
        1 * activity.taxService.commitTaxForOrder(context.seedData.order)
    }

    def "Test that tax is not committed when the order says it should be overridden"() {
        setup: "The order is an instance that overrides tax"
        context.seedData.order = new OrderImpl().with {
            taxOverride = true
            it
        }

        when: "The activity is executed"
        context = activity.execute(context)

        then: "The tax service does not commit tax for the order"
        0 * activity.taxService.commitTaxForOrder(context.seedData.order)
    }
}
