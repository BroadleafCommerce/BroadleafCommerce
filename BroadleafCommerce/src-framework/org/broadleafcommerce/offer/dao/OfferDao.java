package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.Offer;

public interface OfferDao {

	public Offer readOfferById(Long offerId);
	
	public Offer maintainOffer(Offer offer);
	
	public void deleteOffer(Offer offer);
	
	public Offer create();
}
