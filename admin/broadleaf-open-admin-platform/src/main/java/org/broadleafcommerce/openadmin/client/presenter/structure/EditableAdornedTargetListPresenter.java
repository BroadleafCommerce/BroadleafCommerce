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

package org.broadleafcommerce.openadmin.client.presenter.structure;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractSubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class EditableAdornedTargetListPresenter extends AbstractSubPresentable {

    protected EntitySearchDialog searchDialog;
    protected String searchDialogTitle;
    protected String adornedTargetEditTitle;
    protected String[] adornedTargetFields;
    protected HandlerRegistration addClickedHandlerRegistration;
    protected HandlerRegistration editCompletedHandlerRegistration;
    protected HandlerRegistration recordDropHandlerRegistration;
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removedClickedHandlerRegistration;
    protected HandlerRegistration rowDoubleClickedHandlerRegistration;
    
    public EditableAdornedTargetListPresenter(String prefix, GridStructureDisplay display, EntitySearchDialog searchDialog, String[] availableToTypes, String searchDialogTitle, String adornedTargetEditTitle, String... adornedTargetFields) {
        super(prefix, display, availableToTypes);
        this.searchDialog = searchDialog;
        this.searchDialogTitle = searchDialogTitle;
        this.adornedTargetEditTitle = adornedTargetEditTitle;
        this.adornedTargetFields = adornedTargetFields;
    }

    public EditableAdornedTargetListPresenter(String prefix, GridStructureDisplay display, EntitySearchDialog searchDialog, String searchDialogTitle, String adornedTargetEditTitle, String... adornedTargetFields) {
        this(prefix, display, searchDialog, null, searchDialogTitle, adornedTargetEditTitle, adornedTargetFields);
    }

    public EditableAdornedTargetListPresenter(EditableAdornedTargetListPresenter template) {
        this(template.prefix, template.display, template.searchDialog, template.availableToTypes, template.searchDialogTitle, template.adornedTargetEditTitle, template.adornedTargetFields);
        this.abstractDynamicDataSource = template.abstractDynamicDataSource;
        this.readOnly = template.readOnly;
    }
    
    public void bind() {
        addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    searchDialog.search(searchDialogTitle, new SearchItemSelectedHandler() {
                        @SuppressWarnings({ "rawtypes", "unchecked" })
                        public void onSearchItemSelected(SearchItemSelected event) {
                            Map initialValues = ((DynamicEntityDataSource) display.getGrid().getDataSource()).extractRecordValues((TreeNode) event.getRecord());
                            initialValues.put("backup_id", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getPrimaryKeyValue(event.getRecord()));
                            BLCMain.ENTITY_ADD.editNewRecord(adornedTargetEditTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, new ItemEditedHandler() {
                                public void onItemEdited(ItemEdited event) {
                                    ListGridRecord[] recordList = new ListGridRecord[]{(ListGridRecord) event.getRecord()};
                                    DSResponse updateResponse = new DSResponse();
                                    updateResponse.setData(recordList);
                                    DSRequest updateRequest = new DSRequest();
                                    updateRequest.setOperationType(DSOperationType.UPDATE);
                                    display.getGrid().getDataSource().updateCaches(updateResponse, updateRequest);
                                    display.getGrid().deselectAllRecords();
                                    display.getGrid().selectRecord(display.getGrid().getRecordIndex(event.getRecord()));
                                    String primaryKey = display.getGrid().getDataSource().getPrimaryKeyFieldName();
                                    ResultSet results = display.getGrid().getResultSet();
                                    boolean foundRecord = false;
                                    if (results != null) {
                                        foundRecord = display.getGrid().getResultSet().find(primaryKey, event.getRecord().getAttribute(primaryKey)) != null;
                                    }
                                    if (!foundRecord) {
                                        ((AbstractDynamicDataSource) display.getGrid().getDataSource()).setAddedRecord(event.getRecord());
                                        display.getGrid().getDataSource().
                                            fetchData(new Criteria("blc.fetch.from.cache", event.getRecord().getAttribute(primaryKey)), new DSCallback() {
                                                @Override
                                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                                    display.getGrid().setData(response.getData());
                                                    display.getGrid().selectRecord(0);
                                                }
                                            });
                                    }
                                }
                            }, adornedTargetFields, null);
                        }
                    });
                }
            }
        });
        editCompletedHandlerRegistration = display.getGrid().addEditCompleteHandler(new EditCompleteHandler() {
            public void onEditComplete(EditCompleteEvent event) {
                display.getGrid().deselectAllRecords();
                setStartState();
            }
        });
        /*
         * TODO add code to check if the AdornedTargetList has a sort field defined. If not,
         * then disable the re-order functionality
         */
        recordDropHandlerRegistration = display.getGrid().addRecordDropHandler(new RecordDropHandler() {
            public void onRecordDrop(RecordDropEvent event) {
                ListGridRecord record = event.getDropRecords()[0];
                int originalIndex = ((ListGrid) event.getSource()).getRecordIndex(record);
                int newIndex = event.getIndex();
                if (newIndex > originalIndex) {
                    newIndex--;
                }
                AdornedTargetList adornedTargetList = (AdornedTargetList) ((DynamicEntityDataSource) display.getGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
                record.setAttribute(adornedTargetList.getSortField(), newIndex);
                display.getGrid().updateData(record);
            }
        });
        selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            public void onSelectionChanged(SelectionEvent event) {
                if (event.getState()) {
                    display.getRemoveButton().enable();
                } else {
                    display.getRemoveButton().disable();
                }
            }
        });
        removedClickedHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            display.getRemoveButton().disable();
                        }
                    });
                }
            }
        });
        rowDoubleClickedHandlerRegistration = display.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellDoubleClick(CellDoubleClickEvent cellDoubleClickEvent) {
                AdornedTargetList adornedTargetList = (AdornedTargetList) ((DynamicEntityDataSource) display.getGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
                if (adornedTargetList.getSortField() != null) {
                    display.getGrid().getSelectedRecord().setAttribute(adornedTargetList.getSortField(), Integer.parseInt(display.getGrid().getSelectedRecord().getAttribute(adornedTargetList.getSortField()))-1);
                }
                BLCMain.ENTITY_ADD.editRecord(adornedTargetEditTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), display.getGrid().getSelectedRecord(), new ItemEditedHandler() {
                    @Override
                    public void onItemEdited(ItemEdited event) {
                        display.getRemoveButton().disable();
                    }
                }, adornedTargetFields, null, readOnly);
            }
        });
    }

    public HandlerRegistration getAddClickedHandlerRegistration() {
        return addClickedHandlerRegistration;
    }

    public HandlerRegistration getEditCompletedHandlerRegistration() {
        return editCompletedHandlerRegistration;
    }

    public HandlerRegistration getRecordDropHandlerRegistration() {
        return recordDropHandlerRegistration;
    }

    public HandlerRegistration getRemovedClickedHandlerRegistration() {
        return removedClickedHandlerRegistration;
    }

    public HandlerRegistration getSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }

    public String[] getAdornedTargetFields() {
        return adornedTargetFields;
    }

    public void setAdornedTargetFields(String[] adornedTargetFields) {
        this.adornedTargetFields = adornedTargetFields;
    }

    public String getAdornedTargetEditTitle() {
        return adornedTargetEditTitle;
    }

    public void setAdornedTargetEditTitle(String adornedTargetEditTitle) {
        this.adornedTargetEditTitle = adornedTargetEditTitle;
    }

    public EntitySearchDialog getSearchDialog() {
        return searchDialog;
    }

    public void setSearchDialog(EntitySearchDialog searchDialog) {
        this.searchDialog = searchDialog;
    }

    public String getSearchDialogTitle() {
        return searchDialogTitle;
    }

    public void setSearchDialogTitle(String searchDialogTitle) {
        this.searchDialogTitle = searchDialogTitle;
    }

    public HandlerRegistration getRowDoubleClickedHandlerRegistration() {
        return rowDoubleClickedHandlerRegistration;
    }

    public void setRowDoubleClickedHandlerRegistration(HandlerRegistration rowDoubleClickedHandlerRegistration) {
        this.rowDoubleClickedHandlerRegistration = rowDoubleClickedHandlerRegistration;
    }
}
