package org.broadleafcommerce.promotion.dao;

import org.broadleafcommerce.promotion.domain.Offer;

public interface OfferDao {

	public Offer readOfferById(Long offerId);
	
	public Offer maintainOffer(Offer offer);
	
	public void deleteOffer(Offer offer);
	
	public Offer create();
}
