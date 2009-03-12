package org.broadleafcommerce.promotion.domain;

import org.broadleafcommerce.profile.domain.Customer;

public interface OfferCustomer {
	public Long getId() ;

	public void setId(Long id) ;

	public OfferCode getOfferCode() ;

	public void setOfferCode(OfferCode offerCode) ;

	public Customer getCustomer() ;

	public void setCustomer(Customer customer) ;
	
	

}
