#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.client.presenter.product;

import java.util.HashMap;

import ${package}.${artifactId}.client.datasource.ShippingCountryListDataSourceFactory;
import ${package}.${artifactId}.client.datasource.ShippingCountrySearchDataSourceFactory;
import ${package}.${artifactId}.client.view.product.MyCompanyProductDisplay;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import org.broadleafcommerce.${artifactId}.client.presenter.catalog.product.OneToOneProductSkuPresenter;
import org.broadleafcommerce.open${artifactId}.client.BLCMain;
import org.broadleafcommerce.open${artifactId}.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.open${artifactId}.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.open${artifactId}.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.open${artifactId}.client.presenter.structure.SimpleSearchJoinStructurePresenter;
import org.broadleafcommerce.open${artifactId}.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.open${artifactId}.client.setup.PresenterSetupItem;
import org.broadleafcommerce.open${artifactId}.client.view.dynamic.dialog.EntitySearchDialog;

public class MyCompanyProductPresenter extends OneToOneProductSkuPresenter {

	protected SubPresentable shippingCountryPresenter;
	protected HashMap<String, Object> ganzCategoryLibrary = new HashMap<String, Object>();
	
	@Override
	public void bind() {
		super.bind();
		shippingCountryPresenter.bind();
	}
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		super.changeSelection(selectedRecord);
		final AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();

		shippingCountryPresenter.load(selectedRecord, dataSource, null);
	}
	
	@Override
	public void setup() {
		super.setup();
		
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("shippingCountrySearchDS", new ShippingCountrySearchDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource shippingCountrySearchDataSource = (ListGridDataSource) result;
				shippingCountrySearchDataSource.resetPermanentFieldVisibility(
					"countryIso",
					"currencyCode"
				);
				EntitySearchDialog shippingCountrySearchView = new EntitySearchDialog(shippingCountrySearchDataSource);
				ganzCategoryLibrary.put("shippingCountrySearchView", shippingCountrySearchView);
			}
		}));
		
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("shippingCountryDS", new ShippingCountryListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {		
				ListGridDataSource shippingCountryDataSource = (ListGridDataSource) result;
				shippingCountryPresenter = new SimpleSearchJoinStructurePresenter(((MyCompanyProductDisplay) getDisplay()).getShippingCountryDisplay(), (EntitySearchDialog) ganzCategoryLibrary.get("shippingCountrySearchView"), BLCMain.getMessageManager().getString("categorySearchPrompt"));
				shippingCountryPresenter.setDataSource(shippingCountryDataSource, new String[]{"countryIso", "currencyCode"}, new Boolean[]{false, false});
			}
		}));
		
	}

}
