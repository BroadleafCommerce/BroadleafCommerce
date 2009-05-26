package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.util.money.Money;

public interface Adjustment {
    public Long getId();

    public void setId(Long id);

    public Offer getOffer();

    public String getReason();

    public void setReason(String reason);

    public Money getValue();

}
