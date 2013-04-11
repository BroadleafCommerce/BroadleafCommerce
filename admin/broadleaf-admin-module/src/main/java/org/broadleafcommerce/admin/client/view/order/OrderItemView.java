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

package org.broadleafcommerce.admin.client.view.order;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public class OrderItemView extends VLayout implements OrderItemDisplay {
    
    protected ToolStrip toolBar;
    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ToolStripButton saveButton;
    protected ToolStripButton refreshButton;
    protected ListGrid grid;
    protected ListGrid expansionGrid;
    protected FormOnlyView orderItemFormDisplay;

    public OrderItemView(String title, Boolean canReorder, Boolean canEdit) {
        setHeight100();
        setWidth100();
        setStyleName("blcFormBg");
        setOverflow(Overflow.AUTO);
        
        HStack hStack = new HStack(10);
        
        hStack.setHeight("45%");
        hStack.setWidth100();
        hStack.setStyleName("blcFormBg");
        hStack.setAlign(Alignment.CENTER);
        
        VLayout stack = new VLayout();
        stack.setHeight100();
        stack.setWidth100();
        //stack.setLayoutMargin(12);
        
        toolBar = new ToolStrip();
        toolBar.setHeight(30);
        toolBar.setWidth100();
        toolBar.setMinWidth(300);
        toolBar.addSpacer(6);
        addButton = new ToolStripButton();
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/add.png");
        addButton.setDisabled(true);
        toolBar.addButton(addButton);
        toolBar.addSpacer(3);
        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.addSpacer(3);
        saveButton = new ToolStripButton();
        saveButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/save.png");
        saveButton.setTitle(BLCMain.getMessageManager().getString("saveTitle"));
        saveButton.setDisabled(true);
        toolBar.addButton(saveButton);
        toolBar.addSpacer(3);
        refreshButton = new ToolStripButton();
        refreshButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/refresh.png");
        refreshButton.setTitle(BLCMain.getMessageManager().getString("restoreTitle"));
        refreshButton.setTooltip(BLCMain.getMessageManager().getString("restoreTooltip"));
        refreshButton.setDisabled(true);
        toolBar.addButton(refreshButton);
        toolBar.setDisabled(false);
        Label crossSaleLabel = new Label();
        crossSaleLabel.setContents(title);
        crossSaleLabel.setWrap(false);
        toolBar.addSpacer(6);
        toolBar.addMember(crossSaleLabel);
        toolBar.addFill();
        stack.addMember(toolBar);
        
        expansionGrid = new ListGrid();
        expansionGrid.setShowHeader(true);
        expansionGrid.setShowHeaderContextMenu(false);
        expansionGrid.setCanReorderRecords(canReorder);
        expansionGrid.setCanEdit(canEdit);
        expansionGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        expansionGrid.setEditByCell(true);
        expansionGrid.setAutoSaveEdits(true);
        expansionGrid.setSaveByCell(true);
        expansionGrid.setAlternateRecordStyles(true);
        expansionGrid.setCanGroupBy(false);
        if (!canEdit) {
            expansionGrid.setAlternateBodyStyleName("editRowDisabled");
        }
        expansionGrid.setVisibility(Visibility.HIDDEN);
        expansionGrid.setHeight(100);
        expansionGrid.draw();
        
        grid = new ListGrid() {
            @Override  
            protected Canvas getExpansionComponent(final ListGridRecord record) {
                VLayout layout = new VLayout(5);
                layout.setPadding(5);
                layout.addMember(expansionGrid);
                expansionGrid.setVisibility(Visibility.INHERIT);
                String id = ((AbstractDynamicDataSource) grid.getDataSource()).getPrimaryKeyValue(record);
                ((PresentationLayerAssociatedDataSource) expansionGrid.getDataSource()).loadAssociatedGridBasedOnRelationship(id, null);
                return layout;
            }  
        };
        
        grid.setAutoFetchData(false);
        grid.setShowHeader(true);
        grid.setShowHeaderContextMenu(false); 
        grid.setPreventDuplicates(true);
        grid.setCanReorderRecords(canReorder);
        grid.setDisabled(true);
        grid.setCanSort(false);
        grid.setCellPadding(5);
        grid.setCanEdit(canEdit);
        grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        grid.setEditByCell(true);
        grid.setAutoSaveEdits(true);
        grid.setSaveByCell(true);
        grid.setAlternateRecordStyles(true);
        grid.setCanExpandMultipleRecords(false);
        grid.setCanExpandRecords(true);
        grid.setCanGroupBy(false);
        if (!canEdit) {
            grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        hStack.addMember(stack);
        hStack.setOverflow(Overflow.AUTO);
        hStack.setShowResizeBar(true);
        
        addMember(hStack);
        orderItemFormDisplay = new FormOnlyView();
        addMember(orderItemFormDisplay);
    }

    public ToolStrip getToolbar() {
        return toolBar;
    }

    public ToolStripButton getAddButton() {
        return addButton;
    }

    public ToolStripButton getRemoveButton() {
        return removeButton;
    }

    public ListGrid getGrid() {
        return grid;
    }

    public ListGrid getExpansionGrid() {
        return expansionGrid;
    }

    public FormOnlyDisplay getFormOnlyDisplay() {
        return orderItemFormDisplay;
    }

    public ToolStripButton getSaveButton() {
        return saveButton;
    }

    public ToolStripButton getRefreshButton() {
        return refreshButton;
    }
}
