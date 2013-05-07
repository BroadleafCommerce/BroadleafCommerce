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

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationCollectionOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMerge;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationPropertyType;
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
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@EntityListeners(value = { AuditableListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationOverrides(
    value = {
        @AdminPresentationOverride(name="customer", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        //set all customer fields to not prominent, and go ahead and hide them all
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.prominent, booleanOverrideValue = false),
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded, booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="customer.firstName", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        //override the hide from above to show name
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded, booleanOverrideValue = false)
                })
        ),
        @AdminPresentationOverride(name="customer.lastName", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        //override the hide form above to show name
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded, booleanOverrideValue = false)
                })
        ),
        @AdminPresentationOverride(name="locale", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        //hide all locale related fields
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded, booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="currency", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.prominent, booleanOverrideValue = false)
                })
        )
    },
    collections = @AdminPresentationCollectionOverride(name="customer.customerAttributes", value=@AdminPresentationCollection(excluded = true, addType = AddMethodType.PERSIST))
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderImpl_baseOrder")
public class OrderImpl implements Order {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderImpl", allocationSize = 50)
    @Column(name = "ORDER_ID")
    protected Long id;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Column(name = "NAME")
    @Index(name="ORDER_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Name", group = "OrderImpl_Order", order=1, prominent=true)
    protected String name;

    @ManyToOne(targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @Index(name="ORDER_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Customer customer;

    @Column(name = "ORDER_STATUS")
    @Index(name="ORDER_STATUS_INDEX", columnNames={"ORDER_STATUS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Status", group = "OrderImpl_Order", order=2, prominent=true, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.OrderStatus")
    protected String status;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Tax", group = "OrderImpl_Order", order=9, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    @Column(name = "TOTAL_SHIPPING", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total_Shipping", group = "OrderImpl_Order", order=10, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalFulfillmentCharges;

    @Column(name = "ORDER_SUBTOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Subtotal", group = "OrderImpl_Order", order=3, fieldType=SupportedFieldType.MONEY,prominent=true,currencyCodeField="currency.currencyCode")
    protected BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total", group = "OrderImpl_Order", order=1, fieldType= SupportedFieldType.MONEY,prominent=true,currencyCodeField="currency.currencyCode")
    protected BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    @AdminPresentation(friendlyName = "OrderImpl_Order_Submit_Date", group = "OrderImpl_Order", order=12)
    protected Date submitDate;

    @Column(name = "ORDER_NUMBER")
    @Index(name="ORDER_NUMBER_INDEX", columnNames={"ORDER_NUMBER"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Number", group = "OrderImpl_Order", order=3, prominent=true)
    private String orderNumber;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name="ORDER_EMAIL_INDEX", columnNames={"EMAIL_ADDRESS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Email_Address", group = "OrderImpl_Order", order=13)
    protected String emailAddress;

    @OneToMany(mappedBy = "order", targetEntity = OrderItemImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OrderItem> orderItems = new ArrayList<OrderItem>();

    @OneToMany(mappedBy = "order", targetEntity = FulfillmentGroupImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @OrderBy("id")
    protected List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OrderAdjustment> orderAdjustments = new ArrayList<OrderAdjustment>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OfferCodeImpl.class)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_CODE_ID", referencedColumnName = "OFFER_CODE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OfferCode> addedOfferCodes = new ArrayList<OfferCode>();

    @OneToMany(mappedBy = "order", targetEntity = CandidateOrderOfferImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateOrderOffer> candidateOrderOffers = new ArrayList<CandidateOrderOffer>();

    @OneToMany(mappedBy = "order", targetEntity = PaymentInfoImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

    @ManyToMany(targetEntity=OfferInfoImpl.class)
    @JoinTable(name = "BLC_ADDITIONAL_OFFER_INFO", joinColumns = @JoinColumn(name = "BLC_ORDER_ORDER_ID", referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_INFO_ID", referencedColumnName = "OFFER_INFO_ID"))
    @MapKeyJoinColumn(name = "OFFER_ID")
    @MapKeyClass(OfferImpl.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @BatchSize(size = 50)
    protected Map<Offer, OfferInfo> additionalOfferInformation = new HashMap<Offer, OfferInfo>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @MapKey(name="name")
    protected Map<String,OrderAttribute> orderAttributes = new HashMap<String,OrderAttribute>();
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "BroadleafCurrency_Currency_Code", order=1, group = "BroadleafCurrency_Details")
    protected BroadleafCurrency currency;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "LocaleImpl_Code", order=1, group = "LocaleImpl_Details")
    protected Locale locale;

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
        return totalFulfillmentCharges == null ? null : BroadleafCurrencyUtils.getMoney(totalFulfillmentCharges, getCurrency());
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

}
