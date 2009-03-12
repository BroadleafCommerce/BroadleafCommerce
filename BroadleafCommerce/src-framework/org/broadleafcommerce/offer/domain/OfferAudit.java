package org.broadleafcommerce.offer.domain;

import java.sql.Date;

import org.broadleafcommerce.util.money.Money;

public interface OfferAudit {
    public Long getId();

    public void setId(Long id);

    public Offer getOffer();

    public void setOffer(Offer offer);

    public Long getOfferCodeId();

    public void setOfferCodeId(Long offerCodeId);

    public Long getCustomerId();

    public void setCustomerId(Long customerId);

    public void setRelatedId(Long id);

    public Money getRelatedRetailPrice();

    public void setRelatedRetailPrice(Money relatedRetailPrice);

    public Money getRelatedSalePrice();

    public void setRelatedSalePrice(Money relatedSalePrice);

    public Money getRelatedPrice();

    public void setRelatedPrice(Money relatedPrice);

    public Date getRedeemedDate();

    public void setRedeemedDate(Date redeemedDate);
}
