#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.client.view.product;


import org.broadleafcommerce.open${artifactId}.client.view.dynamic.grid.GridStructureDisplay;

public interface MyCompanyProductDisplay {

	public GridStructureDisplay getShippingCountryDisplay();

}