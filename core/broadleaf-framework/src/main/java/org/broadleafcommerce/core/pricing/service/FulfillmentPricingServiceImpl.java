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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.core.catalog.domain.SkuFee;
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.pricing.service.fulfillment.processor.FulfillmentPricingProvider;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FulfillmentPricingServiceImpl implements FulfillmentPricingService {

    protected List<FulfillmentPricingProvider> providers;
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {

        if (fulfillmentGroup.getFulfillmentOption() == null) {
            //There is no shipping option yet. We'll simply set the shipping price to zero for now, and continue.
            fulfillmentGroup.setRetailShippingPrice(new Money(0D));
            fulfillmentGroup.setShippingPrice(new Money(0D));
            fulfillmentGroup.setSaleShippingPrice(new Money(0D));
            return fulfillmentGroup;
        }
        
        //create and associate all the Fulfillment Fees
        List<FulfillmentGroupFee> fulfillmentFees = new ArrayList<FulfillmentGroupFee>();
        for (FulfillmentGroupItem item : fulfillmentGroup.getFulfillmentGroupItems()) {
            List<SkuFee> fees = null;
            if (item.getOrderItem() instanceof BundleOrderItem) {
                fees = ((BundleOrderItem)item.getOrderItem()).getSku().getFees();
            } else if (item.getOrderItem() instanceof DiscreteOrderItem) {
                fees = ((DiscreteOrderItem)item.getOrderItem()).getSku().getFees();
            }
            
            if (fees != null) {
                for (SkuFee fee : fees) {
                    if (SkuFeeType.FULFILLMENT.equals(fee.getFeeType())) {
                        FulfillmentGroupFee fulfillmentFee = fulfillmentGroupService.createFulfillmentGroupFee();
                        fulfillmentFee.setName(fee.getName());
                        fulfillmentFee.setTaxable(fee.getTaxable());
                        fulfillmentFee.setAmount(fee.getAmount());
                        
                        fulfillmentFees.add(fulfillmentFee);
                    }
                }
            }
        }
        
        if (fulfillmentFees.size() > 0) {
            fulfillmentGroup.setFulfillmentGroupFees(fulfillmentFees);
            fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
        }

        for (FulfillmentPricingProvider processor : providers) {
            if (processor.canCalculateCostForFulfillmentGroup(fulfillmentGroup, fulfillmentGroup.getFulfillmentOption())) {
                return processor.calculateCostForFulfillmentGroup(fulfillmentGroup);
            }
        }

        throw new ShippingPriceException("No valid processor was found to calculate the FulfillmentGroup cost with " +
        		"FulfillmentOption id: " + fulfillmentGroup.getFulfillmentOption().getId() + 
        				" and name: " + fulfillmentGroup.getFulfillmentOption().getName());
    }
    
    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, Set<FulfillmentOption> options) throws ShippingPriceException {
        FulfillmentEstimationResponse response = new FulfillmentEstimationResponse();
        HashMap<FulfillmentOption, Money> prices = new HashMap<FulfillmentOption, Money>();
        response.setFulfillmentOptionPrices(prices);
        for (FulfillmentPricingProvider processor : providers) {
            //Leave it up to the providers to determine if they can respond to a pricing estimate.  If they can't, or if one or more of the options that are passed in can't be responded
            //to, then the response from the pricing provider should not include the options that it could not respond to.
            FulfillmentEstimationResponse processorResponse = processor.estimateCostForFulfillmentGroup(fulfillmentGroup, options);
            if (processorResponse != null
                    && processorResponse.getFulfillmentOptionPrices() != null
                    && processorResponse.getFulfillmentOptionPrices().size() > 0) {
                prices.putAll(processorResponse.getFulfillmentOptionPrices());
            }
        }

        return response;
    }

    @Override
    public List<FulfillmentPricingProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<FulfillmentPricingProvider> providers) {
        this.providers = providers;
    }

}
