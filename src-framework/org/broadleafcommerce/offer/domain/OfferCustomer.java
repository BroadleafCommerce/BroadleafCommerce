package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.profile.domain.Customer;

// TODO: Should rename to CustomerOffer
public interface OfferCustomer {
	public Long getId() ;

	public void setId(Long id) ;

	public OfferCode getOfferCode() ;

	public void setOfferCode(OfferCode offerCode) ;

	public Customer getCustomer() ;

	public void setCustomer(Customer customer) ;



}
