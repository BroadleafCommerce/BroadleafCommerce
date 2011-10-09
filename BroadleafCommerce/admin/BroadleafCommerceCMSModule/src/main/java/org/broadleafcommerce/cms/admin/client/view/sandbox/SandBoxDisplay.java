package org.broadleafcommerce.cms.admin.client.view.sandbox;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.Display;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/22/11
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SandBoxDisplay extends Display {

    public ToolStripButton getPromoteAllButton();

	public ToolStripButton getRevertRejectSelectionButton();

    public ToolStripButton getPromoteSelectionButton();

    public ToolStripButton getRevertRejectAllButton();

	public ListGrid getGrid();

    public ToolStripButton getRefreshButton();

	public ToolStrip getToolBar();

    public ToolStripButton getPreviewButton();

}
