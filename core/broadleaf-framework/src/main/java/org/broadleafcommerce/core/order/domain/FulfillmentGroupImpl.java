/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
                    @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                                    booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "currency", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT,
                        booleanOverrideValue = false)),
        @AdminPresentationMergeOverride(name = "personalMessage", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB,
                        overrideValue = FulfillmentGroupImpl.Presentation.Tab.Name.Advanced),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER,
                        intOverrideValue = FulfillmentGroupImpl.Presentation.Tab.Order.Advanced)
        }),
        @AdminPresentationMergeOverride(name = "address", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB,
                        overrideValue = FulfillmentGroupImpl.Presentation.Tab.Name.Address),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER,
                        intOverrideValue = FulfillmentGroupImpl.Presentation.Tab.Order.Address)
        }),
        @AdminPresentationMergeOverride(name = "address.isDefault", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "address.isActive", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "address.isBusiness", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "phone", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "phone.phoneNumber", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = false),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.ORDER,
                        intOverrideValue = FulfillmentGroupImpl.Presentation.FieldOrder.PHONE),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP,
                        overrideValue = "General"),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE,
                        overrideValue = "NOT_REQUIRED")
        })
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "FulfillmentGroupImpl_baseFulfillmentGroup")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class FulfillmentGroupImpl implements FulfillmentGroup, CurrencyCodeIdentifiable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupId")
    @GenericGenerator(
        name="FulfillmentGroupId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentGroupImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl")
        }
    )
    @Column(name = "FULFILLMENT_GROUP_ID")
    protected Long id;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="FG_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Reference_Number", order=Presentation.FieldOrder.REFNUMBER,
            groupOrder = Presentation.Group.Order.General)
    protected String referenceNumber;

    @Column(name = "METHOD")
    @Index(name="FG_METHOD_INDEX", columnNames={"METHOD"})
    @AdminPresentation(excluded = true)
    @Deprecated
    protected String method;
    
    @Column(name = "SERVICE")
    @Index(name="FG_SERVICE_INDEX", columnNames={"SERVICE"})
    @AdminPresentation(excluded = true)
    @Deprecated
    protected String service;

    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Retail_Shipping_Price", order=Presentation.FieldOrder.RETAIL,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal retailFulfillmentPrice;

    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Sale_Shipping_Price", order=Presentation.FieldOrder.SALE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal saleFulfillmentPrice;

    @Column(name = "PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Shipping_Price", order=Presentation.FieldOrder.PRICE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal fulfillmentPrice;

    @Column(name = "TYPE")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Type", order=Presentation.FieldOrder.TYPE,
            fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentType",
            prominent = true, gridOrder = 3000)
    protected String type;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Tax", order=Presentation.FieldOrder.TOTALTAX,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;
    
    @Column(name = "TOTAL_ITEM_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Item_Tax", order=Presentation.FieldOrder.ITEMTAX,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalItemTax;
    
    @Column(name = "TOTAL_FEE_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_Fee_Tax", order=Presentation.FieldOrder.FEETAX,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFeeTax;
    
    @Column(name = "TOTAL_FG_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total_FG_Tax", order=Presentation.FieldOrder.FGTAX,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFulfillmentGroupTax;

    @Column(name = "DELIVERY_INSTRUCTION")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Delivery_Instruction", order=Presentation.FieldOrder.DELIVERINSTRUCTION)
    protected String deliveryInstruction;

    @Column(name = "IS_PRIMARY")
    @Index(name="FG_PRIMARY_INDEX", columnNames={"IS_PRIMARY"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Primary_FG", order=Presentation.FieldOrder.PRIMARY)
    protected boolean primary = false;

    @Column(name = "MERCHANDISE_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Merchandise_Total", order=Presentation.FieldOrder.MERCHANDISETOTAL,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal merchandiseTotal;

    @Column(name = "TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Total", order=Presentation.FieldOrder.TOTAL,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing,
            fieldType= SupportedFieldType.MONEY, prominent = true, gridOrder = 2000)
    protected BigDecimal total;

    @Column(name = "STATUS")
    @Index(name="FG_STATUS_INDEX", columnNames={"STATUS"})
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_FG_Status", order=Presentation.FieldOrder.STATUS,
            fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType",
            prominent = true, gridOrder = 4000)
    protected String status;
    
    @Column(name = "SHIPPING_PRICE_TAXABLE")
    @AdminPresentation(friendlyName = "FulfillmentGroupImpl_Shipping_Price_Taxable", order=Presentation.FieldOrder.TAXABLE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing)
    protected Boolean isShippingPriceTaxable = Boolean.FALSE;
    
    @ManyToOne(targetEntity = FulfillmentOptionImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "FULFILLMENT_OPTION_ID")
    protected FulfillmentOption fulfillmentOption;
    
    @ManyToOne(targetEntity = OrderImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="FG_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true)
    protected Order order;
    
    @Column(name = "FULFILLMENT_GROUP_SEQUNCE")
    protected Integer sequence;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="FG_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;

    /**
     * @deprecated uses the phonePrimary property on AddressImpl instead
     */
    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_ID")
    @Index(name="FG_PHONE_INDEX", columnNames={"PHONE_ID"})
    @Deprecated
    protected Phone phone;
    
    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Index(name="FG_MESSAGE_INDEX", columnNames={"PERSONAL_MESSAGE_ID"})
    protected PersonalMessage personalMessage;
    
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupItemImpl.class, cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="FulfillmentGroupImpl_Items",
            tab = Presentation.Tab.Name.Items, tabOrder = Presentation.Tab.Order.Items)
    protected List<FulfillmentGroupItem> fulfillmentGroupItems = new ArrayList<FulfillmentGroupItem>();
    
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupFeeImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    @AdminPresentationCollection(friendlyName="FulfillmentGroupImpl_Fees",
            tab = Presentation.Tab.Name.Pricing, tabOrder = Presentation.Tab.Order.Pricing)
    protected List<FulfillmentGroupFee> fulfillmentGroupFees = new ArrayList<FulfillmentGroupFee>();
        
    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = CandidateFulfillmentGroupOfferImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateFulfillmentGroupOffer> candidateOffers = new ArrayList<CandidateFulfillmentGroupOffer>();

    @OneToMany(mappedBy = "fulfillmentGroup", targetEntity = FulfillmentGroupAdjustmentImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="FulfillmentGroupImpl_Adjustments",
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced)
    protected List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments = new ArrayList<FulfillmentGroupAdjustment>();
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TaxDetailImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @JoinTable(name = "BLC_FG_FG_TAX_XREF", joinColumns = @JoinColumn(name = "FULFILLMENT_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAX_DETAIL_ID"))
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<TaxDetail> taxes = new ArrayList<TaxDetail>();

    @Column(name = "SHIPPING_OVERRIDE")
    protected Boolean shippingOverride;

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
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl)orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    discreteOrderItems.add(discreteOrderItem);
                }
            } else if (orderItem instanceof DiscreteOrderItem) {
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

    /**
     * @deprecated use the phonePrimary property on the related Address instead
     */
    @Deprecated
    @Override
    public Phone getPhone() {
        return phone;
    }

    /**
     * @deprecated use the phonePrimary property on the related Address instead
     */
    @Deprecated
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
    public Money getRetailFulfillmentPrice() {
        return retailFulfillmentPrice == null ? null :
                BroadleafCurrencyUtils.getMoney(retailFulfillmentPrice, getOrder().getCurrency());
    }

    @Override
    public void setRetailFulfillmentPrice(Money retailFulfillmentPrice) {
        this.retailFulfillmentPrice = Money.toAmount(retailFulfillmentPrice);
    }

    @Override
    public Money getRetailShippingPrice() {
        return getRetailFulfillmentPrice();
    }

    @Override
    public void setRetailShippingPrice(Money retailShippingPrice) {
        setRetailFulfillmentPrice(retailShippingPrice);
    }

    @Override
    public FulfillmentType getType() {
        return FulfillmentType.getInstance(type);
    }

    @Override
    public void setType(FulfillmentType type) {
        this.type = type == null ? null : type.getType();
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
        Money adjustmentsValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
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
    public Money getSaleFulfillmentPrice() {
        return saleFulfillmentPrice == null ? null : BroadleafCurrencyUtils.getMoney(saleFulfillmentPrice,
                getOrder().getCurrency());
    }

    @Override
    public void setSaleFulfillmentPrice(Money saleFulfillmentPrice) {
        this.saleFulfillmentPrice = Money.toAmount(saleFulfillmentPrice);
    }

    @Override
    public Money getSaleShippingPrice() {
        return getSaleFulfillmentPrice();
    }

    @Override
    public void setSaleShippingPrice(Money saleShippingPrice) {
        setSaleFulfillmentPrice(saleShippingPrice);
    }

    @Override
    public Money getFulfillmentPrice() {
        return fulfillmentPrice == null ? null : BroadleafCurrencyUtils.getMoney(fulfillmentPrice,
                getOrder().getCurrency());
    }

    @Override
    public void setFulfillmentPrice(Money fulfillmentPrice) {
        this.fulfillmentPrice = Money.toAmount(fulfillmentPrice);
    }

    @Override
    public Money getShippingPrice() {
        return getFulfillmentPrice();
    }

    @Override
    public void setShippingPrice(Money shippingPrice) {
        setFulfillmentPrice(shippingPrice);
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
        return totalTax == null ? null : BroadleafCurrencyUtils.getMoney(totalTax, getOrder().getCurrency());
    }

    @Override
    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }
    
    @Override
    public Money getTotalItemTax() {
        return totalItemTax == null ? null : BroadleafCurrencyUtils.getMoney(totalItemTax, getOrder().getCurrency());
    }

    @Override
    public void setTotalItemTax(Money totalItemTax) {
        this.totalItemTax = Money.toAmount(totalItemTax);
    }

    @Override
    public Money getTotalFeeTax() {
        return totalFeeTax == null ? null : BroadleafCurrencyUtils.getMoney(totalFeeTax, getOrder().getCurrency());
    }

    @Override
    public void setTotalFeeTax(Money totalFeeTax) {
        this.totalFeeTax = Money.toAmount(totalFeeTax);
    }

    @Override
    public Money getTotalFulfillmentGroupTax() {
        return totalFulfillmentGroupTax == null ? null : BroadleafCurrencyUtils.getMoney(totalFulfillmentGroupTax,
                getOrder().getCurrency());
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
        return merchandiseTotal == null ? null : BroadleafCurrencyUtils.getMoney(merchandiseTotal,
                getOrder().getCurrency());
    }

    @Override
    public void setMerchandiseTotal(Money merchandiseTotal) {
        this.merchandiseTotal = Money.toAmount(merchandiseTotal);
    }

    @Override
    public Money getTotal() {
        return total == null ? null : BroadleafCurrencyUtils.getMoney(total, getOrder().getCurrency());
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
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public Integer getSequence() {
        return this.sequence;
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
    public String getCurrencyCode() {
        if (getOrder().getCurrency() != null) {
            return getOrder().getCurrency().getCurrencyCode();
        }
        return null;
    }

    @Override
    public Boolean getShippingOverride() {
        return shippingOverride == null ? false : shippingOverride;
    }

    @Override
    public void setShippingOverride(Boolean shippingOverride) {
        this.shippingOverride = shippingOverride;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FulfillmentGroupImpl other = (FulfillmentGroupImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (address == null) {
            if (other.address != null) {
                return false;
            }
        } else if (!address.equals(other.address)) {
            return false;
        }
        if (fulfillmentGroupItems == null) {
            if (other.fulfillmentGroupItems != null) {
                return false;
            }
        } else if (!fulfillmentGroupItems.equals(other.fulfillmentGroupItems)) {
            return false;
        }
        return true;
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Items = "FulfillmentGroupImpl_Items_Tab";
                public static final String Pricing = "FulfillmentGroupImpl_Pricing_Tab";
                public static final String Address = "FulfillmentGroupImpl_Address_Tab";
                public static final String Advanced = "FulfillmentGroupImpl_Advanced_Tab";
            }

            public static class Order {
                public static final int Items = 2000;
                public static final int Pricing = 3000;
                public static final int Address = 4000;
                public static final int Advanced = 5000;
            }
        }

        public static class Group {
            public static class Name {
                public static final String Pricing = "FulfillmentGroupImpl_Pricing";
            }

            public static class Order {
                public static final int General = 1000;
                public static final int Pricing = 2000;
            }
        }

        public static class FieldOrder {
            public static final int REFNUMBER = 3000;
            public static final int STATUS = 4000;
            public static final int TYPE = 5000;
            public static final int DELIVERINSTRUCTION = 6000;
            public static final int PRIMARY = 7000;
            public static final int PHONE = 8000;

            public static final int RETAIL = 1000;
            public static final int SALE = 2000;
            public static final int PRICE = 3000;
            public static final int ITEMTAX = 4000;
            public static final int FEETAX = 5000;
            public static final int FGTAX = 6000;
            public static final int TOTALTAX = 7000;
            public static final int MERCHANDISETOTAL = 8000;
            public static final int TOTAL = 9000;
            public static final int TAXABLE = 10000;
        }
    }
}
