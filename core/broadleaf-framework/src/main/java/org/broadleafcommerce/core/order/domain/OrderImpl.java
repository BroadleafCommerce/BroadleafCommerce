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

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKeyManyToMany;

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
    {
        @AdminPresentationOverride(name="customer.auditable", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="customer.challengeQuestion", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="customer.challengeAnswer", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="customer.passwordChangeRequired", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="customer.receiveEmail", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="customer.registered", value=@AdminPresentation(excluded = true))
    }
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
    protected BigDecimal totalShipping;

    @Column(name = "ORDER_SUBTOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Subtotal", group = "OrderImpl_Order", order=3, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderImpl_Order_Total", group = "OrderImpl_Order", order=11, fieldType= SupportedFieldType.MONEY)
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

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OrderAdjustment> orderAdjustments = new ArrayList<OrderAdjustment>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OfferCodeImpl.class)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_CODE_ID", referencedColumnName = "OFFER_CODE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OfferCode> addedOfferCodes = new ArrayList<OfferCode>();

    @OneToMany(mappedBy = "order", targetEntity = CandidateOrderOfferImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateOrderOffer> candidateOrderOffers = new ArrayList<CandidateOrderOffer>();

    @OneToMany(mappedBy = "order", targetEntity = PaymentInfoImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

    @ManyToMany(targetEntity=OfferInfoImpl.class)
    @JoinTable(name = "BLC_ADDITIONAL_OFFER_INFO", inverseJoinColumns = @JoinColumn(name = "OFFER_INFO_ID", referencedColumnName = "OFFER_INFO_ID"))
    @MapKeyManyToMany(joinColumns = {@JoinColumn(name = "OFFER_ID") }, targetEntity=OfferImpl.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected Map<Offer, OfferInfo> additionalOfferInformation = new HashMap<Offer, OfferInfo>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAttributeImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @MapKey(name="value")
    protected Map<String,OrderAttribute> orderAttributes = new HashMap<String,OrderAttribute>();

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

    public Money calculateOrderItemsFinalPrice(boolean includeNonTaxableItems) {
        Money calculatedSubTotal = new Money();
        for (OrderItem orderItem : orderItems) {
        	Money price;
        	if (includeNonTaxableItems) {
        		price = orderItem.getPrice();
        	} else {
        		price = orderItem.getTaxablePrice();
        	}
            calculatedSubTotal = calculatedSubTotal.add(price.multiply(orderItem.getQuantity()));
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
        return OrderStatus.getInstance(status);
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

    public List<CandidateOrderOffer> getCandidateOrderOffers() {
        return candidateOrderOffers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            if(orderItem.isInCategory(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public List<OrderAdjustment> getOrderAdjustments() {
        return this.orderAdjustments;
    }

    protected void setOrderAdjustments(List<OrderAdjustment> orderAdjustments) {
        this.orderAdjustments = orderAdjustments;
    }

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
    	for (DiscreteOrderItem discreteOrderItem : getDiscreteOrderItems()) {
    		if (discreteOrderItem.getSku() != null && discreteOrderItem.getSku().equals(sku)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    public List<OfferCode> getAddedOfferCodes() {
        return addedOfferCodes;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFulfillmentStatus() {
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
    
    public Money getFulfillmentGroupAdjustmentsValue() {
    	Money adjustmentValue = new Money(0);
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
        	adjustmentValue = adjustmentValue.add(fulfillmentGroup.getFulfillmentGroupAdjustmentsValue());
        }
        return adjustmentValue;
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
        totalAdjustmentsValue = totalAdjustmentsValue.add(getOrderAdjustmentsValue());
        totalAdjustmentsValue = totalAdjustmentsValue.add(getFulfillmentGroupAdjustmentsValue());
        return totalAdjustmentsValue;
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

    public Map<String, OrderAttribute> getOrderAttributes() {
        return orderAttributes;
    }

    public void setOrderAttributes(Map<String, OrderAttribute> orderAttributes) {
        this.orderAttributes = orderAttributes;
    }

    @Deprecated
	public void addAddedOfferCode(OfferCode offerCode) {
		addOfferCode(offerCode);
	}
	
	public void addOfferCode(OfferCode offerCode) {
        getAddedOfferCodes().add(offerCode);
    }
	
	@Override
	public int getItemCount() {
		int count = 0;
		for (DiscreteOrderItem doi : getDiscreteOrderItems()) {
			count += doi.getQuantity();
		}
		return count;
	}


	public boolean equals(Object obj) {
	   	if (this == obj)
	        return true;
	    if (obj == null)
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

}
