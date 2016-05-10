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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.util.List;

public interface FulfillmentGroupFee extends Serializable, MultiTenantCloneable<FulfillmentGroupFee> {

    public Long getId();

    public void setId(Long id);

    public FulfillmentGroup getFulfillmentGroup();

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public Money getAmount();

    public void setAmount(Money amount);

    public String getName();

    public void setName(String name);

    public String getReportingCode();

    public void setReportingCode(String reportingCode);
    
    /**
     * Returns whether or not this fee is taxable. If this flag is not set, it returns true by default
     * 
     * @return the taxable flag. If null, returns true
     */
    public Boolean isTaxable();

    /**
     * Sets whether or not this fee is taxable
     * 
     * @param taxable
     */
    public void setTaxable(Boolean taxable);
    
    /**
     * Gets a list of TaxDetail objects, which are taxes that apply directly to this fee.
     * 
     * @return a list of taxes that apply to this fee
     */
    public List<TaxDetail> getTaxes();

    /**
     * Sets the list of TaxDetail objects, which are taxes that apply directly to this fee.
     * 
     * @param taxes the list of taxes on this fee
     */
    public void setTaxes(List<TaxDetail> taxes);
    
    /**
     * Gets the total tax for this fee, which is the sum of all taxes for this fee.
     * This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fee
     */
    public Money getTotalTax();

    /**
     * Sets the total tax for this fee, which is the sum of all taxes for this fee.
     * This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalTax the total tax for this fee
     */
    public void setTotalTax(Money totalTax);
}
