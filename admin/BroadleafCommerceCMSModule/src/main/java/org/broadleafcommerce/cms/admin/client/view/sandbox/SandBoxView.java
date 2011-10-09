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

		toolBar = new ToolStrip();
		toolBar.setHeight(20);
		toolBar.setWidth100();
		toolBar.addSpacer(6);

        revertRejectSelectionButton = new ToolStripButton();
        revertRejectSelectionButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/arrow_left.png");
        toolBar.addButton(revertRejectSelectionButton);
        revertRejectAllButton = new ToolStripButton();
        revertRejectAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_left.png");
        toolBar.addButton(revertRejectAllButton);

        toolBar.addSeparator();

        promoteSelectionButton = new ToolStripButton();
        promoteSelectionButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/arrow_right.png");
        toolBar.addButton(promoteSelectionButton);
        promoteAllButton = new ToolStripButton();
        promoteAllButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_right.png");
        toolBar.addButton(promoteAllButton);

        toolBar.addSpacer(6);
        Label productLabel = new Label();
        productLabel.setContents(BLCMain.getMessageManager().getString("userSandBoxTitle"));
        productLabel.setWrap(false);
        toolBar.addMember(productLabel);

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
        grid.setShowFilterEditor(false);
        grid.setCanGroupBy(false);
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
