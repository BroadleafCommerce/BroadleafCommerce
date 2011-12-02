#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.catalog.domain;

import java.io.Serializable;
import java.util.List;

public interface ShippingCountry extends Serializable{

	public Long getId();
	
	public void setId(Long id);
	
	public String getCurrencyCode();
	
	public void setCurrencyCode(String currencyCode);
	
	public String getCountryISO();
	
	public void setCountryISO(String countryIso);
	
	public List<MyCompanyProduct> getProducts();

	public void setProducts(List<MyCompanyProduct> products);
}
