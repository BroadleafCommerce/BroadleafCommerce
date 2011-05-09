package org.broadleafcommerce.gwt.client.presenter.promotion.offer;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.CustomerListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.OfferListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.OrderItemListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.promotion.offer.OrderListDataSourceFactory;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.promotion.offer.OfferDisplay;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OfferPresenter extends DynamicEntityPresenter implements Instantiable {
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		((OfferDisplay) display).getOrderButton().enable();
		((OfferDisplay) display).getOrderItemButton().enable();
		((OfferDisplay) display).getFulfillmentGroupButton().enable();
		((OfferDisplay) display).getCustomerButton().enable();
		((OfferDisplay) display).getRulesBuilderButton().enable();
	}
	
	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("name", "Untitled");
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord("Create New Offer", (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("name", event.getRecord().getAttribute("name"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}

	@Override
	public void bind() {
		super.bind();
		((OfferDisplay) display).getOrderButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ImgButton) event.getSource()).getSelected()) {
					((OfferDisplay) display).getOrderFilterBuilder().enable();
				} else {
					((OfferDisplay) display).getOrderFilterBuilder().disable();
				}
			}
		});
		((OfferDisplay) display).getOrderItemButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ImgButton) event.getSource()).getSelected()) {
					((OfferDisplay) display).getOrderItemFilterBuilder().enable();
				} else {
					((OfferDisplay) display).getOrderItemFilterBuilder().disable();
				}
			}
		});
		((OfferDisplay) display).getFulfillmentGroupButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ImgButton) event.getSource()).getSelected()) {
					((OfferDisplay) display).getFulfillmentGroupFilterBuilder().enable();
				} else {
					((OfferDisplay) display).getFulfillmentGroupFilterBuilder().disable();
				}
			}
		});
		((OfferDisplay) display).getCustomerButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ImgButton) event.getSource()).getSelected()) {
					((OfferDisplay) display).getCustomerFilterBuilder().enable();
				} else {
					((OfferDisplay) display).getCustomerFilterBuilder().disable();
				}
			}
		});
		((OfferDisplay) display).getRulesBuilderButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ToolStripButton) event.getSource()).getSelected()) {
					((OfferDisplay) display).showFilterBuilder();
				} else {
					((OfferDisplay) display).showRawFields();
				}
			}
		});
	}

	@Override
	public void go(final Canvas container) {
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (loaded) {
					OfferPresenter.super.go(container);
					return;
				}
				OfferListDataSourceFactory.createDataSource("offerDS", new AsyncCallbackAdapter() {
					public void onSuccess(final DataSource top) {
						OrderListDataSourceFactory.createDataSource("offerOrderDS", new AsyncCallbackAdapter() {
							public void onSuccess(final DataSource offerOrderDS) {
								OrderItemListDataSourceFactory.createDataSource("offerOrderItemDS", new AsyncCallbackAdapter() {
									public void onSuccess(final DataSource offerOrderItemDS) {
										((DynamicEntityDataSource) offerOrderItemDS).showAdditionalFields("product.id", "category.id", "sku.id");
										FulfillmentGroupListDataSourceFactory.createDataSource("offerFGDS", new AsyncCallbackAdapter() {
											public void onSuccess(final DataSource offerFGDS) {
												CustomerListDataSourceFactory.createDataSource("offerCustomerDS", new AsyncCallbackAdapter() {
													public void onSuccess(final DataSource offerCustomerDS) {
														((ListGridDataSource) top).hideFields("appliesToOrderRules", "appliesToCustomerRules");	
														setupDisplayItems(top, offerOrderDS, offerOrderItemDS, offerFGDS, offerCustomerDS);
														((ListGridDataSource) top).setupGridFields(new String[]{"name", "description", "type", "value", "startDate", "endDate"}, new Boolean[]{true, true, true, true, true, true});
														OfferPresenter.super.go(container);
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
		});
	}
	
}
