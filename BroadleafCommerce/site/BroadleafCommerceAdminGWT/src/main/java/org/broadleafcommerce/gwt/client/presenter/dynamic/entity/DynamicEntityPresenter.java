package org.broadleafcommerce.gwt.client.presenter.dynamic.entity;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.view.Display;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public abstract class DynamicEntityPresenter extends AbstractEntityPresenter {

	protected DynamicEditDisplay display;
	protected ListGridRecord lastSelectedRecord = null;
	protected Boolean loaded = false;
	protected DynamicFormPresenter formPresenter;
	
	protected HandlerRegistration selectionChangedHandlerRegistration;
	protected HandlerRegistration removeClickHandlerRegistration;
	protected HandlerRegistration addClickHandlerRegistration;
	protected HandlerRegistration entityTypeChangedHandlerRegistration;
	protected HandlerRegistration cellSavedHandlerRegistration;
	
	public void bind() {
		formPresenter.bind();
		addClickHandlerRegistration = display.getListDisplay().getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					addClicked();
				}
			}
        });
		removeClickHandlerRegistration = display.getListDisplay().getRemoveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					//SC.confirm("Are your sure you want to delete this entity ("+display.getGrid().getSelectedRecord().getAttribute("name")+")?", new BooleanCallback() {
						//public void execute(Boolean value) {
							//if (value) {
								removeClicked();
							//}
						//}
					//});
				}
			}
		});
		selectionChangedHandlerRegistration = display.getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
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
							display.getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), true, true);
							display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
							display.getListDisplay().getRemoveButton().enable();
						}
						changeSelection(selectedRecord);
					}
				}
			}
		});
		entityTypeChangedHandlerRegistration = display.getListDisplay().getEntityType().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname((String) event.getItem().getValue());
			}
        });
		cellSavedHandlerRegistration = display.getListDisplay().getGrid().addCellSavedHandler(new CellSavedHandler() {
			public void onCellSaved(CellSavedEvent event) {
				formPresenter.setStartState();
				display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(event.getRecord());
			}
        });
	}
	
	public void go(Canvas container) {
		if (containsDisplay(container)) {
			display.show();
		} else {
			bind();
			container.addChild(display.asCanvas());
			loaded = true;
		}
	}
	
	protected Boolean containsDisplay(Canvas container) {
		return container.contains(display.asCanvas());
	}
	
	public DynamicEditDisplay getDisplay() {
		return display;
	}
	
	public void setDisplay(Display display) {
		this.display = (DynamicEditDisplay) display;
	}
	
	protected void setupDisplayItems(DataSource dataSource) {
		getDisplay().build(dataSource);
		formPresenter = new DynamicFormPresenter(display.getDynamicFormDisplay());
		((PresentationLayerAssociatedDataSource) dataSource).setAssociatedGrid(display.getListDisplay().getGrid());
	}

	protected abstract void changeSelection(Record selectedRecord);
	
	protected abstract void addClicked();
	
	protected abstract void removeClicked();

}
