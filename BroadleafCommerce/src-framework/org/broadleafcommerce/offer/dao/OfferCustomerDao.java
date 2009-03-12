package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferCustomer;

public interface OfferCustomerDao {

	public OfferCustomer readOfferCustomerById(Long offerCustomerId);
	
	public OfferCustomer maintainOfferCustomer(OfferCustomer offerCustomer);
	
	public void deleteOfferCustomer(OfferCustomer offerCustomer);
	
	public OfferCustomer create();
}
