package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.type.OrderStatus;
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
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name="CITY_TAX")
    private BigDecimal cityTax;

    @Column(name="COUNTY_TAX")
    private BigDecimal countyTax;

    @Column(name="STATE_TAX")
    private BigDecimal stateTax;

    @Column(name="COUNTRY_TAX")
    private BigDecimal countryTax;

    @Column(name="TOTAL_TAX")
    private BigDecimal totalTax;

    @Column(name = "ORDER_SUBTOTAL")
    private BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL")
    private BigDecimal total;

    @Column(name = "SUBMIT_DATE")
    private Date submitDate;

    @OneToMany(mappedBy = "orderId", targetEntity = OrderItemImpl.class)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "orderId", targetEntity = FulfillmentGroupImpl.class)
    private List<FulfillmentGroup> fulfillmentGroups;

    @OneToMany(mappedBy = "id", targetEntity = OfferImpl.class)
    @MapKey(name = "id")
    private List<Offer> candidateOffers;

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

    public void setCandidateOffers(List<Offer> candidateOffers) {
        this.candidateOffers = candidateOffers;
    }

    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
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
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    @Override
    public void removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
    }

    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    public void addFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroups.add(fulfillmentGroup);
    }

    public void removeFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroups.remove(fulfillmentGroup);
    }

    @Override
    public void addCandidateOffer(Offer offer) {
        candidateOffers.add(offer);
    }

    @Override
    public List<Offer> getCandidateOffers() {
        return candidateOffers;
    }

    @Override
    public void removeAllOffers() {
        if (candidateOffers != null) {
            candidateOffers.clear();
        }
        if (getOrderItems() != null) {
            for (OrderItem item : getOrderItems()) {
                item.removeAllOffers();
            }
        }

        if (getFulfillmentGroups() != null) {
            for (FulfillmentGroup fg : getFulfillmentGroups()) {
                fg.removeAllOffers();
            }
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
}
