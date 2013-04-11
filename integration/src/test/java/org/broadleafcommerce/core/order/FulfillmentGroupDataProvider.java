/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
