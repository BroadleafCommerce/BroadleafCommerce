package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.Offer;

public interface OfferDao {

	public Offer readOfferById(Long offerId);
	
	public Offer save(Offer offer);
	
	public void delete(Offer offer);
	
	public Offer create();
}
