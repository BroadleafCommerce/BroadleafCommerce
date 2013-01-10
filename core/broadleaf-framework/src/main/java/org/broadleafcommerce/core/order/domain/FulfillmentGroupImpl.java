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

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOfferImpl;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustmentImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.openadmin.client.dto.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.AdminPresentationClass;
import org.broadleafcommerce.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseFulfillmentGroup")
public class FulfillmentGroupImpl implements FulfillmentGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentGroupId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentGroupImpl", allocationSize = 50)
    @Column(name = "FULFILLMENT_GROUP_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="FG_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Order order;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="FG_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName="FG Reference Number", order=1, group="Description", prominent=true)
    protected String referenceNumber;

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupItemImpl.class, cascade = CascadeType.ALL)
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<FulfillmentGroupItem> fulfillmentGroupItems = new ArrayList<FulfillmentGroupItem>();

    @ManyToOne(targetEntity = AddressImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="FG_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_ID")
    @Index(name="FG_PHONE_INDEX", columnNames={"PHONE_ID"})
    protected Phone phone;

    @Column(name = "METHOD")
    @Index(name="FG_METHOD_INDEX", columnNames={"METHOD"})
    @AdminPresentation(friendlyName="Shipping Method", order=2, group="Description", prominent=true)
    protected String method;
    
    @Column(name = "SERVICE")
    @Index(name="FG_SERVICE_INDEX", columnNames={"SERVICE"})
    @AdminPresentation(friendlyName="Shipping Service", order=3, group="Description", prominent=true)
    protected String service;

    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName="Retail Shipping Price", order=1, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal retailShippingPrice;

    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName="Sale Shipping Price", order=2, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal saleShippingPrice;

    @Column(name = "PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName="Shipping Price", order=3, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal shippingPrice;   

    @Column(name = "TYPE")
    @AdminPresentation(friendlyName="FG Type", order=4, group="Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentGroupType")
    protected String type = FulfillmentGroupType.SHIPPING.getType();

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = CandidateFulfillmentGroupOfferImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateFulfillmentGroupOffer> candidateOffers = new ArrayList<CandidateFulfillmentGroupOffer>();

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupAdjustmentImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments = new ArrayList<FulfillmentGroupAdjustment>();

    @Column(name = "CITY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG City Tax", order=4, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal cityTax;

    @Column(name = "COUNTY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG County Tax", order=5, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countyTax;

    @Column(name = "STATE_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG State Tax", order=6, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal stateTax;
    
    @Column(name = "DISTRICT_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG District Tax", order=7, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal districtTax;

    @Column(name = "COUNTRY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG Country Tax", order=8, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countryTax;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG Total Tax", order=9, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    @Column(name = "DELIVERY_INSTRUCTION")
    @AdminPresentation(friendlyName="FG Delivery Instruction", order=4, group="Description")
    protected String deliveryInstruction;

    @Column(name = "IS_PRIMARY")
    @Index(name="FG_PRIMARY_INDEX", columnNames={"IS_PRIMARY"})
    @AdminPresentation(friendlyName="Primary FG", order=5, group="Description")
    protected boolean primary = false;

    @ManyToOne(targetEntity = PersonalMessageImpl.class)
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Index(name="FG_MESSAGE_INDEX", columnNames={"PERSONAL_MESSAGE_ID"})
    protected PersonalMessage personalMessage;

    @Column(name = "MERCHANDISE_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG Merchandise Total", order=10, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal merchandiseTotal;

    @Column(name = "TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="FG Total", order=11, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal total;

    @Column(name = "STATUS")
    @Index(name="FG_STATUS_INDEX", columnNames={"STATUS"})
    @AdminPresentation(friendlyName="FG Status", order=6, group="Description")
    protected String status;
    
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupFeeImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<FulfillmentGroupFee> fulfillmentGroupFees = new ArrayList<FulfillmentGroupFee>();
    
    @Column(name = "SHIPPING_PRICE_TAXABLE")
    @AdminPresentation(friendlyName="Shipping Price Taxable", order=7, group="Pricing")
    protected Boolean isShippingPriceTaxable = Boolean.FALSE;

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
    
    public List<DiscreteOrderItem> getDiscreteOrderItems() {
        List<DiscreteOrderItem> discreteOrderItems = new ArrayList<DiscreteOrderItem>();
        for (FulfillmentGroupItem fgItem : fulfillmentGroupItems) {
            OrderItem orderItem = fgItem.getOrderItem();
            if (orderItem instanceof BundleOrderItemImpl) {
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl)orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    discreteOrderItems.add(discreteOrderItem);
                }
            } else {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem)orderItem;
                discreteOrderItems.add(discreteOrderItem);
            }
        }
        return discreteOrderItems;
    }

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems) {
        this.fulfillmentGroupItems = fulfillmentGroupItems;
    }

    public void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem) {
        if (this.fulfillmentGroupItems == null) {
            this.fulfillmentGroupItems = new Vector<FulfillmentGroupItem>();
        }
        this.fulfillmentGroupItems.add(fulfillmentGroupItem);

    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String fulfillmentMethod) {
        this.method = fulfillmentMethod;
    }

    public Money getRetailShippingPrice() {
        return retailShippingPrice == null ? null : new Money(retailShippingPrice);
    }

    public void setRetailShippingPrice(Money retailShippingPrice) {
        this.retailShippingPrice = Money.toAmount(retailShippingPrice);
    }

    public FulfillmentGroupType getType() {
        return FulfillmentGroupType.getInstance(type);
    }

    public void setType(FulfillmentGroupType type) {
        this.type = type.getType();
    }

    public void addCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateOffer) {
        candidateOffers.add(candidateOffer);
    }

    public List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers() {
        return candidateOffers;
    }

    public void setCandidateFulfillmentGroupOffer(List<CandidateFulfillmentGroupOffer> candidateOffers) {
        this.candidateOffers = candidateOffers;

    }

    public void removeAllCandidateOffers() {
        if (candidateOffers != null) {
            for (CandidateFulfillmentGroupOffer offer : candidateOffers) {
                offer.setFulfillmentGroup(null);
            }
            candidateOffers.clear();
        }
    }

    public List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments() {
        return this.fulfillmentGroupAdjustments;
    }
    
    public Money getFulfillmentGroupAdjustmentsValue() {
        Money adjustmentsValue = new Money(0);
        for (FulfillmentGroupAdjustment adjustment : fulfillmentGroupAdjustments) {
            adjustmentsValue = adjustmentsValue.add(adjustment.getValue());
        }
        return adjustmentsValue;
    }

    public void removeAllAdjustments() {
        if (fulfillmentGroupAdjustments != null) {
            for (FulfillmentGroupAdjustment adjustment : fulfillmentGroupAdjustments) {
                adjustment.setFulfillmentGroup(null);
            }
            fulfillmentGroupAdjustments.clear();
        }
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
    
    public Money getDistrictTax() {
        return districtTax == null ? null : new Money(districtTax);
    }

    public void setDistrictTax(Money districtTax) {
        this.districtTax = Money.toAmount(districtTax);
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
    
    public FulfillmentGroupStatusType getStatus() {
        return FulfillmentGroupStatusType.getInstance(status);
    }

    public void setStatus(FulfillmentGroupStatusType status) {
        this.status = status.getType();
    }

    public List<FulfillmentGroupFee> getFulfillmentGroupFees() {
        return fulfillmentGroupFees;
    }

    public void setFulfillmentGroupFees(List<FulfillmentGroupFee> fulfillmentGroupFees) {
        this.fulfillmentGroupFees = fulfillmentGroupFees;
    }

    public void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee) {
        if (fulfillmentGroupFees == null) {
            fulfillmentGroupFees = new ArrayList<FulfillmentGroupFee>();
        }
        fulfillmentGroupFees.add(fulfillmentGroupFee);
    }

    public void removeAllFulfillmentGroupFees() {
        if (fulfillmentGroupFees != null) {
            fulfillmentGroupFees.clear();
        }
    }

    public Boolean isShippingPriceTaxable() {
        return isShippingPriceTaxable;
    }

    public void setIsShippingPriceTaxable(Boolean isShippingPriceTaxable) {
        this.isShippingPriceTaxable = isShippingPriceTaxable;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((fulfillmentGroupItems == null) ? 0 : fulfillmentGroupItems.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FulfillmentGroupImpl other = (FulfillmentGroupImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

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
