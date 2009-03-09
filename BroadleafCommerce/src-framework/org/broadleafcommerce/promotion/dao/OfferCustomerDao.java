package org.broadleafcommerce.promotion.dao;

import org.broadleafcommerce.promotion.domain.OfferCustomer;

public interface OfferCustomerDao {

	public OfferCustomer readOfferCustomerById(Long offerCustomerId);
	
	public OfferCustomer maintainOfferCustomer(OfferCustomer offerCustomer);
	
	public void deleteOfferCustomer(OfferCustomer offerCustomer);
	
	public OfferCustomer create();
}
