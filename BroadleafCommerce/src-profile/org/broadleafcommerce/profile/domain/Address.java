package org.broadleafcommerce.profile.domain;


public interface Address {

	public void setId(Long id);
	
	public Long getId();
	
	public void setAddressName(String addressName);
	
	public String getAddressName();

	public Customer getCustomer();
	
	public void setCustomer(Customer customer);
	
	public void setAddressLine1(String addressLine1);
	
	public String getAddressLine1();
	
	public void setAddressLine2(String addressLine2);
	
	public String getAddressLine2();
	
	public void setCity(String city);
	
	public String getCity();
	
	public void setStateProvRegion(String stateProvRegion);
	
	public String getStateProvRegion();
	
	public void setPostalCode(String postalCode);
	
	public String getPostalCode();
	
	// This field is temporary and will be removed later
	public String getZipFour();
	
	// This field is temporary and will be removed later
	public void setZipFour(String zipFour);
	
	public void setCountry(CountryEnums.Country country);
	
	public CountryEnums.Country getCountry();
	
	public String getTokenizedAddress();
	
	public void setTokenizedAddress(String tAddress);
	
	public Boolean getStandardized();
	
	public void setStandardized(Boolean standardized);
	
}
