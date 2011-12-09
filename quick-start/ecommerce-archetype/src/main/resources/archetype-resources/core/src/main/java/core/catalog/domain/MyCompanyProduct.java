#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.catalog.domain;

import java.util.List;

import org.broadleafcommerce.${artifactId}.catalog.domain.ProductSku;
import org.broadleafcommerce.${artifactId}.store.domain.ZipCode;

public interface MyCompanyProduct extends ProductSku {

	public Boolean isRestricted();

	public void setRestricted(Boolean restricted);

	public List<ShippingCountry> getShippingCountries();

	public void setShippingCountries(List<ShippingCountry> shippingCountries);

    public ZipCode getZipCode();

    public void setZipCode(ZipCode zipCode);
	
}