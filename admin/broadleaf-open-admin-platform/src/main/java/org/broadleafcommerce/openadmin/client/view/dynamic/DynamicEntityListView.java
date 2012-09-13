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

package org.broadleafcommerce.openadmin.client.view.dynamic;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityListView extends VLayout implements DynamicEntityListDisplay {

	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
    protected ToolStripButton showArchivedButton;
	protected ComboBoxItem entityType = new ComboBoxItem();
	protected ListGrid grid;
	protected ToolStrip toolBar;

    public DynamicEntityListView(DataSource dataSource) {
		this("", dataSource, true);
	}
	
	public DynamicEntityListView(String title, DataSource dataSource) {
		this(title, dataSource, true);
	}
	
	public DynamicEntityListView(String title, DataSource dataSource, Boolean canReorder) {
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

        showArchivedButton = new ToolStripButton();
        String archivedButtonTitle = ((AbstractDynamicDataSource) dataSource).isShowArchived()?BLCMain.getMessageManager().getString("hideArchivedRecords"):BLCMain.getMessageManager().getString("showArchivedRecords");
        showArchivedButton.setTitle(archivedButtonTitle);
        showArchivedButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/find.png");
        showArchivedButton.setVisibility(Visibility.HIDDEN);
        toolBar.addButton(showArchivedButton);

        toolBar.addSpacer(6);

        addMember(toolBar);

        grid = new ListGrid();
        grid.setCanReorderRecords(canReorder);
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setCanEdit(false);
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(false);
        //grid.setDrawAllMaxCells(10);
        grid.setCanSort(true);
        grid.setCanResizeFields(true);
        grid.setShowFilterEditor(true);
        grid.setCanGroupBy(false);
        //grid.setDataPageSize(10);
        grid.setEmptyMessage(BLCMain.getMessageManager().getString("emptyMessage"));
        grid.setAlternateBodyStyleName("editRowDisabled");
        grid.setHoverMoveWithMouse(true);
        grid.setCanHover(true);
        grid.setShowHover(true);
        grid.setHoverOpacity(75);
        grid.setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record != null && record.getAttribute("__locked") != null && record.getAttributeAsBoolean("__locked")) {
                    return BLCMain.getMessageManager().replaceKeys(BLCMain.getMessageManager().getString("lockedMessage"), new String[]{"userName", "date"}, new String[]{record.getAttribute("__lockedUserName"), record.getAttribute("__lockedDate")});
                } else if (record != null && record.getAttribute("_hilite") != null && record.getAttribute("_hilite").equals("listGridDirtyPropertyHilite")) {
                    return BLCMain.getMessageManager().getString("dirtyMessage");
                } else if (record != null && record.getAttribute("_hilite") != null && record.getAttribute("_hilite").equals("listGridInActivePropertyHilite")) {
                    return BLCMain.getMessageManager().getString("inActiveMessage");
                } else if (record != null && record.getAttribute("_hilite") != null && record.getAttribute("_hilite").equals("listGridDeletedPropertyHilite")) {
                    return BLCMain.getMessageManager().getString("deletedMessage");
                }
                return null;
            }
        });
        addMember(grid);
	}

	@Override
    public ToolStripButton getAddButton() {
		return addButton;
	}

	@Override
    public ToolStripButton getRemoveButton() {
		return removeButton;
	}

    @Override
    public ToolStripButton getShowArchivedButton() {
        return showArchivedButton;
    }

	@Override
    public ComboBoxItem getEntityType() {
		return entityType;
	}

	@Override
    public ListGrid getGrid() {
		return grid;
	}

	@Override
    public ToolStrip getToolBar() {
		return toolBar;
	}

}
