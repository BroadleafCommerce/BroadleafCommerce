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
package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.util.List;

import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.util.money.Money;

public interface FulfillmentGroup extends Serializable {

    Long getId();

    void setId(Long id);

    Order getOrder();

    void setOrder(Order order);

    Address getAddress();

    void setAddress(Address address);

    Phone getPhone();

    void setPhone(Phone phone);

    List<FulfillmentGroupItem> getFulfillmentGroupItems();

    void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems);

    void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    String getMethod();

    void setMethod(String fulfillmentMethod);

    Money getRetailShippingPrice();

    void setRetailShippingPrice(Money retailShippingPrice);

    Money getSaleShippingPrice();

    void setSaleShippingPrice(Money saleShippingPrice);

    Money getShippingPrice();

    void setShippingPrice(Money shippingPrice);

    Money getAdjustmentPrice();

    void setAdjustmentPrice(Money adjustmentPrice);

    String getReferenceNumber();

    void setReferenceNumber(String referenceNumber);

    FulfillmentGroupType getType();

    void setType(FulfillmentGroupType type);

    List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers();

    void setCandidateFulfillmentGroupOffer(List<CandidateFulfillmentGroupOffer> candidateOffers);

    void addCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateOffer);

    void removeAllCandidateOffers();

    List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments();

    List<FulfillmentGroupAdjustment> addFulfillmentGroupAdjustment(FulfillmentGroupAdjustment fulfillmentGroupAdjustment);

    void setFulfillmentGroupAdjustments(List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments);

    void removeAllAdjustments();

    Money getCityTax();

    void setCityTax(Money cityTax);

    Money getCountyTax();

    void setCountyTax(Money countyTax);

    Money getStateTax();

    void setStateTax(Money stateTax);
    
    public Money getDistrictTax();

    public void setDistrictTax(Money districtTax);

    Money getCountryTax();

    void setCountryTax(Money countryTax);

    Money getTotalTax();

    void setTotalTax(Money totalTax);

    String getDeliveryInstruction();

    void setDeliveryInstruction(String deliveryInstruction);

    PersonalMessage getPersonalMessage();

    void setPersonalMessage(PersonalMessage personalMessage);

    boolean isPrimary();

    void setPrimary(boolean primary);

    Money getMerchandiseTotal();

    void setMerchandiseTotal(Money merchandiseTotal);

    Money getTotal();

    void setTotal(Money orderTotal);

    FulfillmentGroupStatusType getStatus();
    
    public List<FulfillmentGroupFee> getFulfillmentGroupFees();

    public void setFulfillmentGroupFees(List<FulfillmentGroupFee> fulfillmentGroupFees);

    public void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee);

    public void removeAllFulfillmentGroupFees();
    
    public Boolean isShippingPriceTaxable();

    public void setIsShippingPriceTaxable(Boolean isShippingPriceTaxable);
    
    public String getService();

    public void setService(String service);
    
}
