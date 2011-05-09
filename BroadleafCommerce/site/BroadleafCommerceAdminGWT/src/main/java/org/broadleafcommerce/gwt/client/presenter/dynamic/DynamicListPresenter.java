package org.broadleafcommerce.gwt.client.presenter.dynamic;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.view.Display;
import org.broadleafcommerce.gwt.client.view.dynamic.AbstractView;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicListDisplay;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;

public abstract class DynamicListPresenter extends AbstractPresenter {

	protected DynamicListDisplay<TreeGrid> display;
	protected ListGridRecord lastSelectedRecord = null;
	
	public void bind() {
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					addClicked();
				}
			}
        });
		display.getRemoveButton().addClickHandler(new ClickHandler() {
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
		display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState()) {
					if (!selectedRecord.equals(lastSelectedRecord)) {
						lastSelectedRecord = selectedRecord;
						if (selectedRecord.getAttribute("name") == null || selectedRecord.getAttribute("type") == null){
							display.getDynamicForm().disable();
							display.getFormToolBar().disable();
							display.getRemoveButton().disable();
						} else {
							display.getDynamicForm().enable();
							display.getFormToolBar().enable();
							((DynamicEntityDataSource) display.getGrid().getDataSource()).resetFieldVisibilityBasedOnType(selectedRecord.getAttribute("type"));
							((AbstractView) display).buildFields(display.getGrid().getDataSource(), display.getDynamicForm());
							display.getDynamicForm().editRecord(selectedRecord);
							display.getRemoveButton().enable();
						}
						changeSelection(selectedRecord);
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
	
	public void go(Canvas container) {
		bind();
		if (containsDisplay(container)) {
			display.show();
		} else {
			container.addChild(display.asCanvas());
		}
	}
	
	protected Boolean containsDisplay(Canvas container) {
		return container.contains(display.asCanvas());
	}
	
	public Display getDisplay() {
		return display;
	}
	
	@SuppressWarnings("unchecked")
	public void setDisplay(Display display) {
		this.display = (DynamicListDisplay<TreeGrid>) display;
	}

	protected abstract void changeSelection(Record selectedRecord);
	
	protected abstract void addClicked();
	
	protected abstract void removeClicked();
	
}
