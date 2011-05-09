package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface BaseProcessor {

	public void clearOffersandAdjustments(Order order);
	
	public List<Offer> filterOffers(List<Offer> offers, Customer customer);
	
}
