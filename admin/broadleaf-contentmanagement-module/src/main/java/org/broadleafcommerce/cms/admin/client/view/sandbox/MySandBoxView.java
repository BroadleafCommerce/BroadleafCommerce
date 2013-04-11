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
public class MySandBoxView extends VLayout implements Instantiable, MySandBoxDisplay {

    protected ToolStripButton promoteAllButton;
    protected ToolStripButton promoteSelectionButton;
    protected ToolStripButton revertSelectionButton;
    protected ToolStripButton revertRejectAllButton;
    protected ToolStripButton refreshButton;
    protected ToolStripButton previewButton;
    protected ListGrid grid;
    protected ListGrid pendingGrid;
    protected ToolStrip toolBar;
    protected ToolStrip pendingToolBar;
    protected ToolStripButton reclaimSelectionButton;
    protected ToolStripButton reclaimAllButton;
    protected ToolStripButton releaseSelectionButton;
    protected ToolStripButton releaseAllButton;
    protected ToolStripButton pendingRefreshButton;
    protected ToolStripButton pendingPreviewButton;

    public MySandBoxView() {
        super(20);
        setWidth100();
        setHeight100();
        setLayoutMargin(20);
    }

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        setHeight100();
        setWidth100();

        VLayout insideLayout = new VLayout();
        insideLayout.setHeight("50%");


        Label header = new Label(BLCMain.getMessageManager().getString("userSandBoxTitle"));
        header.setBaseStyle("bl-header");
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
        revertRejectAllButton.setTitle(BLCMain.getMessageManager().getString("revertAllTitle"));
        revertRejectAllButton.setTooltip(BLCMain.getMessageManager().getString("revertAllTooltip"));
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
        revertSelectionButton = new ToolStripButton();
        revertSelectionButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/revert.png");
        revertSelectionButton.setTitle(BLCMain.getMessageManager().getString("revertTitle"));
        revertSelectionButton.setTooltip(BLCMain.getMessageManager().getString("revertTooltip"));
        toolBar.addButton(revertSelectionButton);


        toolBar.addFill();
        refreshButton = new ToolStripButton();
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/headerIcons/refresh.png");
        refreshButton.setTitle(BLCMain.getMessageManager().getString("refreshTitle"));
        toolBar.addButton(refreshButton);
        toolBar.addSpacer(3);
        previewButton = new ToolStripButton();
        previewButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/headerIcons/zoom.png");
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
        grid.setShowFilterEditor(true);
        grid.setCanGroupBy(false);
        grid.setDataPageSize(10);
        grid.setAlternateBodyStyleName("editRowDisabled");
        insideLayout.addMember(grid);

        addMember(insideLayout);

        VLayout insideLayout2 = new VLayout();
        insideLayout2.setHeight("50%");

        Label pendingApprovalLabel = new Label();
        pendingApprovalLabel.setContents(BLCMain.getMessageManager().getString("pendingApprovalTitle"));
        pendingApprovalLabel.setWrap(false);
        pendingApprovalLabel.setBaseStyle("bl-header");
        pendingApprovalLabel.setHeight(15);
        insideLayout2.addMember(pendingApprovalLabel);

        pendingToolBar = new ToolStrip();
        pendingToolBar.setHeight(30);
        pendingToolBar.setWidth100();
        pendingToolBar.addSpacer(6);


        reclaimAllButton = new ToolStripButton();
        reclaimAllButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/reclaim.png");
        reclaimAllButton.setTitle(BLCMain.getMessageManager().getString("reclaimAllTitle"));
        reclaimAllButton.setTooltip(BLCMain.getMessageManager().getString("reclaimAllTooltip"));
        pendingToolBar.addButton(reclaimAllButton);
        pendingToolBar.addSpacer(3);
        releaseAllButton = new ToolStripButton();
        releaseAllButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/unlock.png");
        releaseAllButton.setTitle(BLCMain.getMessageManager().getString("unlockAllTitle"));
        releaseAllButton.setTooltip(BLCMain.getMessageManager().getString("unlockAllTooltip"));
        pendingToolBar.addButton(releaseAllButton);

        pendingToolBar.addSpacer(3);
        pendingToolBar.addSeparator();
        pendingToolBar.addSpacer(3);


        reclaimSelectionButton = new ToolStripButton();
        reclaimSelectionButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/reclaim.png");
        reclaimSelectionButton.setTitle(BLCMain.getMessageManager().getString("reclaimTitle"));
        reclaimSelectionButton.setTooltip(BLCMain.getMessageManager().getString("reclaimTooltip"));
        pendingToolBar.addButton(reclaimSelectionButton);
        pendingToolBar.addSpacer(3);

        releaseSelectionButton = new ToolStripButton();
        releaseSelectionButton.setIcon(GWT.getModuleBaseURL() + "admin/images/button/unlock.png");
        releaseSelectionButton.setTitle(BLCMain.getMessageManager().getString("unlockTitle"));
        releaseSelectionButton.setTooltip(BLCMain.getMessageManager().getString("unlockTooltip"));
        pendingToolBar.addButton(releaseSelectionButton);


        pendingToolBar.addSpacer(6);


        pendingToolBar.addFill();
        pendingRefreshButton = new ToolStripButton();
        pendingRefreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/headerIcons/refresh.png");
        pendingRefreshButton.setTitle(BLCMain.getMessageManager().getString("refreshTitle"));
        pendingToolBar.addButton(pendingRefreshButton);
        pendingToolBar.addSpacer(3);
        pendingPreviewButton = new ToolStripButton();
        pendingPreviewButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/headerIcons/zoom.png");
        pendingPreviewButton.setTitle(BLCMain.getMessageManager().getString("previewTitle"));
        pendingToolBar.addButton(pendingPreviewButton);
        pendingToolBar.addSpacer(6);

        insideLayout2.addMember(pendingToolBar);

        pendingGrid = new ListGrid();
        pendingGrid.setCanReorderRecords(true);
        pendingGrid.setAlternateRecordStyles(true);
        pendingGrid.setSelectionType(SelectionStyle.MULTIPLE);
        pendingGrid.setCanEdit(false);
        pendingGrid.setDataSource(additionalDataSources[0]);
        pendingGrid.setAutoFetchData(true);
        pendingGrid.setDrawAllMaxCells(10);
        pendingGrid.setCanSort(true);
        pendingGrid.setCanResizeFields(true);
        pendingGrid.setShowFilterEditor(true);
        pendingGrid.setCanGroupBy(false);
        pendingGrid.setDataPageSize(10);
        pendingGrid.setAlternateBodyStyleName("editRowDisabled");
        insideLayout2.addMember(pendingGrid);

        addMember(insideLayout2);
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
        return this.revertSelectionButton;
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

    public ListGrid getPendingGrid() {
        return pendingGrid;
    }

    public ToolStripButton getRevertSelectionButton() {
        return revertSelectionButton;
    }

    public void setRevertSelectionButton(ToolStripButton revertSelectionButton) {
        this.revertSelectionButton = revertSelectionButton;
    }

    public ToolStrip getPendingToolBar() {
        return pendingToolBar;
    }

    public void setPendingToolBar(ToolStrip pendingToolBar) {
        this.pendingToolBar = pendingToolBar;
    }

    public ToolStripButton getReclaimSelectionButton() {
        return reclaimSelectionButton;
    }

    public void setReclaimSelectionButton(ToolStripButton reclaimSelectionButton) {
        this.reclaimSelectionButton = reclaimSelectionButton;
    }

    public ToolStripButton getReclaimAllButton() {
        return reclaimAllButton;
    }

    public void setReclaimAllButton(ToolStripButton reclaimAllButton) {
        this.reclaimAllButton = reclaimAllButton;
    }

    public ToolStripButton getReleaseSelectionButton() {
        return releaseSelectionButton;
    }

    public void setReleaseSelectionButton(ToolStripButton releaseSelectionButton) {
        this.releaseSelectionButton = releaseSelectionButton;
    }

    public ToolStripButton getReleaseAllButton() {
        return releaseAllButton;
    }

    public void setReleaseAllButton(ToolStripButton releaseAllButton) {
        this.releaseAllButton = releaseAllButton;
    }

    public ToolStripButton getPendingRefreshButton() {
        return pendingRefreshButton;
    }

    public void setPendingRefreshButton(ToolStripButton pendingRefreshButton) {
        this.pendingRefreshButton = pendingRefreshButton;
    }

    public ToolStripButton getPendingPreviewButton() {
        return pendingPreviewButton;
    }

    public void setPendingPreviewButton(ToolStripButton pendingPreviewButton) {
        this.pendingPreviewButton = pendingPreviewButton;
    }
}
