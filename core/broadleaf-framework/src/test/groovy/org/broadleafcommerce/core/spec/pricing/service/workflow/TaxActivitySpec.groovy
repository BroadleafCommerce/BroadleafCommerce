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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.core.pricing.service.TaxService
import org.broadleafcommerce.core.pricing.service.module.TaxModule
import org.broadleafcommerce.core.pricing.service.workflow.TaxActivity


class TaxActivitySpec extends BasePricingActivitySpec {

    TaxModule mockTaxModule
    TaxService mockTaxService

    def "Test TaxActivity with a provided TaxService and TaxModule"() {
        setup: "Prepare mock objects"
        mockTaxModule = Mock()
        mockTaxService = Mock()
        activity = new TaxActivity().with() {
            taxModule = mockTaxModule
            taxService = mockTaxService
            it
        }

        when: "I execute TaxActivity"
        context = activity.execute(context)

        then: "mockTaxService should be invoked but mockTaxModule should not"
        0 * mockTaxModule.calculateTaxForOrder(_) >> context.seedData
        1 * mockTaxService.calculateTaxForOrder(_) >> context.seedData
    }

    def "Test TaxActivity with a provided TaxModule"() {
        setup: "Prepare mock object"
        mockTaxModule = Mock()
        activity = new TaxActivity().with() {
            taxModule = mockTaxModule
            it
        }

        when: "I execute TaxActivity"
        context = activity.execute(context)

        then: "mockTaxModule should be invoked"
        1 * mockTaxModule.calculateTaxForOrder(_) >> context.seedData
    }

    def "Test TaxActivity with a provided TaxService"() {
        setup: "Prepare mock object"
        mockTaxService = Mock()
        activity = new TaxActivity().with() {
            taxService = mockTaxService
            it
        }

        when: "I execute TaxActivity"
        context = activity.execute(context)

        then: "mockTaxService should be invoked"
        1 * mockTaxService.calculateTaxForOrder(_) >> context.seedData
    }
}
