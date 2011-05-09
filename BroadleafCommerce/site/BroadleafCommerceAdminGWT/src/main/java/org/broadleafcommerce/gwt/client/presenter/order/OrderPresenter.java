package org.broadleafcommerce.gwt.client.presenter.order;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.datasource.order.BundledOrderItemListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.order.CountryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.order.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.order.OrderItemListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.order.OrderListDataSourceFactory;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.SubPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.catalog.OneToOneProductSkuDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.order.OrderDisplay;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class OrderPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected SubPresenter orderItemPresenter;
	protected SubPresenter fulfillmentGroupPresenter;
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		orderItemPresenter.load(selectedRecord, null);
		fulfillmentGroupPresenter.load(selectedRecord, null);
	}
	
	@Override
	public void bind() {
		super.bind();
		orderItemPresenter.bind();
		fulfillmentGroupPresenter.bind();
		selectionChangedHandlerRegistration.removeHandler();
		display.getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState()) {
					if (!selectedRecord.equals(lastSelectedRecord)) {
						lastSelectedRecord = selectedRecord;
						if (selectedRecord.getAttributeAsStringArray("type") == null){
							formPresenter.disable();
							display.getListDisplay().getRemoveButton().disable();
						} else {
							formPresenter.setStartState();
							((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).resetFieldVisibilityBasedOnType(selectedRecord.getAttributeAsStringArray("type"));
							display.getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), false, false);
							display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
							display.getListDisplay().getRemoveButton().enable();
						}
						changeSelection(selectedRecord);
					}
				}
			}
		});
	}
	
	@Override
	protected void addClicked() {
		//do nothing
	}

	@Override
	protected void removeClicked() {
		//do nothing
	}

	@Override
	public void go(final Canvas container) {
		Main.NON_MODAL_PROGRESS.startProgress();
		if (loaded) {
			OrderPresenter.super.go(container);
			return;
		}
		OrderListDataSourceFactory.createDataSource("orderDS", new AsyncCallbackAdapter() {
			public void onSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{"customer.firstName", "customer.lastName", "name", "orderNumber", "status"}, new Boolean[]{false, false, false, false, false});
				
				//disable the toolbar - this view is currently read-only
				((OrderDisplay) getDisplay()).getListDisplay().getToolBar().disable();
				((OrderDisplay) getDisplay()).getDynamicFormDisplay().getToolbar().disable();
				
				OrderItemListDataSourceFactory.createDataSource("orderItemDS", new AsyncCallbackAdapter() {
					public void onSuccess(final DataSource orderItemDS) {
						BundledOrderItemListDataSourceFactory.createDataSource("bundleOrderItemDS", new AsyncCallbackAdapter() {
							public void onSuccess(DataSource bundleOrderItemDS) {
								orderItemPresenter = new OrderItemPresenter(((OrderDisplay) getDisplay()).getOrderItemsDisplay());
								((OrderItemPresenter) orderItemPresenter).setDataSource((ListGridDataSource) orderItemDS, (ListGridDataSource) bundleOrderItemDS, new String[]{"name", "quantity", "price", "retailPrice", "salePrice"}, new Boolean[]{false, false, false, false, false});
						
								((OrderDisplay) getDisplay()).getOrderItemsDisplay().getToolBar().disable();
								
								CountryListDataSourceFactory.createDataSource("countryDS", new AsyncCallbackAdapter() {
									public void onSuccess(final DataSource countryDS) {
										((ListGridDataSource) countryDS).resetFieldVisibility(
											"name"
										);
										EntitySearchDialog countrySearchView = new EntitySearchDialog((ListGridDataSource) countryDS);
										((DynamicEntityDataSource) ((OneToOneProductSkuDisplay) getDisplay()).getListDisplay().getGrid().getDataSource()).
										getFormItemCallbackHandlerManager().addSearchFormItemCallback(
											"address.country", 
											countrySearchView, 
											"Search For A Country", 
											((OrderDisplay) getDisplay()).getFulfillmentGroupDisplay().getGrid(), 
											(DynamicFormDisplay) ((OrderDisplay) getDisplay()).getFulfillmentGroupDisplay()
										);
											
										FulfillmentGroupListDataSourceFactory.createDataSource("fulfillmentGroupDS", new AsyncCallbackAdapter() {
											public void onSuccess(DataSource fgDS) {
												fulfillmentGroupPresenter = new FulfillmentGroupPresenter(((OrderDisplay) getDisplay()).getFulfillmentGroupDisplay());
												((FulfillmentGroupPresenter) fulfillmentGroupPresenter).setDataSource((ListGridDataSource) fgDS, new String[]{"referenceNumber", "method", "service", "shippingPrice", "status", "address.postalCode"}, new Boolean[]{false, false, false, false, false, false});
										
												((DynamicFormDisplay) ((OrderDisplay) getDisplay()).getFulfillmentGroupDisplay()).getToolbar().disable();
		
												OrderPresenter.super.go(container);
												Main.NON_MODAL_PROGRESS.stopProgress();
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
}
