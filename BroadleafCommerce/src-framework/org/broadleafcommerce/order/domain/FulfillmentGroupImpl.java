package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.offer.domain.OfferAuditImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.broadleafcommerce.type.FulfillmentGroupType;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP")

public class FulfillmentGroupImpl implements FulfillmentGroup, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentGroupId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentGroupImpl", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @OneToMany(mappedBy = "fulfillmentGroupId", targetEntity = FulfillmentGroupItemImpl.class)
    private List<FulfillmentGroupItem> fulfillmentGroupItems;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(targetEntity = PhoneImpl.class)
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;

    @Column(name = "METHOD")
    private String method;

    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private FulfillmentGroupType type;

    @OneToMany(mappedBy = "id", targetEntity = OfferImpl.class)
    @MapKey(name = "id")
    private List<Offer> candidateOffers;

    @OneToMany(mappedBy = "id", targetEntity = OfferAuditImpl.class)
    @MapKey(name = "id")
    private List<OfferAudit> appliedOffers;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems) {
        this.fulfillmentGroupItems = fulfillmentGroupItems;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String fulfillmentMethod) {
        this.method = fulfillmentMethod;
    }

    @Override
    public Money getRetailPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    @Override
    public void setRetailPrice(Money fulfillmentCost) {
        this.retailPrice = Money.toAmount(fulfillmentCost);
    }

    @Override
    public FulfillmentGroupType getType() {
        return type;
    }

    @Override
    public void setType(FulfillmentGroupType type) {
        this.type = type;
    }

    @Override
    public void addCandidateOffer(Offer offer) {
        candidateOffers.add(offer);
    }

    @Override
    public List<OfferAudit> getAppliedOffers() {
        return appliedOffers;
    }

    @Override
    public List<Offer> getCandidateOffers() {
        return candidateOffers;
    }

    @Override
    public void setAppliedOffers(List<OfferAudit> offers) {
        this.appliedOffers = offers;

    }

    @Override
    public void setCandaditeOffers(List<Offer> offers) {
        this.candidateOffers = offers;

    }

    @Override
    public void addAppliedOffer(OfferAudit offer) {
        appliedOffers.add(offer);

    }

    public Money getSalePrice() {
        return salePrice == null ? null : new Money(salePrice);
    }

    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    public Money getPrice() {
        return price == null ? null : new Money(price);
    }

    public void setPrice(Money price) {
        this.price = Money.toAmount(price);
    }

    public void setCandidateOffers(List<Offer> candidateOffers) {
        this.candidateOffers = candidateOffers;
    }

    public void removeAllOffers() {
        if (candidateOffers != null) {
            candidateOffers.clear();
        }
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
