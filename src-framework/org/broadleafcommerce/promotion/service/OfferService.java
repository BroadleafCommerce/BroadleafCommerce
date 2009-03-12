package org.broadleafcommerce.promotion.service;

import java.util.List;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.promotion.domain.Offer;
import org.broadleafcommerce.promotion.domain.OfferAudit;
import org.broadleafcommerce.promotion.domain.OfferCode;

public interface OfferService {
	public Offer lookupOfferByCode(String code);
	
	public boolean consumeOffer(Offer offer, Customer customer);
	
	public List<Offer> lookupValidOffersForSystem(String system);
	
	public OfferCode lookupCodeByOffer(Offer offer);
	
	public List<OfferAudit> findAppliedOffers(List<Offer> candidateOffers, OrderItem orderItem);
}
