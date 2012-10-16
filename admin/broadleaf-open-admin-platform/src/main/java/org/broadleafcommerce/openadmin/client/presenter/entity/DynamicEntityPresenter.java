/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.presenter.entity;

import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.AdvancedCollectionDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.AdvancedCollectionLookupDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.ForeignKeyLookupDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.LookupMetadata;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitorAdapter;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.Display;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormBuilder;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridHelper;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author jfischer
 */
public abstract class DynamicEntityPresenter extends AbstractEntityPresenter {

    public static Map<String, CollectionMetadata> collectionMetadatas = new LinkedHashMap<String, CollectionMetadata>();

    protected DynamicEditDisplay display;
    protected ListGridRecord lastSelectedRecord;
    protected Boolean loaded = false;
    protected DynamicFormPresenter formPresenter;
    protected GridHelper gridHelper=new GridHelper();
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removeClickHandlerRegistration;
    protected HandlerRegistration addClickHandlerRegistration;
    protected HandlerRegistration entityTypeChangedHandlerRegistration;
    protected HandlerRegistration cellSavedHandlerRegistration;
    protected HandlerRegistration fetchDataHandlerRegistration;
    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration showArchivedButtonHandlerRegistration;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);
    protected Map<String, SubPresentable> subPresentables = new HashMap<String, SubPresentable>();

    protected Boolean disabled = false;

    protected String[] gridFields;
    protected Map<String, Object> initialValues = new HashMap<String, Object>();

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

    protected void compileDefaultValuesFromCurrentFilter(Map<String, Object> initialValues) {
        Criteria currentCriteria = display.getListDisplay().getGrid().getFilterEditorCriteria();
        if (currentCriteria != null) {
            Map<String, Object> valueMap = currentCriteria.getValues();
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                String fieldName = entry.getKey();
                if (fieldName.endsWith("_Grid")) {
                    fieldName = fieldName.substring(0, fieldName.lastIndexOf("_Grid"));
                }
                FormItem displayField = display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("__display_" + fieldName);
                if (displayField != null) {
                    DataSourceField field = display.getListDisplay().getGrid().getDataSource().getField(entry.getKey());
                    Map valueMap2 = field.getValueMap();
                    if (valueMap2 != null) {
                        initialValues.put(displayField.getName(), valueMap2.get(entry.getValue()));
                    }
                }
                initialValues.put(fieldName, entry.getValue());
            }
        }
    }

    public void setSubPresentable(String dataSourceName, SubPresentable subPresentable) {
        subPresentables.put(dataSourceName, subPresentable);
    }

    public void bind() {
        formPresenter.bind();
        formPresenter.getSaveButtonHandlerRegistration().removeHandler();
        saveButtonHandlerRegistration = display.getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    saveClicked();
                }
            }
        });
        addClickHandlerRegistration = display.getListDisplay().getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    addClicked();
                }
            }
        });
        removeClickHandlerRegistration = display.getListDisplay().getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    SC.confirm("Are your sure you want to delete this entity?", new BooleanCallback() {
                        @Override
                        public void execute(Boolean value) {
                            if (value) {
                                removeClicked();
                            }
                        }
                    });
                }
            }
        });
        fetchDataHandlerRegistration = display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                setStartState();
                formPresenter.disable();
                display.getListDisplay().getGrid().deselectAllRecords();
                for (Map.Entry<String, SubPresentable> subPresentable : subPresentables.entrySet()) {
                    subPresentable.getValue().disable();
                }
                lastSelectedRecord = null;
            }
        });
        selectionChangedHandlerRegistration = display.getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState() && selectedRecord != null) {
                    if (!selectedRecord.equals(lastSelectedRecord)) {
                        lastSelectedRecord = selectedRecord;
                        if (selectedRecord.getAttributeAsStringArray("_type") == null) {
                            formPresenter.disable();
                            display.getListDisplay().getRemoveButton().disable();
                        } else {
                            formPresenter.setStartState();
                            ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(selectedRecord.getAttributeAsStringArray("_type"));
                            String locked = selectedRecord.getAttribute("__locked");
                            display.getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), true, !(locked != null && locked.equals("true")), false, selectedRecord);
                            display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
                            display.getListDisplay().getRemoveButton().enable();
                        }
                        changeSelection(selectedRecord);
                        for (Map.Entry<String, SubPresentable> subPresentable : subPresentables.entrySet()) {
                            //this is only suitable when no callback is required for the load - which is most cases
                            subPresentable.getValue().setStartState();
                            subPresentable.getValue().load(selectedRecord, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource());
                        }
                        display.getDynamicFormDisplay().getSaveButton().disable();
                        display.getDynamicFormDisplay().getRefreshButton().disable();
                    }
                }
            }
        });
        entityTypeChangedHandlerRegistration = display.getListDisplay().getEntityType().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname((String) event.getItem().getValue());
            }
        });
        cellSavedHandlerRegistration = display.getListDisplay().getGrid().addCellSavedHandler(new CellSavedHandler() {
            @Override
            public void onCellSaved(CellSavedEvent event) {
                display.getListDisplay().getGrid().deselectAllRecords();
                display.getListDisplay().getGrid().selectRecord(event.getRecord());
            }
        });
        showArchivedButtonHandlerRegistration = display.getListDisplay().getShowArchivedButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).setShowArchived(!((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).isShowArchived());
                    String title = ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).isShowArchived() ? BLCMain.getMessageManager().getString("hideArchivedRecords") : BLCMain.getMessageManager().getString("showArchivedRecords");
                    display.getListDisplay().getShowArchivedButton().setTitle(title);
                    ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPersistencePerspective().setShowArchivedFields(((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).isShowArchived());
                    display.getListDisplay().getGrid().invalidateCache();
                }
            }
        });
    }

    protected void saveClicked() {
        DSRequest requestProperties = new DSRequest();

        //try {
            //requestProperties.setAttribute("dirtyValues", display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().getChangedValues());
        //} catch (Exception e) {
            //Logger.getLogger(this.getClass().toString()).log(Level.WARNING, "ignore, usually thown in gwt-run mode", e);
        //}
        display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().saveData(new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                if (response.getStatus() != RPCResponse.STATUS_VALIDATION_ERROR) {
                    itemSaved(response, rawData, request);
                    display.getDynamicFormDisplay().getSaveButton().disable();
                    display.getDynamicFormDisplay().getRefreshButton().disable();
                }

            }
        }, requestProperties);
    }

    protected void itemSaved(DSResponse response, Object rawData, DSRequest request) {
        getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(lastSelectedRecord));
    }

    @Override
    public void postSetup(Canvas container) {
        BLCMain.ISNEW = false;
        if (containsDisplay(container)) {
            display.show();
        } else {
            bind();
            for (Map.Entry<String, SubPresentable> subPresentable : subPresentables.entrySet()) {
                subPresentable.getValue().bind();
            }
            container.addChild(display.asCanvas());
            loaded = true;
        }

        if (getDefaultItemId() != null) {
            loadInitialItem();
        }

        if (display.getListDisplay().getGrid().getDataSource().getField("archiveStatus.archived") != null) {
            //this must be an archived enabled entity
            display.getListDisplay().getShowArchivedButton().setVisibility(Visibility.VISIBLE);
        } else {
            display.getListDisplay().getShowArchivedButton().setVisibility(Visibility.HIDDEN);
        }

        if (BLCMain.MODAL_PROGRESS.isActive()) {
            BLCMain.MODAL_PROGRESS.stopProgress();
        }
        if (BLCMain.SPLASH_PROGRESS.isActive()) {
            BLCMain.SPLASH_PROGRESS.stopProgress();
        }
    }

    protected void loadInitialItem() {
        DataSource ds = getDisplay().getListDisplay().getGrid().getDataSource();
        if (ds instanceof DynamicEntityDataSource) {
            Criteria criteria = new Criteria();
            criteria.addCriteria(ds.getPrimaryKeyFieldName(), getDefaultItemId());
            ds.fetchData(criteria, new DSCallback() {
                @Override
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    if (response != null && response.getData() != null && response.getData().length > 0) {
                        getDisplay().getListDisplay().getGrid().setData(response.getData());
                        getDisplay().getListDisplay().getGrid().selectRecord(0);
                    }
                }
            });
        }


    }

    protected Boolean containsDisplay(Canvas container) {
        return container.contains(display.asCanvas());
    }

    @Override
    public DynamicEditDisplay getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(Display display) {
        this.display = (DynamicEditDisplay) display;
    }

    protected void setupDisplayItems(final DataSource entityDataSource, DataSource... additionalDataSources) {
        getDisplay().build(entityDataSource, additionalDataSources);
        gridHelper.traverseTreeAndAddHandlers(getDisplay().getListDisplay().getGrid());
        formPresenter = new DynamicFormPresenter(display.getDynamicFormDisplay());
        ((PresentationLayerAssociatedDataSource) entityDataSource).setAssociatedGrid(display.getListDisplay().getGrid());
        initializeAdvancedCollections();
    }

    public void initializeLookup(final String propertyName, final LookupMetadata metadata) {
        final String dataSourceName = propertyName + "Lookup";
        if (presenterSequenceSetupManager.getDataSource(dataSourceName) != null) {
            java.util.logging.Logger.getLogger(getClass().toString()).log(Level.FINE, "Detected collection metadata for a datasource that is already registered (" + dataSourceName + "). Ignoring this repeated definition.");
            return;
        }
        presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(dataSourceName, new ForeignKeyLookupDataSourceFactory(metadata.getLookupForeignKey()), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource lookupDS) {
                EntitySearchDialog searchView = new EntitySearchDialog((ListGridDataSource) lookupDS, true);
                String viewTitle = BLCMain.getMessageManager().getString(metadata.getFriendlyName());
                if (viewTitle == null) {
                    viewTitle = metadata.getFriendlyName();
                }
                DynamicEntityDataSource parentDataSource;
                if (metadata.getParentDataSourceName() == null || metadata.getParentDataSourceName().length() == 0) {
                    parentDataSource = (DynamicEntityDataSource) metadata.getDefaultDataSource();
                } else {
                    parentDataSource = presenterSequenceSetupManager.getDataSource(metadata.getParentDataSourceName());
                }
                DynamicFormDisplay target;
                if (metadata.getTargetDynamicFormDisplayId() == null || metadata.getTargetDynamicFormDisplayId().length() == 0) {
                    target = getDisplay().getDynamicFormDisplay();
                } else {
                    Layout temp = FormBuilder.findMemberById((Layout) getDisplay(), metadata.getTargetDynamicFormDisplayId());
                    if (!(temp instanceof DynamicFormDisplay)) {
                        throw new RuntimeException("The target destination for a foreign key lookup must be an instance of DynamicFormDisplay. The requested destination (" + metadata.getTargetDynamicFormDisplayId() + ") is an instance of " + temp.getClass().getName());
                    }
                    target = (DynamicFormDisplay) temp;
                }
                parentDataSource.
                        getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        propertyName,
                        searchView,
                        viewTitle,
                        target,
                        metadata.getLookupForeignKey(),
                        null
                );
            }
        }));
    }

    protected void initializeAdvancedCollections() {
        //sort the collection metadatas based on their order attribute
        List<Map.Entry<String, CollectionMetadata>> list = new LinkedList<Map.Entry<String, CollectionMetadata>>(collectionMetadatas.entrySet());
        Iterator<Map.Entry<String, CollectionMetadata>> itr = list.iterator();
        while (itr.hasNext()) {
            if (!itr.next().getKey().startsWith(getClass().getName())) {
                itr.remove();
            }
        }
        Collections.sort(list, new Comparator<Map.Entry<String, CollectionMetadata>>() {
            @Override
            public int compare(Map.Entry<String, CollectionMetadata> o1, Map.Entry<String, CollectionMetadata> o2) {
                return o1.getValue().getOrder().compareTo(o2.getValue().getOrder());
            }
        });

        Map<String, CollectionMetadata> sortedMetadatas = new LinkedHashMap<String, CollectionMetadata>();
        for (Map.Entry<String, CollectionMetadata> entry : list) {
            String key = entry.getKey();
            key = key.substring(key.indexOf("_") + 1, key.length());
            sortedMetadatas.put(key, entry.getValue());
        }

        for (final Map.Entry<String, CollectionMetadata> entry : sortedMetadatas.entrySet()) {
            //only show this edit grid if the collection type inherits from the dynamic presenter's root managed entity
            boolean shouldLoad = false;
            ClassTree classTree = ((DynamicEntityDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).getPolymorphicEntityTree();
            for (String availableType : entry.getValue().getAvailableToTypes()) {
                ClassTree availableTypeResult = classTree.find(availableType);
                if (availableTypeResult != null) {
                    shouldLoad = true;
                    break;
                }
            }
            if (shouldLoad) {
                final String dataSourceName;
                if (entry.getValue().getDataSourceName() != null && entry.getValue().getDataSourceName().length() > 0) {
                    dataSourceName = entry.getValue().getDataSourceName();
                } else {
                    dataSourceName = entry.getKey() + "AdvancedCollectionDS";
                }
                if (presenterSequenceSetupManager.getDataSource(dataSourceName) != null) {
                    java.util.logging.Logger.getLogger(getClass().toString()).log(Level.FINE, "Detected collection metadata for a datasource that is already registered (" + dataSourceName + "). Ignoring this repeated definition.");
                    continue;
                }
                entry.getValue().accept(new MetadataVisitorAdapter() {
                    @Override
                    public void visit(final BasicCollectionMetadata metadata) {
                        //These next two presenter setup item decalarations are tricky from a timing perspective.
                        presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(dataSourceName, new AdvancedCollectionDataSourceFactory(metadata, DynamicEntityPresenter.this), new AsyncCallbackAdapter() {
                            @Override
                            public void onSetupSuccess(final DataSource baseDS) {
                                //only build the form if the add type for this item is persist - otherwise wait for the lookup datasource to be constructed
                                if (metadata.getAddMethodType() == AddMethodType.PERSIST) {
                                    FormBuilder.buildAdvancedCollectionForm(baseDS, metadata, entry.getKey(), DynamicEntityPresenter.this);
                                }
                            }
                        }));
                        //check if the interaction requires a lookup datasource
                        if (metadata.getAddMethodType() == AddMethodType.LOOKUP) {
                            String lookupDSName = dataSourceName + "Lookup";
                            presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(lookupDSName, new AdvancedCollectionLookupDataSourceFactory(metadata), new AsyncCallbackAdapter() {
                                @Override
                                public void onSetupSuccess(DataSource lookupDS) {
                                    FormBuilder.buildAdvancedCollectionForm(presenterSequenceSetupManager.getDataSource(dataSourceName), lookupDS, metadata, entry.getKey(), DynamicEntityPresenter.this);
                                }
                            }));
                        }
                    }

                    @Override
                    public void visit(final AdornedTargetCollectionMetadata metadata) {
                        //These next two presenter setup item decalarations are tricky from a timing perspective.
                        presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(dataSourceName, new AdvancedCollectionDataSourceFactory(metadata, DynamicEntityPresenter.this), new NullAsyncCallbackAdapter()));
                        String lookupDSName = dataSourceName + "Lookup";
                        presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(lookupDSName, new AdvancedCollectionLookupDataSourceFactory(metadata), new AsyncCallbackAdapter() {
                            @Override
                            public void onSetupSuccess(DataSource lookupDS) {
                                FormBuilder.buildAdvancedCollectionForm(presenterSequenceSetupManager.getDataSource(dataSourceName), lookupDS, metadata, entry.getKey(), DynamicEntityPresenter.this);
                            }
                        }));
                    }

                    @Override
                    public void visit(final MapMetadata metadata) {
                        final String lookupDSName = dataSourceName + "Lookup";
                        //These next two presenter setup item decalarations are tricky from a timing perspective.
                        if (metadata.getMapKeyOptionEntityClass() != null && metadata.getMapKeyOptionEntityClass().length() > 0) {
                            presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(lookupDSName, new AdvancedCollectionLookupDataSourceFactory(metadata), new NullAsyncCallbackAdapter()));
                        }
                        presenterSequenceSetupManager.addOrReplaceItem(new PresenterSetupItem(dataSourceName, new AdvancedCollectionDataSourceFactory(metadata, DynamicEntityPresenter.this), new AsyncCallbackAdapter() {
                            @Override
                            public void onSetupSuccess(DataSource baseDS) {
                                if (metadata.getMapKeyOptionEntityClass() == null || metadata.getMapKeyOptionEntityClass().length() == 0) {
                                    FormBuilder.buildAdvancedCollectionForm(baseDS, metadata, entry.getKey(), DynamicEntityPresenter.this);
                                } else {
                                    FormBuilder.buildAdvancedCollectionForm(baseDS, presenterSequenceSetupManager.getDataSource(lookupDSName), metadata, entry.getKey(), DynamicEntityPresenter.this);
                                }
                            }
                        }));
                    }
                });
            }
        }
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
        compileDefaultValuesFromCurrentFilter(initialValues);
        BLCMain.ENTITY_ADD.editNewRecord(newItemTitle, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new ItemEditedHandler() {
            @Override
            public void onItemEdited(ItemEdited event) {
                ListGridRecord[] recordList = new ListGridRecord[]{(ListGridRecord) event.getRecord()};
                DSResponse updateResponse = new DSResponse();
                updateResponse.setData(recordList);
                DSRequest updateRequest = new DSRequest();
                updateRequest.setOperationType(DSOperationType.UPDATE);
                getDisplay().getListDisplay().getGrid().getDataSource().updateCaches(updateResponse, updateRequest);
                getDisplay().getListDisplay().getGrid().deselectAllRecords();
                getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(event.getRecord()));
                String primaryKey = display.getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
                ResultSet results = display.getListDisplay().getGrid().getResultSet();
                boolean foundRecord = false;
                if (results != null) {
                    foundRecord = getDisplay().getListDisplay().getGrid().getResultSet().find(primaryKey, event.getRecord().getAttribute(primaryKey)) != null;
                }
                if (!foundRecord) {
                    ((AbstractDynamicDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).setAddedRecord(event.getRecord());
                    getDisplay().getListDisplay().getGrid().getDataSource().
                            fetchData(new Criteria("blc.fetch.from.cache", event.getRecord().getAttribute(primaryKey)), new DSCallback() {
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
        Record selectedRecord = display.getListDisplay().getGrid().getSelectedRecord();
        final String primaryKey = display.getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
        final String id = selectedRecord.getAttribute(primaryKey);
        display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                if (!(getDisplay().getListDisplay().getGrid() instanceof TreeGrid) && getDisplay().getListDisplay().getGrid().getResultSet() == null) {
                    getDisplay().getListDisplay().getGrid().setData(new Record[]{});
                }
            }
        }, null);
        formPresenter.disable();
        display.getListDisplay().getRemoveButton().disable();
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

    public HandlerRegistration getSaveButtonHandlerRegistration() {
        return saveButtonHandlerRegistration;
    }

    public HandlerRegistration getEntityTypeChangedHandlerRegistration() {
        return entityTypeChangedHandlerRegistration;
    }

    public HandlerRegistration getCellSavedHandlerRegistration() {
        return cellSavedHandlerRegistration;
    }

    @Override
    public PresenterSequenceSetupManager getPresenterSequenceSetupManager() {
        return presenterSequenceSetupManager;
    }

    @Override
    public Boolean getLoaded() {
        return loaded;
    }

    public Map<String, Object> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, Object> initialValues) {
        this.initialValues = initialValues;
    }

    public void setGridFields(String[] gridFields) {
        this.gridFields = gridFields;
    }
}
