package org.broadleafcommerce.gwt.client.presenter;

import org.broadleafcommerce.gwt.client.datasource.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.view.DynamicListDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;

public abstract class DynamicListPresenter<GRID extends ListGrid> implements Presenter {

	protected HandlerManager eventBus;
	protected DynamicListDisplay<GRID> display;
	protected ListGridRecord lastSelectedRecord = null;

	public DynamicListPresenter(HandlerManager eventBus, DynamicListDisplay<GRID> view) {
		this.eventBus = eventBus;
		this.display = view;
	}
	
	public void bind() {
		ClickHandler addHandler = getAddHandler();
		display.getAddButton().addClickHandler(addHandler);
		display.getRemoveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					SC.confirm("Are your sure you want to delete this entity ("+((DynamicEntityDataSource) display.getGrid().getDataSource()).getPolymorphicEntities().get(((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname())+")?", new BooleanCallback() {
						public void execute(Boolean value) {
							if (value) {
								display.getGrid().removeSelectedData();
								display.getDynamicForm().disable();
								display.getFormToolBar().disable();
								display.getDynamicForm().reset();
								display.getRemoveButton().disable();
							}
						}
					});
				}
			}
		});
		display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState()) {
					if (!selectedRecord.equals(lastSelectedRecord)) {
						lastSelectedRecord = selectedRecord;
						display.getDynamicForm().enable();
						display.getFormToolBar().enable();
						((DynamicEntityDataSource) display.getGrid().getDataSource()).resetFieldVisibilityBasedOnType(selectedRecord.getAttribute("type"));
						display.getDynamicForm().setDataSource(display.getGrid().getDataSource());
						display.getDynamicForm().editRecord(selectedRecord);
						display.getRemoveButton().enable();
						changeSelection();
					}
				}
			}
		});
		display.getEntityType().addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				((DynamicEntityDataSource) display.getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname((String) event.getItem().getValue());
			}
        });
		display.getSaveFormButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					display.getDynamicForm().saveData();
					display.getSaveFormButton().disable();
				}
			}
        });
		display.getRefreshButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					display.getDynamicForm().reset();
					display.getSaveFormButton().disable();
				}
			}
        });
		display.getDynamicForm().addItemChangedHandler(new ItemChangedHandler() {
			public void onItemChanged(ItemChangedEvent event) {
				display.getSaveFormButton().enable();
			}
		});
		display.getGrid().addCellSavedHandler(new CellSavedHandler() {
			public void onCellSaved(CellSavedEvent event) {
				display.getSaveFormButton().disable();
				display.getDynamicForm().editRecord(event.getRecord());
			}
        });
	}
	
	public void go(HLayout container) {
		bind();
		if (container.contains(display.asCanvas())) {
			display.show();
		} else {
			container.addMember(display.asCanvas());
		}
	}
	
	protected void changeSelection() {
		//do nothing
	}
	
	protected abstract ClickHandler getAddHandler();
}
