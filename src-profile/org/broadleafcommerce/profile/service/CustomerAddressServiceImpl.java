package org.broadleafcommerce.profile.service;

import java.util.Map;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;

public class CustomerAddressServiceImpl implements CustomerAddressService {

	public Address addAddress(Customer customer, String addressKey, Address address, boolean standardize){
		return null;
	}
	
	public Address updateAddress(Customer customer, String addressKey, Address address, boolean standardize){
		return null;
	}
	
	public Address removeAddress(Customer customer, String addressKey){
		return null;
	}
	
	public Map<String, Address> lookupAddressForCustomer(Customer customer){
		return null;
	}  

}
