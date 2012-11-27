/**
 * Copyright 2012 the original author or authors.
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
package org.broadleafcommerce.admin.client.presenter.catalog.inventory;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.*;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormHiddenEnum;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

public class InventoryPresenter extends CreateBasedListStructurePresenter {

    public InventoryPresenter(GridStructureDisplay display, String editDialogTitle) {
        super("", display, editDialogTitle);
    }

    @Override
    public void bind() {
        dataArrivedHandlerRegistration = display.getGrid().addDataArrivedHandler(new DataArrivedHandler() {
            public void onDataArrived(DataArrivedEvent event) {
                display.getRemoveButton().disable();
            }
        });
        editCompletedHandlerRegistration = display.getGrid().addEditCompleteHandler(new EditCompleteHandler() {
            public void onEditComplete(EditCompleteEvent event) {
                display.getGrid().deselectAllRecords();
                setStartState();
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
        addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    DynamicEntityDataSource ds = (DynamicEntityDataSource) display.getGrid().getDataSource();
                    ForeignKey foreignKey = (ForeignKey) ds.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                    initialValues.put(foreignKey.getManyToField(), abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord));
                    String[] type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
                    initialValues.put("_type", type);

                    ds.getField("quantityAvailable").setCanEdit(true);
                    ds.getField("quantityOnHand").setCanEdit(true);
                    ds.getField("quantityAvailableChange").setAttribute("formHidden", FormHiddenEnum.HIDDEN);
                    ds.getField("quantityOnHandChange").setAttribute("formHidden", FormHiddenEnum.HIDDEN);

                    BLCMain.ENTITY_ADD.editNewRecord(editDialogTitle, ds, initialValues, null, null, null);
                }
            }
        });
        rowDoubleClickedHandlerRegistration = display.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellDoubleClick(CellDoubleClickEvent cellDoubleClickEvent) {

                if (cellDoubleClickEvent.isLeftButtonDown()) {

                    DynamicEntityDataSource ds = (DynamicEntityDataSource) display.getGrid().getDataSource();

                    ds.getField("quantityAvailable").setCanEdit(false);
                    ds.getField("quantityOnHand").setCanEdit(false);
                    ds.getField("quantityAvailableChange").setAttribute("formHidden", FormHiddenEnum.VISIBLE);
                    ds.getField("quantityOnHandChange").setAttribute("formHidden", FormHiddenEnum.VISIBLE);
                    //Add these new 'fake' attributes for the currently selected record so that they will be displayed
                    //on the form
                    display.getGrid().getSelectedRecord().setAttribute("quantityOnHandChange", "");
                    display.getGrid().getSelectedRecord().setAttribute("quantityAvailableChange", "");

                    BLCMain.ENTITY_ADD.editRecord(editDialogTitle, ds, display.getGrid().getSelectedRecord(), null, null, null, readOnly);

                }
            }
        });
    }
}
