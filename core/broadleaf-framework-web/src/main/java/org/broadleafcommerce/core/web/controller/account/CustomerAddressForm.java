package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;

import java.io.Serializable;

public class CustomerAddressForm implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Address address = new AddressImpl();
	protected String addressName;
	protected Long customerAddressId;
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public Long getCustomerAddressId() {
		return customerAddressId;
	}
	public void setCustomerAddressId(Long customerAddressId) {
		this.customerAddressId = customerAddressId;
	}
	
}
