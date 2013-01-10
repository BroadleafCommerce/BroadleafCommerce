package org.broadleafcommerce.openadmin.client.view.user;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * @author jfischer
 */
public interface UserPermissionDisplay {

    public ToolStripButton getAddButton();

        public ListGrid getGrid();

        public ToolStripButton getRemoveButton();

        public ToolStrip getToolBar();

}
