/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.util.List;

public interface FulfillmentGroupFee extends Serializable {

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
