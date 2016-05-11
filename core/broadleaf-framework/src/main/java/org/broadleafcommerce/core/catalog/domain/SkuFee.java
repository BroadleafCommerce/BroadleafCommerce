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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;

import java.io.Serializable;
import java.util.List;

/**
 * Used to represent Sku-specific surcharges when fulfilling this item. For instance there might be a disposal fee
 * when selling batteries or an environmental fee for tires.
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentGroupFee}, {@link FulfillmentGroup}
 *
 */
public interface SkuFee extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    /**
     * Get the name of the surcharge
     * 
     * @return the surcharge name
     */
    public String getName();

    /**
     * Sets the name of the surcharge
     * 
     * @param name
     */
    public void setName(String name);

    /**
     * Get the description of the surcharge
     * 
     * @return the surcharge description
     */
    public String getDescription();

    /**
     * Sets the fee description
     * 
     * @param description
     */
    public void setDescription(String description);

    /**
     * Gets the amount to charge for this surcharge
     * 
     * @return the fee amount
     */
    public Money getAmount();

    /**
     * Sets the amount to charge for this surcharge
     * 
     * @param amount
     */
    public void setAmount(Money amount);

    /**
     * Gets whether or not this surcharge is taxable.
     * 
     * @return true if the surcharge is taxable, false otherwise. Defaults to <b>false</b>
     */
    public Boolean getTaxable();

    /**
     * Sets whether or not this surcharge should be included in tax calculations
     * 
     * @param taxable
     */
    public void setTaxable(Boolean taxable);

    /**
     * Gets the optional MVEL expression used as additional criteria to determine if
     * this fee applies
     * 
     * @return the MVEL expression of extra criteria to determine if this
     * fee applies
     */
    public String getExpression();

    /**
     * Sets the MVEL expression used to determine if this fee should be applied. If this is
     * null or empty, this fee will always be applied
     * 
     * @param expression - a valid MVEL expression
     */
    public void setExpression(String expression);
    
    public SkuFeeType getFeeType();

    public void setFeeType(SkuFeeType feeType);

    /**
     * Gets the Skus associated with this surcharge
     * 
     * @return Skus that have this particular surcharge
     */
    public List<Sku> getSkus();

    /**
     * Sets the Skus associated with this surcharge
     * 
     * @param skus
     */
    public void setSkus(List<Sku> skus);

    BroadleafCurrency getCurrency();

    void setCurrency(BroadleafCurrency currency);
    
}
