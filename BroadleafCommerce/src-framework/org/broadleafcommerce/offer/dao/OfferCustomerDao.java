package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferCustomer;

public interface OfferCustomerDao {

	public OfferCustomer readOfferCustomerById(Long offerCustomerId);
	
	public OfferCustomer save(OfferCustomer offerCustomer);
	
	public void delete(OfferCustomer offerCustomer);
	
	public OfferCustomer create();
}
