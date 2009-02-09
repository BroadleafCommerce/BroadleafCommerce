package org.broadleafcommerce.profile.service;

import java.util.Map;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;

public interface CustomerAddressService {
	
	public Address addAddress(Customer customer, String addressKey, Address address, boolean standardize);
	
	public Address updateAddress(Customer customer, String addressKey, Address address, boolean standardize);
	
	public Address removeAddress(Customer customer, String addressKey);
	
	Map<String, Address> lookupAddressForCustomer(Customer customer);  
}
