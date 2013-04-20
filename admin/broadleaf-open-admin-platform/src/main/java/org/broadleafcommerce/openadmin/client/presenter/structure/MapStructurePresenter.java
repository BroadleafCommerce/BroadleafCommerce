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

package org.broadleafcommerce.openadmin.client.presenter.structure;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractSubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class MapStructurePresenter extends AbstractSubPresentable {

    protected MapStructureEntityEditDialog entityEditDialog;
    protected String entityEditDialogTitle;
    protected Map<String, Object> initialValues = new HashMap<String, Object>(10);
    protected String[] gridFields;
    protected HandlerRegistration dataArrivedHandlerRegistration;
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removeClickedHandlerRegistration;
    protected HandlerRegistration addClickedHandlerRegistration;
    protected HandlerRegistration rowDoubleClickedHandlerRegistration;

    public MapStructurePresenter(String prefix, GridStructureDisplay display, MapStructureEntityEditDialog entityEditDialog, String[] availableToTypes, String entityEditDialogTitle, Map<String, Object> initialValues) {
        super(prefix, display, availableToTypes);
        this.entityEditDialog = entityEditDialog;
        this.entityEditDialogTitle = entityEditDialogTitle;
        if (initialValues != null) {
            this.initialValues.putAll(initialValues);
        }
    }

    public MapStructurePresenter(String prefix, GridStructureDisplay display, MapStructureEntityEditDialog entityEditDialog, String entityEditDialogTitle, Map<String, Object> initialValues) {
        this(prefix, display, entityEditDialog, null, entityEditDialogTitle, initialValues);
    }

    public MapStructurePresenter(String prefix, GridStructureDisplay display, MapStructureEntityEditDialog entityEditDialog, String[] availableToTypes, String entityEditDialogTitle) {
        this(prefix, display, entityEditDialog, availableToTypes, entityEditDialogTitle, null);
    }

    public MapStructurePresenter(String prefix, GridStructureDisplay display, MapStructureEntityEditDialog entityEditDialog, String entityEditDialogTitle) {
        this(prefix, display, entityEditDialog, null, entityEditDialogTitle, null);
    }

    public MapStructurePresenter(MapStructurePresenter template) {
        this(template.prefix, template.display, template.entityEditDialog, template.availableToTypes, template.entityEditDialogTitle, template.initialValues);
        this.abstractDynamicDataSource = template.abstractDynamicDataSource;
        this.readOnly = template.readOnly;
        this.gridFields = template.gridFields;
    }

    @Override
    public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
        display.getGrid().setDataSource(dataSource);
        dataSource.setAssociatedGrid(display.getGrid());
        String[] finalGridFields = dataSource.setupGridFields(gridFields, editable);
        this.gridFields = finalGridFields;
    }
    
    @Override
    public void bind() {
        if (display.getCanEdit()) {
            dataArrivedHandlerRegistration = display.getGrid().addDataArrivedHandler(new DataArrivedHandler() {
                @Override
                public void onDataArrived(DataArrivedEvent event) {
                    display.getRemoveButton().disable();
                }
            });
            selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
                @Override
                public void onSelectionChanged(SelectionEvent event) {
                    if (event.getState()) {
                        display.getRemoveButton().enable();
                    } else {
                        display.getRemoveButton().disable();
                    }
                }
            });
            removeClickedHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (event.isLeftButtonDown()) {
                        display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
                            @Override
                            public void execute(DSResponse response, Object rawData, DSRequest request) {
                                display.getRemoveButton().disable();
                            }
                        });
                    }
                }
            });
            addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (event.isLeftButtonDown()) {
                        DynamicEntityDataSource dataSource = (DynamicEntityDataSource) display.getGrid().getDataSource();
                        initialValues.put("symbolicId", dataSource.getCompatibleModule(dataSource.getPersistencePerspective().getOperationTypes().getAddType()).getLinkedValue());
                        String[] type = associatedRecord.getAttributeAsStringArray("_type");
                        if (type == null) {
                            type = new String[] { ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname() };
                        }
                        initialValues.put("_type", type);
                        entityEditDialog.editNewRecord(entityEditDialogTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, null, gridFields, null);
                    }
                }
            });
            rowDoubleClickedHandlerRegistration = display.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler() {
                @Override
                public void onCellDoubleClick(CellDoubleClickEvent cellDoubleClickEvent) {
                    entityEditDialog.editRecord(entityEditDialogTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), display.getGrid().getSelectedRecord(), null, gridFields, null, readOnly);
                }
            });
        }
    }

    public HandlerRegistration getAddClickedHandlerRegistration() {
        return addClickedHandlerRegistration;
    }

    public HandlerRegistration getDataArrivedHandlerRegistration() {
        return dataArrivedHandlerRegistration;
    }

    public HandlerRegistration getRemoveClickedHandlerRegistration() {
        return removeClickedHandlerRegistration;
    }

    public HandlerRegistration getRowDoubleClickedHandlerRegistration() {
        return rowDoubleClickedHandlerRegistration;
    }

    public HandlerRegistration getSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }

    public String[] getGridFields() {
        return gridFields;
    }

    public void setGridFields(String[] gridFields) {
        this.gridFields = gridFields;
    }

    public MapStructureEntityEditDialog getEntityEditDialog() {
        return entityEditDialog;
    }

    public void setEntityEditDialog(MapStructureEntityEditDialog entityEditDialog) {
        this.entityEditDialog = entityEditDialog;
    }

    public String getEntityEditDialogTitle() {
        return entityEditDialogTitle;
    }

    public void setEntityEditDialogTitle(String entityEditDialogTitle) {
        this.entityEditDialogTitle = entityEditDialogTitle;
    }

    public Map<String, Object> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, Object> initialValues) {
        this.initialValues = initialValues;
    }
}
