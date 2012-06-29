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

package org.broadleafcommerce.core.pricing.service;

import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentProcessor;

import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
public interface FulfillmentService {

    /**
     * 
     * @param fulfillmentGroup
     * @return
     * @throws ShippingPriceException if <b>fulfillmentGroup</b> does not have a FulfillmentOption associated to it or
     * if there was no processor found to calculate costs for <b>fulfillmentGroup</b>
     */
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException;

    /**
     * 
     * @param fulfillmentGroup
     * @param option
     * @return
     * @throws ShippingPriceException if no processor was found to estimate costs for <b>fulfillmentGroup</b> with the given <b>option</b>
     */
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) throws ShippingPriceException;
    
    public List<FulfillmentProcessor> getFulfillmentProcessors();

    public void setFulfillmentProcessors(List<FulfillmentProcessor> fulfillmentProcessors);

}
