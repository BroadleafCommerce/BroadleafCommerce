package org.broadleafcommerce.core.offer.domain;


import java.math.BigDecimal;



public interface OfferTier extends Comparable<OfferTier>{

    Long getId();

    void setId(Long id);

    BigDecimal getAmount();

    Long getMinQuantity();

    void setAmount(BigDecimal amount);

    void setMinQuantity(Long minQuantity);

    Offer getOffer();

    void setOffer(Offer offer);

}
