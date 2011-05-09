package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.CustomerListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.OfferListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.OrderItemListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.OrderListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.ItemBuilderDisplay;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.OfferDisplay;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OfferPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected ListGridDataSource entityDataSource;
	protected Window currentHelp = null;
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		getDisplay().getAdvancedButton().setSelected(false);
		getDisplay().getAdvancedButton().enable();
		//Since the form is built dynamically each time the grid selection changes, we have to re-bind the event
		getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				if (event.getValue().toString().equals("FULFILLMENT_GROUP")) {
					getDisplay().getStepFGLabel().setVisible(true);
					getDisplay().getFulfillmentGroupFilterBuilder().setVisible(true);
					getDisplay().getStepFGForm().setVisible(true);
				} else {
					getDisplay().getStepFGLabel().setVisible(false);
					getDisplay().getFulfillmentGroupFilterBuilder().setVisible(false);
					getDisplay().getStepFGForm().setVisible(true);
				}
			}
		});
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
		getDisplay().getAdvancedButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (((ToolStripButton) event.getSource()).getSelected()) {
					entityDataSource.resetPermanentFieldVisibilityBasedOnType(display.getListDisplay().getGrid().getSelectedRecord().getAttributeAsStringArray("_type"));
				} else {
					entityDataSource.resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority");
				}
				getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().buildFields(entityDataSource, true, true, true);
				getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(display.getListDisplay().getGrid().getSelectedRecord());
			}
		});
		selectionChangedHandlerRegistration.removeHandler();
		getDisplay().getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState()) {
					if (!selectedRecord.equals(lastSelectedRecord)) {
						lastSelectedRecord = selectedRecord;
						if (selectedRecord.getAttributeAsStringArray("_type") == null){
							formPresenter.disable();
							display.getListDisplay().getRemoveButton().disable();
						} else {
							formPresenter.setStartState();
							entityDataSource.resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority");
							getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), true, true, true);
							getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
							getDisplay().getListDisplay().getRemoveButton().enable();
						}
						changeSelection(selectedRecord);
					}
				}
			}
		});
		getDisplay().getHelpButtonType().addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (currentHelp == null) {
					currentHelp = createWin(
							"Offer Obtain Settings Help", 
							"<B>Automatic</B> Offer is always made available to eligible customers and carts.<br>" +
							"<B>Shared Code</B> Offer is made available to all customers who enter the correct code.<br>" +
							"<B>Limited-Use Code</B> Offer is made available to a single customer who enters the correct code. This type of code may be used only up to a specified number of times.<br>" +
							"<B>System</B> Offer is made available to one or more customers via a separate process that the Broadleaf admin is unaware of. Some customers may wish to create a custom feature outside of the Broadleaf Commerce admin to associate offers with customers.",
							true, 300, 200, getDisplay().getHelpButtonType().getAbsoluteLeft() + 26, getDisplay().getHelpButtonType().getAbsoluteTop()
					);
					currentHelp.show();
				}
			}
		});
		getDisplay().getHelpButtonType().addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				if (currentHelp != null) {
					currentHelp.destroy();
					currentHelp = null;
				}
			}
		});
		getDisplay().getDeliveryTypeRadio().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				if (event.getValue().toString().equals("CODE") || event.getValue().toString().equals("LIMITED-USE")) {
					getDisplay().getCodeField().enable();
				} else {
					getDisplay().getCodeField().disable();
				}
				if (event.getValue().toString().equals("LIMITED-USE")) {
					getDisplay().getMaxUseField().enable();
				} else {
					getDisplay().getMaxUseField().disable();
				}
			}
		});
		getDisplay().getCustomerRuleRadio().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				if (event.getValue().toString().equals("CUSTOMER_RULE")) {
					getDisplay().getCustomerFilterBuilder().enable();
				} else {
					getDisplay().getCustomerFilterBuilder().disable();
				}
			}
		});
		getDisplay().getFgRuleRadio().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				if (event.getValue().toString().equals("FG_RULE")) {
					getDisplay().getFulfillmentGroupFilterBuilder().enable();
				} else {
					getDisplay().getFulfillmentGroupFilterBuilder().disable();
				}
			}
		});
		getDisplay().getItemRuleRadio().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				if (event.getValue().toString().equals("ITEM_RULE")) {
					getDisplay().getAddItemButton().enable();
					for (ItemBuilderDisplay display : getDisplay().getItemBuilderViews()) {
						display.enable();
					}
				} else {
					getDisplay().getAddItemButton().disable();
					for (ItemBuilderDisplay display : getDisplay().getItemBuilderViews()) {
						display.disable();
					}
				}
			}
		});
		getDisplay().getHelpButtonBogo().addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (currentHelp == null) {
					currentHelp = createWin(
							"Buy One/Get One Help", 
							"Buy One/Get One style offers allows promotion administrators to specify offers that are triggered by detection of a certain " +
							"quantity of one or more items in the cart. Rules are entered that match any combination of items and a quantity is entered for " +
							"how many times the rule must match. Also, a subsequent target rule is entered that matches any combination of discount target " +
							"items in the cart and a quantity. As a result, the system will search for any qualifying items and matching quantities in the cart, " + 
							"and if found, will apply the defined discount to the matched target items and quantities.",
							true, 300, 200, getDisplay().getHelpButtonBogo().getAbsoluteLeft() + 26, getDisplay().getHelpButtonBogo().getAbsoluteTop()
					);
					currentHelp.show();
				}
			}
		});
		getDisplay().getHelpButtonBogo().addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				if (currentHelp != null) {
					currentHelp.destroy();
					currentHelp = null;
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
						entityDataSource = (ListGridDataSource) top;
						OrderListDataSourceFactory.createDataSource("offerOrderDS", new AsyncCallbackAdapter() {
							public void onSuccess(final DataSource offerOrderDS) {
								OrderItemListDataSourceFactory.createDataSource("offerOrderItemDS", new AsyncCallbackAdapter() {
									public void onSuccess(final DataSource offerOrderItemDS) {
										((DynamicEntityDataSource) offerOrderItemDS).permanentlyShowFields("product.id", "category.id", "sku.id");
										FulfillmentGroupListDataSourceFactory.createDataSource("offerFGDS", new AsyncCallbackAdapter() {
											public void onSuccess(final DataSource offerFGDS) {
												CustomerListDataSourceFactory.createDataSource("offerCustomerDS", new AsyncCallbackAdapter() {
													public void onSuccess(final DataSource offerCustomerDS) {
														((DynamicEntityDataSource) offerCustomerDS).permanentlyShowFields("id");
														((ListGridDataSource) top).permanentlyHideFields("appliesToOrderRules", "appliesToCustomerRules");
														((ListGridDataSource) top).resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority");
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

	@Override
	public OfferDisplay getDisplay() {
		return (OfferDisplay) display;
	}
	
	public Window createWin(String title, String content, boolean autoSizing, int width, int height, int left, int top) {  
        Label label = new Label(content);  
        label.setWidth100();  
        label.setHeight100();  
        label.setPadding(5);  
        label.setValign(VerticalAlignment.TOP);  
  
        Window window = new Window();  
        window.setAutoSize(autoSizing);  
        window.setTitle(title);  
        window.setWidth(width);  
        window.setHeight(height);
        window.setLeft(left);
        window.setTop(top);  
        window.setCanDragReposition(true);  
        window.setCanDragResize(true);  
        window.addItem(label); 
        window.setShowCloseButton(false);
        window.setShowMinimizeButton(false);
  
        return window;  
    } 
}
