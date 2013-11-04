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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.PreviewStatus;
import org.broadleafcommerce.common.persistence.Previewable;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
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
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

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
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderImpl_baseOrder")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = "preview", skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = {"multiTenantSite"})
})
public class OrderImpl implements Order, AdminMainEntity, CurrencyCodeIdentifiable, Previewable {

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
    @AdminPresentation(friendlyName = "OrderImpl_Order_Name", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.NAME, prominent=true, groupOrder = Presentation.Group.Order.General,
            gridOrder = 2000)
    protected String name;

    @ManyToOne(targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @Index(name="ORDER_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    @AdminPresentation(friendlyName = "OrderImpl_Customer", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.CUSTOMER, groupOrder = Presentation.Group.Order.General)
    @AdminPresentationToOneLookup()
    protected Customer customer;

    @Column(name = "ORDER_STATUS")
    @Index(name="ORDER_STATUS_INDEX", columnNames={"ORDER_STATUS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Status", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.STATUS, prominent=true, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.order.service.type.OrderStatus",
            groupOrder = Presentation.Group.Order.General, gridOrder = 3000)
    protected String status;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Tax", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.TOTALTAX, fieldType=SupportedFieldType.MONEY,
            groupOrder = Presentation.Group.Order.General)
    protected BigDecimal totalTax;

    @Column(name = "TOTAL_SHIPPING", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Shipping", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.TOTALFGCHARGES, fieldType=SupportedFieldType.MONEY,
            groupOrder = Presentation.Group.Order.General)
    protected BigDecimal totalFulfillmentCharges;

    @Column(name = "ORDER_SUBTOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Subtotal", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.SUBTOTAL, fieldType=SupportedFieldType.MONEY,prominent=true,
            groupOrder = Presentation.Group.Order.General,
            gridOrder = 4000)
    protected BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.TOTAL, fieldType= SupportedFieldType.MONEY,
            groupOrder = Presentation.Group.Order.General)
    protected BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    @AdminPresentation(friendlyName = "OrderImpl_Order_Submit_Date", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.SUBMITDATE, groupOrder = Presentation.Group.Order.General, prominent = true,
            gridOrder = 5000)
    protected Date submitDate;

    @Column(name = "ORDER_NUMBER")
    @Index(name="ORDER_NUMBER_INDEX", columnNames={"ORDER_NUMBER"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Number", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.ORDERNUMBER, prominent=true, groupOrder = Presentation.Group.Order.General,
            gridOrder = 1000)
    private String orderNumber;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name="ORDER_EMAIL_INDEX", columnNames={"EMAIL_ADDRESS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Email_Address", group = Presentation.Group.Name.General,
            order=Presentation.FieldOrder.EMAILADDRESS, groupOrder = Presentation.Group.Order.General)
    protected String emailAddress;

    @OneToMany(mappedBy = "order", targetEntity = OrderItemImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Order_Items",
            tab = Presentation.Tab.Name.OrderItems, tabOrder = Presentation.Tab.Order.OrderItems)
    protected List<OrderItem> orderItems = new ArrayList<OrderItem>();

    @OneToMany(mappedBy = "order", targetEntity = FulfillmentGroupImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Fulfillment_Groups",
                tab = Presentation.Tab.Name.FulfillmentGroups, tabOrder = Presentation.Tab.Order.FulfillmentGroups)
    protected List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Adjustments",
                tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
                order = Presentation.FieldOrder.ADJUSTMENTS)
    protected List<OrderAdjustment> orderAdjustments = new ArrayList<OrderAdjustment>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OfferCodeImpl.class)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "ORDER_ID",
            referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_CODE_ID",
            referencedColumnName = "OFFER_CODE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Offer_Codes",
                tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
                manyToField = "orders", order = Presentation.FieldOrder.OFFERCODES)
    protected List<OfferCode> addedOfferCodes = new ArrayList<OfferCode>();

    @OneToMany(mappedBy = "order", targetEntity = CandidateOrderOfferImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateOrderOffer> candidateOrderOffers = new ArrayList<CandidateOrderOffer>();

    @OneToMany(mappedBy = "order", targetEntity = PaymentInfoImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderImpl_Payment_Infos",
                tab = Presentation.Tab.Name.Payment, tabOrder = Presentation.Tab.Order.Payment)
    protected List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

    @ManyToMany(targetEntity=OfferInfoImpl.class)
    @JoinTable(name = "BLC_ADDITIONAL_OFFER_INFO", joinColumns = @JoinColumn(name = "BLC_ORDER_ORDER_ID",
            referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_INFO_ID",
            referencedColumnName = "OFFER_INFO_ID"))
    @MapKeyJoinColumn(name = "OFFER_ID")
    @MapKeyClass(OfferImpl.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @BatchSize(size = 50)
    protected Map<Offer, OfferInfo> additionalOfferInformation = new HashMap<Offer, OfferInfo>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAttributeImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @MapKey(name="name")
    @AdminPresentationMap(friendlyName = "OrderImpl_Attributes",
        forceFreeFormKeys = true, keyPropertyFriendlyName = "OrderImpl_Attributes_Key_Name"
    )
    protected Map<String,OrderAttribute> orderAttributes = new HashMap<String,OrderAttribute>();
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(excluded = true)
    protected BroadleafCurrency currency;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(excluded = true)
    protected Locale locale;

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
    public Money getRemainingTotal() {
        Money myTotal = getTotal();
        if (myTotal == null) {
            return null;
        }
        Money totalPayments = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (PaymentInfo pi : getPaymentInfos()) {
            if (pi.getAmount() != null) {
                totalPayments = totalPayments.add(pi.getAmount());
            }
        }
        return myTotal.subtract(totalPayments);
    }

    @Override
    public Money getCapturedTotal() {
        Money totalCaptured = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (PaymentInfo pi : getPaymentInfos()) {
            totalCaptured = totalCaptured.add(pi.getPaymentCapturedAmount());
        }
        return totalCaptured;
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
    public List<PaymentInfo> getPaymentInfos() {
        return paymentInfos;
    }

    @Override
    public void setPaymentInfos(List<PaymentInfo> paymentInfos) {
        this.paymentInfos = paymentInfos;
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
    public int getItemCount() {
        int count = 0;
        for (DiscreteOrderItem doi : getDiscreteOrderItems()) {
            count += doi.getQuantity();
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
        if (getClass() != obj.getClass()) {
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

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String OrderItems = "OrderImpl_Order_Items_Tab";
                public static final String FulfillmentGroups = "OrderImpl_Fulfillment_Groups_Tab";
                public static final String Payment = "OrderImpl_Payment_Tab";
                public static final String Advanced = "OrderImpl_Advanced_Tab";
            }

            public static class Order {
                public static final int OrderItems = 2000;
                public static final int FulfillmentGroups = 3000;
                public static final int Payment = 4000;
                public static final int Advanced = 5000;
            }
        }

        public static class Group {
            public static class Name {
                public static final String General = "OrderImpl_Order";
            }

            public static class Order {
                public static final int General = 1000;
            }
        }

        public static class FieldOrder {
            public static final int NAME = 1000;
            public static final int CUSTOMER = 2000;
            public static final int TOTAL = 3000;
            public static final int STATUS = 4000;
            public static final int SUBTOTAL = 5000;
            public static final int ORDERNUMBER = 6000;
            public static final int TOTALTAX = 7000;
            public static final int TOTALFGCHARGES = 8000;
            public static final int SUBMITDATE = 9000;
            public static final int EMAILADDRESS = 10000;

            public static final int ADJUSTMENTS = 1000;
            public static final int OFFERCODES = 2000;
        }
    }
}
