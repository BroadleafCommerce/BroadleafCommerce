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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.PreviewStatus;
import org.broadleafcommerce.common.persistence.Previewable;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OfferInfoImpl;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@EntityListeners(value = { AuditableListener.class, OrderPersistedEntityListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.PREVIEW, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class OrderImpl implements Order, AdminMainEntity, CurrencyCodeIdentifiable, Previewable, OrderAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderId")
    @GenericGenerator(
        name="OrderId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.OrderImpl")
        }
    )
    @Column(name = "ORDER_ID")
    protected Long id;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Embedded
    protected PreviewStatus previewable = new PreviewStatus();

    @Column(name = "NAME")
    @Index(name="ORDER_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Name", group = GroupName.General,
            order=FieldOrder.NAME)
    protected String name;

    @ManyToOne(targetEntity = CustomerImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @Index(name="ORDER_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    @AdminPresentation(friendlyName = "OrderImpl_Customer", group = GroupName.Customer,
            order=FieldOrder.CUSTOMER)
    @AdminPresentationToOneLookup()
    protected Customer customer;

    @Column(name = "ORDER_STATUS")
    @Index(name="ORDER_STATUS_INDEX", columnNames={"ORDER_STATUS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Status", group = GroupName.General,
            order=FieldOrder.STATUS, prominent=true, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.order.service.type.OrderStatus",
            gridOrder = 1000)
    protected String status;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Tax", group = GroupName.OrderTotals,
            order=FieldOrder.TOTALTAX, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    @Column(name = "TOTAL_SHIPPING", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Shipping", group = GroupName.OrderTotals,
            order=FieldOrder.TOTALFGCHARGES, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFulfillmentCharges;

    @Column(name = "ORDER_SUBTOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Subtotal", group = GroupName.OrderTotals,
            order=FieldOrder.SUBTOTAL, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total", group = GroupName.OrderTotals,
            order=FieldOrder.TOTAL,
            fieldType = SupportedFieldType.MONEY,
            prominent=true,
            gridOrder = 4000)
    protected BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    @AdminPresentation(friendlyName = "OrderImpl_Order_Submit_Date", group = GroupName.General,
            order=FieldOrder.SUBMITDATE,
            prominent = true,
            gridOrder = 5000)
    protected Date submitDate;

    @Column(name = "ORDER_NUMBER")
    @Index(name="ORDER_NUMBER_INDEX", columnNames={"ORDER_NUMBER"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Number", group = GroupName.General,
            order = FieldOrder.ORDERNUMBER,
            prominent = true,
            gridOrder = 3000)
    private String orderNumber;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name="ORDER_EMAIL_INDEX", columnNames={"EMAIL_ADDRESS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Email_Address", group = GroupName.Customer,
            order=FieldOrder.EMAILADDRESS)
    protected String emailAddress;

    @OneToMany(mappedBy = "order", targetEntity = OrderItemImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Order_Items",
            tab = TabName.General)
    protected List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", targetEntity = FulfillmentGroupImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Fulfillment_Groups",
                tab = TabName.FulfillmentGroups)
    protected List<FulfillmentGroup> fulfillmentGroups = new ArrayList<>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Adjustments",
                group = GroupName.Advanced,
                order = FieldOrder.ADJUSTMENTS)
    protected List<OrderAdjustment> orderAdjustments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OfferCodeImpl.class, cascade = CascadeType.REFRESH)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "ORDER_ID",
            referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_CODE_ID",
            referencedColumnName = "OFFER_CODE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Offer_Codes",
                group = GroupName.Advanced,
                manyToField = "orders", order = FieldOrder.OFFERCODES)
    protected List<OfferCode> addedOfferCodes = new ArrayList<>();

    @OneToMany(mappedBy = "order", targetEntity = CandidateOrderOfferImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateOrderOffer> candidateOrderOffers = new ArrayList<>();

    @OneToMany(mappedBy = "order", targetEntity = OrderPaymentImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Payments",
                tab = TabName.Payment)
    protected List<OrderPayment> payments = new ArrayList<>();

    @ManyToMany(targetEntity=OfferInfoImpl.class, cascade = CascadeType.REFRESH)
    @JoinTable(name = "BLC_ADDITIONAL_OFFER_INFO", joinColumns = @JoinColumn(name = "BLC_ORDER_ORDER_ID",
            referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_INFO_ID",
            referencedColumnName = "OFFER_INFO_ID"))
    @MapKeyJoinColumn(name = "OFFER_ID")
    @MapKeyClass(OfferImpl.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @BatchSize(size = 50)
    protected Map<Offer, OfferInfo> additionalOfferInformation = new HashMap<>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAttributeImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @MapKey(name="name")
    @AdminPresentationMap(friendlyName = "OrderImpl_Attributes",
        forceFreeFormKeys = true, keyPropertyFriendlyName = "OrderImpl_Attributes_Key_Name",
        group = GroupName.Advanced, order = FieldOrder.ATTRIBUTES)
    protected Map<String,OrderAttribute> orderAttributes = new HashMap<>();
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(excluded = true)
    protected BroadleafCurrency currency;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(excluded = true)
    protected Locale locale;

    @Column(name = "TAX_OVERRIDE")
    protected Boolean taxOverride;

    @Transient
    protected List<ActivityMessageDTO> orderMessages;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Auditable getAuditable() {
        return auditable;
    }

    @Override
    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    @Override
    public Money getSubTotal() {
        return subTotal == null ? null : BroadleafCurrencyUtils.getMoney(subTotal, getCurrency());
    }

    @Override
    public void setSubTotal(Money subTotal) {
        this.subTotal = Money.toAmount(subTotal);
    }

    @Override
    public Money calculateSubTotal() {
        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(getCurrency());
        for (OrderItem orderItem : orderItems) {
            calculatedSubTotal = calculatedSubTotal.add(orderItem.getTotalPrice());
        }
        return calculatedSubTotal;
    }

    @Override
    public void assignOrderItemsFinalPrice() {
        for (OrderItem orderItem : orderItems) {
            orderItem.assignFinalPrice();
        }
    }

    @Override
    public Money getTotal() {
        return total == null ? null : BroadleafCurrencyUtils.getMoney(total, getCurrency());
    }

    @Override
    public Money getTotalAfterAppliedPayments() {
        Money myTotal = getTotal();
        if (myTotal == null) {
            return null;
        }
        Money totalPayments = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (OrderPayment payment : getPayments()) {
            //add up all active payments that are not UNCONFIRMED Final Payments
            if (payment.isActive() && payment.getAmount() != null && 
                (!payment.isFinalPayment() || payment.isConfirmed())) {
                totalPayments = totalPayments.add(payment.getAmount());
            }
        }
        return myTotal.subtract(totalPayments);
    }

    @Override
    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }

    @Override
    public Boolean getPreview() {
        if (previewable == null) {
            previewable = new PreviewStatus();
        }
        return previewable.getPreview();
    }

    @Override
    public void setPreview(Boolean preview) {
        if (previewable == null) {
            previewable = new PreviewStatus();
        }
        previewable.setPreview(preview);
    }

    @Override
    public Date getSubmitDate() {
        return submitDate;
    }

    @Override
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.getInstance(status);
    }

    @Override
    public void setStatus(OrderStatus status) {
        this.status = status.getType();
    }

    @Override
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    @Override
    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    @Override
    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    @Override
    public void setCandidateOrderOffers(List<CandidateOrderOffer> candidateOrderOffers) {
        this.candidateOrderOffers = candidateOrderOffers;
    }

    @Override
    public List<CandidateOrderOffer> getCandidateOrderOffers() {
        return candidateOrderOffers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Money getTotalTax() {
        return totalTax == null ? null : BroadleafCurrencyUtils.getMoney(totalTax, getCurrency());
    }

    @Override
    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }

    @Override
    public Money getTotalShipping() {
        return getTotalFulfillmentCharges();
    }

    @Override
    public void setTotalShipping(Money totalShipping) {
        setTotalFulfillmentCharges(totalShipping);
    }

    @Override
    public Money getTotalFulfillmentCharges() {
        return totalFulfillmentCharges == null ? null : BroadleafCurrencyUtils.getMoney(totalFulfillmentCharges,
                getCurrency());
    }

    @Override
    public void setTotalFulfillmentCharges(Money totalFulfillmentCharges) {
        this.totalFulfillmentCharges = Money.toAmount(totalFulfillmentCharges);
    }

    @Override
    public List<OrderPayment> getPayments() {
        return payments;
    }

    @Override
    public void setPayments(List<OrderPayment> payments) {
        this.payments = payments;
    }

    @Override
    public boolean hasCategoryItem(String categoryName) {
        for (OrderItem orderItem : orderItems) {
            if(orderItem.isInCategory(categoryName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<OrderAdjustment> getOrderAdjustments() {
        return this.orderAdjustments;
    }

    protected void setOrderAdjustments(List<OrderAdjustment> orderAdjustments) {
        this.orderAdjustments = orderAdjustments;
    }

    @Override
    public List<DiscreteOrderItem> getDiscreteOrderItems() {
        List<DiscreteOrderItem> discreteOrderItems = new ArrayList<DiscreteOrderItem>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl)orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    discreteOrderItems.add(discreteOrderItem);
                }
            } else if (orderItem instanceof DiscreteOrderItem) {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                discreteOrderItems.add(discreteOrderItem);
            }
        }
        return discreteOrderItems;
    }

    @Override
    public List<OrderItem> getNonDiscreteOrderItems() {
        List<OrderItem> nonDiscreteOrderItems = new ArrayList<OrderItem>();
        for (OrderItem orderItem : orderItems) {
            if (!SkuAccessor.class.isAssignableFrom(orderItem.getClass())) {
                nonDiscreteOrderItems.add(orderItem);
            }
        }
        return nonDiscreteOrderItems;
    }

    @Override
    public boolean containsSku(Sku sku) {
        for (OrderItem orderItem : getOrderItems()) {
            if (orderItem instanceof DiscreteOrderItem) {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                if (discreteOrderItem.getSku() != null && discreteOrderItem.getSku().equals(sku)) {
                    return true;
                }
            } else if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                if (bundleOrderItem.getSku() != null && bundleOrderItem.getSku().equals(sku)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public List<OfferCode> getAddedOfferCodes() {
        return addedOfferCodes;
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    @Override
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String getFulfillmentStatus() {
        return null;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public Map<Offer, OfferInfo> getAdditionalOfferInformation() {
        return additionalOfferInformation;
    }

    @Override
    public void setAdditionalOfferInformation(Map<Offer, OfferInfo> additionalOfferInformation) {
        this.additionalOfferInformation = additionalOfferInformation;
    }

    @Override
    public Money getItemAdjustmentsValue() {
        Money itemAdjustmentsValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (OrderItem orderItem : orderItems) {
            itemAdjustmentsValue = itemAdjustmentsValue.add(orderItem.getTotalAdjustmentValue());
        }
        return itemAdjustmentsValue;
    }
    
    @Override
    public Money getFulfillmentGroupAdjustmentsValue() {
        Money adjustmentValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            adjustmentValue = adjustmentValue.add(fulfillmentGroup.getFulfillmentGroupAdjustmentsValue());
        }
        return adjustmentValue;
    }

    @Override
    public Money getOrderAdjustmentsValue() {
        Money orderAdjustmentsValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (OrderAdjustment orderAdjustment : orderAdjustments) {
            orderAdjustmentsValue = orderAdjustmentsValue.add(orderAdjustment.getValue());
        }
        return orderAdjustmentsValue;
    }

    @Override
    public Money getTotalAdjustmentsValue() {
        Money totalAdjustmentsValue = getItemAdjustmentsValue();
        totalAdjustmentsValue = totalAdjustmentsValue.add(getOrderAdjustmentsValue());
        totalAdjustmentsValue = totalAdjustmentsValue.add(getFulfillmentGroupAdjustmentsValue());
        return totalAdjustmentsValue;
    }

    @Override
    public boolean updatePrices() {
        boolean updated = false;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.updateSaleAndRetailPrices()) {
                updated = true;
            }
        }
        return updated;
    }

    @Override
    public boolean finalizeItemPrices() {
        boolean updated = false;
        for (OrderItem orderItem : orderItems) {
            orderItem.finalizePrice();
        }
        return updated;
    }

    @Override
    public Map<String, OrderAttribute> getOrderAttributes() {
        return orderAttributes;
    }

    @Override
    public void setOrderAttributes(Map<String, OrderAttribute> orderAttributes) {
        this.orderAttributes = orderAttributes;
    }

    @Override
    @Deprecated
    public void addAddedOfferCode(OfferCode offerCode) {
        addOfferCode(offerCode);
    }
    
    @Override
    public void addOfferCode(OfferCode offerCode) {
        getAddedOfferCodes().add(offerCode);
    }
    
    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }
    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public Boolean getTaxOverride() {
        return taxOverride == null ? false : taxOverride;
    }

    @Override
    public void setTaxOverride(Boolean taxOverride) {
        this.taxOverride = taxOverride;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (DiscreteOrderItem doi : getDiscreteOrderItems()) {
            if (doi.getParentOrderItem() == null) {
                count += doi.getQuantity();
            }
        }
        for (OrderItem oi : getNonDiscreteOrderItems()) {
            count += oi.getQuantity();
        }
        return count;
    }

    @Override
    public boolean getHasOrderAdjustments() {
        Money orderAdjustmentsValue = getOrderAdjustmentsValue();
        if (orderAdjustmentsValue != null) {
            return (orderAdjustmentsValue.compareTo(BigDecimal.ZERO) != 0);
        }
        return false;
    }

    @Override
    public String getMainEntityName() {
        String customerName = null;
        String orderNumber = getOrderNumber();
        if (!StringUtils.isEmpty(getCustomer().getFirstName()) && !StringUtils.isEmpty(getCustomer().getLastName())) {
            customerName = getCustomer().getFirstName() + " " + getCustomer().getLastName();
        }
        if (!StringUtils.isEmpty(orderNumber) && !StringUtils.isEmpty(customerName)) {
            return orderNumber + " - " + customerName;
        }
        if (!StringUtils.isEmpty(orderNumber)) {
            return orderNumber;
        }
        if (!StringUtils.isEmpty(customerName)) {
            return customerName;
        }
        return "";
    }

    @Override
    public String getCurrencyCode() {
        if (getCurrency() != null) {
            return getCurrency().getCurrencyCode();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        OrderImpl other = (OrderImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        Date otherDateCreated = other.auditable != null ? other.auditable.getDateCreated() : null;
        if (myDateCreated == null) {
            if (otherDateCreated != null) {
                return false;
            }
        } else if (!myDateCreated.equals(otherDateCreated)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        result = prime * result + ((myDateCreated == null) ? 0 : myDateCreated.hashCode());
        return result;
    }

    @Override
    public List<ActivityMessageDTO> getOrderMessages() {
        if (this.orderMessages == null) {
            this.orderMessages = new ArrayList<ActivityMessageDTO>();
        }
        return this.orderMessages;
    }

    @Override
    public void setOrderMessages(List<ActivityMessageDTO> orderMessages) {
        this.orderMessages = orderMessages;
    }

    @Override
    public <G extends Order> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Order cloned = createResponse.getClone();
        cloned.setCurrency(getCurrency());
        cloned.setEmailAddress(emailAddress);
        cloned.setLocale(getLocale());
        cloned.setName(name);
        cloned.setOrderNumber(orderNumber);
        cloned.setTotalTax(getTotalTax());
        cloned.setSubmitDate(submitDate);
        cloned.setCustomer(customer);
        cloned.setStatus(getStatus());
        cloned.setTotalFulfillmentCharges(getTotalFulfillmentCharges());
        cloned.setSubTotal(getSubTotal());
        cloned.setTaxOverride(taxOverride == null ? null : taxOverride);
        for(OrderItem entry : orderItems){
            OrderItem clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setOrder(cloned);
            cloned.getOrderItems().add(clonedEntry);
        }
        for(Map.Entry<Offer, OfferInfo> entry : additionalOfferInformation.entrySet()){
            Offer clonedOffer = entry.getKey().createOrRetrieveCopyInstance(context).getClone();
            OfferInfo clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            cloned.getAdditionalOfferInformation().put(clonedOffer,clonedEntry);
        }
        for(Map.Entry<String,OrderAttribute> entry : orderAttributes.entrySet()){
            OrderAttribute clonedAttribute = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            clonedAttribute.setOrder(cloned);
            cloned.getOrderAttributes().put(entry.getKey(),clonedAttribute);
        }
        for(FulfillmentGroup fulfillmentGroup: fulfillmentGroups) {
            FulfillmentGroup fg = fulfillmentGroup.createOrRetrieveCopyInstance(context).getClone();
            fg.setOrder(cloned);
            cloned.getFulfillmentGroups().add(fg);
        }
        for(OrderPayment orderPayment : payments) {
            if (orderPayment.isActive()) {
                OrderPayment payment = orderPayment.createOrRetrieveCopyInstance(context).getClone();
                payment.setOrder(cloned);
                cloned.getPayments().add(payment);
            }
        }
        for(OfferCode entry : addedOfferCodes){
            // We do not want to clone the offer code since that will cascade through offer. We only want to create
            // the new relationship between this cloned order and the existing offer code.
            cloned.getAddedOfferCodes().add(entry);
        }

        cloned.setTotal(total == null ? null : new Money(total));

        return  createResponse;
    }


}
