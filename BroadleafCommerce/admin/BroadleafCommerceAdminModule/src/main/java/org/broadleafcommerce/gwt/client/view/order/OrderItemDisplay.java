package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface OrderItemDisplay extends DynamicFormDisplay {

	public ToolStripButton getAddButton();
	
	public ListGrid getGrid();
	
	public ToolStripButton getRemoveButton();
	
	public ListGrid getExpansionGrid();
	
}
