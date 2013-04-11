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

package org.broadleafcommerce.openadmin.client.view.user;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;

/**
 * 
 * @author jfischer
 *
 */
public class UserPermissionView extends VLayout implements UserPermissionDisplay {

    protected ToolStrip toolBar;
    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ListGrid grid;

    public UserPermissionView(Boolean canReorder, Boolean canEdit) {
        setHeight100();
        setWidth100();
        setStyleName("blcFormBg");
        setOverflow(Overflow.AUTO);
        
        HStack hStack = new HStack(10);
        hStack.setID("userPermissionHStack");
        
        hStack.setHeight("45%");
        hStack.setWidth100();
        hStack.setStyleName("blcFormBg");
        hStack.setAlign(Alignment.CENTER);
        
        VLayout stack = new VLayout();
        stack.setID("userPermissionVerticalLayout");
        stack.setHeight100();
        stack.setWidth100();
        
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
        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.addSpacer(6);
        toolBar.setDisabled(false);
        toolBar.addFill();
        stack.addMember(toolBar);

        grid = new ListGrid();
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
        grid.setCanGroupBy(false);
        if (!canEdit) {
            grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        hStack.addMember(stack);
        hStack.setOverflow(Overflow.AUTO);
        
        addMember(hStack);
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

}
