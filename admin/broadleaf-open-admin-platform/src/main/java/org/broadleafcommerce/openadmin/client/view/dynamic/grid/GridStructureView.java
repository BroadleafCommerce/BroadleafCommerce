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

package org.broadleafcommerce.openadmin.client.view.dynamic.grid;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;

/**
 * 
 * @author jfischer
 *
 */
public class GridStructureView extends HStack implements GridStructureDisplay {
    
    protected ToolStrip toolBar;
    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ListGrid grid;
    protected Boolean canEdit;

    public GridStructureView(String title, Boolean canReorder, Boolean canEdit) {
        this(title, canReorder, canEdit, false);
    }

    public GridStructureView(String title, Boolean canReorder, Boolean canEdit, Boolean autoFetchData) {
        super(10);

        this.canEdit = canEdit;
        setHeight(300);
        setWidth100();
        setBackgroundColor("#eaeaea");
        setAlign(Alignment.CENTER);
        
        VStack stack = new VStack();
        stack.setHeight(250);
        stack.setWidth100();
        stack.setLayoutMargin(12);

        Label header = new Label(title);
        header.setBaseStyle("blcHeader");
        header.setHeight(15);

        stack.addMember(header);
        
        toolBar = new ToolStrip();
        toolBar.setHeight(30);
        toolBar.setWidth100();
        toolBar.setMinWidth(300);
        toolBar.addSpacer(6);
        addButton = new ToolStripButton();
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        addButton.setDisabled(true);
        toolBar.addButton(addButton);
        toolBar.addSpacer(6);
        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.setDisabled(false);
        toolBar.addFill();
        stack.addMember(toolBar);
        grid = new ListGrid();
        grid.setAutoFetchData(autoFetchData);
        grid.setShowHeader(true);
        grid.setShowHeaderContextMenu(false); 
        grid.setPreventDuplicates(true);
        grid.setCanReorderRecords(canReorder);
        grid.setHeight100();
        grid.setDisabled(true);
        grid.setCanSort(false);
        grid.setCellPadding(5);
        grid.setCanEdit(false);
        //grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        //grid.setEditByCell(true);
        //grid.setAutoSaveEdits(true);
        //grid.setSaveByCell(true);
        grid.setAlternateRecordStyles(true);
        grid.setCanGroupBy(false);
        if (!canEdit) {
            grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        addMember(stack);
        setOverflow(Overflow.VISIBLE);
    }

    public ToolStrip getToolBar() {
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

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }
}
