/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.pricing.service.module;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.util.money.Money;

public class SimpleShippingModule implements ShippingModule {

    public static final String MODULENAME = "simpleShippingModule";

    protected String name = MODULENAME;

    private double flatRateShippingPrice;

    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        Money shipping = new Money(flatRateShippingPrice);
        fulfillmentGroup.setShippingPrice(shipping);
        fulfillmentGroup.setRetailShippingPrice(shipping);
        fulfillmentGroup.setSaleShippingPrice(shipping);

        return fulfillmentGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFlatRateShippingPrice() {
        return flatRateShippingPrice;
    }

    public void setFlatRateShippingPrice(double flatRateShippingPrice) {
        this.flatRateShippingPrice = flatRateShippingPrice;
    }

}
