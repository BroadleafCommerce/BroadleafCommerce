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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBand;
import org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBandImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * 
 * @author Phillip Verheyden
 */
public class BandedPriceFulfillmentTest extends TestCase {
    
    public void testPriceBandRate() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"10", "20", "30"}, 
                                                          new String[]{"10", "20", "30"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE});
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2)));
        assertEquals(Money.ZERO, calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5)));
    }
    
    public void testMinimumAmountsWithZero() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"0", "20", "30"}, 
                                                          new String[]{"10", "20", "30"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE});
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2)));
        assertEquals(new Money("10.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5)));
        assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5)));
    }

    
    public void testPriceBandPercentage() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"10", "30", "20"}, 
                                                          new String[]{".10", ".20", ".30"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.PERCENTAGE,
                                                                                                FulfillmentBandResultAmountType.PERCENTAGE,
                                                                                                FulfillmentBandResultAmountType.PERCENTAGE});

        assertEquals(new Money("1.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("15.00"), 3)));
        assertEquals(new Money("6.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3)));
        assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5)));
        assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5)));
    }
    
    public void testPriceBandRatesWithPercentages() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"150", "30", "20", "150", "10", "9", "80"}, 
                                                          new String[]{"50", "20", ".30", "20", ".10", "5", ".5"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.PERCENTAGE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.PERCENTAGE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.PERCENTAGE});
        
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("35.00"), 5)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("9999.00"), 9)));
        assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5)));
        assertEquals(new Money("1.80"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6)));
        assertEquals(new Money("50"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5)));
        assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("66"), 6)));
        assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("150"), 5)));
    }
    
    /**
     * If the retail price sum falls within 2 bands but with the same retail minimum, the lowest price should be selected
     */
    public void testLowestPriceSelection() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"10", "10", "10"}, 
                                                          new String[]{"30", "20", "10"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE});
        assertEquals(calculationResponse(option, createCandidateOrder(new BigDecimal("10.00"), 2)), new Money("10.00"));
    }
    
    public void testFlatRatesExclusive() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"100"}, 
                                                          new String[]{"30"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE});
        
        assertEquals(new Money("45"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 3, new String[]{"10", "15", "20"}, option)));
        assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("80.00"), 1, new String[]{"5"}, option)));
        assertEquals(new Money("10"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 2, new String[]{"8", "2"}, option)));
    }

    public void testFlatRatesWithBands() throws Exception {
        BandedPriceFulfillmentOption option = createBands(new String[]{"30", "20", "10"}, 
                                                          new String[]{"30", "20", "10"}, 
                                                          new FulfillmentBandResultAmountType[]{FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE,
                                                                                                FulfillmentBandResultAmountType.RATE});
        assertEquals(new Money("35"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[]{"10", "15"}, option)));
        assertEquals(new Money("125"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[]{"5", "100", "20"}, option)));
        assertEquals(new Money("41"), calculationResponse(option, createCandidateOrder(new BigDecimal("60.00"), 6, new String[]{"8", "2", "1"}, option)));
    }
    
    protected Order createCandidateOrder(BigDecimal retailTotal, int orderItemsToCreate) {
        return createCandidateOrder(retailTotal, orderItemsToCreate, null, null);
    }
    
    /**
     * 
     * @param retailTotal - how much the retail total of the order items should add up to
     * @param orderItemsToCreate - the number of order items to split the retail total across
     * @param flatRates - the flat rates to assign to the OrderItems that are created. To have an Order that is mixed between OrderItems and
     * DiscreteOrderItems (which are created for flat rates) ensure that the size of this array is less than <b>orderItemsToCreate</b>
     * @param option - the option to associate with the flat rates
     * @return
     */
    protected Order createCandidateOrder(BigDecimal retailTotal, int orderItemsToCreate, String[] flatRates, BandedPriceFulfillmentOption option) {
        if (flatRates != null && flatRates.length > orderItemsToCreate) {
            throw new IllegalStateException("Flat rates for Skus should be less than or equal to the number of order items being created");
        }
        Order result = new OrderImpl();

        List<FulfillmentGroupItem> fulfillmentItems = new ArrayList<FulfillmentGroupItem>();
        for (int i = 0; i < orderItemsToCreate; i++) {
            OrderItem orderItem = (flatRates != null && i < flatRates.length) ? new DiscreteOrderItemImpl() : new OrderItemImpl();
            
            if (orderItem instanceof DiscreteOrderItem && option != null) {
                Sku sku = new SkuImpl();
                sku.getFulfillmentFlatRates().put(option, new BigDecimal(flatRates[i]));
                ((DiscreteOrderItem)orderItem).setSku(sku);
            }
            
            orderItem.setPrice(new Money(retailTotal.divide(new BigDecimal(orderItemsToCreate))));
            orderItem.setOrder(result);
            FulfillmentGroupItem fulfillmentItem = new FulfillmentGroupItemImpl();
            fulfillmentItem.setOrderItem(orderItem);
            fulfillmentItem.setQuantity(1);
            
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
    
    protected Money calculationResponse(BandedPriceFulfillmentOption option, Order order) throws Exception {
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
    protected BandedPriceFulfillmentOption createBands(String[] minimumAmounts, String[] resultAmounts, FulfillmentBandResultAmountType[] resultAmountTypes) {
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
    
}
