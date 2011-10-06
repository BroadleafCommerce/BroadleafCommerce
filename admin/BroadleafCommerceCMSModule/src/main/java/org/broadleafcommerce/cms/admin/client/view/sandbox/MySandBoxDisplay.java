package org.broadleafcommerce.cms.admin.client.view.sandbox;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/22/11
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MySandBoxDisplay extends SandBoxDisplay {

    public ListGrid getPendingGrid();

    public ToolStripButton getRevertSelectionButton();

    public void setRevertSelectionButton(ToolStripButton revertSelectionButton);

    public ToolStrip getPendingToolBar();

    public void setPendingToolBar(ToolStrip pendingToolBar);

    public ToolStripButton getReclaimSelectionButton();

    public void setReclaimSelectionButton(ToolStripButton reclaimSelectionButton);

    public ToolStripButton getReclaimAllButton();

    public void setReclaimAllButton(ToolStripButton reclaimAllButton);

    public ToolStripButton getReleaseSelectionButton();

    public void setReleaseSelectionButton(ToolStripButton releaseSelectionButton);

    public ToolStripButton getReleaseAllButton();

    public void setReleaseAllButton(ToolStripButton releaseAllButton);

    public ToolStripButton getPendingRefreshButton();

    public void setPendingRefreshButton(ToolStripButton pendingRefreshButton);

    public ToolStripButton getPendingPreviewButton();

    public void setPendingPreviewButton(ToolStripButton pendingPreviewButton);
    
}
