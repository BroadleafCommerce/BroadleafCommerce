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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOfferImpl;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustmentImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "FulfillmentGroupImpl_baseFulfillmentGroup")
public class FulfillmentGroupImpl implements FulfillmentGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentGroupId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentGroupImpl", allocationSize = 50)
    @Column(name = "FULFILLMENT_GROUP_ID")
    protected Long id;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="FG_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Reference_Number", order=1, group = "FulfillmentGroupImpl_Description", prominent=true)
    protected String referenceNumber;

    @Column(name = "METHOD")
    @Index(name="FG_METHOD_INDEX", columnNames={"METHOD"})
    @AdminPresentation(excluded = true, friendlyName = "FulfillmentGroupImpl_Shipping_Method", order=2, group = "FulfillmentGroupImpl_Description", prominent=true)
    @Deprecated
    protected String method;
    
    @Column(name = "SERVICE")
    @Index(name="FG_SERVICE_INDEX", columnNames={"SERVICE"})
    @AdminPresentation(excluded = true, friendlyName = "FulfillmentGroupImpl_Shipping_Service", order=3, group = "FulfillmentGroupImpl_Description", prominent=true)
    @Deprecated
    protected String service;

    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Retail_Shipping_Price", order=1, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal retailShippingPrice;

    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Sale_Shipping_Price", order=2, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal saleShippingPrice;

    @Column(name = "PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Shipping_Price", order=3, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal shippingPrice;   

    @Column(name = "TYPE")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Type", order=4, group = "FulfillmentGroupImpl_Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentType")
    protected String type = FulfillmentType.PHYSICAL.getType();

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Tax", order=9, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;
    
    @Column(name = "TOTAL_ITEM_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Item_Tax", order=9, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalItemTax;
    
    @Column(name = "TOTAL_FEE_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Fee_Tax", order=9, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFeeTax;
    
    @Column(name = "TOTAL_FG_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_FG_Tax", order=9, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFulfillmentGroupTax;

    @Column(name = "DELIVERY_INSTRUCTION")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Delivery_Instruction", order=4, group = "FulfillmentGroupImpl_Description")
    protected String deliveryInstruction;

    @Column(name = "IS_PRIMARY")
    @Index(name="FG_PRIMARY_INDEX", columnNames={"IS_PRIMARY"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Primary_FG", order=5, group = "FulfillmentGroupImpl_Description")
    protected boolean primary = false;

    @Column(name = "MERCHANDISE_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Merchandise_Total", order=10, group = "FulfillmentGroupImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal merchandiseTotal;

    @Column(name = "TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total", order=11, group = "FulfillmentGroupImpl_Pricing", fieldType= SupportedFieldType.MONEY)
    protected BigDecimal total;

    @Column(name = "STATUS")
    @Index(name="FG_STATUS_INDEX", columnNames={"STATUS"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Status", order=6, group = "FulfillmentGroupImpl_Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType")
    protected String status;
    
    @Column(name = "SHIPPING_PRICE_TAXABLE")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Shipping_Price_Taxable", order=7, group = "FulfillmentGroupImpl_Pricing")
    protected Boolean isShippingPriceTaxable = Boolean.FALSE;
    
    @ManyToOne(targetEntity = FulfillmentOptionImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "FULFILLMENT_OPTION_ID")
    protected FulfillmentOption fulfillmentOption;
    
    @ManyToOne(targetEntity = OrderImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="FG_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Order order;
    
    @ManyToOne(targetEntity = AddressImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="FG_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_ID")
    @Index(name="FG_PHONE_INDEX", columnNames={"PHONE_ID"})
    protected Phone phone;
    
    @ManyToOne(targetEntity = PersonalMessageImpl.class)
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Index(name="FG_MESSAGE_INDEX", columnNames={"PERSONAL_MESSAGE_ID"})
    protected PersonalMessage personalMessage;
    
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupItemImpl.class, cascade = CascadeType.ALL)
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<FulfillmentGroupItem> fulfillmentGroupItems = new ArrayList<FulfillmentGroupItem>();
    
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupFeeImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<FulfillmentGroupFee> fulfillmentGroupFees = new ArrayList<FulfillmentGroupFee>();
        
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = CandidateFulfillmentGroupOfferImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateFulfillmentGroupOffer> candidateOffers = new ArrayList<CandidateFulfillmentGroupOffer>();

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupAdjustmentImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments = new ArrayList<FulfillmentGroupAdjustment>();
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TaxDetailImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_FG_FG_TAX_XREF", joinColumns = @JoinColumn(name = "FULFILLMENT_GROUP_ID"), inverseJoinColumns = @JoinColumn(name = "TAX_DETAIL_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<TaxDetail> taxes = new ArrayList<TaxDetail>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Order getOrder() {
        return order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public FulfillmentOption getFulfillmentOption() {
        return fulfillmentOption;
    }

    @Override
    public void setFulfillmentOption(FulfillmentOption fulfillmentOption) {
        this.fulfillmentOption = fulfillmentOption;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public List<FulfillmentGroupItem> getFulfillmentGroupItems() {
        return fulfillmentGroupItems;
    }
    
    @Override
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

    @Override
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

    @Override
    public Phone getPhone() {
        return phone;
    }

    @Override
    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @Override
    @Deprecated
    public String getMethod() {
        return method;
    }

    @Override
    @Deprecated
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
    public FulfillmentType getType() {
        return FulfillmentType.getInstance(type);
    }

    @Override
    public void setType(FulfillmentType type) {
        this.type = type.getType();
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
            for (CandidateFulfillmentGroupOffer offer : candidateOffers) {
                offer.setFulfillmentGroup(null);
            }
            candidateOffers.clear();
        }
    }

    @Override
    public List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments() {
        return this.fulfillmentGroupAdjustments;
    }
    
    @Override
    public Money getFulfillmentGroupAdjustmentsValue() {
    	Money adjustmentsValue = new Money(0);
        for (FulfillmentGroupAdjustment adjustment : fulfillmentGroupAdjustments) {
        	adjustmentsValue = adjustmentsValue.add(adjustment.getValue());
        }
        return adjustmentsValue;
    }

    @Override
    public void removeAllAdjustments() {
        if (fulfillmentGroupAdjustments != null) {
            for (FulfillmentGroupAdjustment adjustment : fulfillmentGroupAdjustments) {
                adjustment.setFulfillmentGroup(null);
            }
            fulfillmentGroupAdjustments.clear();
        }
    }

    @Override
    public void setFulfillmentGroupAdjustments(List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments) {
        this.fulfillmentGroupAdjustments = fulfillmentGroupAdjustments;
    }

    @Override
    public Money getSaleShippingPrice() {
        return saleShippingPrice == null ? null : new Money(saleShippingPrice);
    }

    @Override
    public void setSaleShippingPrice(Money saleShippingPrice) {
        this.saleShippingPrice = Money.toAmount(saleShippingPrice);
    }

    @Override
    public Money getShippingPrice() {
        return shippingPrice == null ? null : new Money(shippingPrice);
    }

    @Override
    public void setShippingPrice(Money shippingPrice) {
        this.shippingPrice = Money.toAmount(shippingPrice);
    }
    
    @Override
    public List<TaxDetail> getTaxes() {
        return taxes;
    }

    @Override
    public void setTaxes(List<TaxDetail> taxes) {
        this.taxes = taxes;
    }

    @Override
    public Money getTotalTax() {
        return totalTax == null ? null : new Money(totalTax);
    }

    @Override
    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }
    
    @Override
    public Money getTotalItemTax() {
		return totalItemTax == null ? null : new Money(totalItemTax);
	}

	@Override
    public void setTotalItemTax(Money totalItemTax) {
		this.totalItemTax = Money.toAmount(totalItemTax);
	}

	@Override
    public Money getTotalFeeTax() {
		return totalFeeTax == null ? null : new Money(totalFeeTax);
	}

	@Override
    public void setTotalFeeTax(Money totalFeeTax) {
		this.totalFeeTax = Money.toAmount(totalFeeTax);
	}

	@Override
    public Money getTotalFulfillmentGroupTax() {
		return totalFulfillmentGroupTax == null ? null : new Money(totalFulfillmentGroupTax);
	}

	@Override
    public void setTotalFulfillmentGroupTax(Money totalFulfillmentGroupTax) {
		this.totalFulfillmentGroupTax = Money.toAmount(totalFulfillmentGroupTax);
	}

	@Override
    public String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    @Override
    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    @Override
    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    @Override
    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public Money getMerchandiseTotal() {
        return merchandiseTotal == null ? null : new Money(merchandiseTotal);
    }

    @Override
    public void setMerchandiseTotal(Money merchandiseTotal) {
        this.merchandiseTotal = Money.toAmount(merchandiseTotal);
    }

    @Override
    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    @Override
    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }
    
    @Override
    public FulfillmentGroupStatusType getStatus() {
        return FulfillmentGroupStatusType.getInstance(status);
    }

    @Override
    public void setStatus(FulfillmentGroupStatusType status) {
        this.status = status.getType();
    }

    @Override
    public List<FulfillmentGroupFee> getFulfillmentGroupFees() {
        return fulfillmentGroupFees;
    }

    @Override
    public void setFulfillmentGroupFees(List<FulfillmentGroupFee> fulfillmentGroupFees) {
        this.fulfillmentGroupFees = fulfillmentGroupFees;
    }

    @Override
    public void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee) {
        if (fulfillmentGroupFees == null) {
            fulfillmentGroupFees = new ArrayList<FulfillmentGroupFee>();
        }
        fulfillmentGroupFees.add(fulfillmentGroupFee);
    }

    @Override
    public void removeAllFulfillmentGroupFees() {
        if (fulfillmentGroupFees != null) {
            fulfillmentGroupFees.clear();
        }
    }

    @Override
    public Boolean isShippingPriceTaxable() {
		return isShippingPriceTaxable;
	}

	@Override
    public void setIsShippingPriceTaxable(Boolean isShippingPriceTaxable) {
		this.isShippingPriceTaxable = isShippingPriceTaxable;
	}

	@Override
	@Deprecated
    public String getService() {
		return service;
	}

	@Override
	@Deprecated
    public void setService(String service) {
		this.service = service;
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
