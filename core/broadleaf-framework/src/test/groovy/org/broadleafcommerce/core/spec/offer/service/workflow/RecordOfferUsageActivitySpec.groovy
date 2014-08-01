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

import org.broadleafcommerce.core.offer.domain.*
import org.broadleafcommerce.core.offer.service.OfferAuditService
import org.broadleafcommerce.core.offer.service.OfferService
import org.broadleafcommerce.core.offer.service.workflow.RecordOfferUsageActivity
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.spec.checkout.service.workflow.BaseCheckoutActivitySpec
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal

/**
 * @author Elbert Bautista (elbertbautista)
 */
class RecordOfferUsageActivitySpec extends BaseCheckoutActivitySpec {

    Set<Offer> appliedOffers = new HashSet<Offer>()

    def setup() {
        def rollbackStateLocal = new RollbackStateLocal();
        rollbackStateLocal.setThreadId("SPOCK_THREAD");
        rollbackStateLocal.setWorkflowId("TEST");
        RollbackStateLocal.setRollbackStateLocal(rollbackStateLocal);

        new ActivityStateManagerImpl().init()

        Offer testOffer = new OfferImpl()
        testOffer.id = 1
        appliedOffers << testOffer
    }

    def "Test Offer Audits are registered with the Activity State Manager"() {
        setup: "I have one offer on the order"

        //Initiate Mocks
        OfferAudit offerAudit = new OfferAuditImpl()

        OfferAuditService mockAuditService = Mock()
        mockAuditService.create() >> offerAudit
        mockAuditService.save(_) >> {OfferAudit audit -> audit}

        OfferService mockOfferService = Mock()
        mockOfferService.getUniqueOffersFromOrder(_) >> {Order order -> appliedOffers}
        mockOfferService.getOffersRetrievedFromCodes(*_) >> new HashMap<Offer, OfferCode>()

        activity = new RecordOfferUsageActivity().with {
            offerService = mockOfferService
            offerAuditService = mockAuditService
            it
        }

        when: "I execute the RecordOfferUsageActivity"
        context = activity.execute(context);

        then: "There should be one OfferAudit in the rollback state"
        Stack<ActivityStateManagerImpl.StateContainer> containers = ActivityStateManagerImpl.stateManager.stateMap.get("SPOCK_THREAD_TEST")
        containers.size() == 1
        Map<String, Object> stateItems = containers.pop().getStateItems()
        List<OfferAudit> audits = stateItems.get(RecordOfferUsageActivity.SAVED_AUDITS)
        audits.size() == 1
        audits.get(0) == offerAudit
    }

}