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

package org.broadleafcommerce.cms.admin.client.view.sandbox;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SandBoxView extends VLayout implements Instantiable, SandBoxDisplay {

    protected ToolStripButton promoteAllButton;
    protected ToolStripButton promoteSelectionButton;
	protected ToolStripButton revertRejectSelectionButton;
    protected ToolStripButton revertRejectAllButton;
    protected ToolStripButton refreshButton;
    protected ToolStripButton previewButton;
	protected ListGrid grid;
	protected ToolStrip toolBar;

    public SandBoxView() {
        setWidth100();
        setHeight100();
        setLayoutMargin(20);
    }

	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        setHeight100();
		setWidth100();

        VLayout insideLayout = new VLayout();

        Label header = new Label(BLCMain.getMessageManager().getString("approverSandBoxTitle"));
        header.setBaseStyle("blcHeader");
        header.setHeight(15);

        insideLayout.addMember(header);

		toolBar = new ToolStrip();
		toolBar.setHeight(30);
		toolBar.setWidth100();
		toolBar.addSpacer(6);

        promoteAllButton = new ToolStripButton();
        promoteAllButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/promote.png");
        promoteAllButton.setTitle(BLCMain.getMessageManager().getString("promoteAllTitle"));
        promoteAllButton.setTooltip(BLCMain.getMessageManager().getString("promoteAllTooltip"));
        toolBar.addButton(promoteAllButton);

        toolBar.addSpacer(3);

        revertRejectAllButton = new ToolStripButton();
        revertRejectAllButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/revert.png");
        revertRejectAllButton.setTitle(BLCMain.getMessageManager().getString("rejectAllTitle"));
        revertRejectAllButton.setTooltip(BLCMain.getMessageManager().getString("rejectAllTooltip"));
        toolBar.addButton(revertRejectAllButton);

        toolBar.addSpacer(3);
        toolBar.addSeparator();
        toolBar.addSpacer(3);

        promoteSelectionButton = new ToolStripButton();
        promoteSelectionButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/promote.png");
        promoteSelectionButton.setTitle(BLCMain.getMessageManager().getString("promoteTitle"));
        promoteSelectionButton.setTooltip(BLCMain.getMessageManager().getString("promoteTooltip"));
        toolBar.addButton(promoteSelectionButton);

        toolBar.addSpacer(3);

        revertRejectSelectionButton = new ToolStripButton();
        revertRejectSelectionButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/revert.png");
        revertRejectSelectionButton.setTitle(BLCMain.getMessageManager().getString("rejectTitle"));
        revertRejectSelectionButton.setTooltip(BLCMain.getMessageManager().getString("rejectTooltip"));
        toolBar.addButton(revertRejectSelectionButton);


        toolBar.addFill();
        refreshButton = new ToolStripButton();
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");
        refreshButton.setTitle(BLCMain.getMessageManager().getString("refreshTitle"));
        toolBar.addButton(refreshButton);
        previewButton = new ToolStripButton();
        previewButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/zoom.png");
        previewButton.setTitle(BLCMain.getMessageManager().getString("previewTitle"));
        toolBar.addButton(previewButton);
        toolBar.addSpacer(6);

        insideLayout.addMember(toolBar);

        grid = new ListGrid();
        grid.setCanReorderRecords(true);
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.MULTIPLE);
        grid.setCanEdit(false);
        grid.setDataSource(entityDataSource);
        grid.setAutoFetchData(true);
        grid.setDrawAllMaxCells(10);
        grid.setCanSort(true);
        grid.setCanResizeFields(true);
        grid.setShowFilterEditor(false);
        grid.setCanGroupBy(true);
        grid.setGroupByField("groupDescription");
        grid.setDataPageSize(10);
        grid.setAlternateBodyStyleName("editRowDisabled");
        insideLayout.addMember(grid);

        addMember(insideLayout);
	}

    public Canvas asCanvas() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getAddButton()
	 */
	public ToolStripButton getPromoteAllButton() {
		return promoteAllButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay#getRemoveButton()
	 */
	public ToolStripButton getRevertRejectSelectionButton() {
		return revertRejectSelectionButton;
	}

    public ToolStripButton getPromoteSelectionButton() {
        return promoteSelectionButton;
    }

    public ToolStripButton getRevertRejectAllButton() {
        return revertRejectAllButton;
    }

    public ToolStripButton getRefreshButton() {
        return refreshButton;
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

    public ToolStripButton getPreviewButton() {
        return previewButton;
    }
}
