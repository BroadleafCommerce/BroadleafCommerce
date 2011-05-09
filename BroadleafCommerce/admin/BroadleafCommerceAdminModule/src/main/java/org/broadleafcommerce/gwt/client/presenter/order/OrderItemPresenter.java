package org.broadleafcommerce.gwt.client.presenter.order;

import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicFormPresenter;
import org.broadleafcommerce.gwt.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.order.OrderItemDisplay;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class OrderItemPresenter extends DynamicFormPresenter implements SubPresentable {

	protected OrderItemDisplay display;
	
	protected Record associatedRecord;
	protected AbstractDynamicDataSource abstractDynamicDataSource;
	protected Boolean disabled = false;
	
	public OrderItemPresenter(OrderItemDisplay display) {
		super((DynamicFormDisplay) display);
		this.display = display;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
		display.getFormOnlyDisplay().buildFields(dataSource, true, false, true);
	}
	
	public void setExpansionDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getExpansionGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getExpansionGrid());
		dataSource.setupGridFields(gridFields, editable);
	}
	
	public void setStartState() {
		if (!disabled) {
			super.setStartState();
			display.getAddButton().enable();
			display.getGrid().enable();
			display.getRemoveButton().disable();
		}
	}
	
	public void enable() {
		disabled = false;
		super.enable();
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().enable();
		display.getToolbar().enable();
	}
	
	public void disable() {
		disabled = true;
		super.disable();
		display.getAddButton().disable();
		display.getGrid().disable();
		display.getRemoveButton().disable();
		display.getToolbar().disable();
	}
	
	public void setReadOnly(Boolean readOnly) {
		if (readOnly) {
			disable();
			display.getGrid().enable();
		} else {
			enable();
		}
	}
	
	public void load(Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource, final DSCallback cb) {
		this.associatedRecord = associatedRecord;
		this.abstractDynamicDataSource = abstractDynamicDataSource;
		String id = abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord);
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
		super.bind();
		display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					display.getRemoveButton().enable();
					((DynamicEntityDataSource) display.getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
					display.getFormOnlyDisplay().buildFields(display.getGrid().getDataSource(),false, false, true);
					display.getFormOnlyDisplay().getForm().editRecord(event.getRecord());
					display.getFormOnlyDisplay().getForm().enable();
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		display.getExpansionGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					//display.getRemoveButton().enable();
					((DynamicEntityDataSource) display.getExpansionGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
					display.getFormOnlyDisplay().buildFields(display.getExpansionGrid().getDataSource(),false, false, true);
					display.getFormOnlyDisplay().getForm().editRecord(event.getRecord());
					display.getFormOnlyDisplay().getForm().enable();
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
