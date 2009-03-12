package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferCode;

public interface OfferCodeDao {
	
	public OfferCode readOfferCodeById(Long offerCode);
	
	public OfferCode maintainOfferCode(OfferCode offerCode);
	
	public void deleteOfferCode(OfferCode offerCodeId);
	
	public OfferCode create();
	
}
