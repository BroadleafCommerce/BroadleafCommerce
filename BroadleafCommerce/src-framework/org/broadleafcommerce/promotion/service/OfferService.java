package org.broadleafcommerce.promotion.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.promotion.domain.Offer;

public interface OfferService {
	public Offer lookupOfferByCode(String code);
	
	public boolean consumeOffer(Offer offer, Customer customer);
	
	public List<Offer> lookupValidOffersForSystem(String system);
}
