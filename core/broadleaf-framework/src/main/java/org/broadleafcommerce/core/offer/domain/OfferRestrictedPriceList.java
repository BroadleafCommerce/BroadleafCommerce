package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.pricelist.domain.PriceList;


public interface OfferRestrictedPriceList {

    PriceList getPriceList();

    Offer getOffer();

    void setOffer(Offer offer);

    void setPriceList(PriceList priceList);

}
