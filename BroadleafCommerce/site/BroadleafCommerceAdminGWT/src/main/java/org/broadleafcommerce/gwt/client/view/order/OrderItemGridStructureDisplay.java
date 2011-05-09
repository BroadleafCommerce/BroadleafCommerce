package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyDisplay;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface OrderItemGridStructureDisplay {

	public ToolStripButton getAddButton();
	
	public ListGrid getGrid();
	
	public ToolStripButton getRemoveButton();

	public ToolStrip getToolBar();
	
	public ListGrid getExpansionGrid();
	
	public FormOnlyDisplay getOrderItemFormDisplay();
	
}
