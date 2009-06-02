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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOfferImpl;
import org.broadleafcommerce.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.offer.domain.FulfillmentGroupAdjustmentImpl;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP")
public class FulfillmentGroupImpl implements FulfillmentGroup, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentGroupId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentGroupImpl", allocationSize = 1)
    @Column(name = "FULFILLMENT_GROUP_ID")
    private Long id;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupItemImpl.class, cascade = CascadeType.ALL)
    private List<FulfillmentGroupItem> fulfillmentGroupItems = new ArrayList<FulfillmentGroupItem>();

    @ManyToOne(targetEntity = AddressImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;

    @Column(name = "METHOD")
    private String method;

    //TODO change column name to RETAIL_SHIPPING_PRICE
    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailShippingPrice;

    //TODO change column name to SALE_SHIPPING_PRICE
    @Column(name = "SALE_PRICE")
    private BigDecimal saleShippingPrice;

    //TODO change column name to SHIPPING_PRICE
    @Column(name = "PRICE")
    private BigDecimal shippingPrice;

    @Transient
    private BigDecimal adjustmentPrice;  // retailPrice with adjustments

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private FulfillmentGroupType type;

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = CandidateFulfillmentGroupOfferImpl.class, cascade = {CascadeType.ALL})
    private List<CandidateFulfillmentGroupOffer> candidateOffers = new ArrayList<CandidateFulfillmentGroupOffer>();

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupAdjustmentImpl.class, cascade = {CascadeType.ALL})
    private List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments = new ArrayList<FulfillmentGroupAdjustment>();

    @Column(name = "CITY_TAX")
    private BigDecimal cityTax;

    @Column(name = "COUNTY_TAX")
    private BigDecimal countyTax;

    @Column(name = "STATE_TAX")
    private BigDecimal stateTax;

    @Column(name = "COUNTRY_TAX")
    private BigDecimal countryTax;

    @Column(name = "TOTAL_TAX")
    private BigDecimal totalTax;

    @Column(name = "DELIVERY_INSTRUCTION")
    private String deliveryInstruction;

    @Column(name = "IS_PRIMARY")
    private boolean primary = false;

    @ManyToOne(targetEntity = PersonalMessageImpl.class)
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    private PersonalMessage personalMessage;

    @Column(name = "MERCHANDISE_TOTAL")
    private BigDecimal merchandiseTotal;

    @Column(name = "TOTAL")
    private BigDecimal total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public List<FulfillmentGroupItem> getFulfillmentGroupItems() {
        return fulfillmentGroupItems;
    }

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems) {
        this.fulfillmentGroupItems = fulfillmentGroupItems;
    }

    @Override
    public void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem) {
        if (this.fulfillmentGroupItems == null) {
            this.fulfillmentGroupItems = new Vector<FulfillmentGroupItem>();
        }
        this.fulfillmentGroupItems.add(fulfillmentGroupItem);

    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String fulfillmentMethod) {
        this.method = fulfillmentMethod;
    }

    @Override
    public Money getRetailShippingPrice() {
        return retailShippingPrice == null ? null : new Money(retailShippingPrice);
    }

    @Override
    public void setRetailShippingPrice(Money retailShippingPrice) {
        this.retailShippingPrice = Money.toAmount(retailShippingPrice);
    }

    @Override
    public FulfillmentGroupType getType() {
        return type;
    }

    @Override
    public void setType(FulfillmentGroupType type) {
        this.type = type;
    }

    @Override
    public void addCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateOffer) {
        candidateOffers.add(candidateOffer);
    }

    @Override
    public List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers() {
        return candidateOffers;
    }

    @Override
    public void setCandidateFulfillmentGroupOffer(List<CandidateFulfillmentGroupOffer> candidateOffers) {
        this.candidateOffers = candidateOffers;

    }

    @Override
    public void removeAllCandidateOffers() {
        if (candidateOffers != null) {
            candidateOffers.clear();
        }
    }

    public List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments() {
        return this.fulfillmentGroupAdjustments;
    }

    /*
     * Adds the adjustment to the order item's adjustment list an discounts the order item's adjustment
     * price by the value of the adjustment.
     */
    public List<FulfillmentGroupAdjustment> addFulfillmentGroupAdjustment(FulfillmentGroupAdjustment fulfillmentGroupAdjustment) {
        if (this.fulfillmentGroupAdjustments.size() == 0) {
            adjustmentPrice = retailShippingPrice;
        }
        adjustmentPrice = adjustmentPrice.subtract(fulfillmentGroupAdjustment.getValue().getAmount());
        this.fulfillmentGroupAdjustments.add(fulfillmentGroupAdjustment);
        return this.fulfillmentGroupAdjustments;
    }

    public void removeAllAdjustments() {
        if (fulfillmentGroupAdjustments != null) {
            fulfillmentGroupAdjustments.clear();
        }
        adjustmentPrice = null;

    }

    public void setFulfillmentGroupAdjustments(List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments) {
        this.fulfillmentGroupAdjustments = fulfillmentGroupAdjustments;
    }

    public Money getSaleShippingPrice() {
        return saleShippingPrice == null ? null : new Money(saleShippingPrice);
    }

    public void setSaleShippingPrice(Money saleShippingPrice) {
        this.saleShippingPrice = Money.toAmount(saleShippingPrice);
    }

    public Money getShippingPrice() {
        return shippingPrice == null ? null : new Money(shippingPrice);
    }

    public void setShippingPrice(Money shippingPrice) {
        this.shippingPrice = Money.toAmount(shippingPrice);
    }

    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    public void setAdjustmentPrice(Money adjustmentPrice) {
        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    }

    public Money getCityTax() {
        return cityTax == null ? null : new Money(cityTax);
    }

    public void setCityTax(Money cityTax) {
        this.cityTax = Money.toAmount(cityTax);
    }

    public Money getCountyTax() {
        return countyTax == null ? null : new Money(countyTax);
    }

    public void setCountyTax(Money countyTax) {
        this.countyTax = Money.toAmount(countyTax);
    }

    public Money getStateTax() {
        return stateTax == null ? null : new Money(stateTax);
    }

    public void setStateTax(Money stateTax) {
        this.stateTax = Money.toAmount(stateTax);
    }

    public Money getCountryTax() {
        return countryTax == null ? null : new Money(countryTax);
    }

    public void setCountryTax(Money countryTax) {
        this.countryTax = Money.toAmount(countryTax);
    }

    public Money getTotalTax() {
        return totalTax == null ? null : new Money(totalTax);
    }

    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }

    public String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Money getMerchandiseTotal() {
        return merchandiseTotal == null ? null : new Money(merchandiseTotal);
    }

    public void setMerchandiseTotal(Money merchandiseTotal) {
        this.merchandiseTotal = Money.toAmount(merchandiseTotal);
    }

    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((fulfillmentGroupItems == null) ? 0 : fulfillmentGroupItems.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FulfillmentGroupImpl other = (FulfillmentGroupImpl) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (fulfillmentGroupItems == null) {
            if (other.fulfillmentGroupItems != null)
                return false;
        } else if (!fulfillmentGroupItems.equals(other.fulfillmentGroupItems))
            return false;
        return true;
    }

}
