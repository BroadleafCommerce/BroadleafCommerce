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
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.domain.OfferInfo;
import org.broadleafcommerce.offer.domain.OfferInfoImpl;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
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
    protected String name;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    protected Customer customer;

    @Column(name = "ORDER_STATUS")
    protected String status;

    @Column(name = "CITY_TAX")
    protected BigDecimal cityTax;

    @Column(name = "COUNTY_TAX")
    protected BigDecimal countyTax;

    @Column(name = "STATE_TAX")
    protected BigDecimal stateTax;

    @Column(name = "COUNTRY_TAX")
    protected BigDecimal countryTax;

    @Column(name = "TOTAL_TAX")
    protected BigDecimal totalTax;

    @Column(name = "TOTAL_SHIPPING")
    protected BigDecimal totalShipping;

    @Column(name = "ORDER_SUBTOTAL")
    protected BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL")
    protected BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    protected Date submitDate;

    @Column(name = "ORDER_NUMBER")
    private String orderNumber;

    @Column(name = "EMAIL_ADDRESS")
    protected String emailAddress;

    @Transient
    protected BigDecimal adjustmentPrice;  // retailPrice with order adjustments (no item adjustments)

    @OneToMany(mappedBy = "order", targetEntity = OrderItemImpl.class, cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OrderItem> orderItems = new ArrayList<OrderItem>();

    @OneToMany(mappedBy = "order", targetEntity = FulfillmentGroupImpl.class, cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    @OrderBy("id")
    protected List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OrderAdjustment> orderAdjustments = new ArrayList<OrderAdjustment>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OfferCodeImpl.class)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_CODE_ID", referencedColumnName = "OFFER_CODE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OfferCode> addedOfferCodes = new ArrayList<OfferCode>();

    @OneToMany(mappedBy = "order", targetEntity = CandidateOrderOfferImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<CandidateOrderOffer> candidateOrderOffers = new ArrayList<CandidateOrderOffer>();

    @OneToMany(mappedBy = "order", targetEntity = PaymentInfoImpl.class, cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

    @ManyToMany(targetEntity=OfferInfoImpl.class)
    @JoinTable(name = "BLC_ADDITIONAL_OFFER_INFO", inverseJoinColumns = @JoinColumn(name = "OFFER_INFO_ID", referencedColumnName = "OFFER_INFO_ID"))
    @MapKeyJoinColumn(name = "OFFER_ID")
    @MapKeyClass(OfferImpl.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @BatchSize(size = 50)
    protected Map<Offer, OfferInfo> additionalOfferInformation = new HashMap<Offer, OfferInfo>();

    @Transient
    protected boolean markedForOffer;

    @Transient
    protected boolean notCombinableOfferApplied = false;

    @Transient
    protected boolean hasOrderAdjustments = false;

    @Transient
    protected Money shippingPriceWithoutOffers = new Money(0);


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public Money getSubTotal() {
        return subTotal == null ? null : new Money(subTotal);
    }

    public void setSubTotal(Money subTotal) {
        this.subTotal = Money.toAmount(subTotal);
    }

    public Money calculateOrderItemsCurrentPrice() {
        Money calculatedSubTotal = new Money();
        for (OrderItem orderItem : orderItems) {
            Money currentItemPrice = orderItem.getCurrentPrice();
            calculatedSubTotal = calculatedSubTotal.add(new Money(currentItemPrice.doubleValue() * orderItem.getQuantity()));
        }
        return calculatedSubTotal;
    }

    public Money calculateOrderItemsFinalPrice() {
        Money calculatedSubTotal = new Money();
        for (OrderItem orderItem : orderItems) {
            Money currentItemPrice = orderItem.getPrice();
            calculatedSubTotal = calculatedSubTotal.add(new Money(currentItemPrice.doubleValue() * orderItem.getQuantity()));
        }
        return calculatedSubTotal;
    }

    /**
     * Assigns a final price to all the order items
     */
    public void assignOrderItemsFinalPrice() {
        for (OrderItem orderItem : orderItems) {
            orderItem.assignFinalPrice();
        }
    }

    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }

    public Money getRemainingTotal() {
        Money myTotal = getTotal();
        if (myTotal == null) {
            return null;
        }
        Money totalPayments = new Money(BigDecimal.ZERO);
        for (PaymentInfo pi : getPaymentInfos()) {
            if (pi.getAmount() != null) {
                totalPayments = totalPayments.add(pi.getAmount());
            }
        }
        return myTotal.subtract(totalPayments);
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status == null ? null : OrderStatus.getInstance(status);
    }

    public void setStatus(OrderStatus status) {
        this.status = status.getType();
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    public void setCandidateOrderOffers(List<CandidateOrderOffer> candidateOrderOffers) {
        this.candidateOrderOffers = candidateOrderOffers;
    }

    public void addCandidateOrderOffer(CandidateOrderOffer candidateOrderOffer) {
        candidateOrderOffers.add(candidateOrderOffer);
    }

    public List<CandidateOrderOffer> getCandidateOrderOffers() {
        return candidateOrderOffers;
    }

    public void removeAllCandidateOffers() {
        removeAllCandidateOrderOffers();
        if (getOrderItems() != null) {
            for (OrderItem item : getOrderItems()) {
                item.removeAllCandidateItemOffers();
            }
        }

        if (getFulfillmentGroups() != null) {
            for (FulfillmentGroup fg : getFulfillmentGroups()) {
                fg.removeAllCandidateOffers();
            }
        }
    }

    public void removeAllCandidateOrderOffers() {
        if (candidateOrderOffers != null) {
            for (CandidateOrderOffer candidate : candidateOrderOffers) {
                candidate.setOrder(null);
            }
            candidateOrderOffers.clear();
        }
    }

    public boolean isMarkedForOffer() {
        return markedForOffer;
    }

    public void setMarkedForOffer(boolean markedForOffer) {
        this.markedForOffer = markedForOffer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Money getTotalShipping() {
        return totalShipping == null ? null : new Money(totalShipping);
    }

    public void setTotalShipping(Money totalShipping) {
        this.totalShipping = Money.toAmount(totalShipping);
    }

    public List<PaymentInfo> getPaymentInfos() {
        return paymentInfos;
    }

    public void setPaymentInfos(List<PaymentInfo> paymentInfos) {
        this.paymentInfos = paymentInfos;
    }

    public boolean hasCategoryItem(String categoryName) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.isInCategory(categoryName)) {
                return true;
            }
        }
        return false;
    }

    protected List<OrderAdjustment> getOrderAdjustments() {
        return this.orderAdjustments;
    }

    /*
     * Adds the adjustment to the order item's adjustment list an discounts the order item's adjustment
     * price by the value of the adjustment.
     */
    public List<OrderAdjustment> addOrderAdjustments(OrderAdjustment orderAdjustment) {
        if (this.orderAdjustments.size() == 0) {
            adjustmentPrice = getSubTotal().getAmount();
        }
        adjustmentPrice = adjustmentPrice.subtract(orderAdjustment.getValue().getAmount());
        this.orderAdjustments.add(orderAdjustment);
        if (!orderAdjustment.getOffer().isCombinableWithOtherOffers()) {
            notCombinableOfferApplied = true;
        }
        hasOrderAdjustments = true;
        return this.orderAdjustments;
    }

    public void removeAllAdjustments() {
        removeAllItemAdjustments();
        removeAllFulfillmentAdjustments();
        removeAllOrderAdjustments();
    }

    public void removeAllOrderAdjustments() {
        if (orderAdjustments != null) {
            for (OrderAdjustment adjustment : orderAdjustments) {
                adjustment.setOrder(null);
            }
            orderAdjustments.clear();
        }
        adjustmentPrice = null;
        notCombinableOfferApplied = false;
        hasOrderAdjustments = false;
    }

    public void removeAllItemAdjustments() {
        for (OrderItem orderItem : orderItems) {
            orderItem.removeAllAdjustments();
        }
    }

    public void removeAllFulfillmentAdjustments() {
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            fulfillmentGroup.removeAllAdjustments();
        }
    }

    protected void setOrderAdjustments(List<OrderAdjustment> orderAdjustments) {
        this.orderAdjustments = orderAdjustments;
    }

    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    public void setAdjustmentPrice(Money adjustmentPrice) {
        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    }

    /*
     * Checks the order adjustment to see if it is not stackable
     */
    public boolean containsNotStackableOrderOffer() {
        boolean isContainsNotStackableOrderOffer = false;
        for (OrderAdjustment orderAdjustment : orderAdjustments) {
            if (!orderAdjustment.getOffer().isStackable()) {
                isContainsNotStackableOrderOffer = true;
                break;
            }
        }
        return isContainsNotStackableOrderOffer;
    }

    public List<DiscreteOrderItem> getDiscreteOrderItems() {
        List<DiscreteOrderItem> discreteOrderItems = new ArrayList<DiscreteOrderItem>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem instanceof BundleOrderItemImpl) {
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl) orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    discreteOrderItems.add(discreteOrderItem);
                }
            } else {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                discreteOrderItems.add(discreteOrderItem);
            }
        }
        return discreteOrderItems;
    }

    public List<DiscreteOrderItem> getDiscountableDiscreteOrderItems() {
        List<DiscreteOrderItem> discreteOrderItems = new ArrayList<DiscreteOrderItem>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem instanceof BundleOrderItemImpl) {
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl) orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    if (discreteOrderItem.getSku().isDiscountable()) {
                        discreteOrderItems.add(discreteOrderItem);
                    }
                }
            } else {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                if (discreteOrderItem.getSku().isDiscountable()) {
                    discreteOrderItems.add(discreteOrderItem);
                }
            }
        }
        return discreteOrderItems;
    }

    public List<OfferCode> getAddedOfferCodes() {
        return addedOfferCodes;
    }

    public void addAddedOfferCode(OfferCode addedOfferCode) {
        addedOfferCodes.add(addedOfferCode);
    }

    public void removeAllAddedOfferCodes() {
        if (addedOfferCodes != null) {
            addedOfferCodes.clear();
        }
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFulfillmentStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Map<Offer, OfferInfo> getAdditionalOfferInformation() {
        return additionalOfferInformation;
    }

    public void setAdditionalOfferInformation(Map<Offer, OfferInfo> additionalOfferInformation) {
        this.additionalOfferInformation = additionalOfferInformation;
    }

    public Money getItemAdjustmentsValue() {
        Money itemAdjustmentsValue = new Money(0);
        for (OrderItem orderItem : orderItems) {
            itemAdjustmentsValue = itemAdjustmentsValue.add(orderItem.getAdjustmentValue().multiply(orderItem.getQuantity()));
        }
        return itemAdjustmentsValue;
    }

    public Money getOrderAdjustmentsValue() {
        Money orderAdjustmentsValue = new Money(0);
        for (OrderAdjustment orderAdjustment : orderAdjustments) {
            orderAdjustmentsValue = orderAdjustmentsValue.add(orderAdjustment.getValue());
        }
        return orderAdjustmentsValue;
    }

    public Money getTotalAdjustmentsValue() {
        Money totalAdjustmentsValue = getItemAdjustmentsValue();
        if (totalAdjustmentsValue != null) {
            totalAdjustmentsValue = totalAdjustmentsValue.add(getOrderAdjustmentsValue());
        } else {
            totalAdjustmentsValue = getOrderAdjustmentsValue();
        }
        return totalAdjustmentsValue;
    }

    public boolean isNotCombinableOfferApplied() {
        return notCombinableOfferApplied;
    }

    public boolean isHasOrderAdjustments() {
        return hasOrderAdjustments;
    }

    public boolean updatePrices() {
        boolean updated = false;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.updatePrices()) {
                updated = true;
            }
        }
        return updated;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderImpl other = (OrderImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        Date otherDateCreated = other.auditable != null ? other.auditable.getDateCreated() : null;
        if (myDateCreated == null) {
            if (otherDateCreated != null)
                return false;
        } else if (!myDateCreated.equals(otherDateCreated))
            return false;
        return true;
    }

    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        result = prime * result + ((myDateCreated == null) ? 0 : myDateCreated.hashCode());
        return result;
    }

    public void setShippingPriceWithoutOffers(Money shippingPriceWithoutOffers) {
        this.shippingPriceWithoutOffers = shippingPriceWithoutOffers;

    }

    public void addToShippingPriceWithoutOffers(Money price) {
        if (price != null) {
            this.shippingPriceWithoutOffers = this.shippingPriceWithoutOffers.add(price);
        }
    }

    public Money getShippingPriceWithoutOffers() {
        return shippingPriceWithoutOffers;
    }

}
