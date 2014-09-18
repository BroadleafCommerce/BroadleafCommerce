package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface OfferOfferRuleXref extends Serializable {

    Long getId();

    void setId(Long id);

    Offer getOffer();

    void setOffer(Offer offer);

    OfferRule getOfferRule();

    void setOfferRule(OfferRule offerRule);

    String getKey();

    void setKey(String key);

}
