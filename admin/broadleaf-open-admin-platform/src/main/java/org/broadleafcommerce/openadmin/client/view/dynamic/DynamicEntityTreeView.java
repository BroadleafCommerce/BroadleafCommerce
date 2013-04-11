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
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityTreeView extends VLayout implements DynamicEntityListDisplay {
    
    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ToolStripButton showArchivedButton;
    protected ComboBoxItem entityType = new ComboBoxItem();
    protected TreeGrid grid;
    protected ToolStrip toolBar;

    public DynamicEntityTreeView(String title, DataSource dataSource) {
        this(title, dataSource, false);
    }
    
    public DynamicEntityTreeView(String title, DataSource dataSource, boolean showRoot) {
        toolBar = new ToolStrip();
        toolBar.setHeight(30);
        toolBar.setWidth100();
        toolBar.addSpacer(6);

        addButton = new ToolStripButton();  
        addButton.setDisabled(true);
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/add.png");
        toolBar.addButton(addButton);

        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);

        toolBar.addSpacer(6);
        toolBar.addFill();

        showArchivedButton = new ToolStripButton();
        String archivedButtonTitle = ((AbstractDynamicDataSource) dataSource).isShowArchived()?BLCMain.getMessageManager().getString("hideArchivedRecords"):BLCMain.getMessageManager().getString("showArchivedRecords");
        showArchivedButton.setTitle(archivedButtonTitle);
        showArchivedButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/headerIcons/find.png");
        showArchivedButton.setVisibility(Visibility.HIDDEN);
      //  toolBar.addButton(showArchivedButton);

        toolBar.addSpacer(6);
        
        addMember(toolBar);
        grid = new TreeGrid();
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setCanEdit(false);
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(true);
        grid.setDrawAheadRatio(4);
        grid.setCanSort(false);
        grid.setCanResizeFields(true);
        grid.setShowRoot(showRoot);
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

    @Override
    public ToolStripButton getShowArchivedButton() {
        return showArchivedButton;
    }

}
