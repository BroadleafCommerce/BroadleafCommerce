/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This entity is a collection of properties shared between different band implementations.
 * Out of the box, Broadleaf provides implementations for banded weight and banded price.
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentPriceBand}, {@link FulfillmentWeightBand}
 */
public interface FulfillmentBand extends Serializable {

    public Long getId();

    public void setId(Long id);

    /**
     * Gets the amount that should be applied to the fulfillment
     * cost for the {@link FulfillmentGroup}. This could be applied as
     * a percentage or as a flat rate, depending on the result of calling
     * {@link #getResultType()}. This is required and should never be null
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
