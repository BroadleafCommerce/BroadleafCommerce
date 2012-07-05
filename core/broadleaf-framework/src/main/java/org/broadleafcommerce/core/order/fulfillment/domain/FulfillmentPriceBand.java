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

package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>This entity defines the bands that can be specified for {@link BandedPriceFulfillmentOption}. Bands
 * work on the retail price of an {@link Order} and should be calculated as follows:</p>
 * <ol>
 *  <li>The retail prices of all of the {@link OrderItems} in a {@link FulfillmentGroup} (which
 *  is obtained through their relationship with {@link FulfillmentGroupItem} are summed together</li>
 *  <li>The {@link FulfillmentPriceBand} should be looked up by getting the closest band less
 *  than the sum of the retail price</li>
 *  <li>If {@link #getResultAmountType()} returns {@link FulfillmentBandResultAmountType#RATE}, then
 *  the cost for the fulfillment group is whatever is defined in {@link #getResultAmount()}</li>
 *  <li>If {@link #getResultAmountType()} returns {@link FulfillmentBandResultAmountType#PERCENTAGE}, then
 *  the fulfillment cost is the percentage obtained by {@link #getResultAmount()} * retailPriceTotal</li>
 * </ol>
 * 
 * @author Phillip Verheyden
 * @see {@link BandedPriceFulfillmentOption}
 */
public interface FulfillmentPriceBand extends Serializable {

    public Long getId();

    public void setId(Long id);

    /**
     * Gets the minimum amount that this band is valid for. If the addition
     * of all of the retail prices on all the {@link OrderItem}s in a {@link FulfillmentGroup}
     * comes to at least this amount, this band result amount will be applied to the
     * fulfillment cost.
     * 
     * @return the minimum retail price amount of the sum of the {@link OrderItem}s in a
     * {@link FulfillmentGroup} that this band qualifies for
     */
    public BigDecimal getRetailPriceMinimumAmount();

    /**
     * Set the minimum amount that this band is valid for. If the addition
     * of all of the retail prices on all the {@link OrderItem}s in a {@link FulfillmentGroup}
     * comes to at least this amount, this band result amount will be applied to the
     * fulfillment cost.
     * 
     * @param minimumRetailPriceAmount - the minimum retail price amount from adding up
     * the {@link OrderItem}s in a {@link FulfillmentGroup}
     */
    public void setRetailPriceMinimumAmount(BigDecimal retailPriceMinimumAmount);

    /**
     * Gets the amount that should be applied to the fulfillment
     * cost for the {@link FulfillmentGroup}. This could be applied as
     * a percentage or as a flat rate, depending on the result of calling
     * {@link #getResultType()}.
     * 
     * @return the amount to apply for this band
     */
    public BigDecimal getResultAmount();

    /**
     * Sets the amount that should be applied to the fulfillment cost
     * for this band. This can be either a flat rate or a percentage depending
     * on {@link #getResultType()}.
     * 
     * @param resultAmount - the percentage or flat rate that should be applied
     * as a fulfillment cost for this band
     */
    public void setResultAmount(BigDecimal resultAmount);

    /**
     * Gets how {@link #getResultAmount} should be applied to the fulfillment cost
     * 
     * @return the type of {@link #getResultAmount()} which determines how that value
     * should be calculated into the cost
     */
    public FulfillmentBandResultAmountType getResultAmountType();

    /**
     * Sets how {@link #getResultAmount()} should be applied to the fulfillment cost
     * 
     * @param resultAmountType - how the value from {@link #getResultAmount()} should be
     * applied to the cost of the {@link FulfillmentGroup}
     */
    public void setResultAmountType(FulfillmentBandResultAmountType resultAmountType);

}
