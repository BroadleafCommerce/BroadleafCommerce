package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface OfferTargetCriteriaXref extends Serializable {

    Long getId();

    void setId(Long id);

    Offer getOffer();

    void setOffer(Offer offer);

    OfferItemCriteria getOfferItemCriteria();

    void setOfferItemCriteria(OfferItemCriteria offerItemCriteria);

}
