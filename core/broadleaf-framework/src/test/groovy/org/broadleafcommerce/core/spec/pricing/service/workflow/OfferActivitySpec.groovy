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

import org.broadleafcommerce.core.offer.domain.OfferCodeImpl
import org.broadleafcommerce.core.offer.service.OfferService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.pricing.service.workflow.OfferActivity

class OfferActivitySpec extends BasePricingActivitySpec {

    OfferService mockOfferService
    OrderService mockOrderService

    def setup() {
        mockOfferService = Mock()
        mockOrderService = Mock()
    }

    def"Test a valid run with valid data"() {

        activity = new OfferActivity().with {
            offerService = mockOfferService
            orderService = mockOrderService
            it
        }

        when: "I execute the OfferActivity"
        context = activity.execute(context)

        then: "orderService's addOfferCodes should have run and offerService's buildOfferListForOrder as well as applyAndSaveOffersToOrder should have run"
        1 * mockOfferService.buildOfferCodeListForCustomer(_) >> [new OfferCodeImpl()]
        1 * mockOfferService.applyAndSaveOffersToOrder(_, _) >> context.seedData
        1 * mockOrderService.addOfferCodes(_, _, _) >> context.seedData
        context.seedData != null
    }
}
