package org.broadleafcommerce.profile.domain;


//@Entity
//@Table(name="BLC_CUSTOMER_ADDRESS")
public class BroadleafCustomerAddress implements Address {

	private Long id;
	private String addressName;	
	private Customer customer;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private CountryEnums.Country country;
	private String postalCode;
	private String stateProvRegion;
	private String tokenizedAddress;
    private Boolean standardized = Boolean.FALSE;
    // This field is temporary and will be removed later
	private String zipFour;    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public CountryEnums.Country getCountry() {
		return country;
	}
	public void setCountry(CountryEnums.Country country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getStateProvRegion() {
		return stateProvRegion;
	}
	public void setStateProvRegion(String stateProvRegion) {
		this.stateProvRegion = stateProvRegion;
	}
	public String getTokenizedAddress() {
		return tokenizedAddress;
	}
	public void setTokenizedAddress(String tokenizedAddress) {
		this.tokenizedAddress = tokenizedAddress;
	}
	public Boolean getStandardized() {
		return standardized;
	}
	public void setStandardized(Boolean standardized) {
		this.standardized = standardized;
	}
	public String getZipFour() {
		return zipFour;
	}
	public void setZipFour(String zipFour) {
		this.zipFour = zipFour;
	}
	


}
