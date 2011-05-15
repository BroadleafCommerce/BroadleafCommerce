/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.gwt.client.presenter.entity;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.gwt.client.view.Display;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
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

/**
 * 
 * @author jfischer
 *
 */
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
	protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);
	
	protected Boolean disabled = false;
	
	public void setStartState() {
		if (!disabled) {
			formPresenter.setStartState();
			display.getListDisplay().getAddButton().enable();
			display.getListDisplay().getGrid().enable();
			display.getListDisplay().getRemoveButton().disable();
		}
	}
	
	public void enable() {
		disabled = false;
		formPresenter.enable();
		display.getListDisplay().getAddButton().enable();
		display.getListDisplay().getGrid().enable();
		display.getListDisplay().getRemoveButton().enable();
		display.getListDisplay().getToolBar().enable();
	}
	
	public void disable() {
		disabled = true;
		formPresenter.disable();
		display.getListDisplay().getAddButton().disable();
		display.getListDisplay().getGrid().disable();
		display.getListDisplay().getRemoveButton().disable();
		display.getListDisplay().getToolBar().disable();
	}
	
	public void setReadOnly(Boolean readOnly) {
		if (readOnly) {
			disable();
			display.getListDisplay().getGrid().enable();
		} else {
			enable();
		}
	}
	
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
					removeClicked();
				}
			}
		});
		selectionChangedHandlerRegistration = display.getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
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
							((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(selectedRecord.getAttributeAsStringArray("_type"));
							display.getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), true, true, false);
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
				display.getListDisplay().getGrid().deselectAllRecords();
				display.getListDisplay().getGrid().selectRecord(event.getRecord());
			}
        });
	}
	
	public void postSetup(Canvas container) {
		if (containsDisplay(container)) {
			display.show();
		} else {
			bind();
			container.addChild(display.asCanvas());
			loaded = true;
		}
		BLCMain.MODAL_PROGRESS.stopProgress();
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
	
	protected void setupDisplayItems(DataSource entityDataSource, DataSource... additionalDataSources) {
		getDisplay().build(entityDataSource, additionalDataSources);
		formPresenter = new DynamicFormPresenter(display.getDynamicFormDisplay());
		((PresentationLayerAssociatedDataSource) entityDataSource).setAssociatedGrid(display.getListDisplay().getGrid());
	}

	protected abstract void changeSelection(Record selectedRecord);
	
	protected abstract void addClicked();
	
	protected void removeClicked() {
		SC.confirm("Are your sure you want to delete this entity?", new BooleanCallback() {
			public void execute(Boolean value) {
				if (value) {
					display.getListDisplay().getGrid().removeSelectedData();
					formPresenter.disable();
					display.getListDisplay().getRemoveButton().disable();
				}
			}
		});
	}

	public HandlerRegistration getSelectionChangedHandlerRegistration() {
		return selectionChangedHandlerRegistration;
	}

	public HandlerRegistration getRemoveClickHandlerRegistration() {
		return removeClickHandlerRegistration;
	}

	public HandlerRegistration getAddClickHandlerRegistration() {
		return addClickHandlerRegistration;
	}

	public HandlerRegistration getEntityTypeChangedHandlerRegistration() {
		return entityTypeChangedHandlerRegistration;
	}

	public HandlerRegistration getCellSavedHandlerRegistration() {
		return cellSavedHandlerRegistration;
	}

	public PresenterSequenceSetupManager getPresenterSequenceSetupManager() {
		return presenterSequenceSetupManager;
	}

	public Boolean getLoaded() {
		return loaded;
	}

}
