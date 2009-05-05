package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferCode;

public interface OfferCodeDao {
	
	public OfferCode readOfferCodeById(Long offerCode);
	
	public OfferCode save(OfferCode offerCode);
	
	public void delete(OfferCode offerCodeId);
	
	public OfferCode create();
	
}
