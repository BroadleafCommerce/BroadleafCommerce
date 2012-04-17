/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;
import java.util.List;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Phone;

public interface FulfillmentGroup extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public Phone getPhone();

    public void setPhone(Phone phone);

    public List<FulfillmentGroupItem> getFulfillmentGroupItems();

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems);

    public void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    public String getMethod();

    public void setMethod(String fulfillmentMethod);

    public Money getRetailShippingPrice();

    public void setRetailShippingPrice(Money retailShippingPrice);

    public Money getSaleShippingPrice();

    public void setSaleShippingPrice(Money saleShippingPrice);

    public Money getShippingPrice();

    public void setShippingPrice(Money shippingPrice);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public FulfillmentGroupType getType();

    void setType(FulfillmentGroupType type);

    public List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers();

    public void setCandidateFulfillmentGroupOffer(List<CandidateFulfillmentGroupOffer> candidateOffers);

    public void addCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateOffer);

    public void removeAllCandidateOffers();

    public List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments();

    public void setFulfillmentGroupAdjustments(List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments);

    public void removeAllAdjustments();
    
    /**
     * Gets a list of TaxDetail objects, which are taxes that apply directly to this fulfillment group. 
     * An example of a such a tax would be a shipping tax.
     * 
     * @return a list of taxes that apply to this fulfillment group
     */
    public List<TaxDetail> getTaxes();

    /**
     * Gets the list of TaxDetail objects, which are taxes that apply directly to this fulfillment group. 
     * An example of a such a tax would be a shipping tax.
     * 
     * @param taxes the list of taxes on this fulfillment group
     */
    public void setTaxes(List<TaxDetail> taxes);

    /**
     * Gets the total tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group items, fees, and taxes on this fulfillment group itself (such as a shipping tax).
     * This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for the fulfillment group
     */
    public Money getTotalTax();

    /**
     * Sets the total tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group items, fees, and taxes on this fulfillment group itself (such as a shipping tax).
     * This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param the total tax for this fulfillment group
     */
    public void setTotalTax(Money totalTax);
    
    /**
     * Gets the total item tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group items. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    public Money getTotalItemTax();

    /**
     * Sets the total item tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group items. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param the total tax for this fulfillment group
     */
    public void setTotalItemTax(Money totalItemTax);
    
    /**
     * Gets the total fee tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group fees. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    public Money getTotalFeeTax();

    /**
     * Sets the total fee tax for this fulfillment group, which is the sum of the taxes on all fulfillment 
     * group fees. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param the total tax for this fulfillment group
     */
    public void setTotalFeeTax(Money totalFeeTax);
    
    /**
     * Gets the total fulfillment group tax for this fulfillment group, which is the sum of the taxes 
     * on this fulfillment group itself (such as a shipping tax) only. It does not include the taxes on 
     * items or fees in this fulfillment group. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    public Money getTotalFulfillmentGroupTax();

    /**
     * Sets the total fulfillment group tax for this fulfillment group, which is the sum of the taxes 
     * on this fulfillment group itself (such as a shipping tax) only. It does not include the taxes on 
     * items or fees in this fulfillment group. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param the total tax for this fulfillment group
     */
    public void setTotalFulfillmentGroupTax(Money totalFulfillmentGroupTax);

    public String getDeliveryInstruction();

    public void setDeliveryInstruction(String deliveryInstruction);

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isPrimary();

    public void setPrimary(boolean primary);

    public Money getMerchandiseTotal();

    public void setMerchandiseTotal(Money merchandiseTotal);

    public Money getTotal();

    public void setTotal(Money orderTotal);

    public FulfillmentGroupStatusType getStatus();

    public void setStatus(FulfillmentGroupStatusType status);
    
    public List<FulfillmentGroupFee> getFulfillmentGroupFees();

    public void setFulfillmentGroupFees(List<FulfillmentGroupFee> fulfillmentGroupFees);

    public void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee);

    public void removeAllFulfillmentGroupFees();
    
	public String getService();

	public void setService(String service);
	
	public List<DiscreteOrderItem> getDiscreteOrderItems();
	
	public Money getFulfillmentGroupAdjustmentsValue();
	
}
