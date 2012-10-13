package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.pricelist.domain.PriceList;

import java.io.Serializable;


public interface OfferRestrictedPriceList extends Serializable {

    PriceList getPriceList();

    Offer getOffer();

    void setOffer(Offer offer);

    void setPriceList(PriceList priceList);

}
