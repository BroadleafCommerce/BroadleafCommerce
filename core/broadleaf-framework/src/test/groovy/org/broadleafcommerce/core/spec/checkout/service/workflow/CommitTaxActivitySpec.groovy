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
package org.broadleafcommerce.core.spec.checkout.service.workflow;

import static org.junit.Assert.*;

import org.broadleafcommerce.core.checkout.service.workflow.CommitTaxActivity
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.pricing.service.TaxService
import org.junit.Test;


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
