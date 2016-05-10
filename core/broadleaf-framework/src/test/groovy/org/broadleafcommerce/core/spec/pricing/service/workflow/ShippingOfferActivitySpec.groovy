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
