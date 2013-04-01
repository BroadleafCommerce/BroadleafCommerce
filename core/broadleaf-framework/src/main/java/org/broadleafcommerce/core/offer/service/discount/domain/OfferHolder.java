package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.core.offer.domain.Offer;


public interface OfferHolder {

    Offer getOffer();

    BroadleafCurrency getCurrency();

}
