/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import java.math.BigDecimal;

/**
 * <p>This entity defines the bands that can be specified for {@link BandedWeightFulfillmentOption}. Bands
 * work on the cumulated weight of an {@link Order} and should be calculated as follows:</p>
 * <ol>
 *  <li>The weight of all of the {@link OrderItem}s (via the relationship to {@link Sku}) in a {@link FulfillmentGroup} (which
 *  is obtained through their relationship with {@link FulfillmentGroupItem} are summed together</li>
 *  <li>The {@link FulfillmentWeightBand} should be looked up by getting the closest band less
 *  than the sum of the weights</li>
 *  <li>If {@link #getResultAmountType()} returns {@link FulfillmentBandResultAmountType#RATE}, then
 *  the cost for the fulfillment group is whatever is defined in {@link #getResultAmount()}</li>
 *  <li>If {@link #getResultAmountType()} returns {@link FulfillmentBandResultAmountType#PERCENTAGE}, then
 *  the fulfillment cost is the percentage obtained by {@link #getResultAmount()} * retailPriceTotal</li>
 *  <li>If two bands have the same weight, the cheapest resulting amount is used</li>
 * </ol>
 * <p>Note: this implementation assumes that units of measurement (lb, kg, etc) are the same across the site implementation</p>
 *
 * @author Phillip Verheyden
 * 
 */
public interface FulfillmentWeightBand extends FulfillmentBand {

    public BigDecimal getMinimumWeight();
    
    public void setMinimumWeight(BigDecimal weight);
    
    public BandedWeightFulfillmentOption getOption();

    public void setOption(BandedWeightFulfillmentOption option);
    
    public WeightUnitOfMeasureType getWeightUnitOfMeasure();
    
    public void setWeightUnitOfMeasure(WeightUnitOfMeasureType weightUnitOfMeasure);

}
