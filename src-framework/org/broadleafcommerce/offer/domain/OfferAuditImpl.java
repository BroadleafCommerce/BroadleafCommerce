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
package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.util.money.Money;

@Entity
@Table(name = "OFFER_AUDIT")
@Inheritance(strategy = InheritanceType.JOINED)
public class OfferAuditImpl implements OfferAudit {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OfferAuditId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OfferAuditId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OfferAuditImpl", allocationSize = 50)
    @Column(name = "OFFER_AUDIT_ID")
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinColumn(name = "OFFER_ID")
    protected Offer offer;

    @Column(name = "OFFER_CODE_ID")
    protected Long offerCodeId;

    @Column(name = "CUSTOMER_ID")
    protected Long customerId;

    @Column(name = "OFFER_TYPE")
    protected String offerType;

    @Column(name = "RELATED_ID")
    protected Long relatedId;

    @Column(name = "RELATED_RETAIL_PRICE")
    protected BigDecimal relatedRetailPrice;

    @Column(name = "RELATED_SALE_PRICE")
    protected BigDecimal relatedSalePrice;

    @Column(name = "RELATED_PRICE")
    protected BigDecimal relatedPrice;

    @Column(name = "REDEEMED_DATE")
    protected Date redeemedDate;

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
        return offerType == null ? null : OfferDiscountType.getInstance(offerType);
    }

    public void setOfferType(OfferDiscountType offerType) {
        this.offerType = offerType.getType();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((offerCodeId == null) ? 0 : offerCodeId.hashCode());
        result = prime * result + ((offerType == null) ? 0 : offerType.hashCode());
        result = prime * result + ((redeemedDate == null) ? 0 : redeemedDate.hashCode());
        result = prime * result + ((relatedId == null) ? 0 : relatedId.hashCode());
        result = prime * result + ((relatedPrice == null) ? 0 : relatedPrice.hashCode());
        result = prime * result + ((relatedRetailPrice == null) ? 0 : relatedRetailPrice.hashCode());
        result = prime * result + ((relatedSalePrice == null) ? 0 : relatedSalePrice.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OfferAuditImpl other = (OfferAuditImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customerId == null) {
            if (other.customerId != null)
                return false;
        } else if (!customerId.equals(other.customerId))
            return false;
        if (offer == null) {
            if (other.offer != null)
                return false;
        } else if (!offer.equals(other.offer))
            return false;
        if (offerCodeId == null) {
            if (other.offerCodeId != null)
                return false;
        } else if (!offerCodeId.equals(other.offerCodeId))
            return false;
        if (offerType == null) {
            if (other.offerType != null)
                return false;
        } else if (!offerType.equals(other.offerType))
            return false;
        if (redeemedDate == null) {
            if (other.redeemedDate != null)
                return false;
        } else if (!redeemedDate.equals(other.redeemedDate))
            return false;
        if (relatedId == null) {
            if (other.relatedId != null)
                return false;
        } else if (!relatedId.equals(other.relatedId))
            return false;
        if (relatedPrice == null) {
            if (other.relatedPrice != null)
                return false;
        } else if (!relatedPrice.equals(other.relatedPrice))
            return false;
        if (relatedRetailPrice == null) {
            if (other.relatedRetailPrice != null)
                return false;
        } else if (!relatedRetailPrice.equals(other.relatedRetailPrice))
            return false;
        if (relatedSalePrice == null) {
            if (other.relatedSalePrice != null)
                return false;
        } else if (!relatedSalePrice.equals(other.relatedSalePrice))
            return false;
        return true;
    }
}
