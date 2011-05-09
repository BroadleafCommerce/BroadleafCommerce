package org.broadleafcommerce.gwt.client.view.dynamic;

import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface SubItemDisplay extends DynamicFormDisplay {

	public ToolStripButton getAddButton();
	
	public ListGrid getGrid();
	
	public ToolStripButton getRemoveButton();
	
}
