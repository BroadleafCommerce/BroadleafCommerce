package org.broadleafcommerce.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.util.money.Money;

@Entity
@Table(name = "OFFER_AUDIT")
public class OfferAuditImpl implements Serializable, OfferAudit {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "OFFER_AUDIT_ID")
    private Long id;

    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    @Column(name = "OFFER_CODE_ID")
    private Long offerCodeId;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "OFFER_TYPE")
    private OfferDiscountType offerType;

    @Column(name = "RELATED_ID")
    private Long relatedId;

    @Column(name = "RELATED_RETAIL_PRICE")
    private BigDecimal relatedRetailPrice;

    @Column(name = "RELATED_SALE_PRICE")
    private BigDecimal relatedSalePrice;

    @Column(name = "RELATED_PRICE")
    private BigDecimal relatedPrice;

    @Column(name = "REDEEMED_DATE")
    private Date redeemedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Long getOfferCodeId() {
        return offerCodeId;
    }

    public void setOfferCodeId(Long offerCodeId) {
        this.offerCodeId = offerCodeId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OfferDiscountType getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferDiscountType offerType) {
        this.offerType = offerType;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public Money getRelatedRetailPrice() {
        return relatedRetailPrice == null ? null : new Money(relatedRetailPrice);
    }

    public void setRelatedRetailPrice(Money relatedRetailPrice) {
        this.relatedRetailPrice = Money.toAmount(relatedRetailPrice);
    }

    public Money getRelatedSalePrice() {
        return relatedSalePrice == null ? null : new Money(relatedSalePrice);
    }

    public void setRelatedSalePrice(Money relatedSalePrice) {
        this.relatedSalePrice = Money.toAmount(relatedSalePrice);
    }

    public Money getRelatedPrice() {
        return relatedPrice == null ? null : new Money(relatedPrice);
    }

    public void setRelatedPrice(Money relatedPrice) {
        this.relatedPrice = Money.toAmount(relatedPrice);
    }

    public Date getRedeemedDate() {
        return redeemedDate;
    }

    public void setRedeemedDate(Date redeemedDate) {
        this.redeemedDate = redeemedDate;
    }
}
