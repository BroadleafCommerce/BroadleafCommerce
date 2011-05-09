package org.broadleafcommerce.gwt.client.presenter.order;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.SubPresenter;
import org.broadleafcommerce.gwt.client.view.order.OrderItemGridStructureDisplay;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class OrderItemPresenter implements SubPresenter {

	protected OrderItemGridStructureDisplay display;
	
	protected Record associatedRecord;
	
	public OrderItemPresenter(OrderItemGridStructureDisplay display) {
		this.display = display;
	}
	
	public void setDataSource(ListGridDataSource dataSource, ListGridDataSource expansionDataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
		display.getOrderItemFormDisplay().buildFields(dataSource, true, true);
		
		display.getExpansionGrid().setDataSource(expansionDataSource);
		expansionDataSource.setAssociatedGrid(display.getExpansionGrid());
		expansionDataSource.setupGridFields(gridFields, editable);
	}
	
	public void setStartState() {
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().disable();
	}
	
	public void enable() {
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().enable();
	}
	
	public void disable() {
		display.getAddButton().disable();
		display.getGrid().disable();
		display.getRemoveButton().disable();
	}
	
	public void load(Record associatedRecord, final DSCallback cb) {
		this.associatedRecord = associatedRecord;
		String id = associatedRecord.getAttribute("id");
		((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					setStartState();
					if (cb != null) {
						cb.execute(response, rawData, request);
					}
				}
			}
		});
	}
	
	public void bind() {
		display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					display.getRemoveButton().enable();
					((DynamicEntityDataSource) display.getGrid().getDataSource()).resetFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("type"));
					display.getOrderItemFormDisplay().buildFields(display.getGrid().getDataSource(),false, false);
					display.getOrderItemFormDisplay().getForm().editRecord(event.getRecord());
					display.getOrderItemFormDisplay().getForm().enable();
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		display.getExpansionGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					//display.getRemoveButton().enable();
					((DynamicEntityDataSource) display.getExpansionGrid().getDataSource()).resetFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("type"));
					display.getOrderItemFormDisplay().buildFields(display.getExpansionGrid().getDataSource(),false, false);
					display.getOrderItemFormDisplay().getForm().editRecord(event.getRecord());
					display.getOrderItemFormDisplay().getForm().enable();
				} else {
					//display.getRemoveButton().disable();
				}
			}
		});
		display.getRemoveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								display.getRemoveButton().disable();
							}
						}
					});
				}
			}
		});
	}
}
