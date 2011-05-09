package org.broadleafcommerce.gwt.client.presenter.promotion.offercode;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.OfferListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.promotion.offercode.OfferCodeListDataSourceFactory;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.promotion.offercode.OfferCodeDisplay;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

public class OfferCodePresenter extends DynamicEntityPresenter implements Instantiable {
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		//do nothing
	}
	
	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("offerCode", "Untitled");
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord("Create New Offer Code", (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("offerCode", event.getRecord().getAttribute("offerCode"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}

	@Override
	public void go(final Canvas container) {
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (loaded) {
					OfferCodePresenter.super.go(container);
					return;
				}
				OfferCodeListDataSourceFactory.createDataSource("offerCodeDS", new AsyncCallbackAdapter() {
					public void onSuccess(final DataSource top) {
						setupDisplayItems(top);
						
						OfferListDataSourceFactory.createDataSource("offerCodeOfferDS", new AsyncCallbackAdapter() {
							public void onSuccess(final DataSource offerCodeOfferDS) {
								final EntitySearchDialog offerSearchView = new EntitySearchDialog((ListGridDataSource) offerCodeOfferDS);
								
								((DynamicEntityDataSource) top).
								getFormItemCallbackHandlerManager().addSearchFormItemCallback(
									"offer", 
									offerSearchView, 
									"Search For A Offer", 
									display.getDynamicFormDisplay()
								);
								
								((ListGridDataSource) top).setupGridFields(new String[]{"offerCode", "startDate", "endDate", "maxUses", "uses"}, new Boolean[]{true, true, true, true, false});
								OfferCodePresenter.super.go(container);
							}
						});
					}
				});
			}
		});
	}

	@Override
	public OfferCodeDisplay getDisplay() {
		return (OfferCodeDisplay) display;
	}
	
}
