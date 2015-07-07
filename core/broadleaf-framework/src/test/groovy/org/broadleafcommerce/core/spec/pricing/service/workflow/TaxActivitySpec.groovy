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
