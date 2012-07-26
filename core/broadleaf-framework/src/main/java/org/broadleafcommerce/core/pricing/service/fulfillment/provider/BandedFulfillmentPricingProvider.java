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

package org.broadleafcommerce.core.pricing.service.fulfillment.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.FulfillmentPriceException;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedWeightFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBand;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Used in conjunction with {@link BandedPriceFulfillmentOption} and {@link BandedWeightFulfillmentOption}. 
 *  If 2 bands are configured equal to each other (meaning, there are 2 {@link FulfillmentPriceBand}s that have the 
 *  same retail price minimum or 2 {@link FulfillmentWeightBand}s that have the same minimum weight), 
 *  this will choose the cheaper rate of the 2</p>
 * <p>If the retail total does not fall within a configured price band, the total cost of fulfillment is zero</p>
 * <p>
 * Note: For {@link BandedWeightFulfillmentOption}, this assumes that all of your weights have the same units
 * </p>
 * @author Phillip Verheyden
 * @see {@link BandedPriceFulfillmentOption}, {@link FulfillmentPriceBand}
 */
public class BandedFulfillmentPricingProvider implements FulfillmentPricingProvider {

    protected static final Log LOG = LogFactory.getLog(BandedFulfillmentPricingProvider.class);

    @Override
    public boolean canCalculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, FulfillmentOption option) {
        return (option instanceof BandedPriceFulfillmentOption) || (option instanceof BandedWeightFulfillmentOption);
    }

    @Override
    public FulfillmentGroup calculateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws FulfillmentPriceException {
        if (fulfillmentGroup.getFulfillmentGroupItems().size() == 0) {
            LOG.warn("fulfillment group (" + fulfillmentGroup.getId() + ") does not contain any fulfillment group items. Unable to price banded shipping");
            fulfillmentGroup.setShippingPrice(Money.ZERO);
            fulfillmentGroup.setSaleShippingPrice(Money.ZERO);
            fulfillmentGroup.setRetailShippingPrice(Money.ZERO);
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

        throw new FulfillmentPriceException("An unsupported FulfillmentOption was passed to the calculateCostForFulfillmentGroup method");
    }

    @Override
    public FulfillmentEstimationResponse estimateCostForFulfillmentGroup(FulfillmentGroup fulfillmentGroup, Set<FulfillmentOption> options) throws FulfillmentPriceException {

        //Set up the response object
        FulfillmentEstimationResponse res = new FulfillmentEstimationResponse();
        HashMap<FulfillmentOption, Money> shippingPrices = new HashMap<FulfillmentOption, Money>();
        res.setFulfillmentOptionPrices(shippingPrices);

        for (FulfillmentOption option : options) {
            if (canCalculateCostForFulfillmentGroup(fulfillmentGroup, option)) {
                
                List<? extends FulfillmentBand> bands = null;
                if (option instanceof BandedPriceFulfillmentOption) {
                    bands = ((BandedPriceFulfillmentOption) option).getBands();
                } else if (option instanceof BandedWeightFulfillmentOption) {
                    bands = ((BandedWeightFulfillmentOption) option).getBands();
                }
                
                if (bands == null || bands.isEmpty()) {
                    //Something is misconfigured. There are no bands associated with this fulfillment option
                    throw new IllegalStateException("There were no Fulfillment Price Bands configred for a BandedPriceFulfillmentOption with ID: "
                            + option.getId());
                }

                //Calculate the amount that the band will be applied to
                BigDecimal retailTotal = BigDecimal.ZERO;
                BigDecimal flatTotal = BigDecimal.ZERO;
                BigDecimal weightTotal = BigDecimal.ZERO;
                for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    
                    //If this item has a Sku associated with it which also has a flat rate for this fulfillment option, don't add it to the retail
                    //total but instead tack it onto the final rate
                    boolean addToTotal = true;
                    Sku sku = null;
                    if (fulfillmentGroupItem.getOrderItem() instanceof DiscreteOrderItem) {
                        sku = ((DiscreteOrderItem)fulfillmentGroupItem.getOrderItem()).getSku();
                    } else if (fulfillmentGroupItem.getOrderItem() instanceof BundleOrderItem) {
                        sku = ((BundleOrderItem)fulfillmentGroupItem.getOrderItem()).getSku();
                    }

                    if (sku != null && option.getUseFlatRates()) {                        
                        BigDecimal rate = sku.getFulfillmentFlatRates().get(option);
                        if (rate != null) {
                            addToTotal = false;
                            flatTotal = flatTotal.add(rate);
                        }
                    }
                    
                    if (addToTotal) {
                        BigDecimal price = (fulfillmentGroupItem.getRetailPrice() != null) ? fulfillmentGroupItem.getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity())) : null;
                        if (price == null) {
                            price = fulfillmentGroupItem.getOrderItem().getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity()));
                        }
                        retailTotal = retailTotal.add(price);
                        
                        if (sku != null && sku.getWeight() != null && sku.getWeight().getWeight() != null) {
                            weightTotal = weightTotal.add(sku.getWeight().getWeight());
                        }
                    }
                }

                BigDecimal lowestFulfillmentAmount = BigDecimal.ZERO;
                BigDecimal lowestFulfillmentBandMinimum = BigDecimal.ZERO;
                for (FulfillmentBand band : bands) {
                    
                    BigDecimal bandMinimumAmount = BigDecimal.ZERO;
                    boolean foundMatch = false;
                    if (band instanceof FulfillmentPriceBand) {
                        bandMinimumAmount = ((FulfillmentPriceBand) band).getRetailPriceMinimumAmount();
                        foundMatch = retailTotal.compareTo(bandMinimumAmount) >= 0;
                    } else if (band instanceof FulfillmentWeightBand) {
                        bandMinimumAmount = ((FulfillmentWeightBand) band).getMinimumWeight();
                        foundMatch = weightTotal.compareTo(bandMinimumAmount) >= 0;
                    }
                    
                    if (foundMatch) {
                        //So far, we've found a potential match
                        //Now, determine if this is a percentage or actual amount
                        FulfillmentBandResultAmountType resultAmountType = band.getResultAmountType();
                        BigDecimal bandFulfillmentPrice = null;
                        if (FulfillmentBandResultAmountType.RATE.equals(resultAmountType)) {
                            bandFulfillmentPrice = band.getResultAmount();
                        } else if (FulfillmentBandResultAmountType.PERCENTAGE.equals(resultAmountType)) {
                            //Since this is a percentage, we calculate the result amount based on retailTotal and the band percentage
                            bandFulfillmentPrice = retailTotal.multiply(band.getResultAmount());
                        } else {
                            LOG.warn("Unknown FulfillmentBandResultAmountType: " + resultAmountType.getType() + " Should be RATE or PERCENTAGE. Ignoring.");
                        }
                        
                        if (bandFulfillmentPrice != null) {
                            //If there is a duplicate price band (meaning, 2 price bands are configured with the same miniumum retail price)
                            //then the lowest fulfillment amount should only be updated if the result of the current band being looked at
                            //is cheaper
                            if (lowestFulfillmentBandMinimum.equals(bandMinimumAmount)) {
                                if (bandFulfillmentPrice.compareTo(lowestFulfillmentAmount) <= 0) {
                                    lowestFulfillmentAmount = bandFulfillmentPrice;
                                    lowestFulfillmentBandMinimum = bandMinimumAmount;
                                }
                            } else if (bandMinimumAmount.compareTo(lowestFulfillmentBandMinimum) > 0) {
                                lowestFulfillmentAmount = bandFulfillmentPrice;
                                lowestFulfillmentBandMinimum = bandMinimumAmount;
                            }
                            
                        }
                    }
                }
                
                //add the flat rate amount calculated on the Sku
                lowestFulfillmentAmount = lowestFulfillmentAmount.add(flatTotal);

                shippingPrices.put(option, new Money(lowestFulfillmentAmount));
            }
        }

        return res;
    }

}
