package org.broadleafcommerce.gwt.client.view.dynamic.grid;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface GridStructureDisplay {

	public ToolStripButton getAddButton();
	
	public ListGrid getGrid();
	
	public ToolStripButton getRemoveButton();

	public ToolStrip getToolBar();
	
}
