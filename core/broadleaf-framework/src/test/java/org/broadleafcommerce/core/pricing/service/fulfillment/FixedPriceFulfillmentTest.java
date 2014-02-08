/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.fulfillment;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FixedPriceFulfillmentPricingProvider;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FulfillmentEstimationResponse;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class FixedPriceFulfillmentTest extends TestCase {

    public void testNullFulfillmentOptionInEstimation() throws Exception {
        Set<FulfillmentOption> options = new HashSet<FulfillmentOption>();
        FixedPriceFulfillmentOption option1 = new FixedPriceFulfillmentOptionImpl();
        option1.setPrice(new Money(BigDecimal.ONE));
        FixedPriceFulfillmentOption option2= new FixedPriceFulfillmentOptionImpl();
        option2.setPrice(new Money(BigDecimal.TEN));
        
        options.add(option1);
        options.add(option2);
        
        FixedPriceFulfillmentPricingProvider provider = new FixedPriceFulfillmentPricingProvider();
        FulfillmentGroup fg = new FulfillmentGroupImpl();
        FulfillmentEstimationResponse response = provider.estimateCostForFulfillmentGroup(fg, options);
        
        for (Entry<? extends FulfillmentOption, Money> entry : response.getFulfillmentOptionPrices().entrySet()) {
            assertEquals(((FixedPriceFulfillmentOption) entry.getKey()).getPrice(), entry.getValue()); 
        }
    }

}
