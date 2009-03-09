package org.broadleafcommerce.promotion.dao;

import org.broadleafcommerce.promotion.domain.OfferCode;

public interface OfferCodeDao {
	
	public OfferCode readOfferCodeById(Long offerCode);
	
	public OfferCode maintainOfferCode(OfferCode offerCode);
	
	public void deleteOfferCode(OfferCode offerCodeId);
	
	public OfferCode create();
	
}
