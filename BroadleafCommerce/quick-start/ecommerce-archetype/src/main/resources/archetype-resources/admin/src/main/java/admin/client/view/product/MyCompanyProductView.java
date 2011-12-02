#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.client.view.product;

import ${package}.${artifactId}.client.MyCompanyAdminModule;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.${artifactId}.client.view.catalog.product.OneToOneProductSkuView;
import org.broadleafcommerce.open${artifactId}.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.open${artifactId}.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.open${artifactId}.client.view.dynamic.grid.GridStructureView;


public class MyCompanyProductView extends OneToOneProductSkuView implements MyCompanyProductDisplay {

	protected GridStructureView shippingCountryDisplay;
	
	@Override
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		super.build(entityDataSource, additionalDataSources);
		shippingCountryDisplay = new GridStructureView(MyCompanyAdminModule.ADMINMESSAGES.shippingCountryListTitle(), false, false);
		((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(shippingCountryDisplay);
	}

	/* (non-Javadoc)
	 * @see com.ganz.${artifactId}.client.view.product.GanzProductDisplay${symbol_pound}getAdditionalAttributesDisplay()
	 */
	@Override
	public GridStructureDisplay getShippingCountryDisplay() {
		return shippingCountryDisplay;
	}
	
}
