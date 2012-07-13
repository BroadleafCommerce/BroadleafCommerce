/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.pricing.service.fulfillment.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBand;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used in conjunction with {@link BandedPriceFulfillmentOption}.
 * 
 * @author Phillip Verheyden
 * @see {@link BandedPriceFulfillmentOption}, {@link FulfillmentPriceBand}
 */
public class BandedPriceFulfillmentPricingProvider implements FulfillmentPricingProvider {

    protected static final Log LOG = LogFactory.getLog(BandedPriceFulfillmentPricingProvider.class);

    @Override
    public boolean canCalculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        return (option instanceof BandedPriceFulfillmentOption);
    }

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {
        if (fulfillmentGroup.getFulfillmentGroupItems().size() == 0) {
            LOG.warn("fulfillment group (" + fulfillmentGroup.getId() + ") does not contain any fulfillment group items. Unable to price banded shipping");
            fulfillmentGroup.setShippingPrice(new Money(0D));
            fulfillmentGroup.setSaleShippingPrice(new Money(0D));
            fulfillmentGroup.setRetailShippingPrice(new Money(0D));
            return fulfillmentGroup;
        }

        if (canCalculateCostForFulfillmentGroup(fulfillmentGroup, fulfillmentGroup.getFulfillmentOption())) {
            //In this case, the estimation logic is the same as calculation logic. Call the estimation service to get the prices.
            HashSet<FulfillmentOption> options = new HashSet<FulfillmentOption>();
            options.add(fulfillmentGroup.getFulfillmentOption());
            FulfillmentEstimationResponse response = estimateCostForFulfillmentGroup(fulfillmentGroup, options);
            fulfillmentGroup.setSaleShippingPrice(response.getFulfillmentOptionPrices().get(fulfillmentGroup.getFulfillmentOption()));
            fulfillmentGroup.setRetailShippingPrice(response.getFulfillmentOptionPrices().get(fulfillmentGroup.getFulfillmentOption()));
            fulfillmentGroup.setShippingPrice(response.getFulfillmentOptionPrices().get(fulfillmentGroup.getFulfillmentOption()));

            return fulfillmentGroup;
        }

        throw new ShippingPriceException("An unsupported FulfillmentOption was passed to the calculateCostForFulfillmentGroup method");
    }

    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, Set<FulfillmentOption> options) throws ShippingPriceException {

        //Set up the response object
        FulfillmentEstimationResponse res = new FulfillmentEstimationResponse();
        HashMap<BandedPriceFulfillmentOption, Money> shippingPrices = new HashMap<BandedPriceFulfillmentOption, Money>();
        res.setFulfillmentOptionPrices(shippingPrices);

        for (FulfillmentOption option : options) {
            if (canCalculateCostForFulfillmentGroup(fulfillmentGroup, option)) {
                BandedPriceFulfillmentOption bandedPriceFulfillmentOption = (BandedPriceFulfillmentOption)option;
                List<FulfillmentPriceBand> bands = bandedPriceFulfillmentOption.getBands();
                if (bands == null || bands.isEmpty()) {
                    //Something is misconfigured. There are no bands associated with this fulfillment option
                    throw new IllegalStateException("There were no Fulfillment Price Bands configred for a BandedPriceFulfillmentOption with ID: "
                            + bandedPriceFulfillmentOption.getId());
                }

                //Calculate the amount that the band will be applied to
                BigDecimal retailTotal = new BigDecimal(0D);
                for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    BigDecimal price = (fulfillmentGroupItem.getRetailPrice() != null) ? fulfillmentGroupItem.getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity())) : null;
                    if (price == null) {
                        price = fulfillmentGroupItem.getOrderItem().getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity()));
                    }
                    retailTotal = retailTotal.add(price);
                }

                BigDecimal fulfillmentAmount = new BigDecimal(0D);
                for (FulfillmentPriceBand band : bands) {
                    BigDecimal bandRetailPriceMinimumAmount = band.getRetailPriceMinimumAmount();
                    if (retailTotal.compareTo(bandRetailPriceMinimumAmount) >= 0) {
                        //So far, we've found a potenial match
                        //Now, determine if this is a percentage or actual amount
                        FulfillmentBandResultAmountType resultAmountType = band.getResultAmountType();
                        if (FulfillmentBandResultAmountType.RATE.equals(resultAmountType)) {
                            if (band.getResultAmount().compareTo(fulfillmentAmount) <= 0) {
                                //We found a matching option that is cheaper than what we found before
                                fulfillmentAmount = band.getResultAmount();
                            }
                        } else if (FulfillmentBandResultAmountType.PERCENTAGE.equals(resultAmountType)) {
                            //Since this is a percentage, we calculate the result amount based on retailTotal and the band percentage
                            BigDecimal resultAmount = retailTotal.multiply(band.getResultAmount());
                            if (resultAmount.compareTo(fulfillmentAmount) <= 0) {
                                //We found a matching option that is cheaper than what we found before
                                fulfillmentAmount = resultAmount;
                            }
                        } else {
                            LOG.warn("Unknown FulfillmentBandResultAmountType: " + resultAmountType.getType() + " Should be RATE or PERCENTAGE. Ignoring.");
                        }
                    }
                }

                shippingPrices.put(bandedPriceFulfillmentOption, new Money(fulfillmentAmount));
            }
        }

        return res;
    }

}
