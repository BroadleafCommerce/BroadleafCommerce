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

import org.broadleafcommerce.core.offer.domain.Offer
import org.broadleafcommerce.core.offer.domain.OfferCode
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl
import org.broadleafcommerce.core.offer.domain.OfferImpl
import org.broadleafcommerce.core.offer.service.OfferAuditService
import org.broadleafcommerce.core.offer.service.OfferService
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException
import org.broadleafcommerce.core.offer.service.workflow.VerifyCustomerMaxOfferUsesActivity
import org.broadleafcommerce.core.order.domain.Order

class VerifyCustomerMaxOfferUsesActivitySpec extends BaseCheckoutActivitySpec {

    Set<Offer> appliedOffers
    OfferService mockOfferService
    OfferAuditService mockOfferAuditService

    def setup() {
        Offer testOffer = new OfferImpl()
        testOffer.maxUsesPerCustomer = 2
        appliedOffers = [testOffer] as Set

        mockOfferService = Mock()
        mockOfferService.getUniqueOffersFromOrder(_) >> {Order order -> appliedOffers}

        mockOfferAuditService = Mock()

        OfferCode offerCode = new OfferCodeImpl()
        offerCode.maxUses = 2
        context.seedData.order.addOfferCode(offerCode)
    }

    def "Test that exception is thrown when one customer has used an offer more times than is allowed"() {
        setup:
        mockOfferAuditService.countUsesByCustomer(_,_) >> 3
        mockOfferAuditService.countOfferCodeUses(_) >> 1
        activity = new VerifyCustomerMaxOfferUsesActivity().with {
            offerService = mockOfferService
            offerAuditService = mockOfferAuditService
            it
        }

        when: "I execute the VerifyCustomerMaxOfferUsesActivity"
        context = activity.execute(context)

        then: "OfferMaxUseExceededException gets thrown"
        thrown(OfferMaxUseExceededException)
    }

    def "Test that exception is thrown when an offer code has been used the maximum number of times"() {
        setup:
        mockOfferAuditService.countUsesByCustomer(_,_) >> 1
        mockOfferAuditService.countOfferCodeUses(_) >> 3
        activity = new VerifyCustomerMaxOfferUsesActivity().with {
            offerService = mockOfferService
            offerAuditService = mockOfferAuditService
            it
        }

        when: "I execute the VerifyCustomerMaxOfferUsesActivity"
        context = activity.execute(context)

        then: "OfferMaxUseExceededException gets thrown"
        thrown(OfferMaxUseExceededException)
    }

    def "Test that no exception is thrown on valid state"() {
        setup:
        mockOfferAuditService.countUsesByCustomer(_,_) >> 1
        mockOfferAuditService.countOfferCodeUses(_) >> 1
        activity = new VerifyCustomerMaxOfferUsesActivity().with {
            offerService = mockOfferService
            offerAuditService = mockOfferAuditService
            it
        }

        when: "I execute the VerifyCustomerMaxOfferUsesActivity"
        context = activity.execute(context)

        then: "No exception gets thrown"
        notThrown(OfferMaxUseExceededException)
    }
}
