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

package org.broadleafcommerce.openadmin.client.view.dynamic;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityListView extends VLayout implements DynamicEntityListDisplay {

	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected ComboBoxItem entityType = new ComboBoxItem();
	protected ListGrid grid;
	protected ToolStrip toolBar;

    public DynamicEntityListView(DataSource dataSource) {
		this("", dataSource, true, true);
	}
	
	public DynamicEntityListView(String title, DataSource dataSource) {
		this(title, dataSource, true, true);
	}
	
	public DynamicEntityListView(String title, DataSource dataSource, Boolean canReorder, Boolean canEdit) {
		super();
		toolBar = new ToolStrip();
		toolBar.setHeight(30);
		toolBar.setWidth100();
		toolBar.addSpacer(6);
        addButton = new ToolStripButton();
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/actions/add.png");
        toolBar.addButton(addButton);
        toolBar.addSpacer(6);
        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);

        toolBar.addFill();

        addMember(toolBar);

        grid = new ListGrid();
        grid.setCanReorderRecords(canReorder);
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setCanEdit(true);
        grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        grid.setEditByCell(true);
        grid.setAutoSaveEdits(true);
        grid.setSaveByCell(true);
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(false);
        //grid.setDrawAllMaxCells(10);
        grid.setCanSort(true);
        grid.setCanResizeFields(true);
        grid.setShowFilterEditor(true);
        grid.setCanGroupBy(false);
        //grid.setDataPageSize(10);
        grid.setEmptyMessage(BLCMain.getMessageManager().getString("emptyMessage"));
        if (!canEdit) {
        	grid.setAlternateBodyStyleName("editRowDisabled");
        }
        grid.setHoverMoveWithMouse(true);
        grid.setCanHover(true);
        grid.setShowHover(true);
        grid.setHoverOpacity(75);
        grid.setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record != null && record.getAttribute("__locked") != null && record.getAttributeAsBoolean("__locked")) {
                    return BLCMain.getMessageManager().replaceKeys(BLCMain.getMessageManager().getString("lockedMessage"), new String[]{"userName", "date"}, new String[]{record.getAttribute("__lockedUserName"), record.getAttribute("__lockedDate")});
                }
                return null;
            }
        });
        addMember(grid);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getAddButton()
	 */
	public ToolStripButton getAddButton() {
		return addButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getRemoveButton()
	 */
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getEntityType()
	 */
	public ComboBoxItem getEntityType() {
		return entityType;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getGrid()
	 */
	public ListGrid getGrid() {
		return grid;
	}

	public ToolStrip getToolBar() {
		return toolBar;
	}

}
