package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER")
public class OrderImpl implements Order, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderImpl", allocationSize = 1)
    @Column(name = "ORDER_ID")
    private Long id;

    @Embedded
    private Auditable auditable;

    @Column(name = "NAME")
    private String name;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @Column(name = "ORDER_STATUS")
    private String status;

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

    @Column(name = "TOTAL_SHIPPING")
    private BigDecimal totalShipping;

    @Column(name = "ORDER_SUBTOTAL")
    private BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL")
    private BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    private Date submitDate;

    @Transient
    private BigDecimal adjustmentPrice;  // retailPrice with order adjustments (no item adjustments)

    @OneToMany(mappedBy = "order", targetEntity = OrderItemImpl.class, cascade = {CascadeType.ALL})
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    @OneToMany(mappedBy = "order", targetEntity = FulfillmentGroupImpl.class, cascade = {CascadeType.ALL})
    private List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();

    @OneToMany(mappedBy = "order", targetEntity = OrderAdjustmentImpl.class, cascade = {CascadeType.ALL})
    private List<OrderAdjustment> orderAdjustments = new ArrayList<OrderAdjustment>();

    //TODO does this work?? MapKey is supposed to be used with the type "Map" This should be a many to many. Make sure to add a cascade annotation with delete_orphans as well.
    @OneToMany(mappedBy = "id", targetEntity = OfferImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "id")
    private List<CandidateOrderOffer> candidateOffers = new ArrayList<CandidateOrderOffer>();

    @OneToMany(mappedBy = "order", targetEntity = PaymentInfoImpl.class, cascade = {CascadeType.ALL})
    private List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

    @Transient
    private boolean markedForOffer;

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

    /*
     * TODO we prob need to remove this method, as subtotal is currently
     * handled through the pricing workflow. We do not want to handle pricing
     * from 2 different directions.
     */
    public Money calculateSubTotal() {
        Money calculatedSubTotal = new Money();
        for (OrderItem orderItem : orderItems) {
            Money currentItemPrice = orderItem.getCurrentPrice();
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

    public void setCandidateOffers(List<CandidateOrderOffer> candidateOffers) {
        this.candidateOffers = candidateOffers;
    }

    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }

    public Money getRemainingTotal() {
        if (getPaymentInfos() == null) {
            return null;
        }
        Money totalPayments = new Money(BigDecimal.ZERO);
        for (PaymentInfo pi : getPaymentInfos()) {
            if (pi.getAmount() != null) {
                totalPayments = totalPayments.add(pi.getAmount());
            }
        }
        return getTotal().subtract(totalPayments);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    @Override
    public void addCandidateOrderOffer(CandidateOrderOffer candidateOffer) {
        candidateOffers.add(candidateOffer);
    }

    @Override
    public List<CandidateOrderOffer> getCandidateOrderOffers() {
        return candidateOffers;
    }

    @Override
    public void removeAllCandidateOffers() {
        if (candidateOffers != null) {
            candidateOffers.clear();
        }
        if (getOrderItems() != null) {
            for (OrderItem item : getOrderItems()) {
                item.removeAllCandidateOffers();
            }
        }

        if (getFulfillmentGroups() != null) {
            for (FulfillmentGroup fg : getFulfillmentGroups()) {
                fg.removeAllCandidateOffers();
            }
        }
    }

    @Override
    public void removeAllOrderCandidateOffers() {
        if (candidateOffers != null) {
            candidateOffers.clear();
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof OrderImpl)) return false;

        OrderImpl item = (OrderImpl) other;

        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        if (customer != null ? !customer.equals(item.customer) : item.customer != null) return false;

        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        Date otherDateCreated = item.auditable != null ? item.auditable.getDateCreated() : null;
        if (myDateCreated != null ? !myDateCreated.equals(otherDateCreated) : otherDateCreated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        Date myDateCreated = auditable != null ? auditable.getDateCreated() : null;
        result = 31 * result + (myDateCreated != null ? myDateCreated.hashCode() : 0);

        return result;
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

    public List<OrderAdjustment> getOrderAdjustments() {
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
        return this.orderAdjustments;
    }

    public void reapplyOrderAdjustments(){
        adjustmentPrice = getSubTotal().getAmount();
        for (OrderAdjustment orderAdjustment : orderAdjustments) {
            orderAdjustment.computeAdjustmentValue();
            adjustmentPrice = adjustmentPrice.subtract(orderAdjustment.getValue().getAmount());
        }
    }

    public void removeAllAdjustments() {
        removeAllItemAdjustments();
        removeAllFulfillmentAdjustments();
        removeAllOrderAdjustments();
    }

    public void removeAllOrderAdjustments() {
        if (orderAdjustments != null) {
            orderAdjustments.clear();
        }
        adjustmentPrice = null;
    }

    public void removeAllItemAdjustments() {
        for (OrderItem orderItem: orderItems) {
            orderItem.removeAllAdjustments();
        }
    }

    public void removeAllFulfillmentAdjustments() {
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            fulfillmentGroup.removeAllAdjustments();
        }
    }

    public void setOrderAdjustments(List<OrderAdjustment> orderAdjustments) {
        this.orderAdjustments = orderAdjustments;
    }

    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    public void setAdjustmentPrice(Money adjustmentPrice) {
        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    }

    /*
     * Checks to see if the orders items in this order has an adjustment with a not combinable
     * offer.
     */
    public boolean containsNotCombinableItemOffer() {
        boolean isContainsNotCombinableItemOffer = false;
        for (OrderItem orderItem: orderItems) {
            for (OrderItemAdjustment itemAdjustment : orderItem.getOrderItemAdjustments()) {
                if (!itemAdjustment.getOffer().isCombinableWithOtherOffers()) {
                    isContainsNotCombinableItemOffer = true;
                    break;
                }
            }
        }
        return isContainsNotCombinableItemOffer;
    }

    /*
     * Checks the order adjustment to see if it is not stackable
     */
    public boolean containsNotStackableOrderOffer() {
        boolean isContainsNotStackableOrderOffer = false;
        for (OrderAdjustment orderAdjustment: orderAdjustments) {
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
                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl)orderItem;
                for (DiscreteOrderItem descreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                    discreteOrderItems.add(descreteOrderItem);
                }
            } else {
                DiscreteOrderItem descreteOrderItem = (DiscreteOrderItem)orderItem;
                discreteOrderItems.add(descreteOrderItem);
            }
        }
        return discreteOrderItems;
    }


}
