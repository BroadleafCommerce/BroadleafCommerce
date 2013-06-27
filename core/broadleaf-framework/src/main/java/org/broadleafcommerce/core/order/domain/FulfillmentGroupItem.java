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
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;

import java.io.Serializable;
import java.util.List;

public interface FulfillmentGroupItem extends Serializable {

    public Long getId();

    public void setId(Long id);

    public FulfillmentGroup getFulfillmentGroup();

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public OrderItem getOrderItem();

    public void setOrderItem(OrderItem orderItem);

    public int getQuantity();

    public void setQuantity(int quantity);

    public Money getRetailPrice();

    public Money getSalePrice();

    /**
     * @deprecated Use {@link #getTotalItemAmount()} or {@link #getTotalItemTaxableAmount()}
     */
    public Money getPrice();
    
    public Money getTotalItemAmount();

    public void setTotalItemAmount(Money amount);
    
    public Money getProratedOrderAdjustmentAmount();

    public void setProratedOrderAdjustmentAmount(Money amount);

    public Money getTotalItemTaxableAmount();

    public void setTotalItemTaxableAmount(Money amount);    

    public FulfillmentGroupStatusType getStatus();

    public void setStatus(FulfillmentGroupStatusType status);
    
    public void removeAssociations();

    public FulfillmentGroupItem clone();
    
    /**
     * Gets a list of TaxDetail objects, which are taxes that apply directly to this item.
     * The amount in each TaxDetail takes into account the quantity of this item
     * 
     * @return a list of taxes that apply to this item
     */
    public List<TaxDetail> getTaxes();

    /**
     * Sets the list of TaxDetail objects, which are taxes that apply directly to this item.
     * The amount in each TaxDetail must take into account the quantity of this item
     * 
     * @param taxes the list of taxes on this item
     */
    public void setTaxes(List<TaxDetail> taxes);
    
    /**
     * Gets the total tax for this item, which is the sum of all taxes for this item.
     * This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this item
     */
    public Money getTotalTax();

    /**
     * Sets the total tax for this item, which is the sum of all taxes for this item.
     * This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalTax the total tax for this item
     */
    public void setTotalTax(Money totalTax);

    /**
     * Returns true if this item has pro-rated order adjustments.
     * @return
     */
    boolean getHasProratedOrderAdjustments();

}
