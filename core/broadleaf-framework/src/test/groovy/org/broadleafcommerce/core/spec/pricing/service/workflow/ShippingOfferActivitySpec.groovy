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

import org.broadleafcommerce.core.offer.service.ShippingOfferService
import org.broadleafcommerce.core.pricing.service.workflow.ShippingOfferActivity


class ShippingOfferActivitySpec extends BasePricingActivitySpec {

    def "Test execution of ShippingOfferActivity"() {
        setup: "Prepare a mock of ShippingOfferService"
        ShippingOfferService mockShippingOfferService = Mock()
        activity = new ShippingOfferActivity().with() {
            shippingOfferService = mockShippingOfferService
            it
        }

        when: "I execute ShippingOfferActivity"
        context = activity.execute(context)

        then: "mockShippingOfferService is invoked once"
        1 * mockShippingOfferService.reviewOffers(_)
    }
}
