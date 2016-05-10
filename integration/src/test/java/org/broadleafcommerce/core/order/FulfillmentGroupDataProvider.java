/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.order;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.pricing.service.workflow.type.ShippingServiceType;
import org.testng.annotations.DataProvider;

public class FulfillmentGroupDataProvider {

    @DataProvider(name = "basicFulfillmentGroup")
    public static Object[][] provideBasicSalesFulfillmentGroup() {
        FulfillmentGroupImpl sos = new FulfillmentGroupImpl();
        sos.setReferenceNumber("123456789");
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        sos.setFulfillmentOption(option);
        return new Object[][] { { sos } };
    }
    
    @DataProvider(name = "basicFulfillmentGroupLegacy")
    public static Object[][] provideBasicSalesFulfillmentGroupLegacy() {
        FulfillmentGroupImpl sos = new FulfillmentGroupImpl();
        sos.setReferenceNumber("123456789");
        sos.setMethod("standard");
        sos.setService(ShippingServiceType.BANDED_SHIPPING.getType());
        return new Object[][] { { sos } };
    }
}
