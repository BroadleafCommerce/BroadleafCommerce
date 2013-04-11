/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.presenter.entity;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
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
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.view.Display;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplayWithoutForm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public abstract class DynamicEntityPresenterWithoutForm extends AbstractEntityPresenter {

    protected DynamicEditDisplayWithoutForm display;
    protected ListGridRecord lastSelectedRecord = null;
    protected Boolean loaded = false;
    
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removeClickHandlerRegistration;
    protected HandlerRegistration addClickHandlerRegistration;
    protected HandlerRegistration entityTypeChangedHandlerRegistration;
    protected HandlerRegistration cellSavedHandlerRegistration;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);
    
    protected Boolean disabled = false;

    protected String newItemTitle = "Create new item";
    protected Criteria fetchAfterAddCriteria = new Criteria();
    protected String[] gridFields;
    protected Map<String, Object> initialValues = new HashMap<String, Object>();
    
    public void setStartState() {
        if (!disabled) {
            display.getListDisplay().getAddButton().enable();
            display.getListDisplay().getGrid().enable();
            display.getListDisplay().getRemoveButton().disable();
        }
    }
    
    public void enable() {
        disabled = false;
        display.getListDisplay().getAddButton().enable();
        display.getListDisplay().getGrid().enable();
        display.getListDisplay().getRemoveButton().enable();
        display.getListDisplay().getToolBar().enable();
    }
    
    public void disable() {
        disabled = true;
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
                if (event.getState() && selectedRecord != null) {
                    if (!selectedRecord.equals(lastSelectedRecord)) {
                        lastSelectedRecord = selectedRecord;
                        if (selectedRecord.getAttributeAsStringArray("_type") == null){
                            display.getListDisplay().getRemoveButton().disable();
                        } else {
                            ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(selectedRecord.getAttributeAsStringArray("_type"));
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
        BLCMain.ISNEW = false;
        if (containsDisplay(container)) {
            display.show();
        } else {
            bind();
            container.addChild(display.asCanvas());
            loaded = true;
        }
        if (BLCMain.MODAL_PROGRESS.isActive()) {
            BLCMain.MODAL_PROGRESS.stopProgress();
        }
        if (BLCMain.SPLASH_PROGRESS.isActive()) {
            BLCMain.SPLASH_PROGRESS.stopProgress();
        }
    }
    
    protected Boolean containsDisplay(Canvas container) {
        return container.contains(display.asCanvas());
    }
    
    public DynamicEditDisplayWithoutForm getDisplay() {
        return display;
    }
    
    public void setDisplay(Display display) {
        this.display = (DynamicEditDisplayWithoutForm) display;
    }
    
    protected void setupDisplayItems(DataSource entityDataSource, DataSource... additionalDataSources) {
        getDisplay().build(entityDataSource, additionalDataSources);
        ((PresentationLayerAssociatedDataSource) entityDataSource).setAssociatedGrid(display.getListDisplay().getGrid());
    }

    protected void changeSelection(Record selectedRecord) {
        // place holder
    }

    protected void addClicked() {
        addClicked(BLCMain.getMessageManager().getString("newItemTitle"));
    }

    protected void addClicked(final String newItemTitle) {
        initialValues.remove("_type");
        LinkedHashMap<String, String> polymorphicEntities = ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) {
            BLCMain.POLYMORPHIC_ADD.search(BLCMain.getMessageManager().getString("selectPolymorphicType"), polymorphicEntities, new SearchItemSelectedHandler() {
                @Override
                public void onSearchItemSelected(SearchItemSelected event) {
                    ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname(event.getRecord().getAttribute("fullyQualifiedType"));
                    addNewItem(newItemTitle);
                }
            });
        } else {
            addNewItem(newItemTitle);
        }
    }

    protected void addNewItem(String newItemTitle) {
        initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
        BLCMain.ENTITY_ADD.editNewRecord(newItemTitle, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new ItemEditedHandler() {
            public void onItemEdited(ItemEdited event) {
                ListGridRecord[] recordList = new ListGridRecord[]{(ListGridRecord) event.getRecord()};
                DSResponse updateResponse = new DSResponse();
                updateResponse.setData(recordList);
                getDisplay().getListDisplay().getGrid().getDataSource().updateCaches(updateResponse);
                getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(event.getRecord()));
                String primaryKey = display.getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
                boolean foundRecord = getDisplay().getListDisplay().getGrid().getResultSet().find(primaryKey, event.getRecord().getAttribute(primaryKey)) != null;
                if (!foundRecord) {
                    ((AbstractDynamicDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).setAddedRecord(event.getRecord());
                    getDisplay().getListDisplay().getGrid().getDataSource().fetchData(new Criteria("blc.fetch.from.cache", event.getRecord().getAttribute(primaryKey)), new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            getDisplay().getListDisplay().getGrid().setData(response.getData());
                            getDisplay().getListDisplay().getGrid().selectRecord(0);
                        }
                    });
                }
            }
        }, null, null);
    }
    
    protected void removeClicked() {
        SC.confirm("Are your sure you want to delete this entity?", new BooleanCallback() {
            public void execute(Boolean value) {
                if (value) {
                    display.getListDisplay().getGrid().removeSelectedData();
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

    public void setAddNewItemTitle(String newItemTitle) {
        this.newItemTitle = newItemTitle;
    }

    public void setFetchAfterAddCriteria(Criteria criteria) {
        this.fetchAfterAddCriteria = criteria;
    }

    public void setGridFields(String[] gridFields) {
        this.gridFields = gridFields;
    }

}
