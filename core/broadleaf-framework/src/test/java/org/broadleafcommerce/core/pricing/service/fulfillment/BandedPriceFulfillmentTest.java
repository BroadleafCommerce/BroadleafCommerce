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

package org.broadleafcommerce.core.pricing.service.fulfillment;

import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.domain.Weight;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedWeightFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedWeightFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBandImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentWeightBandImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Phillip Verheyden
 */
public class BandedPriceFulfillmentTest extends TestCase {

    public void testPriceBandRate() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "10", "20", "30" },
                new String[] { "10", "20", "30" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE });
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2, option)));
        assertEquals(Money.ZERO, calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testMinimumAmountsWithZero() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "0", "20", "30" },
                new String[] { "10", "20", "30" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE });
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2, option)));
        assertEquals(new Money("10.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testPriceBandPercentage() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "10", "30", "20" },
                new String[] { ".10", ".20", ".30" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.PERCENTAGE,
                        FulfillmentBandResultAmountType.PERCENTAGE,
                        FulfillmentBandResultAmountType.PERCENTAGE });

        assertEquals(new Money("1.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("15.00"), 3, option)));
        assertEquals(new Money("6.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testPriceBandRatesWithPercentages() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "150", "30", "20", "150", "10", "9", "80" },
                new String[] { "50", "20", ".30", "20", ".10", "5", ".5" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.PERCENTAGE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.PERCENTAGE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.PERCENTAGE });

        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("35.00"), 5, option)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("9999.00"), 9, option)));
        assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        assertEquals(new Money("1.80"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, option)));
        assertEquals(new Money("50"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
        assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("66"), 6, option)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("150"), 5, option)));
    }

    /**
     * If the retail price sum falls within 2 bands but with the same retail minimum, the lowest price should be selected
     */
    public void testLowestPriceSelection() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "10", "10", "10" },
                new String[] { "30", "20", "10" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE });
        assertEquals(calculationResponse(option, createCandidateOrder(new BigDecimal("10.00"), 2, option)), new Money("10.00"));
    }

    public void testFlatRatesExclusive() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "100" },
                new String[] { "30" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE });

        assertEquals(new Money("45"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 3, new String[] { "10", "15", "20" }, null, option)));
        assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("80.00"), 1, new String[] { "5" }, null, option)));
        assertEquals(new Money("10"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 2, new String[] { "8", "2" }, null, option)));
    }

    public void testFlatRatesWithBands() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[] { "30", "20", "10" },
                new String[] { "30", "20", "10" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE });
        assertEquals(new Money("35"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[] { "10", "15" }, null, option)));
        assertEquals(new Money("125"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[] { "5", "100", "20" }, null, option)));
        assertEquals(new Money("41"), calculationResponse(option, createCandidateOrder(new BigDecimal("60.00"), 6, new String[] { "8", "2", "1" }, null, option)));
    }

    public void testWeightBandsWithQuantities() throws Exception {
        BandedWeightFulfillmentOption option = createWeightBands(new String[] { "50", "100", "65" },
                new String[] { "30", "20", "10" },
                new FulfillmentBandResultAmountType[] { FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE,
                        FulfillmentBandResultAmountType.RATE });

        //60lbs
        assertEquals(new Money("30"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 3, null, new int[] { 2, 3, 5 }, option)));
        //66lbs
        assertEquals(new Money("10"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, null, new int[] { 4, 1, 2, 5, 5, 5 }, option)));
        //120lbs
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("60.00"), 3, null, new int[] { 2, 3, 2 }, option)));
    }

    protected Order createCandidateOrder(BigDecimal retailTotal, int orderItemsToCreate, FulfillmentOption option) {
        return createCandidateOrder(retailTotal, orderItemsToCreate, null, null, option);
    }

    /**
     * 
     * @param total - this number divided by the number of items to create is the value of either the weight or the price
     * (depending on which <b>option</b> is being passed in) for a single order item. Note that the final price of each item
     * will be: (<b>total</b> / <b>orderItemsToCreate</b>) * <b>quantity</b>
     * @param orderItemsToCreate - the number of order items to split the retail total across
     * @param flatRates - the flat rates to assign to the OrderItems that are created. To have an Order that is mixed between OrderItems and
     * DiscreteOrderItems (which are created for flat rates) ensure that the size of this array is less than <b>orderItemsToCreate</b>
     * @param quantities - the quantities to assign to each OrderItem. If specified, this should be equal to the number of
     * items to create
     * @param option - the option to associate with the flat rates
     * @return
     */
    protected Order createCandidateOrder(BigDecimal total, int orderItemsToCreate, String[] flatRates, int[] quantities, FulfillmentOption option) {
        if (flatRates != null && flatRates.length > orderItemsToCreate) {
            throw new IllegalStateException("Flat rates for Skus should be less than or equal to the number of order items being created");
        }
        if (quantities != null && quantities.length != orderItemsToCreate) {
            throw new IllegalStateException("Quantities for Skus should be less than or equal to the number of order items being created");
        }

        Order result = new OrderImpl();

        List<FulfillmentGroupItem> fulfillmentItems = new ArrayList<FulfillmentGroupItem>();
        for (int i = 0; i < orderItemsToCreate; i++) {
            DiscreteOrderItem orderItem = new DiscreteOrderItemImpl();
            Sku sku = new SkuImpl();
            //set the sku price to some arbitrary amount - won't matter because the test is based on order item price
            sku.setRetailPrice(new Money("1"));
            orderItem.setSku(sku);

            if (flatRates != null && i < flatRates.length) {
                sku.getFulfillmentFlatRates().put(option, new BigDecimal(flatRates[i]));
            }

            if (option instanceof BandedPriceFulfillmentOption) {
                orderItem.setPrice(new Money(total.divide(new BigDecimal(orderItemsToCreate))));
            } else if (option instanceof BandedWeightFulfillmentOption) {
                Weight weight = new Weight();
                weight.setWeight(total.divide(new BigDecimal(orderItemsToCreate)));
                weight.setWeightUnitOfMeasure(WeightUnitOfMeasureType.POUNDS);
                orderItem.getSku().setWeight(weight);
                orderItem.setPrice(new Money(BigDecimal.ZERO));
            }
            orderItem.setOrder(result);

            FulfillmentGroupItem fulfillmentItem = new FulfillmentGroupItemImpl();
            fulfillmentItem.setOrderItem(orderItem);
            if (quantities == null) {
                fulfillmentItem.setQuantity(1);
            } else {
                fulfillmentItem.setQuantity(quantities[i]);
            }

            fulfillmentItems.add(fulfillmentItem);
        }

        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setOrder(result);
        group.setFulfillmentGroupItems(fulfillmentItems);
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group);

        result.setFulfillmentGroups(groups);
        return result;
    }

    protected Money calculationResponse(FulfillmentOption option, Order order) throws Exception {
        Set<FulfillmentOption> options = new HashSet<FulfillmentOption>();
        options.add(option);
        BandedFulfillmentPricingProvider provider = new BandedFulfillmentPricingProvider();
        return provider.estimateCostForFulfillmentGroup(order.getFulfillmentGroups().get(0), options).getFulfillmentOptionPrices().get(option);
    }

    /**
     * Creates price bands with the given minimum amounts, results and result types. All of the lists should be the same size
     * @param minimumAmounts
     * @param resultAmounts
     * @param resultAmountTypes
     * @return
     */
    protected BandedPriceFulfillmentOption createPriceBands(String[] minimumAmounts, String[] resultAmounts, FulfillmentBandResultAmountType[] resultAmountTypes) {
        if ((minimumAmounts.length != resultAmounts.length) || (resultAmounts.length != resultAmountTypes.length)) {
            throw new IllegalStateException("All lists should be the same length");
        }

        List<FulfillmentPriceBand> bands = new ArrayList<FulfillmentPriceBand>();
        for (int i = 0; i < minimumAmounts.length; i++) {
            FulfillmentPriceBand band = new FulfillmentPriceBandImpl();
            band.setRetailPriceMinimumAmount(new BigDecimal(minimumAmounts[i]));
            band.setResultAmount(new BigDecimal(resultAmounts[i]));
            band.setResultAmountType(resultAmountTypes[i]);

            bands.add(band);
        }

        BandedPriceFulfillmentOption option = new BandedPriceFulfillmentOptionImpl();
        option.setBands(bands);
        return option;
    }

    protected BandedWeightFulfillmentOption createWeightBands(String[] minimumAmounts, String[] resultAmounts, FulfillmentBandResultAmountType[] resultAmountTypes) {
        if ((minimumAmounts.length != resultAmounts.length) || (resultAmounts.length != resultAmountTypes.length)) {
            throw new IllegalStateException("All lists should be the same length");
        }

        List<FulfillmentWeightBand> bands = new ArrayList<FulfillmentWeightBand>();
        for (int i = 0; i < minimumAmounts.length; i++) {
            FulfillmentWeightBand band = new FulfillmentWeightBandImpl();
            band.setMinimumWeight(new BigDecimal(minimumAmounts[i]));
            band.setWeightUnitOfMeasure(WeightUnitOfMeasureType.POUNDS);
            band.setResultAmount(new BigDecimal(resultAmounts[i]));
            band.setResultAmountType(resultAmountTypes[i]);

            bands.add(band);
        }

        BandedWeightFulfillmentOption option = new BandedWeightFulfillmentOptionImpl();
        option.setBands(bands);
        return option;
    }

}
