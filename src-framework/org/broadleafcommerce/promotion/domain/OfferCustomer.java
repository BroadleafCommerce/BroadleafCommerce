package org.broadleafcommerce.promotion.domain;

import org.broadleafcommerce.profile.domain.Customer;

public interface OfferCustomer {
	public Long getId() ;

	public void setId(Long id) ;

	public OfferImpl getOfferCode() ;

	public void setOfferCode(OfferImpl offerCode) ;

	public Customer getCustomer() ;

	public void setCustomer(Customer customer) ;
	
	

}
