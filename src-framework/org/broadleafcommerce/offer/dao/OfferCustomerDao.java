package org.broadleafcommerce.offer.dao;

import java.util.List;

import org.broadleafcommerce.offer.domain.OfferCustomer;
import org.broadleafcommerce.profile.domain.Customer;

// TODO: should rename to CustomerOfferDao
public interface OfferCustomerDao {

	public OfferCustomer readOfferCustomerById(Long offerCustomerId);

    public List<OfferCustomer> readOffersByCustomer(Customer customer);

	public OfferCustomer save(OfferCustomer offerCustomer);

	public void delete(OfferCustomer offerCustomer);

	public OfferCustomer create();
}
