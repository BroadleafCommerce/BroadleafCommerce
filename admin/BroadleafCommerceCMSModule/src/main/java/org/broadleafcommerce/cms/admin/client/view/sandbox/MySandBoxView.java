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

		toolBar = new ToolStrip();
		toolBar.setHeight(20);
		toolBar.setWidth100();
		toolBar.addSpacer(6);

        revertSelectionButton = new ToolStripButton();
        revertSelectionButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/arrow_down.png");
        toolBar.addButton(revertSelectionButton);
        revertRejectAllButton = new ToolStripButton();
        revertRejectAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_down.png");
        toolBar.addButton(revertRejectAllButton);

        toolBar.addSeparator();

        promoteSelectionButton = new ToolStripButton();
        promoteSelectionButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/arrow_up.png");
        toolBar.addButton(promoteSelectionButton);
        promoteAllButton = new ToolStripButton();
        promoteAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_up.png");
        toolBar.addButton(promoteAllButton);

        toolBar.addSpacer(6);
        Label mySandBoxLabel = new Label();
        mySandBoxLabel.setContents(BLCMain.getMessageManager().getString("userSandBoxTitle"));
        mySandBoxLabel.setWrap(false);
        toolBar.addMember(mySandBoxLabel);

        toolBar.addFill();
        refreshButton = new ToolStripButton();
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");
        toolBar.addButton(refreshButton);
        previewButton = new ToolStripButton();
        previewButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/zoom.png");
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

		pendingToolBar = new ToolStrip();
		pendingToolBar.setHeight(20);
		pendingToolBar.setWidth100();
		pendingToolBar.addSpacer(6);

        reclaimSelectionButton = new ToolStripButton();
        reclaimSelectionButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/arrow_left.png");
        pendingToolBar.addButton(reclaimSelectionButton);
        reclaimAllButton = new ToolStripButton();
        reclaimAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_left.png");
        pendingToolBar.addButton(reclaimAllButton);

        pendingToolBar.addSeparator();

        releaseSelectionButton = new ToolStripButton();
        releaseSelectionButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/arrow_down.png");
        pendingToolBar.addButton(releaseSelectionButton);
        releaseAllButton = new ToolStripButton();
        releaseAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_down.png");
        pendingToolBar.addButton(releaseAllButton);

        pendingToolBar.addSpacer(6);
        Label pendingApprovalLabel = new Label();
        pendingApprovalLabel.setContents(BLCMain.getMessageManager().getString("pendingApprovalTitle"));
        pendingApprovalLabel.setWrap(false);
        pendingToolBar.addMember(pendingApprovalLabel);

        pendingToolBar.addFill();
        pendingRefreshButton = new ToolStripButton();
        pendingRefreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");
        pendingToolBar.addButton(pendingRefreshButton);
        pendingPreviewButton = new ToolStripButton();
        pendingPreviewButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/zoom.png");
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
