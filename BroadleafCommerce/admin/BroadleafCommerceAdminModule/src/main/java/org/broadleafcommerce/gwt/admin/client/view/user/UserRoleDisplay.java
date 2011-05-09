package org.broadleafcommerce.gwt.admin.client.view.user;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface UserRoleDisplay {

	public ToolStripButton getAddButton();
	
	public ListGrid getGrid();
	
	public ToolStripButton getRemoveButton();
	
	public ListGrid getExpansionGrid();
	
	public ToolStrip getToolBar();
	
}
